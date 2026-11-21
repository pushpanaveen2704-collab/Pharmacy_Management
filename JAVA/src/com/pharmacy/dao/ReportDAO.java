package com.pharmacy.dao;

import com.pharmacy.model.DashboardSummary;
import com.pharmacy.model.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public DashboardSummary getDashboardSummary() throws SQLException {
        DashboardSummary ds = new DashboardSummary();
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Overall Totals
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM medicines")) {
                    if (rs.next()) ds.setTotalMedicinesRegistered(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM suppliers")) {
                    if (rs.next()) ds.setTotalSuppliers(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM purchases")) {
                    if (rs.next()) ds.setTotalPurchaseTransactions(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sales")) {
                    if (rs.next()) ds.setTotalSalesTransactions(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(current_stock), 0) FROM stock")) {
                    if (rs.next()) ds.setTotalAvailableStock(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(quantity_sold), 0) FROM sale_items")) {
                    if (rs.next()) ds.setTotalMedicinesSold(rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount), 0.0) FROM sales")) {
                    if (rs.next()) ds.setTotalRevenue(rs.getDouble(1));
                }
            }

            // 2. Stock & Expiry Health
            String stockSql = "SELECT " +
                "COALESCE(SUM(CASE WHEN s.current_stock > s.reorder_level AND m.expiry_date >= ? THEN 1 ELSE 0 END), 0) as avail, " +
                "COALESCE(SUM(CASE WHEN s.current_stock > 0 AND s.current_stock <= s.reorder_level THEN 1 ELSE 0 END), 0) as low, " +
                "COALESCE(SUM(CASE WHEN s.current_stock = 0 THEN 1 ELSE 0 END), 0) as out_stock, " +
                "COALESCE(SUM(CASE WHEN m.expiry_date < ? THEN 1 ELSE 0 END), 0) as exp, " +
                "COALESCE(SUM(CASE WHEN m.expiry_date >= ? AND m.expiry_date <= ? THEN 1 ELSE 0 END), 0) as near_exp " +
                "FROM stock s JOIN medicines m ON s.medicine_id = m.medicine_id";

            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                ps.setString(1, todayStr);
                ps.setString(2, todayStr);
                ps.setString(3, todayStr);
                ps.setString(4, today.plusDays(30).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ds.setAvailableMedicines(rs.getInt("avail"));
                        ds.setLowStockMedicines(rs.getInt("low"));
                        ds.setOutOfStockMedicines(rs.getInt("out_stock"));
                        ds.setExpiredMedicines(rs.getInt("exp"));
                        ds.setNearExpiryMedicines(rs.getInt("near_exp"));
                    }
                }
            }

            // 3. Today's Performance
            String todaySql = "SELECT COUNT(DISTINCT s.sale_id) as txn_count, " +
                              "COALESCE(SUM(si.quantity_sold), 0) as total_qty, " +
                              "COALESCE(SUM(s.total_amount), 0.0) as rev " +
                              "FROM sales s " +
                              "LEFT JOIN sale_items si ON s.sale_id = si.sale_id " +
                              "WHERE s.sale_date = ?";
            try (PreparedStatement ps = conn.prepareStatement(todaySql)) {
                ps.setString(1, todayStr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ds.setTodayTransactions(rs.getInt("txn_count"));
                        ds.setTodayMedicinesSold(rs.getInt("total_qty"));
                        ds.setTodayRevenue(rs.getDouble("rev"));
                    }
                }
            }

            // 4. Top Selling Medicine
            String topSql = "SELECT m.name, SUM(si.quantity_sold) as total_qty, SUM(si.subtotal) as total_rev " +
                            "FROM sale_items si " +
                            "JOIN medicines m ON si.medicine_id = m.medicine_id " +
                            "GROUP BY m.medicine_id, m.name " +
                            "ORDER BY total_qty DESC LIMIT 1";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(topSql)) {
                if (rs.next()) {
                    ds.setTopSellingMedicineName(rs.getString("name"));
                    ds.setTopSellingQuantitySold(rs.getInt("total_qty"));
                    ds.setTopSellingRevenue(rs.getDouble("total_rev"));
                }
            }

            // 5. Quick Alerts
            if (ds.getOutOfStockMedicines() > 0) {
                ds.addQuickAlert("[WARNING] " + ds.getOutOfStockMedicines() + " medicines are out of stock.");
            }
            if (ds.getLowStockMedicines() > 0) {
                ds.addQuickAlert("[WARNING] " + ds.getLowStockMedicines() + " medicines have low stock.");
            }
            if (ds.getNearExpiryMedicines() > 0) {
                ds.addQuickAlert("[WARNING] " + ds.getNearExpiryMedicines() + " medicines are nearing expiry.");
            }
            if (ds.getExpiredMedicines() > 0) {
                ds.addQuickAlert("[ALERT] " + ds.getExpiredMedicines() + " expired medicines require attention.");
            }
            if (ds.getQuickAlerts().isEmpty()) {
                ds.addQuickAlert("[INFO] All inventory stock and expiry levels are within normal operating parameters.");
            }
        }
        return ds;
    }

    public List<Map<String, Object>> getRankedMedicines(boolean mostSold, int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String order = mostSold ? "DESC" : "ASC";
        String sql = "SELECT m.medicine_id, m.name, m.category, " +
                     "COALESCE(SUM(si.quantity_sold), 0) as total_sold, " +
                     "COALESCE(SUM(si.subtotal), 0.0) as total_revenue " +
                     "FROM medicines m " +
                     "LEFT JOIN sale_items si ON m.medicine_id = si.medicine_id " +
                     "GROUP BY m.medicine_id, m.name, m.category " +
                     "ORDER BY total_sold " + order + ", total_revenue " + order + " " +
                     "LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("rank", rank++);
                    map.put("medicine_id", rs.getString("medicine_id"));
                    map.put("medicine_name", rs.getString("name"));
                    map.put("category", rs.getString("category"));
                    map.put("quantity_sold", rs.getInt("total_sold"));
                    map.put("revenue", rs.getDouble("total_revenue"));
                    list.add(map);
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getMedicineWiseSales() throws SQLException {
        return getRankedMedicines(true, 100);
    }

    public List<Map<String, Object>> getDailyRevenueAnalysis() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sale_date, COUNT(sale_id) as txn_count, SUM(total_amount) as daily_rev " +
                     "FROM sales GROUP BY sale_date ORDER BY sale_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("date", rs.getString("sale_date"));
                map.put("transactions", rs.getInt("txn_count"));
                map.put("revenue", rs.getDouble("daily_rev"));
                list.add(map);
            }
        }
        return list;
    }

    public List<Medicine> getNearExpiryMedicines(int daysThreshold) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines WHERE expiry_date >= ? AND expiry_date <= ? ORDER BY expiry_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today.toString());
            ps.setString(2, thresholdDate.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medicine m = new Medicine();
                    m.setMedicineId(rs.getString("medicine_id"));
                    m.setName(rs.getString("name"));
                    m.setCategory(rs.getString("category"));
                    m.setManufacturer(rs.getString("manufacturer"));
                    m.setUnitPrice(rs.getDouble("unit_price"));
                    m.setExpiryDate(LocalDate.parse(rs.getString("expiry_date")));
                    m.setReorderLevel(rs.getInt("reorder_level"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    public List<Medicine> getExpiredMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines WHERE expiry_date < ? ORDER BY expiry_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, today.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medicine m = new Medicine();
                    m.setMedicineId(rs.getString("medicine_id"));
                    m.setName(rs.getString("name"));
                    m.setCategory(rs.getString("category"));
                    m.setManufacturer(rs.getString("manufacturer"));
                    m.setUnitPrice(rs.getDouble("unit_price"));
                    m.setExpiryDate(LocalDate.parse(rs.getString("expiry_date")));
                    m.setReorderLevel(rs.getInt("reorder_level"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    public List<Medicine> getMedicinesExpiringThisMonth() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines WHERE expiry_date >= ? AND expiry_date <= ? ORDER BY expiry_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startOfMonth.toString());
            ps.setString(2, endOfMonth.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medicine m = new Medicine();
                    m.setMedicineId(rs.getString("medicine_id"));
                    m.setName(rs.getString("name"));
                    m.setCategory(rs.getString("category"));
                    m.setManufacturer(rs.getString("manufacturer"));
                    m.setUnitPrice(rs.getDouble("unit_price"));
                    m.setExpiryDate(LocalDate.parse(rs.getString("expiry_date")));
                    m.setReorderLevel(rs.getInt("reorder_level"));
                    list.add(m);
                }
            }
        }
        return list;
    }
}

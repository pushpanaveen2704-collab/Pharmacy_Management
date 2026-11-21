package com.pharmacy.dao;

import com.pharmacy.model.Stock;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public List<Stock> getAllStock() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, s.medicine_id, m.name as medicine_name, m.category, " +
                     "s.current_stock, s.reorder_level, m.expiry_date " +
                     "FROM stock s " +
                     "JOIN medicines m ON s.medicine_id = m.medicine_id " +
                     "ORDER BY s.medicine_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStock(rs));
            }
        }
        return list;
    }

    public Stock getStockByMedicineId(String medicineId) throws SQLException {
        String sql = "SELECT s.stock_id, s.medicine_id, m.name as medicine_name, m.category, " +
                     "s.current_stock, s.reorder_level, m.expiry_date " +
                     "FROM stock s " +
                     "JOIN medicines m ON s.medicine_id = m.medicine_id " +
                     "WHERE s.medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStock(rs);
                }
            }
        }
        return null;
    }

    public boolean updateStock(String medicineId, int newQuantity) throws SQLException {
        String sql = "UPDATE stock SET current_stock = ?, last_updated = CURRENT_TIMESTAMP WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setString(2, medicineId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Stock> getLowStockMedicines() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, s.medicine_id, m.name as medicine_name, m.category, " +
                     "s.current_stock, s.reorder_level, m.expiry_date " +
                     "FROM stock s " +
                     "JOIN medicines m ON s.medicine_id = m.medicine_id " +
                     "WHERE s.current_stock > 0 AND s.current_stock <= s.reorder_level " +
                     "ORDER BY s.current_stock ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStock(rs));
            }
        }
        return list;
    }

    public List<Stock> getOutOfStockMedicines() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, s.medicine_id, m.name as medicine_name, m.category, " +
                     "s.current_stock, s.reorder_level, m.expiry_date " +
                     "FROM stock s " +
                     "JOIN medicines m ON s.medicine_id = m.medicine_id " +
                     "WHERE s.current_stock = 0 " +
                     "ORDER BY s.medicine_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStock(rs));
            }
        }
        return list;
    }

    private Stock mapResultSetToStock(ResultSet rs) throws SQLException {
        int stockId = rs.getInt("stock_id");
        String medId = rs.getString("medicine_id");
        String name = rs.getString("medicine_name");
        String cat = rs.getString("category");
        int curStock = rs.getInt("current_stock");
        int reorder = rs.getInt("reorder_level");
        String exp = rs.getString("expiry_date");
        LocalDate expDate = exp != null ? LocalDate.parse(exp) : null;
        return new Stock(stockId, medId, name, cat, curStock, reorder, expDate);
    }
}

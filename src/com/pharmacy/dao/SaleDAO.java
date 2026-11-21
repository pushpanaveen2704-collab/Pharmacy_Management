package com.pharmacy.dao;

import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public synchronized boolean recordSale(Sale sale) throws SQLException {
        String sqlCheckStock = "SELECT current_stock FROM stock WHERE medicine_id = ?";
        String sqlCheckExpiry = "SELECT expiry_date FROM medicines WHERE medicine_id = ?";
        String sqlSale = "INSERT INTO sales (sale_id, sale_date, customer_name, total_amount) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, selling_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlDeductStock = "UPDATE stock SET current_stock = current_stock - ?, last_updated = CURRENT_TIMESTAMP WHERE medicine_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Validation checks for all items first
                for (SaleItem item : sale.getItems()) {
                    // Check Expiry
                    try (PreparedStatement psExp = conn.prepareStatement(sqlCheckExpiry)) {
                        psExp.setString(1, item.getMedicineId());
                        try (ResultSet rs = psExp.executeQuery()) {
                            if (rs.next()) {
                                LocalDate exp = LocalDate.parse(rs.getString("expiry_date"));
                                if (exp.isBefore(LocalDate.now())) {
                                    throw new SQLException("Expired medicine cannot be sold! Medicine " + item.getMedicineId() + 
                                                           " expired on " + exp);
                                }
                            } else {
                                throw new SQLException("Medicine ID not found: " + item.getMedicineId());
                            }
                        }
                    }

                    // Check Stock
                    try (PreparedStatement psStock = conn.prepareStatement(sqlCheckStock)) {
                        psStock.setString(1, item.getMedicineId());
                        try (ResultSet rs = psStock.executeQuery()) {
                            if (rs.next()) {
                                int avail = rs.getInt("current_stock");
                                if (avail < item.getQuantitySold()) {
                                    throw new SQLException("Insufficient stock available for " + item.getMedicineId() + 
                                                           ". Available: " + avail + ", Requested: " + item.getQuantitySold());
                                }
                            } else {
                                throw new SQLException("No stock record found for medicine " + item.getMedicineId());
                            }
                        }
                    }
                }

                // 2. Insert Sale Master
                try (PreparedStatement psSale = conn.prepareStatement(sqlSale)) {
                    psSale.setString(1, sale.getSaleId());
                    psSale.setString(2, sale.getSaleDate().toString());
                    psSale.setString(3, sale.getCustomerName());
                    psSale.setDouble(4, sale.getTotalAmount());
                    psSale.executeUpdate();
                }

                // 3. Insert Sale Items and Deduct Stock
                try (PreparedStatement psItem = conn.prepareStatement(sqlItem);
                     PreparedStatement psDeduct = conn.prepareStatement(sqlDeductStock)) {
                    for (SaleItem item : sale.getItems()) {
                        psItem.setString(1, sale.getSaleId());
                        psItem.setString(2, item.getMedicineId());
                        psItem.setInt(3, item.getQuantitySold());
                        psItem.setDouble(4, item.getSellingPrice());
                        psItem.setDouble(5, item.getSubtotal());
                        psItem.executeUpdate();

                        psDeduct.setInt(1, item.getQuantitySold());
                        psDeduct.setString(2, item.getMedicineId());
                        psDeduct.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Sale> getAllSales() throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT sale_id, sale_date, customer_name, total_amount FROM sales ORDER BY sale_date DESC, sale_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sale s = new Sale();
                s.setSaleId(rs.getString("sale_id"));
                s.setSaleDate(LocalDate.parse(rs.getString("sale_date")));
                s.setCustomerName(rs.getString("customer_name"));
                s.setTotalAmount(rs.getDouble("total_amount"));
                list.add(s);
            }
        }
        return list;
    }

    public Sale getSaleById(String id) throws SQLException {
        String sql = "SELECT sale_id, sale_date, customer_name, total_amount FROM sales WHERE sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale s = new Sale();
                    s.setSaleId(rs.getString("sale_id"));
                    s.setSaleDate(LocalDate.parse(rs.getString("sale_date")));
                    s.setCustomerName(rs.getString("customer_name"));
                    s.setTotalAmount(rs.getDouble("total_amount"));
                    s.setItems(getSaleItems(id));
                    return s;
                }
            }
        }
        return null;
    }

    public List<Sale> getDailySales(LocalDate date) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT sale_id, sale_date, customer_name, total_amount FROM sales WHERE sale_date = ? ORDER BY sale_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale();
                    s.setSaleId(rs.getString("sale_id"));
                    s.setSaleDate(LocalDate.parse(rs.getString("sale_date")));
                    s.setCustomerName(rs.getString("customer_name"));
                    s.setTotalAmount(rs.getDouble("total_amount"));
                    s.setItems(getSaleItems(s.getSaleId()));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public List<SaleItem> getSaleItems(String saleId) throws SQLException {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT si.sale_item_id, si.sale_id, si.medicine_id, m.name as medicine_name, " +
                     "si.quantity_sold, si.selling_price, si.subtotal " +
                     "FROM sale_items si " +
                     "LEFT JOIN medicines m ON si.medicine_id = m.medicine_id " +
                     "WHERE si.sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem();
                    item.setSaleItemId(rs.getInt("sale_item_id"));
                    item.setSaleId(rs.getString("sale_id"));
                    item.setMedicineId(rs.getString("medicine_id"));
                    item.setMedicineName(rs.getString("medicine_name"));
                    item.setQuantitySold(rs.getInt("quantity_sold"));
                    item.setSellingPrice(rs.getDouble("selling_price"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public boolean isSaleExists(String id) throws SQLException {
        String sql = "SELECT 1 FROM sales WHERE sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

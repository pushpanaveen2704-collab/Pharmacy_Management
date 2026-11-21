package com.pharmacy.dao;

import com.pharmacy.model.Purchase;
import com.pharmacy.model.PurchaseItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    public boolean recordPurchase(Purchase purchase) throws SQLException {
        String sqlPurchase = "INSERT INTO purchases (purchase_id, supplier_id, purchase_date, total_amount) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO purchase_items (purchase_id, medicine_id, batch_no, expiry_date, quantity, purchase_price) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE stock SET current_stock = current_stock + ?, last_updated = CURRENT_TIMESTAMP WHERE medicine_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPur = conn.prepareStatement(sqlPurchase);
                 PreparedStatement psItem = conn.prepareStatement(sqlItem);
                 PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock)) {

                // 1. Insert Purchase
                psPur.setString(1, purchase.getPurchaseId());
                psPur.setString(2, purchase.getSupplierId());
                psPur.setString(3, purchase.getPurchaseDate().toString());
                psPur.setDouble(4, purchase.getTotalAmount());
                psPur.executeUpdate();

                // 2. Insert Items and Update Stock
                for (PurchaseItem item : purchase.getItems()) {
                    psItem.setString(1, purchase.getPurchaseId());
                    psItem.setString(2, item.getMedicineId());
                    psItem.setString(3, item.getBatchNo());
                    psItem.setString(4, item.getExpiryDate().toString());
                    psItem.setInt(5, item.getQuantity());
                    psItem.setDouble(6, item.getPurchasePrice());
                    psItem.executeUpdate();

                    psStock.setInt(1, item.getQuantity());
                    psStock.setString(2, item.getMedicineId());
                    psStock.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Purchase> getAllPurchases() throws SQLException {
        List<Purchase> list = new ArrayList<>();
        String sql = "SELECT p.purchase_id, p.supplier_id, s.supplier_name, p.purchase_date, p.total_amount " +
                     "FROM purchases p " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "ORDER BY p.purchase_date DESC, p.purchase_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Purchase p = new Purchase();
                p.setPurchaseId(rs.getString("purchase_id"));
                p.setSupplierId(rs.getString("supplier_id"));
                p.setSupplierName(rs.getString("supplier_name"));
                p.setPurchaseDate(LocalDate.parse(rs.getString("purchase_date")));
                p.setTotalAmount(rs.getDouble("total_amount"));
                list.add(p);
            }
        }
        return list;
    }

    public Purchase getPurchaseById(String id) throws SQLException {
        String sql = "SELECT p.purchase_id, p.supplier_id, s.supplier_name, p.purchase_date, p.total_amount " +
                     "FROM purchases p " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "WHERE p.purchase_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Purchase p = new Purchase();
                    p.setPurchaseId(rs.getString("purchase_id"));
                    p.setSupplierId(rs.getString("supplier_id"));
                    p.setSupplierName(rs.getString("supplier_name"));
                    p.setPurchaseDate(LocalDate.parse(rs.getString("purchase_date")));
                    p.setTotalAmount(rs.getDouble("total_amount"));
                    p.setItems(getPurchaseItems(id));
                    return p;
                }
            }
        }
        return null;
    }

    public List<Purchase> getPurchasesBySupplier(String supplierId) throws SQLException {
        List<Purchase> list = new ArrayList<>();
        String sql = "SELECT p.purchase_id, p.supplier_id, s.supplier_name, p.purchase_date, p.total_amount " +
                     "FROM purchases p " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "WHERE p.supplier_id = ? " +
                     "ORDER BY p.purchase_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Purchase p = new Purchase();
                    p.setPurchaseId(rs.getString("purchase_id"));
                    p.setSupplierId(rs.getString("supplier_id"));
                    p.setSupplierName(rs.getString("supplier_name"));
                    p.setPurchaseDate(LocalDate.parse(rs.getString("purchase_date")));
                    p.setTotalAmount(rs.getDouble("total_amount"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<PurchaseItem> getPurchaseItems(String purchaseId) throws SQLException {
        List<PurchaseItem> items = new ArrayList<>();
        String sql = "SELECT pi.purchase_item_id, pi.purchase_id, pi.medicine_id, m.name as medicine_name, " +
                     "pi.batch_no, pi.expiry_date, pi.quantity, pi.purchase_price " +
                     "FROM purchase_items pi " +
                     "LEFT JOIN medicines m ON pi.medicine_id = m.medicine_id " +
                     "WHERE pi.purchase_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, purchaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseItem item = new PurchaseItem();
                    item.setPurchaseItemId(rs.getInt("purchase_item_id"));
                    item.setPurchaseId(rs.getString("purchase_id"));
                    item.setMedicineId(rs.getString("medicine_id"));
                    item.setMedicineName(rs.getString("medicine_name"));
                    item.setBatchNo(rs.getString("batch_no"));
                    item.setExpiryDate(LocalDate.parse(rs.getString("expiry_date")));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPurchasePrice(rs.getDouble("purchase_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public boolean isPurchaseExists(String id) throws SQLException {
        String sql = "SELECT 1 FROM purchases WHERE purchase_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

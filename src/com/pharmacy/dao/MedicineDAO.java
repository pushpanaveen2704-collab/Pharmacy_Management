package com.pharmacy.dao;

import com.pharmacy.model.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public boolean addMedicine(Medicine med) throws SQLException {
        String sqlMed = "INSERT INTO medicines (medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlStock = "INSERT OR IGNORE INTO stock (medicine_id, current_stock, reorder_level) VALUES (?, 0, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psMed = conn.prepareStatement(sqlMed);
                 PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                
                psMed.setString(1, med.getMedicineId());
                psMed.setString(2, med.getName());
                psMed.setString(3, med.getCategory());
                psMed.setString(4, med.getManufacturer());
                psMed.setDouble(5, med.getUnitPrice());
                psMed.setString(6, med.getExpiryDate().toString());
                psMed.setInt(7, med.getReorderLevel());
                psMed.executeUpdate();

                psStock.setString(1, med.getMedicineId());
                psStock.setInt(2, med.getReorderLevel());
                psStock.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines ORDER BY medicine_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMedicine(rs));
            }
        }
        return list;
    }

    public Medicine getMedicineById(String id) throws SQLException {
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicine(rs);
                }
            }
        }
        return null;
    }

    public List<Medicine> searchMedicines(String keyword) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level " +
                     "FROM medicines WHERE LOWER(medicine_id) LIKE ? OR LOWER(name) LIKE ? OR LOWER(category) LIKE ? " +
                     "ORDER BY medicine_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedicine(rs));
                }
            }
        }
        return list;
    }

    public boolean updateMedicine(Medicine med) throws SQLException {
        String sql = "UPDATE medicines SET name = ?, category = ?, manufacturer = ?, unit_price = ?, " +
                     "expiry_date = ?, reorder_level = ? WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, med.getName());
            ps.setString(2, med.getCategory());
            ps.setString(3, med.getManufacturer());
            ps.setDouble(4, med.getUnitPrice());
            ps.setString(5, med.getExpiryDate().toString());
            ps.setInt(6, med.getReorderLevel());
            ps.setString(7, med.getMedicineId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMedicine(String id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isMedicineExists(String id) throws SQLException {
        String sql = "SELECT 1 FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getString("medicine_id"));
        m.setName(rs.getString("name"));
        m.setCategory(rs.getString("category"));
        m.setManufacturer(rs.getString("manufacturer"));
        m.setUnitPrice(rs.getDouble("unit_price"));
        String exp = rs.getString("expiry_date");
        if (exp != null) {
            m.setExpiryDate(LocalDate.parse(exp));
        }
        m.setReorderLevel(rs.getInt("reorder_level"));
        return m;
    }
}

package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.SupplierDao;
import com.warungkupos.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements SupplierDao {

    public SupplierDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (name, contact, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContact());
            pstmt.setString(3, supplier.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambahkan supplier, tidak ada baris yang terpengaruh.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setId(generatedKeys.getInt(1));
                } else {
                    // System.err.println("Warning: Gagal mendapatkan ID supplier yang di-generate setelah insert.");
                }
            }
        }
    }

    @Override
    public Supplier getSupplierById(int id) throws SQLException {
        String sql = "SELECT id, name, contact, address FROM Suppliers WHERE id = ?";
        Supplier supplier = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    supplier = new Supplier();
                    supplier.setId(rs.getInt("id"));
                    supplier.setName(rs.getString("name"));
                    supplier.setContact(rs.getString("contact"));
                    supplier.setAddress(rs.getString("address"));
                }
            }
        }
        return supplier;
    }

    @Override
    public Supplier getSupplierByName(String name) throws SQLException {
        String sql = "SELECT id, name, contact, address FROM Suppliers WHERE name = ?";
        Supplier supplier = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    supplier = new Supplier();
                    supplier.setId(rs.getInt("id"));
                    supplier.setName(rs.getString("name"));
                    supplier.setContact(rs.getString("contact"));
                    supplier.setAddress(rs.getString("address"));
                }
            }
        }
        return supplier;
    }

    @Override
    public List<Supplier> getAllSuppliers() throws SQLException {
        String sql = "SELECT id, name, contact, address FROM Suppliers ORDER BY name ASC";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("name"));
                supplier.setContact(rs.getString("contact"));
                supplier.setAddress(rs.getString("address"));
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }

    @Override
    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE Suppliers SET name = ?, contact = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContact());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setInt(4, supplier.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteSupplier(int id) throws SQLException {
        // Service layer akan memanggil isSupplierInUse sebelum memanggil ini jika perlu
        String sql = "DELETE FROM Suppliers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean isSupplierInUse(int supplierId) throws SQLException {
        // Implementasi ini bergantung pada bagaimana supplier direlasikan dengan entitas lain.
        // Misalnya, jika tabel 'Products' memiliki kolom 'supplier_id':
        // String sql = "SELECT COUNT(*) AS usage_count FROM Products WHERE supplier_id = ?";
        // try (Connection conn = DatabaseManager.getConnection();
        //      PreparedStatement pstmt = conn.prepareStatement(sql)) {
        //     pstmt.setInt(1, supplierId);
        //     try (ResultSet rs = pstmt.executeQuery()) {
        //         if (rs.next()) {
        //             return rs.getInt("usage_count") > 0;
        //         }
        //     }
        // }
        // Untuk saat ini, jika tidak ada relasi langsung yang didefinisikan di skema DB kita,
        // kita kembalikan false. Ini perlu disesuaikan jika skema Anda memiliki relasi tersebut.
        return false; 
    }
}
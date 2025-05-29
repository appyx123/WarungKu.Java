package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.CategoryDao;
import com.warungkupos.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    public CategoryDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Gagal menambahkan kategori, tidak ada baris yang terpengaruh.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1)); // Set ID yang di-generate ke objek kategori
                } else {
                    // System.err.println("Warning: Gagal mendapatkan ID kategori yang di-generate setelah insert.");
                }
            }
        }
    }

    @Override
    public Category getCategoryById(int id) throws SQLException {
        String sql = "SELECT id, name FROM Categories WHERE id = ?";
        Category category = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                }
            }
        }
        return category;
    }

    @Override
    public Category getCategoryByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM Categories WHERE name = ?";
        Category category = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                }
            }
        }
        return category;
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT id, name FROM Categories ORDER BY name ASC";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Categories SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteCategory(int id) throws SQLException {
        // Pastikan kategori tidak sedang digunakan sebelum menghapus (logika ini ada di Service)
        String sql = "DELETE FROM Categories WHERE id = ?";
         try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean isCategoryInUse(int categoryId) throws SQLException {
        // Metode ini memeriksa apakah ada produk yang menggunakan kategori ini
        String sql = "SELECT COUNT(*) AS product_count FROM Products WHERE category_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_count") > 0;
                }
            }
        }
        return false; // Seharusnya tidak sampai sini jika query berhasil
    }
}
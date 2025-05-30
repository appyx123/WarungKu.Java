package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.ProductDao;
import com.warungkupos.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    public ProductDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Products (name, price, stock, category_id, supplier_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setBigDecimal(2, product.getPrice());
            pstmt.setInt(3, product.getStock());
            pstmt.setInt(4, product.getCategoryId());
            // Set supplier_id menjadi NULL di DB jika id = 0 (placeholder "Pilih Supplier")
            if (product.getSupplierId() == 0) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(5, product.getSupplierId());
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambahkan produk, tidak ada baris yang terpengaruh.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.supplier_id, s.name as supplier_name " +
                     "FROM Products p " +
                     "JOIN Categories c ON p.category_id = c.id " +
                     "LEFT JOIN Suppliers s ON p.supplier_id = s.id " + 
                     "WHERE p.id = ?";
        Product product = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id")); 
                    product.setSupplierName(rs.getString("supplier_name")); 
                }
            }
        }
        return product;
    }
    
    @Override
    public Product getProductByName(String name) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.supplier_id, s.name as supplier_name " +
                     "FROM Products p " +
                     "JOIN Categories c ON p.category_id = c.id " +
                     "LEFT JOIN Suppliers s ON p.supplier_id = s.id " + 
                     "WHERE p.name = ?";
        Product product = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id")); 
                    product.setSupplierName(rs.getString("supplier_name")); 
                }
            }
        }
        return product;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.supplier_id, s.name as supplier_name " +
                     "FROM Products p " +
                     "JOIN Categories c ON p.category_id = c.id " +
                     "LEFT JOIN Suppliers s ON p.supplier_id = s.id " + 
                     "ORDER BY p.name ASC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));
                product.setSupplierId(rs.getInt("supplier_id")); 
                product.setSupplierName(rs.getString("supplier_name")); 
                products.add(product);
            }
        }
        return products;
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.supplier_id, s.name as supplier_name " +
                     "FROM Products p " +
                     "JOIN Categories c ON p.category_id = c.id " +
                     "LEFT JOIN Suppliers s ON p.supplier_id = s.id " + 
                     "WHERE p.category_id = ? ORDER BY p.name ASC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id")); 
                    product.setSupplierName(rs.getString("supplier_name")); 
                    products.add(product);
                }
            }
        }
        return products;
    }
    
    @Override
    public List<Product> searchProducts(String keyword) throws SQLException {
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.supplier_id, s.name as supplier_name " +
                     "FROM Products p " +
                     "JOIN Categories c ON p.category_id = c.id " +
                     "LEFT JOIN Suppliers s ON p.supplier_id = s.id " + 
                     "WHERE LOWER(p.name) LIKE LOWER(?) OR LOWER(c.name) LIKE LOWER(?) OR LOWER(s.name) LIKE LOWER(?) " + 
                     "ORDER BY p.name ASC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setSupplierId(rs.getInt("supplier_id")); 
                    product.setSupplierName(rs.getString("supplier_name")); 
                    products.add(product);
                }
            }
        }
        return products;
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Products SET name = ?, price = ?, stock = ?, category_id = ?, supplier_id = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setBigDecimal(2, product.getPrice());
            pstmt.setInt(3, product.getStock());
            pstmt.setInt(4, product.getCategoryId());
            // Set supplier_id menjadi NULL di DB jika id = 0 (placeholder "Pilih Supplier")
            if (product.getSupplierId() == 0) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(5, product.getSupplierId());
            }
            pstmt.setInt(6, product.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM Products WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateProductStock(int productId, int newStock, Connection conn) throws SQLException {
        String sql = "UPDATE Products SET stock = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // <--- METODE BARU: isSupplierInUse
    @Override
    public boolean isSupplierInUse(int supplierId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Products WHERE supplier_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
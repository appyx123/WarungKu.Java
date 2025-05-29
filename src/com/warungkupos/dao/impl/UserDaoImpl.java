package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.UserDao;
import com.warungkupos.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Untuk mengambil generated keys
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    public UserDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addUser(User user) throws SQLException {
        // Kolom di DB: username, password, role, full_name. ID auto_increment.
        String sql = "INSERT INTO Users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // Password harus sudah di-hash oleh service
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFullName());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Gagal menambahkan user, tidak ada baris yang terpengaruh.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1)); // Set ID yang di-generate ke objek user
                } else {
                    // Tidak selalu menjadi error jika tabel tidak mengembalikan generated keys,
                    // tapi untuk auto_increment seharusnya ada.
                    // System.err.println("Warning: Gagal mendapatkan ID user yang di-generate setelah insert.");
                }
            }
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, role, full_name FROM Users WHERE username = ?";
        User user = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password")); // Password hash dari DB
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                }
            }
        }
        return user;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT id, username, password, role, full_name FROM Users WHERE id = ?";
        User user = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                }
            }
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id, username, role, full_name FROM Users ORDER BY username ASC"; // Password tidak diambil untuk list umum
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                // user.setPassword(rs.getString("password")); // Tidak mengambil password
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        // Umumnya username tidak diubah. Jika bisa diubah, perlu penanganan khusus.
        // Di sini kita update password, role, dan full_name berdasarkan username.
        // Jika ID adalah primary key yang stabil, lebih baik update berdasarkan ID.
        // Asumsi user.getId() sudah ada jika user diambil dari DB.
        String sql = "UPDATE Users SET password = ?, role = ?, full_name = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getPassword()); // Password harus sudah di-hash oleh service
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getFullName());
            pstmt.setInt(4, user.getId()); // Update berdasarkan ID
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean CekUser(String username) throws SQLException { // Nama metode dari interface
        String sql = "SELECT COUNT(*) AS user_count FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_count") > 0;
                }
            }
        }
        return false; // Seharusnya tidak sampai sini jika query berhasil
    }
}
package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.TransactionDetailDao;
import com.warungkupos.model.TransactionDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetailDaoImpl implements TransactionDetailDao {

    public TransactionDetailDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addTransactionDetail(TransactionDetail detail, Connection conn) throws SQLException {
        String sql = "INSERT INTO TransactionDetails (transaction_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        // Menggunakan koneksi yang diberikan
        // Asumsi tabel TransactionDetails memiliki PK auto-increment bernama 'id' jika detail.setId() dipanggil
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, detail.getTransactionId());
            pstmt.setInt(2, detail.getProductId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getUnitPrice());
            pstmt.setBigDecimal(5, detail.getSubtotal());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Coba ambil generated key (ID untuk TransactionDetail)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        detail.setId(generatedKeys.getInt(1)); 
                    }
                }
            } else {
                 throw new SQLException("Gagal membuat detail transaksi, tidak ada baris yang terpengaruh.");
            }
        }
        // Jangan tutup 'conn' di sini karena dikelola oleh pemanggil (Service)
    }

    @Override
    public void addTransactionDetails(List<TransactionDetail> details, Connection conn) throws SQLException {
        String sql = "INSERT INTO TransactionDetails (transaction_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        // Menggunakan koneksi yang diberikan, tidak ada manajemen transaksi internal (commit/rollback) di sini
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) { 
            for (TransactionDetail detail : details) {
                pstmt.setInt(1, detail.getTransactionId());
                pstmt.setInt(2, detail.getProductId());
                pstmt.setInt(3, detail.getQuantity());
                pstmt.setBigDecimal(4, detail.getUnitPrice());
                pstmt.setBigDecimal(5, detail.getSubtotal());
                // ID untuk setiap TransactionDetail diasumsikan auto-increment oleh DB
                // atau sudah di-set jika memiliki ID sendiri yang bukan auto-increment.
                // Jika setId dipanggil di addTransactionDetail, itu untuk 1 detail.
                // Untuk batch, kita biasanya tidak mengambil generated keys per item batch.
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        // Jangan tutup 'conn' di sini
    }

    @Override
    public List<TransactionDetail> getTransactionDetailsByTransactionId(int transactionId) throws SQLException {
        String sql = "SELECT td.id, td.transaction_id, td.product_id, p.name as product_name, td.quantity, td.unit_price, td.subtotal " +
                     "FROM TransactionDetails td JOIN Products p ON td.product_id = p.id " +
                     "WHERE td.transaction_id = ?";
        List<TransactionDetail> details = new ArrayList<>();
        // Metode read ini bisa menggunakan koneksi sendiri karena tidak mengubah data
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransactionDetail detail = new TransactionDetail();
                    detail.setId(rs.getInt("id")); // Asumsi 'id' adalah PK TransactionDetails
                    detail.setTransactionId(rs.getInt("transaction_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setProductName(rs.getString("product_name")); 
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                    detail.setSubtotal(rs.getBigDecimal("subtotal"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    @Override
    public boolean deleteTransactionDetailsByTransactionId(int transactionId, Connection conn) throws SQLException {
        String sql = "DELETE FROM TransactionDetails WHERE transaction_id = ?";
        // Menggunakan koneksi yang diberikan
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate(); 
            // Tidak perlu cek affectedRows secara ketat untuk delete ini,
            // karena bisa saja 0 jika transaksi tidak punya detail (misalnya, sudah dihapus).
            // Yang penting query berhasil dieksekusi tanpa SQLException.
            return true; 
        }
        // Jangan tutup 'conn' di sini
    }
}
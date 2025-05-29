package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.RecycleBinDao;
import com.warungkupos.model.RecycleBinDetail;
import com.warungkupos.model.RecycleBinTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecycleBinDaoImpl implements RecycleBinDao {

    public RecycleBinDaoImpl() {
        // Konstruktor
    }

    @Override
    public void addTransactionToRecycleBin(RecycleBinTransaction transactionHeader, List<RecycleBinDetail> details, Connection conn) throws SQLException {
        String sqlInsertHeader = "INSERT INTO RecycleBinTransactions (id, original_transaction_date, original_total_amount, original_username, deleted_date, deleted_by) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlInsertDetail = "INSERT INTO RecycleBinDetails (recycle_bin_transaction_id, original_product_id, original_product_name, original_quantity, original_unit_price, original_subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        
        // Insert Header
        try (PreparedStatement pstmtHeader = conn.prepareStatement(sqlInsertHeader)) {
            pstmtHeader.setInt(1, transactionHeader.getId()); 
            pstmtHeader.setTimestamp(2, new Timestamp(transactionHeader.getOriginalTransactionDate().getTime()));
            pstmtHeader.setBigDecimal(3, transactionHeader.getOriginalTotalAmount());
            pstmtHeader.setString(4, transactionHeader.getOriginalUsername());
            pstmtHeader.setTimestamp(5, new Timestamp(transactionHeader.getDeletedDate().getTime()));
            pstmtHeader.setString(6, transactionHeader.getDeletedBy());
            pstmtHeader.executeUpdate();
        }

        // Insert Details jika ada
        if (details != null && !details.isEmpty()) {
            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlInsertDetail)) {
                for (RecycleBinDetail detail : details) {
                    pstmtDetail.setInt(1, detail.getRecycleBinTransactionId()); 
                    pstmtDetail.setInt(2, detail.getOriginalProductId());
                    pstmtDetail.setString(3, detail.getOriginalProductName());
                    pstmtDetail.setInt(4, detail.getOriginalQuantity());
                    pstmtDetail.setBigDecimal(5, detail.getOriginalUnitPrice());
                    pstmtDetail.setBigDecimal(6, detail.getOriginalSubtotal());
                    pstmtDetail.addBatch();
                }
                pstmtDetail.executeBatch();
            }
        }
        // Tidak ada commit/rollback/close 'conn' di sini, dikelola oleh Service yang memanggil
    }
    
    @Override
    public void addTransactionToRecycleBin(RecycleBinTransaction transactionHeader, List<RecycleBinDetail> details) throws SQLException {
        Connection conn = null;
        boolean originalAutoCommit = false;
        try {
            conn = DatabaseManager.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); 
            
            // Panggil versi yang menerima koneksi
            addTransactionToRecycleBin(transactionHeader, details, conn);
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { 
                try { 
                    conn.rollback(); 
                } catch (SQLException ex) { 
                    System.err.println("Rollback gagal pada addTransactionToRecycleBin: " + ex.getMessage());
                } 
            }
            throw e; // Lemparkan exception asli agar bisa ditangani di atas
        } finally {
            if (conn != null) { 
                try { 
                    if(originalAutoCommit) {
                        conn.setAutoCommit(true); 
                    }
                    conn.close(); 
                } catch (SQLException e) { 
                    System.err.println("Error menutup koneksi pada addTransactionToRecycleBin: " + e.getMessage());
                } 
            }
        }
    }

    @Override
    public RecycleBinTransaction getRecycledTransactionById(int originalTransactionId) throws SQLException {
        String sql = "SELECT id, original_transaction_date, original_total_amount, original_username, deleted_date, deleted_by FROM RecycleBinTransactions WHERE id = ?";
        RecycleBinTransaction transaction = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, originalTransactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction = new RecycleBinTransaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setOriginalTransactionDate(new Date(rs.getTimestamp("original_transaction_date").getTime()));
                    transaction.setOriginalTotalAmount(rs.getBigDecimal("original_total_amount"));
                    transaction.setOriginalUsername(rs.getString("original_username"));
                    transaction.setDeletedDate(new Date(rs.getTimestamp("deleted_date").getTime()));
                    transaction.setDeletedBy(rs.getString("deleted_by"));
                }
            }
        }
        return transaction;
    }

    @Override
    public List<RecycleBinTransaction> getAllRecycledTransactions() throws SQLException {
        String sql = "SELECT id, original_transaction_date, original_total_amount, original_username, deleted_date, deleted_by FROM RecycleBinTransactions ORDER BY deleted_date DESC";
        List<RecycleBinTransaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                RecycleBinTransaction transaction = new RecycleBinTransaction();
                transaction.setId(rs.getInt("id"));
                transaction.setOriginalTransactionDate(new Date(rs.getTimestamp("original_transaction_date").getTime()));
                transaction.setOriginalTotalAmount(rs.getBigDecimal("original_total_amount"));
                transaction.setOriginalUsername(rs.getString("original_username"));
                transaction.setDeletedDate(new Date(rs.getTimestamp("deleted_date").getTime()));
                transaction.setDeletedBy(rs.getString("deleted_by"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public List<RecycleBinDetail> getRecycledDetailsByTransactionId(int originalTransactionId) throws SQLException {
        String sql = "SELECT recycle_bin_detail_id, recycle_bin_transaction_id, original_product_id, original_product_name, original_quantity, original_unit_price, original_subtotal " +
                     "FROM RecycleBinDetails WHERE recycle_bin_transaction_id = ?";
        List<RecycleBinDetail> details = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, originalTransactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RecycleBinDetail detail = new RecycleBinDetail();
                    detail.setId(rs.getInt("recycle_bin_detail_id")); 
                    detail.setRecycleBinTransactionId(rs.getInt("recycle_bin_transaction_id"));
                    detail.setOriginalProductId(rs.getInt("original_product_id"));
                    detail.setOriginalProductName(rs.getString("original_product_name"));
                    detail.setOriginalQuantity(rs.getInt("original_quantity"));
                    detail.setOriginalUnitPrice(rs.getBigDecimal("original_unit_price"));
                    detail.setOriginalSubtotal(rs.getBigDecimal("original_subtotal"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    @Override
    public boolean deletePermanentlyFromRecycleBin(int originalTransactionId, Connection conn) throws SQLException {
        String sqlDeleteDetails = "DELETE FROM RecycleBinDetails WHERE recycle_bin_transaction_id = ?";
        String sqlDeleteHeader = "DELETE FROM RecycleBinTransactions WHERE id = ?";
        int affectedRowsHeader = 0;

        try (PreparedStatement pstmtDetails = conn.prepareStatement(sqlDeleteDetails)) {
            pstmtDetails.setInt(1, originalTransactionId);
            pstmtDetails.executeUpdate(); 
        }

        try (PreparedStatement pstmtHeader = conn.prepareStatement(sqlDeleteHeader)) {
            pstmtHeader.setInt(1, originalTransactionId);
            affectedRowsHeader = pstmtHeader.executeUpdate();
        }
        return affectedRowsHeader > 0;
        // Tidak ada commit/rollback/close 'conn' di sini
    }

    @Override
    public boolean deletePermanentlyFromRecycleBin(int originalTransactionId) throws SQLException {
        Connection conn = null;
        boolean originalAutoCommit = false;
        boolean result = false;
        try {
            conn = DatabaseManager.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            result = deletePermanentlyFromRecycleBin(originalTransactionId, conn);

            conn.commit();
            return result;
        } catch (SQLException e) {
            if (conn != null) { 
                try { 
                    conn.rollback(); 
                } catch (SQLException ex) { 
                    System.err.println("Rollback gagal pada deletePermanentlyFromRecycleBin: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) { 
                try { 
                    if(originalAutoCommit) {
                        conn.setAutoCommit(true); 
                    }
                    conn.close(); 
                } catch (SQLException e) { 
                    System.err.println("Error menutup koneksi pada deletePermanentlyFromRecycleBin: " + e.getMessage());
                }
            }
        }
    }
}
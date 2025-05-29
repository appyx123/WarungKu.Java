package com.warungkupos.dao.impl;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.TransactionDao;
import com.warungkupos.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

    public TransactionDaoImpl() {
        // Konstruktor
    }

    @Override
    public int addTransaction(Transaction transaction, Connection conn) throws SQLException {
        String sql = "INSERT INTO Transactions (transaction_date, total_amount, username) VALUES (?, ?, ?)";
        int transactionId = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, new Timestamp(transaction.getTransactionDate().getTime()));
            pstmt.setBigDecimal(2, transaction.getTotalAmount());
            pstmt.setString(3, transaction.getUsername());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transactionId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Gagal membuat transaksi, tidak ada ID yang dihasilkan.");
                    }
                }
            } else {
                throw new SQLException("Gagal membuat transaksi, tidak ada baris yang terpengaruh.");
            }
        }
        return transactionId;
    }

    @Override
    public Transaction getTransactionById(int id) throws SQLException {
        String sql = "SELECT id, transaction_date, total_amount, username FROM Transactions WHERE id = ?";
        Transaction transaction = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setTransactionDate(new Date(rs.getTimestamp("transaction_date").getTime()));
                    transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                    transaction.setUsername(rs.getString("username"));
                }
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT id, transaction_date, total_amount, username FROM Transactions ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTransactionDate(new Date(rs.getTimestamp("transaction_date").getTime()));
                transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                transaction.setUsername(rs.getString("username"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByUsername(String username) throws SQLException {
        String sql = "SELECT id, transaction_date, total_amount, username FROM Transactions WHERE username = ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setTransactionDate(new Date(rs.getTimestamp("transaction_date").getTime()));
                    transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                    transaction.setUsername(rs.getString("username"));
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT id, transaction_date, total_amount, username FROM Transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            long oneDayMillis = 24 * 60 * 60 * 1000 - 1; 
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime() + oneDayMillis)); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setTransactionDate(new Date(rs.getTimestamp("transaction_date").getTime()));
                    transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                    transaction.setUsername(rs.getString("username"));
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    @Override
    public boolean deleteTransaction(int id) throws SQLException {
        String sql = "DELETE FROM Transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteTransaction(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM Transactions WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // <--- METODE BARU: hasTransactionsByUsername
    @Override
    public boolean hasTransactionsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Transactions WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
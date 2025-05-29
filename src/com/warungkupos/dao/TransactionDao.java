package com.warungkupos.dao;

import com.warungkupos.model.Transaction;
import java.sql.Connection;
import java.util.List;
import java.util.Date;

public interface TransactionDao {
    int addTransaction(Transaction transaction, Connection conn) throws Exception;
    Transaction getTransactionById(int id) throws Exception;
    List<Transaction> getAllTransactions() throws Exception;
    List<Transaction> getTransactionsByUsername(String username) throws Exception;
    List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws Exception;
    boolean deleteTransaction(int id) throws Exception;
    boolean deleteTransaction(int id, Connection conn) throws Exception;

    // <--- METODE BARU: Untuk mengecek apakah user punya transaksi
    boolean hasTransactionsByUsername(String username) throws Exception; 
}
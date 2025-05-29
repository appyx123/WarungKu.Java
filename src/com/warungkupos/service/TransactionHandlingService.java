package com.warungkupos.service;

import com.warungkupos.model.Transaction;
import com.warungkupos.model.TransactionDetail;
import java.util.List;
import java.util.Map; // Untuk pasangan Product ID dan Kuantitas
import java.util.Date;

public interface TransactionHandlingService {

    /**
     * Membuat transaksi baru.
     * Operasi ini harus bersifat transaksional (semua atau tidak sama sekali).
     * @param username Pengguna yang melakukan transaksi.
     * @param itemsToPurchase Peta berisi Product ID sebagai key dan Kuantitas sebagai value.
     * @return Objek Transaction yang berhasil dibuat.
     * @throws ServiceException Jika terjadi error bisnis (mis. stok tidak cukup, produk tidak ditemukan).
     * @throws Exception Jika terjadi error umum (mis. masalah database).
     */
    Transaction createTransaction(String username, Map<Integer, Integer> itemsToPurchase) throws ServiceException, Exception;

    /**
     * Mengambil riwayat transaksi untuk pengguna tertentu.
     * @param username Username pengguna.
     * @return List Transaction.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Jika terjadi error umum.
     */
    List<Transaction> getTransactionHistoryByUsername(String username) throws ServiceException, Exception;

    /**
     * Mengambil semua transaksi (untuk Admin).
     * @return List Transaction.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Jika terjadi error umum.
     */
    List<Transaction> getAllTransactions() throws ServiceException, Exception;
    
    /**
     * Mengambil semua transaksi dalam rentang tanggal tertentu (untuk Admin/Laporan).
     * @param startDate Tanggal mulai.
     * @param endDate Tanggal akhir.
     * @return List Transaction.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Jika terjadi error umum.
     */
    List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws ServiceException, Exception;

    /**
     * Mengambil detail dari sebuah transaksi.
     * @param transactionId ID transaksi.
     * @return List TransactionDetail.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Jika terjadi error umum.
     */
    List<TransactionDetail> getTransactionDetails(int transactionId) throws ServiceException, Exception;
}
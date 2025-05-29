package com.warungkupos.dao;

import com.warungkupos.model.TransactionDetail;
import java.sql.Connection; // Pastikan import ini ada
import java.util.List;

public interface TransactionDetailDao {

    /**
     * Menambahkan satu detail transaksi ke database menggunakan koneksi yang ada.
     * @param detail Objek TransactionDetail yang akan ditambahkan.
     * @param conn Koneksi database yang sudah ada dan dikelola oleh service.
     * @throws Exception Jika terjadi error SQL.
     */
    void addTransactionDetail(TransactionDetail detail, Connection conn) throws Exception;

    /**
     * Menambahkan beberapa detail transaksi secara batch ke database menggunakan koneksi yang ada.
     * @param details List dari objek TransactionDetail yang akan ditambahkan.
     * @param conn Koneksi database yang sudah ada dan dikelola oleh service.
     * @throws Exception Jika terjadi error SQL.
     */
    void addTransactionDetails(List<TransactionDetail> details, Connection conn) throws Exception; 

    /**
     * Mengambil semua detail transaksi berdasarkan ID transaksi utama.
     * @param transactionId ID dari transaksi utama.
     * @return List dari TransactionDetail.
     * @throws Exception Jika terjadi error SQL.
     */
    List<TransactionDetail> getTransactionDetailsByTransactionId(int transactionId) throws Exception;

    /**
     * Menghapus semua detail transaksi berdasarkan ID transaksi utama menggunakan koneksi yang ada.
     * @param transactionId ID dari transaksi utama yang detailnya akan dihapus.
     * @param conn Koneksi database yang sudah ada dan dikelola oleh service.
     * @return true jika operasi berhasil (meskipun mungkin tidak ada detail yang terhapus).
     * @throws Exception Jika terjadi error SQL.
     */
    boolean deleteTransactionDetailsByTransactionId(int transactionId, Connection conn) throws Exception;
}
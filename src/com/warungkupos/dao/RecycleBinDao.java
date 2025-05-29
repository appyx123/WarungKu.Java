package com.warungkupos.dao;

import com.warungkupos.model.RecycleBinDetail;
import com.warungkupos.model.RecycleBinTransaction;
import java.sql.Connection; // Pastikan import ini ada
import java.util.List;

public interface RecycleBinDao {

    /**
     * Menyimpan transaksi dan detailnya ke recycle bin, mengelola koneksi internal.
     * @param transactionHeader Objek RecycleBinTransaction.
     * @param details List dari RecycleBinDetail.
     * @throws Exception Jika terjadi error SQL.
     */
    void addTransactionToRecycleBin(RecycleBinTransaction transactionHeader, List<RecycleBinDetail> details) throws Exception;

    /**
     * Menyimpan transaksi dan detailnya ke recycle bin menggunakan koneksi eksternal.
     * @param transactionHeader Objek RecycleBinTransaction.
     * @param details List dari RecycleBinDetail.
     * @param conn Koneksi database yang sudah ada dan dikelola oleh service.
     * @throws Exception Jika terjadi error SQL.
     */
    void addTransactionToRecycleBin(RecycleBinTransaction transactionHeader, List<RecycleBinDetail> details, Connection conn) throws Exception;

    /**
     * Mengambil transaksi yang di-recycle berdasarkan ID aslinya.
     * @param originalTransactionId ID asli transaksi.
     * @return Objek RecycleBinTransaction atau null jika tidak ditemukan.
     * @throws Exception Jika terjadi error SQL.
     */
    RecycleBinTransaction getRecycledTransactionById(int originalTransactionId) throws Exception;

    /**
     * Mengambil semua transaksi yang ada di recycle bin.
     * @return List dari RecycleBinTransaction.
     * @throws Exception Jika terjadi error SQL.
     */
    List<RecycleBinTransaction> getAllRecycledTransactions() throws Exception;

    /**
     * Mengambil detail dari satu transaksi yang ada di recycle bin.
     * @param originalTransactionId ID asli transaksi di recycle bin.
     * @return List dari RecycleBinDetail.
     * @throws Exception Jika terjadi error SQL.
     */
    List<RecycleBinDetail> getRecycledDetailsByTransactionId(int originalTransactionId) throws Exception;

    /**
     * Menghapus transaksi dan detailnya secara permanen dari recycle bin, mengelola koneksi internal.
     * @param originalTransactionId ID asli transaksi yang akan dihapus.
     * @return true jika berhasil menghapus header transaksi.
     * @throws Exception Jika terjadi error SQL.
     */
    boolean deletePermanentlyFromRecycleBin(int originalTransactionId) throws Exception;

    /**
     * Menghapus transaksi dan detailnya secara permanen dari recycle bin menggunakan koneksi eksternal.
     * @param originalTransactionId ID asli transaksi yang akan dihapus.
     * @param conn Koneksi database yang sudah ada dan dikelola oleh service.
     * @return true jika berhasil menghapus header transaksi.
     * @throws Exception Jika terjadi error SQL.
     */
    boolean deletePermanentlyFromRecycleBin(int originalTransactionId, Connection conn) throws Exception;
}
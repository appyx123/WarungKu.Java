package com.warungkupos.service;

import com.warungkupos.model.RecycleBinDetail;
import com.warungkupos.model.RecycleBinTransaction;
// Import Transaction tidak secara langsung digunakan di interface ini,
// tapi mungkin berguna jika ada parameter terkait user yang melakukan operasi.
// import com.warungkupos.model.Transaction;

import java.util.List;

public interface RecycleBinService {

    /**
     * Memindahkan transaksi aktif ke recycle bin.
     * Ini melibatkan pembacaan dari tabel utama, penyimpanan ke tabel recycle bin,
     * dan penghapusan dari tabel utama. Semua dalam satu transaksi.
     * @param transactionId ID transaksi aktif yang akan dipindahkan.
     * @param deletedByUsername Username pengguna (Admin) yang melakukan operasi ini.
     * @throws ServiceException Jika transaksi tidak ditemukan atau terjadi error saat proses.
     * @throws Exception Error umum lainnya.
     */
    void moveTransactionToRecycleBin(int transactionId, String deletedByUsername) throws ServiceException, Exception;

    /**
     * Mengembalikan transaksi dari recycle bin ke tabel transaksi aktif.
     * Ini melibatkan pembacaan dari tabel recycle bin, penyimpanan kembali ke tabel utama,
     * dan penghapusan dari tabel recycle bin. Semua dalam satu transaksi.
     * PENTING: Operasi ini TIDAK akan mengembalikan/mengurangi stok produk secara otomatis.
     * Ini hanya mengembalikan catatan transaksinya.
     * @param originalTransactionId ID transaksi (seperti yang tersimpan di recycle bin) yang akan dikembalikan.
     * @throws ServiceException Jika transaksi di recycle bin tidak ditemukan atau terjadi error.
     * @throws Exception Error umum lainnya.
     */
    void restoreTransactionFromRecycleBin(int originalTransactionId) throws ServiceException, Exception;

    /**
     * Mendapatkan daftar semua transaksi yang ada di recycle bin.
     * @return List RecycleBinTransaction.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Error umum lainnya.
     */
    List<RecycleBinTransaction> getAllRecycledTransactions() throws ServiceException, Exception;

    /**
     * Mendapatkan detail dari satu transaksi yang ada di recycle bin.
     * @param originalTransactionId ID transaksi di recycle bin.
     * @return List RecycleBinDetail.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Error umum lainnya.
     */
    List<RecycleBinDetail> getRecycledTransactionDetails(int originalTransactionId) throws ServiceException, Exception;

    /**
     * Menghapus transaksi secara permanen dari recycle bin.
     * Operasi ini juga harus transaksional jika melibatkan penghapusan header dan detail.
     * @param originalTransactionId ID transaksi di recycle bin yang akan dihapus permanen.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Error umum lainnya.
     */
    void permanentlyDeleteFromRecycleBin(int originalTransactionId) throws ServiceException, Exception;
}
package com.warungkupos.service;

import com.warungkupos.model.Product; // Untuk ringkasan stok
import com.warungkupos.model.Transaction; // Untuk laporan penjualan
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReportGenerationService {

    /**
     * Menghitung total penjualan dalam rentang tanggal tertentu.
     * @param startDate Tanggal mulai.
     * @param endDate Tanggal akhir.
     * @return BigDecimal yang merepresentasikan total penjualan.
     * @throws ServiceException Jika terjadi error saat mengambil data transaksi.
     * @throws Exception Error umum lainnya.
     */
    BigDecimal getTotalSalesByDateRange(Date startDate, Date endDate) throws ServiceException, Exception;

    /**
     * Mendapatkan daftar semua produk beserta stoknya untuk ringkasan stok.
     * @return List dari objek Product.
     * @throws ServiceException Jika terjadi error saat mengambil data produk.
     * @throws Exception Error umum lainnya.
     */
    List<Product> getProductStockSummary() throws ServiceException, Exception;

    /**
     * Mendapatkan daftar transaksi dalam rentang tanggal tertentu.
     * Ini bisa digunakan sebagai dasar untuk laporan penjualan detail.
     * @param startDate Tanggal mulai.
     * @param endDate Tanggal akhir.
     * @return List dari objek Transaction.
     * @throws ServiceException Jika terjadi error saat mengambil data transaksi.
     * @throws Exception Error umum lainnya.
     */
    List<Transaction> getSalesTransactionsByDateRange(Date startDate, Date endDate) throws ServiceException, Exception;
    
    // Anda bisa menambahkan metode lain di sini sesuai kebutuhan laporan, misalnya:
    // - Laporan produk terlaris
    // - Laporan penjualan per kategori
    // - dll.
}
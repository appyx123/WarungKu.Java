package com.warungkupos.service;

import com.warungkupos.model.StockAlert; // Menggunakan model StockAlert
import java.util.List;

public interface StockNotificationService {

    /**
     * Mendapatkan daftar produk yang stoknya di bawah atau sama dengan batas yang ditentukan.
     * @param stockThreshold Batas minimum stok. Jika stok produk <= threshold, produk dianggap stok rendah.
     * @return List dari objek StockAlert.
     * @throws ServiceException Jika terjadi error saat mengambil data produk.
     * @throws Exception Error umum lainnya.
     */
    List<StockAlert> getLowStockProducts(int stockThreshold) throws ServiceException, Exception;
}
package com.warungkupos.service;

import com.warungkupos.dao.ProductDao;
import com.warungkupos.dao.impl.ProductDaoImpl;
import com.warungkupos.model.Product;
import com.warungkupos.model.StockAlert;

import java.util.ArrayList;
import java.util.List;

public class StockNotificationServiceImpl implements StockNotificationService {

    private final ProductDao productDao;

    // Konstruktor default
    public StockNotificationServiceImpl() {
        this.productDao = new ProductDaoImpl();
    }

    // Konstruktor untuk dependency injection (lebih baik untuk testing)
    public StockNotificationServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<StockAlert> getLowStockProducts(int stockThreshold) throws ServiceException, Exception {
        if (stockThreshold < 0) {
            // Meskipun stok tidak mungkin negatif, threshold bisa saja diset negatif oleh kesalahan input
            // throw new ServiceException("Batas stok tidak boleh negatif."); 
            // Atau kita anggap saja 0 jika negatif. Untuk kasus ini, biarkan saja,
            // karena produk dengan stok 0 atau lebih akan selalu > threshold negatif.
            // Lebih baik jika GUI memvalidasi threshold > 0.
        }

        List<StockAlert> lowStockAlerts = new ArrayList<>();
        try {
            List<Product> allProducts = productDao.getAllProducts(); // Mengambil semua produk

            for (Product product : allProducts) {
                if (product.getStock() <= stockThreshold) {
                    StockAlert alert = new StockAlert(
                            product.getId(),
                            product.getName(),
                            product.getCategoryName(), // Dari join di ProductDaoImpl.getAllProducts()
                            product.getStock(),
                            stockThreshold // Menyimpan threshold yang digunakan untuk alert ini
                    );
                    lowStockAlerts.add(alert);
                }
            }
        } catch (Exception e) {
            // SQLException dari DAO atau error lain
            throw new ServiceException("Gagal mendapatkan daftar produk stok rendah: " + e.getMessage(), e);
        }
        return lowStockAlerts;
    }
}
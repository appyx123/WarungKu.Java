package com.warungkupos.service;

import com.warungkupos.dao.ProductDao;
import com.warungkupos.dao.TransactionDao;
import com.warungkupos.dao.impl.ProductDaoImpl;
import com.warungkupos.dao.impl.TransactionDaoImpl;
import com.warungkupos.model.Product;
import com.warungkupos.model.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ReportGenerationServiceImpl implements ReportGenerationService {

    private final TransactionDao transactionDao;
    private final ProductDao productDao;

    // Konstruktor default
    public ReportGenerationServiceImpl() {
        this.transactionDao = new TransactionDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    // Konstruktor untuk dependency injection (lebih baik untuk testing)
    public ReportGenerationServiceImpl(TransactionDao transactionDao, ProductDao productDao) {
        this.transactionDao = transactionDao;
        this.productDao = productDao;
    }

    @Override
    public BigDecimal getTotalSalesByDateRange(Date startDate, Date endDate) throws ServiceException, Exception {
        if (startDate == null || endDate == null) {
            throw new ServiceException("Tanggal mulai dan tanggal akhir tidak boleh kosong untuk laporan penjualan.");
        }
        if (startDate.after(endDate)) {
            throw new ServiceException("Tanggal mulai tidak boleh setelah tanggal akhir.");
        }

        try {
            List<Transaction> transactions = transactionDao.getTransactionsByDateRange(startDate, endDate);
            BigDecimal totalSales = BigDecimal.ZERO;
            for (Transaction transaction : transactions) {
                totalSales = totalSales.add(transaction.getTotalAmount());
            }
            return totalSales;
        } catch (Exception e) {
            // SQLException dari DAO atau error lain
            throw new ServiceException("Gagal menghasilkan laporan total penjualan: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> getProductStockSummary() throws ServiceException, Exception {
        try {
            return productDao.getAllProducts(); // Mengambil semua produk dengan info stok dan kategori
        } catch (Exception e) {
            throw new ServiceException("Gagal menghasilkan ringkasan stok produk: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getSalesTransactionsByDateRange(Date startDate, Date endDate) throws ServiceException, Exception {
        if (startDate == null || endDate == null) {
            throw new ServiceException("Tanggal mulai dan tanggal akhir tidak boleh kosong untuk laporan transaksi.");
        }
        if (startDate.after(endDate)) {
            throw new ServiceException("Tanggal mulai tidak boleh setelah tanggal akhir.");
        }
        
        try {
            return transactionDao.getTransactionsByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new ServiceException("Gagal mengambil data transaksi penjualan: " + e.getMessage(), e);
        }
    }
}
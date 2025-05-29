package com.warungkupos.service; // Sesuai struktur Anda

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.ProductDao;
import com.warungkupos.dao.TransactionDao;
import com.warungkupos.dao.TransactionDetailDao;
import com.warungkupos.dao.impl.ProductDaoImpl;
import com.warungkupos.dao.impl.TransactionDaoImpl;
import com.warungkupos.dao.impl.TransactionDetailDaoImpl;
import com.warungkupos.model.Product;
import com.warungkupos.model.Transaction;
import com.warungkupos.model.TransactionDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TransactionHandlingServiceImpl implements TransactionHandlingService {

    private final ProductDao productDao;
    private final TransactionDao transactionDao;
    private final TransactionDetailDao transactionDetailDao;

    public TransactionHandlingServiceImpl() {
        this.productDao = new ProductDaoImpl();
        this.transactionDao = new TransactionDaoImpl();
        this.transactionDetailDao = new TransactionDetailDaoImpl();
    }

    // Konstruktor untuk dependency injection (lebih baik untuk testing)
    public TransactionHandlingServiceImpl(ProductDao productDao, TransactionDao transactionDao, TransactionDetailDao transactionDetailDao) {
        this.productDao = productDao;
        this.transactionDao = transactionDao;
        this.transactionDetailDao = transactionDetailDao;
    }

    @Override
    public Transaction createTransaction(String username, Map<Integer, Integer> itemsToPurchase) throws ServiceException, Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Username tidak valid untuk transaksi.");
        }
        if (itemsToPurchase == null || itemsToPurchase.isEmpty()) {
            throw new ServiceException("Tidak ada item untuk dibeli.");
        }

        Connection conn = null;
        Transaction newTransaction = null;
        List<TransactionDetail> transactionDetailsForDb = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            // Langkah 1: Validasi dan persiapan data sebelum memulai transaksi DB
            List<Product> productsToUpdateStock = new ArrayList<>();
            List<Integer> newStockLevels = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : itemsToPurchase.entrySet()) {
                int productId = entry.getKey();
                int quantityToPurchase = entry.getValue();

                if (quantityToPurchase <= 0) {
                    throw new ServiceException("Kuantitas produk ID " + productId + " harus lebih dari 0.");
                }

                Product product = productDao.getProductById(productId);
                if (product == null) {
                    throw new ServiceException("Produk dengan ID " + productId + " tidak ditemukan.");
                }
                if (product.getStock() < quantityToPurchase) {
                    throw new ServiceException("Stok produk '" + product.getName() + "' tidak mencukupi. Sisa: " + product.getStock());
                }

                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantityToPurchase));
                totalAmount = totalAmount.add(subtotal);

                TransactionDetail detail = new TransactionDetail();
                // ID transaksi akan di-set setelah header transaksi disimpan
                detail.setProductId(productId);
                // detail.setProductName(product.getName()); // Model TransactionDetail sudah menyimpan ini
                detail.setQuantity(quantityToPurchase);
                detail.setUnitPrice(product.getPrice());
                detail.setSubtotal(subtotal);
                transactionDetailsForDb.add(detail);

                productsToUpdateStock.add(product);
                newStockLevels.add(product.getStock() - quantityToPurchase);
            }

            // Langkah 2: Mulai transaksi database
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // Langkah 3: Buat header transaksi
            newTransaction = new Transaction(new Date(), totalAmount, username);
            int transactionId = transactionDao.addTransaction(newTransaction, conn); // Menggunakan versi DAO dengan Connection
            newTransaction.setId(transactionId);

            // Langkah 4: Simpan detail transaksi
            for (TransactionDetail detail : transactionDetailsForDb) {
                detail.setTransactionId(transactionId);
            }
            // Menggunakan versi DAO dengan Connection untuk batch insert
            transactionDetailDao.addTransactionDetails(transactionDetailsForDb, conn); 

            // Langkah 5: Update stok produk
            for (int i = 0; i < productsToUpdateStock.size(); i++) {
                Product product = productsToUpdateStock.get(i);
                int newStock = newStockLevels.get(i);
                // Menggunakan versi DAO dengan Connection
                productDao.updateProductStock(product.getId(), newStock, conn);
            }
            
            conn.commit(); // Semua berhasil, commit transaksi

        } catch (SQLException sqlEx) { // Tangkap SQLException secara spesifik
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException exRollback) {
                    throw new Exception("Gagal melakukan rollback transaksi dan gagal rollback: " + exRollback.getMessage(), sqlEx);
                }
            }
            throw new Exception("Gagal membuat transaksi karena masalah database: " + sqlEx.getMessage(), sqlEx);
        } catch (ServiceException servEx) { // Tangkap ServiceException dari validasi
             if (conn != null) { // Jika error terjadi setelah koneksi dibuka tapi sebelum commit
                try {
                    conn.rollback();
                } catch (SQLException exRollback) {
                     // Log error rollback
                }
            }
            throw servEx; // Lemparkan kembali ServiceException
        } catch (Exception e) { // Tangkap Exception umum lainnya
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException exRollback) {
                     // Log error rollback
                }
            }
            throw new Exception("Gagal membuat transaksi karena kesalahan tidak terduga: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException exClose) {
                    // Log error penutupan koneksi
                }
            }
        }
        return newTransaction;
    }

    @Override
    public List<Transaction> getTransactionHistoryByUsername(String username) throws ServiceException, Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Username tidak boleh kosong.");
        }
        try {
            return transactionDao.getTransactionsByUsername(username);
        } catch (Exception e) {
            throw new Exception("Gagal mengambil riwayat transaksi untuk pengguna '" + username + "': " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() throws ServiceException, Exception {
        try {
            return transactionDao.getAllTransactions();
        } catch (Exception e) {
            throw new Exception("Gagal mengambil semua transaksi: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws ServiceException, Exception {
        if (startDate == null || endDate == null) {
            throw new ServiceException("Tanggal mulai dan tanggal akhir tidak boleh kosong.");
        }
        if (startDate.after(endDate)) {
            throw new ServiceException("Tanggal mulai tidak boleh setelah tanggal akhir.");
        }
        try {
            return transactionDao.getTransactionsByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new Exception("Gagal mengambil transaksi berdasarkan rentang tanggal: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransactionDetail> getTransactionDetails(int transactionId) throws ServiceException, Exception {
        if (transactionId <= 0) {
            throw new ServiceException("ID Transaksi tidak valid.");
        }
        try {
            return transactionDetailDao.getTransactionDetailsByTransactionId(transactionId);
        } catch (Exception e) {
            throw new Exception("Gagal mengambil detail untuk transaksi ID " + transactionId + ": " + e.getMessage(), e);
        }
    }
}
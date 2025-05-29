package com.warungkupos.service;

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.ProductDao;
import com.warungkupos.dao.RecycleBinDao;
import com.warungkupos.dao.TransactionDao;
import com.warungkupos.dao.TransactionDetailDao;
import com.warungkupos.dao.impl.ProductDaoImpl;
import com.warungkupos.dao.impl.RecycleBinDaoImpl;
import com.warungkupos.dao.impl.TransactionDaoImpl;
import com.warungkupos.dao.impl.TransactionDetailDaoImpl;
import com.warungkupos.model.Product; // Meskipun tidak dipakai di sini, impor bisa saja ada
import com.warungkupos.model.RecycleBinDetail;
import com.warungkupos.model.RecycleBinTransaction;
import com.warungkupos.model.Transaction;
import com.warungkupos.model.TransactionDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecycleBinServiceImpl implements RecycleBinService {

    private final TransactionDao transactionDao;
    private final TransactionDetailDao transactionDetailDao;
    private final RecycleBinDao recycleBinDao;
    private final ProductDao productDao; 

    public RecycleBinServiceImpl() {
        this.transactionDao = new TransactionDaoImpl();
        this.transactionDetailDao = new TransactionDetailDaoImpl();
        this.recycleBinDao = new RecycleBinDaoImpl();
        this.productDao = new ProductDaoImpl(); 
    }

    public RecycleBinServiceImpl(TransactionDao transactionDao, TransactionDetailDao transactionDetailDao, RecycleBinDao recycleBinDao, ProductDao productDao) {
        this.transactionDao = transactionDao;
        this.transactionDetailDao = transactionDetailDao;
        this.recycleBinDao = recycleBinDao;
        this.productDao = productDao;
    }

    @Override
    public void moveTransactionToRecycleBin(int transactionId, String deletedByUsername) throws ServiceException, Exception {
        Connection conn = null;
        try {
            Transaction originalTransaction = transactionDao.getTransactionById(transactionId);
            if (originalTransaction == null) {
                throw new ServiceException("Transaksi dengan ID " + transactionId + " tidak ditemukan.");
            }
            List<TransactionDetail> originalDetails = transactionDetailDao.getTransactionDetailsByTransactionId(transactionId);

            RecycleBinTransaction rbTransaction = new RecycleBinTransaction(
                    originalTransaction.getId(),
                    originalTransaction.getTransactionDate(),
                    originalTransaction.getTotalAmount(),
                    originalTransaction.getUsername(),
                    new Date(), 
                    deletedByUsername
            );

            List<RecycleBinDetail> rbDetails = new ArrayList<>();
            for (TransactionDetail detail : originalDetails) {
                rbDetails.add(new RecycleBinDetail(
                        detail.getId(), 
                        originalTransaction.getId(), 
                        detail.getProductId(),
                        detail.getProductName(), 
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getSubtotal()
                ));
            }

            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            recycleBinDao.addTransactionToRecycleBin(rbTransaction, rbDetails, conn);
            transactionDetailDao.deleteTransactionDetailsByTransactionId(transactionId, conn);
            transactionDao.deleteTransaction(transactionId, conn); 

            conn.commit(); 

        } catch (SQLException | ServiceException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { System.err.println("Rollback gagal saat memindahkan transaksi ke recycle bin: " + ex.getMessage()); }
            }
            if (e instanceof ServiceException) throw (ServiceException) e;
            throw new Exception("Gagal memindahkan transaksi ke recycle bin: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { System.err.println("Error menutup koneksi saat memindahkan transaksi ke recycle bin: " + ex.getMessage());}
            }
        }
    }

    @Override
    public void restoreTransactionFromRecycleBin(int originalTransactionId) throws ServiceException, Exception {
        Connection conn = null;
        try {
            RecycleBinTransaction rbTransaction = recycleBinDao.getRecycledTransactionById(originalTransactionId);
            if (rbTransaction == null) {
                throw new ServiceException("Transaksi dengan ID " + originalTransactionId + " tidak ditemukan di recycle bin.");
            }
            List<RecycleBinDetail> rbDetails = recycleBinDao.getRecycledDetailsByTransactionId(originalTransactionId);

            // Buat objek Transaction baru TANPA ID jika ID di tabel Transactions adalah AUTO_INCREMENT
            // Biarkan database yang generate ID baru untuk transaksi yang direstore.
            Transaction restoredTransactionHeader = new Transaction(
                    // JANGAN SET ID DI SINI JIKA TRANSACTIONS.ID ADALAH AUTO_INCREMENT
                    // rbTransaction.getId(), // Hapus atau set ke 0
                    rbTransaction.getOriginalTransactionDate(),
                    rbTransaction.getOriginalTotalAmount(),
                    rbTransaction.getOriginalUsername()
            );

            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // Simpan header transaksi dan DAPATKAN ID BARU yang di-generate database
            int newGeneratedTransactionId = transactionDao.addTransaction(restoredTransactionHeader, conn);
            // Set ID yang baru di-generate ke objek header untuk referensi jika perlu
            restoredTransactionHeader.setId(newGeneratedTransactionId);


            List<TransactionDetail> restoredDetailsForDb = new ArrayList<>();
            for (RecycleBinDetail rbDetail : rbDetails) {
                TransactionDetail detail = new TransactionDetail(
                        // Jika TransactionDetails.id adalah AUTO_INCREMENT, JANGAN SET ID di sini
                        // rbDetail.getId(), // Hapus atau set ke 0 untuk ID detail
                        newGeneratedTransactionId, // <<< GUNAKAN ID TRANSAKSI BARU YANG DI-GENERATE
                        rbDetail.getOriginalProductId(),
                        rbDetail.getOriginalQuantity(),
                        rbDetail.getOriginalUnitPrice(),
                        rbDetail.getOriginalSubtotal()
                );
                detail.setProductName(rbDetail.getOriginalProductName());
                restoredDetailsForDb.add(detail);
            }
            
            // Simpan semua detail transaksi dengan ID header yang baru
            if (!restoredDetailsForDb.isEmpty()) {
                // Jika Anda menggunakan addTransactionDetail satu per satu:
                for (TransactionDetail detailToSave : restoredDetailsForDb) {
                    transactionDetailDao.addTransactionDetail(detailToSave, conn);
                }
                // Atau jika menggunakan batch insert:
                // transactionDetailDao.addTransactionDetails(restoredDetailsForDb, conn);
            }

            recycleBinDao.deletePermanentlyFromRecycleBin(originalTransactionId, conn);

            conn.commit();

        } catch (SQLException | ServiceException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { System.err.println("Rollback gagal saat mengembalikan transaksi: " + ex.getMessage()); }
            }
            if (e instanceof ServiceException) throw (ServiceException) e;
            // Tambahkan penyebab asli ke Exception baru untuk detail lebih lanjut
            throw new Exception("Gagal mengembalikan transaksi dari recycle bin: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { System.err.println("Error menutup koneksi saat mengembalikan transaksi: " + ex.getMessage());}
            }
        }
    }

    @Override
    public List<RecycleBinTransaction> getAllRecycledTransactions() throws ServiceException, Exception {
        try {
            return recycleBinDao.getAllRecycledTransactions();
        } catch (Exception e) {
            throw new ServiceException("Gagal mendapatkan daftar transaksi dari recycle bin: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RecycleBinDetail> getRecycledTransactionDetails(int originalTransactionId) throws ServiceException, Exception {
         if (originalTransactionId <= 0) {
            throw new ServiceException("ID Transaksi recycle bin tidak valid.");
        }
        try {
            return recycleBinDao.getRecycledDetailsByTransactionId(originalTransactionId);
        } catch (Exception e) {
            throw new ServiceException("Gagal mendapatkan detail transaksi dari recycle bin: " + e.getMessage(), e);
        }
    }

    @Override
    public void permanentlyDeleteFromRecycleBin(int originalTransactionId) throws ServiceException, Exception {
        Connection conn = null;
        try {
            RecycleBinTransaction txToHardDelete = recycleBinDao.getRecycledTransactionById(originalTransactionId);
            if (txToHardDelete == null) {
                throw new ServiceException("Transaksi dengan ID " + originalTransactionId + " tidak ditemukan di recycle bin untuk dihapus permanen.");
            }

            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            recycleBinDao.deletePermanentlyFromRecycleBin(originalTransactionId, conn);
            
            conn.commit();
        } catch (SQLException | ServiceException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { System.err.println("Rollback gagal saat menghapus permanen transaksi: " + ex.getMessage());}
            }
            if (e instanceof ServiceException) throw (ServiceException) e;
            throw new Exception("Gagal menghapus permanen transaksi dari recycle bin: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { System.err.println("Error menutup koneksi saat menghapus permanen transaksi: " + ex.getMessage());}
            }
        }
    }
}
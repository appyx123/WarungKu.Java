package com.warungkupos.controller;

import com.warungkupos.dao.UserDao; // Untuk total pengguna
import com.warungkupos.service.ProductManagementService;
import com.warungkupos.service.ReportGenerationService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.service.StockNotificationService;
import com.warungkupos.service.TransactionHandlingService;
import com.warungkupos.util.AppConstants; // Untuk default threshold stok rendah
import com.warungkupos.util.DateFormatter; // Untuk tanggal hari ini/bulan ini
import com.warungkupos.view.admin.AdminMainDashboardPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List; // Untuk List<Product> dari StockNotificationService

public class AdminMainDashboardController {

    private AdminMainDashboardPanel view;
    private ProductManagementService productManagementService;
    private TransactionHandlingService transactionHandlingService;
    private ReportGenerationService reportGenerationService;
    private StockNotificationService stockNotificationService;
    private UserDao userDao; // Untuk menghitung total pengguna

    public AdminMainDashboardController(AdminMainDashboardPanel view,
                                        ProductManagementService productManagementService,
                                        TransactionHandlingService transactionHandlingService,
                                        ReportGenerationService reportGenerationService,
                                        StockNotificationService stockNotificationService,
                                        UserDao userDao) { // Tambahkan UserDao di sini
        this.view = view;
        this.productManagementService = productManagementService;
        this.transactionHandlingService = transactionHandlingService;
        this.reportGenerationService = reportGenerationService;
        this.stockNotificationService = stockNotificationService;
        this.userDao = userDao;

        attachListeners();
        initializeView();
    }

    private void initializeView() {
        refreshStats(); // Muat statistik saat controller diinisialisasi
    }

    private void attachListeners() {
        view.getRefreshButton().addActionListener(e -> refreshStats());
    }

    // Metode utama untuk mengambil dan menampilkan semua statistik
    public void refreshStats() {
        try {
            // --- Statistik Inventaris ---
            view.setTotalProducts(productManagementService.getAllProducts().size());
            view.setTotalCategories(productManagementService.getAllCategories().size());
            view.setTotalSuppliers(productManagementService.getAllSuppliers().size());
            view.setLowStockProducts(stockNotificationService.getLowStockProducts(AppConstants.DEFAULT_LOW_STOCK_THRESHOLD).size());

            // --- Statistik Penjualan/Transaksi Hari Ini ---
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            String todayStr = DateFormatter.formatCustom(today, DateFormatter.STORAGE_DATE_FORMAT);
            Date startOfToday = DateFormatter.parseCustom(todayStr + " 00:00:00", DateFormatter.STORAGE_DATE_TIME_FORMAT);
            Date endOfToday = DateFormatter.parseCustom(todayStr + " 23:59:59", DateFormatter.STORAGE_DATE_TIME_FORMAT);
            
            BigDecimal salesToday = reportGenerationService.getTotalSalesByDateRange(startOfToday, endOfToday);
            int transactionsToday = reportGenerationService.getSalesTransactionsByDateRange(startOfToday, endOfToday).size();
            view.setTotalSalesToday(salesToday);
            view.setTotalTransactionsToday(transactionsToday);

            // --- Statistik Penjualan/Transaksi Bulan Ini ---
            cal = Calendar.getInstance(); // Reset calendar ke awal bulan ini
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startOfMonth = cal.getTime();
            
            cal = Calendar.getInstance(); // Reset calendar ke akhir bulan ini
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endOfMonth = cal.getTime();
            
            BigDecimal salesMonth = reportGenerationService.getTotalSalesByDateRange(startOfMonth, endOfMonth);
            int transactionsMonth = reportGenerationService.getSalesTransactionsByDateRange(startOfMonth, endOfMonth).size();
            view.setTotalSalesMonth(salesMonth);
            view.setTotalTransactionsMonth(transactionsMonth);

            // --- Statistik Pengguna ---
            view.setTotalUsers(userDao.getAllUsers().size()); // Menggunakan UserDao langsung

        } catch (ServiceException se) {
            view.showMessage("Gagal memuat statistik: " + se.getMessage(), "Error Statistik", JOptionPane.ERROR_MESSAGE);
            se.printStackTrace();
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memuat statistik: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
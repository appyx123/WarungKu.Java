package com.warungkupos.controller;

import com.warungkupos.model.StockAlert;
import com.warungkupos.service.ServiceException;
import com.warungkupos.service.StockNotificationService;
import com.warungkupos.util.AppConstants; // Untuk mengambil default threshold
import com.warungkupos.view.admin.StockAlertPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockAlertController {

    private StockAlertPanel view;
    private StockNotificationService stockNotificationService;
    private int currentStockThreshold; // Untuk menyimpan threshold yang sedang digunakan

    public StockAlertController(StockAlertPanel view, StockNotificationService stockNotificationService) {
        this.view = view;
        this.stockNotificationService = stockNotificationService;
        this.currentStockThreshold = AppConstants.DEFAULT_LOW_STOCK_THRESHOLD; // Ambil dari konstanta

        attachListeners();
        initializeView();
    }

    private void initializeView() {
        // Set info label awal dengan threshold default
        view.setInfoLabel("Menampilkan produk dengan stok <= " + currentStockThreshold + " unit.");
        loadStockAlerts();
    }

    private void attachListeners() {
        view.getRefreshButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mungkin di masa depan ada input untuk mengubah threshold di panel
                // Untuk sekarang, selalu gunakan threshold yang sudah ditentukan.
                loadStockAlerts();
            }
        });
    }

    private void loadStockAlerts() {
        // Anda bisa membuat input field di StockAlertPanel untuk mengubah threshold ini jika diperlukan
        // Untuk saat ini, kita gunakan currentStockThreshold
        try {
            List<StockAlert> alerts = stockNotificationService.getLowStockProducts(currentStockThreshold);
            view.displayStockAlerts(alerts);
            // Update info label jika threshold bisa berubah atau untuk konfirmasi
            if (alerts.isEmpty()) {
                 view.setInfoLabel("Tidak ada produk dengan stok rendah (<= " + currentStockThreshold + " unit).");
            } else {
                 view.setInfoLabel("Peringatan untuk produk dengan stok <= " + currentStockThreshold + " unit.");
            }
        } catch (ServiceException se) {
            view.showMessage("Gagal memuat peringatan stok: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memuat peringatan stok: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Metode ini bisa dipanggil jika Anda menambahkan UI untuk mengubah threshold
    public void setStockThresholdAndRefresh(int newThreshold) {
        if (newThreshold < 0) {
            view.showMessage("Batas stok tidak boleh negatif.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.currentStockThreshold = newThreshold;
        view.setInfoLabel("Menampilkan produk dengan stok <= " + currentStockThreshold + " unit.");
        loadStockAlerts();
    }
}
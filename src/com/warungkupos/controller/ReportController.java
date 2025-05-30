package com.warungkupos.controller;

import com.warungkupos.model.Product;
import com.warungkupos.service.ReportGenerationService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.util.DateFormatter;
import com.warungkupos.view.admin.ReportPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar; // <--- TAMBAHKAN IMPORT INI
import java.util.Date;
import java.util.List;

public class ReportController {

    private ReportPanel view;
    private ReportGenerationService reportService;

    public ReportController(ReportPanel view, ReportGenerationService reportService) {
        this.view = view;
        this.reportService = reportService;
        attachListeners();
        initializeView();
    }

    private void initializeView() {
        loadProductStockSummary();
        view.displayTotalSales(BigDecimal.ZERO); 
        
        // --- BARU: Auto-fill filter tanggal dan generate laporan ---
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime(); // Hari ini

        cal.add(Calendar.MONTH, -1); // Kurangi 1 bulan
        Date oneMonthAgo = cal.getTime(); // Satu bulan lalu

        // Format tanggal ke string yyyy-MM-dd
        String startDateString = DateFormatter.formatCustom(oneMonthAgo, DateFormatter.STORAGE_DATE_FORMAT);
        String endDateString = DateFormatter.formatCustom(today, DateFormatter.STORAGE_DATE_FORMAT);

        // Set ke field di view
        view.setStartDateField(startDateString);
        view.setEndDateField(endDateString);

        // Otomatis tampilkan laporan penjualan untuk periode ini
        generateSalesReport();
        // --- AKHIR BARU ---
    }

    private void attachListeners() {
        view.getGenerateSalesReportButton().addActionListener(e -> generateSalesReport());
        view.getRefreshStockReportButton().addActionListener(e -> loadProductStockSummary());
    }

    private void generateSalesReport() {
        if (!view.validateDateInputs()) {
            return;
        }

        String startDateStr = view.getStartDateFieldText();
        String endDateStr = view.getEndDateFieldText();

        // Karena validateDateInputs() sudah berhasil, kita tahu string ini valid dan tidak kosong
        Date startDate = DateFormatter.parseDateFromStorage(startDateStr);
        Date endDate = DateFormatter.parseDateFromStorage(endDateStr);

        try {
            BigDecimal totalSales = reportService.getTotalSalesByDateRange(startDate, endDate);
            view.displayTotalSales(totalSales);
        } catch (ServiceException se) {
            view.showMessage("Gagal menghasilkan laporan penjualan: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat menghasilkan laporan penjualan: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadProductStockSummary() {
        try {
            List<Product> productStockList = reportService.getProductStockSummary();
            view.displayProductStockSummary(productStockList);
        } catch (ServiceException se) {
            view.showMessage("Gagal memuat ringkasan stok produk: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memuat ringkasan stok: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
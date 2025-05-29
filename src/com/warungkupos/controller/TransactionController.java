package com.warungkupos.controller;

import com.warungkupos.model.Transaction;
import com.warungkupos.model.TransactionDetail;
import com.warungkupos.service.RecycleBinService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.service.TransactionHandlingService;
import com.warungkupos.util.DateFormatter;
import com.warungkupos.view.admin.TransactionManagementPanel; // <--- IMPORT YANG BENAR DITAMBAHKAN

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // Mencakup Frame
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionController {

    private TransactionManagementPanel view;
    private TransactionHandlingService transactionService;
    private RecycleBinService recycleBinService;
    private NumberFormat currencyFormatter;


    public TransactionController(TransactionManagementPanel view,
                                 TransactionHandlingService transactionService,
                                 RecycleBinService recycleBinService) {
        this.view = view;
        this.transactionService = transactionService;
        this.recycleBinService = recycleBinService;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        attachListeners();
        initializeView();
    }

    private void initializeView() {
        loadAllTransactions();
        if (view.getStartDateField() != null) view.getStartDateField().setText("");
        if (view.getEndDateField() != null) view.getEndDateField().setText("");

        view.getViewDetailsButton().setEnabled(false);
        view.getMoveToRecycleBinButton().setEnabled(false);
    }

    private void attachListeners() {
        view.getFilterButton().addActionListener(e -> filterTransactions());
        view.getResetButton().addActionListener(e -> {
            if (view.getStartDateField() != null) view.getStartDateField().setText("");
            if (view.getEndDateField() != null) view.getEndDateField().setText("");
            loadAllTransactions();
        });
        view.getViewDetailsButton().addActionListener(e -> viewTransactionDetails());
        view.getMoveToRecycleBinButton().addActionListener(e -> moveSelectedTransactionToRecycleBin());

        view.getTransactionTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = view.getTransactionTable().getSelectedRow() != -1;
                view.getViewDetailsButton().setEnabled(rowSelected);
                view.getMoveToRecycleBinButton().setEnabled(rowSelected);
            }
        });
    }

    private void loadAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            view.displayTransactions(transactions);
        } catch (Exception e) {
            view.showMessage("Gagal memuat semua transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterTransactions() {
        if (!view.validateDateInputs()) {
            return;
        }

        String startDateStr = view.getStartDateFieldText();
        String endDateStr = view.getEndDateFieldText();

        if (startDateStr.isEmpty() && endDateStr.isEmpty()) {
            loadAllTransactions();
            return;
        }
        
        Date startDate = DateFormatter.parseDateFromStorage(startDateStr);
        Date endDate = DateFormatter.parseDateFromStorage(endDateStr);

        try {
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            view.displayTransactions(transactions);

        } catch (ServiceException se) {
            view.showMessage("Error saat filter transaksi: " + se.getMessage(), "Filter Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat filter transaksi: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void viewTransactionDetails() {
        int selectedRow = view.getTransactionTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Pilih transaksi untuk dilihat detailnya.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) view.getTransactionTable().getModel().getValueAt(
            view.getTransactionTable().convertRowIndexToModel(selectedRow), 0);

        try {
            List<TransactionDetail> details = transactionService.getTransactionDetails(transactionId);
            if (details.isEmpty()) {
                view.showMessage("Tidak ada detail untuk transaksi ini.", "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Detail Transaksi ID: " + transactionId, true);
            detailDialog.setSize(500, 350);
            detailDialog.setLocationRelativeTo(view);
            
            String[] columnNames = {"ID Produk", "Nama Produk", "Kuantitas", "Harga Satuan", "Subtotal"};
            DefaultTableModel detailTableModel = new DefaultTableModel(columnNames, 0);
            for(TransactionDetail detail : details) {
                detailTableModel.addRow(new Object[]{
                    detail.getProductId(),
                    detail.getProductName(),
                    detail.getQuantity(),
                    currencyFormatter.format(detail.getUnitPrice()),
                    currencyFormatter.format(detail.getSubtotal())
                });
            }
            JTable detailTable = new JTable(detailTableModel);
            detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailTable.setRowHeight(22);

            detailDialog.add(new JScrollPane(detailTable));
            detailDialog.setVisible(true);

        } catch (Exception e) {
            view.showMessage("Gagal memuat detail transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void moveSelectedTransactionToRecycleBin() {
        int selectedRow = view.getTransactionTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Pilih transaksi untuk dipindahkan ke recycle bin.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) view.getTransactionTable().getModel().getValueAt(
            view.getTransactionTable().convertRowIndexToModel(selectedRow), 0);
        String transactionInfo = "ID: " + transactionId + ", Tanggal: " + 
                                 view.getTransactionTable().getModel().getValueAt(view.getTransactionTable().convertRowIndexToModel(selectedRow), 1);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Anda yakin ingin memindahkan transaksi ini ke recycle bin?\n" + transactionInfo,
                "Konfirmasi Pindah ke Recycle Bin",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String adminUsername = "admin_system"; 
            try {
                recycleBinService.moveTransactionToRecycleBin(transactionId, adminUsername);
                view.showMessage("Transaksi berhasil dipindahkan ke recycle bin.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadAllTransactions(); 
            }
            // ... (sisa error handling sama) ...
            catch (ServiceException se) {
                view.showMessage("Gagal memindahkan ke recycle bin: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
package com.warungkupos.controller;

import com.warungkupos.model.RecycleBinDetail;
import com.warungkupos.model.RecycleBinTransaction;
import com.warungkupos.service.RecycleBinService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.util.DateFormatter; 
import com.warungkupos.view.admin.RecycleBinPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // Import ini akan mencakup Frame
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// ... (sisa kode sama, pastikan import java.awt.Frame atau java.awt.* ada) ...
// Kode lainnya untuk RecycleBinController yang sudah saya berikan sebelumnya adalah benar,
// hanya pastikan importnya ada.
public class RecycleBinController {

    private RecycleBinPanel view;
    private RecycleBinService recycleBinService;
    private NumberFormat currencyFormatter;

    public RecycleBinController(RecycleBinPanel view, RecycleBinService recycleBinService) {
        this.view = view;
        this.recycleBinService = recycleBinService;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        attachListeners();
        initializeView();
    }

    private void initializeView() {
        loadRecycledTransactions();
        view.getViewRecycledDetailsButton().setEnabled(false);
        view.getRestoreTransactionButton().setEnabled(false);
        view.getDeletePermanentlyButton().setEnabled(false);
    }

    private void attachListeners() {
        view.getRefreshButton().addActionListener(e -> loadRecycledTransactions());
        view.getViewRecycledDetailsButton().addActionListener(e -> viewRecycledTransactionDetails());
        view.getRestoreTransactionButton().addActionListener(e -> restoreSelectedTransaction());
        view.getDeletePermanentlyButton().addActionListener(e -> deletePermanentlySelectedTransaction());

        view.getRecycleBinTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = view.getRecycleBinTable().getSelectedRow() != -1;
                view.getViewRecycledDetailsButton().setEnabled(rowSelected);
                view.getRestoreTransactionButton().setEnabled(rowSelected);
                view.getDeletePermanentlyButton().setEnabled(rowSelected);
            }
        });
    }

    private void loadRecycledTransactions() {
        try {
            List<RecycleBinTransaction> transactions = recycleBinService.getAllRecycledTransactions();
            view.displayRecycledTransactions(transactions);
        } catch (Exception e) {
            view.showMessage("Gagal memuat data recycle bin: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewRecycledTransactionDetails() {
        int selectedRow = view.getRecycleBinTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Pilih transaksi dari recycle bin untuk dilihat detailnya.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) view.getRecycleBinTable().getModel().getValueAt(
            view.getRecycleBinTable().convertRowIndexToModel(selectedRow), 0); 

        try {
            List<RecycleBinDetail> details = recycleBinService.getRecycledTransactionDetails(transactionId);
            if (details.isEmpty()) {
                view.showMessage("Tidak ada detail untuk transaksi ini di recycle bin.", "Detail Transaksi Dihapus", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Detail Transaksi Dihapus (ID Asli: " + transactionId + ")", true);
            detailDialog.setSize(600, 400); 
            detailDialog.setLocationRelativeTo(view);

            String[] columnNames = {"ID Produk Asli", "Nama Produk Asli", "Kuantitas Asli", "Harga Satuan Asli", "Subtotal Asli"};
            DefaultTableModel detailTableModel = new DefaultTableModel(columnNames, 0);
            for(RecycleBinDetail detail : details) {
                detailTableModel.addRow(new Object[]{
                    detail.getOriginalProductId(),
                    detail.getOriginalProductName(),
                    detail.getOriginalQuantity(),
                    currencyFormatter.format(detail.getOriginalUnitPrice()),
                    currencyFormatter.format(detail.getOriginalSubtotal())
                });
            }
            JTable detailTable = new JTable(detailTableModel);
            detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailTable.setRowHeight(22);

            detailDialog.add(new JScrollPane(detailTable));
            detailDialog.setVisible(true);

        } catch (Exception e) {
            view.showMessage("Gagal memuat detail transaksi dari recycle bin: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void restoreSelectedTransaction() {
        int selectedRow = view.getRecycleBinTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Pilih transaksi dari recycle bin untuk dikembalikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) view.getRecycleBinTable().getModel().getValueAt(
            view.getRecycleBinTable().convertRowIndexToModel(selectedRow), 0);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Anda yakin ingin mengembalikan transaksi ini (ID Asli: " + transactionId + ") ke daftar transaksi aktif?",
                "Konfirmasi Restore Transaksi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                recycleBinService.restoreTransactionFromRecycleBin(transactionId);
                view.showMessage("Transaksi berhasil dikembalikan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadRecycledTransactions(); 
            } catch (ServiceException se) {
                view.showMessage("Gagal mengembalikan transaksi: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat mengembalikan transaksi: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deletePermanentlySelectedTransaction() {
        int selectedRow = view.getRecycleBinTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Pilih transaksi dari recycle bin untuk dihapus permanen.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) view.getRecycleBinTable().getModel().getValueAt(
            view.getRecycleBinTable().convertRowIndexToModel(selectedRow), 0);

        int confirm = JOptionPane.showConfirmDialog(view,
                "PERINGATAN: Anda yakin ingin menghapus transaksi ini (ID Asli: " + transactionId + ") secara permanen?\nData tidak akan bisa dikembalikan.",
                "Konfirmasi Hapus Permanen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                recycleBinService.permanentlyDeleteFromRecycleBin(transactionId);
                view.showMessage("Transaksi berhasil dihapus permanen.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadRecycledTransactions(); 
            } catch (ServiceException se) {
                view.showMessage("Gagal menghapus permanen: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat menghapus permanen: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
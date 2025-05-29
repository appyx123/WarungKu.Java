package com.warungkupos.controller;

import com.warungkupos.model.Supplier;
import com.warungkupos.service.ProductManagementService; // Service yang sama digunakan
import com.warungkupos.service.ServiceException;
import com.warungkupos.view.admin.SupplierManagementPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SupplierController {

    private SupplierManagementPanel view;
    private ProductManagementService productService; // Menggunakan ProductManagementService

    public SupplierController(SupplierManagementPanel view, ProductManagementService productService) {
        this.view = view;
        this.productService = productService;
        attachListeners();
        initializeView();
    }

    private void initializeView() {
        loadSuppliers();
        view.clearForm(); // Pastikan form bersih di awal
    }

    private void attachListeners() {
        view.getAddButton().addActionListener(e -> addSupplier());
        view.getUpdateButton().addActionListener(e -> updateSupplier());
        view.getDeleteButton().addActionListener(e -> deleteSupplier());
        view.getClearButton().addActionListener(e -> view.clearForm());

        view.getSupplierTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getSupplierTable().getSelectedRow() != -1) {
                    view.fillFormFromSelectedTableRow();
                }
            }
        });
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = productService.getAllSuppliers();
            view.displaySuppliers(suppliers);
        } catch (Exception e) {
            view.showMessage("Gagal memuat daftar supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        Supplier supplier = view.getSupplierFromForm();
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            view.showMessage("Nama supplier tidak boleh kosong.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Validasi lain bisa ditambahkan di sini atau di service

        try {
            productService.addSupplier(supplier);
            view.showMessage("Supplier '" + supplier.getName() + "' berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadSuppliers(); // Muat ulang tabel
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal menambahkan supplier: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat menambahkan supplier: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateSupplier() {
        String idText = view.getSupplierIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih supplier dari tabel untuk diupdate.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Supplier supplier = view.getSupplierFromForm();
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            view.showMessage("Nama supplier tidak boleh kosong.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ID sudah di-set di objek supplier oleh getSupplierFromForm()

        try {
            productService.updateSupplier(supplier);
            view.showMessage("Supplier berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadSuppliers(); // Muat ulang tabel
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal memperbarui supplier: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memperbarui supplier: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSupplier() {
        String idText = view.getSupplierIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih supplier dari tabel untuk dihapus.", "Hapus Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int supplierId;
        try {
            supplierId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            view.showMessage("ID supplier tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String supplierName = view.getSupplierNameField().getText(); // Ambil nama untuk pesan konfirmasi

        int confirm = JOptionPane.showConfirmDialog(view,
                "Apakah Anda yakin ingin menghapus supplier '" + supplierName + "' (ID: " + supplierId + ")?",
                "Konfirmasi Hapus Supplier",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteSupplier(supplierId);
                view.showMessage("Supplier berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers(); // Muat ulang tabel
                view.clearForm();
            } catch (ServiceException se) {
                view.showMessage("Gagal menghapus supplier: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat menghapus supplier: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
package com.warungkupos.controller;

import com.warungkupos.model.Category;
import com.warungkupos.service.ProductManagementService; // Service yang sama digunakan
import com.warungkupos.service.ServiceException;
import com.warungkupos.view.admin.CategoryManagementPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoryController {

    private CategoryManagementPanel view;
    private ProductManagementService productService; // Menggunakan ProductManagementService

    public CategoryController(CategoryManagementPanel view, ProductManagementService productService) {
        this.view = view;
        this.productService = productService;
        attachListeners();
        initializeView();
    }

    private void initializeView() {
        loadCategories();
        view.clearForm(); // Pastikan form bersih di awal
    }

    private void attachListeners() {
        view.getAddButton().addActionListener(e -> addCategory());
        view.getUpdateButton().addActionListener(e -> updateCategory());
        view.getDeleteButton().addActionListener(e -> deleteCategory());
        view.getClearButton().addActionListener(e -> view.clearForm());

        view.getCategoryTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getCategoryTable().getSelectedRow() != -1) {
                    view.fillFormFromSelectedTableRow();
                }
            }
        });
    }

    private void loadCategories() {
        try {
            List<Category> categories = productService.getAllCategories();
            view.displayCategories(categories);
        } catch (Exception e) {
            view.showMessage("Gagal memuat daftar kategori: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCategory() {
        Category category = view.getCategoryFromForm();
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            view.showMessage("Nama kategori tidak boleh kosong.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            productService.addCategory(category);
            view.showMessage("Kategori '" + category.getName() + "' berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadCategories(); // Muat ulang tabel
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal menambahkan kategori: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat menambahkan kategori: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateCategory() {
        String idText = view.getCategoryIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih kategori dari tabel untuk diupdate.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category category = view.getCategoryFromForm();
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            view.showMessage("Nama kategori tidak boleh kosong.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ID sudah di-set di objek category oleh getCategoryFromForm() jika categoryIdField terisi

        try {
            productService.updateCategory(category);
            view.showMessage("Kategori berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadCategories(); // Muat ulang tabel
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal memperbarui kategori: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memperbarui kategori: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteCategory() {
        String idText = view.getCategoryIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih kategori dari tabel untuk dihapus.", "Hapus Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            view.showMessage("ID kategori tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String categoryName = view.getCategoryNameField().getText(); // Ambil nama untuk pesan konfirmasi

        int confirm = JOptionPane.showConfirmDialog(view,
                "Apakah Anda yakin ingin menghapus kategori '" + categoryName + "' (ID: " + categoryId + ")?",
                "Konfirmasi Hapus Kategori",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteCategory(categoryId);
                view.showMessage("Kategori berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadCategories(); // Muat ulang tabel
                view.clearForm();
            } catch (ServiceException se) {
                view.showMessage("Gagal menghapus kategori: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat menghapus kategori: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
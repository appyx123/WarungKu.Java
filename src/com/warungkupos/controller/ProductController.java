package com.warungkupos.controller;

import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier; // <--- PASTIKAN IMPORT INI ADA
import com.warungkupos.service.ProductManagementService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.view.admin.ProductManagementPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList; // Untuk placeholder supplier

public class ProductController {

    private ProductManagementPanel view;
    private ProductManagementService productService;

    public ProductController(ProductManagementPanel view, ProductManagementService productService) {
        this.view = view;
        this.productService = productService;
        attachListeners();
        initializeView();
    }

    private void initializeView() {
        refreshCategoriesComboBox();
        refreshSuppliersComboBox(); // <--- BARIS INI DITAMBAHKAN: Muat Supplier
        loadProducts();
        view.clearForm();
    }

    private void attachListeners() {
        view.getAddButton().addActionListener(e -> addProduct());
        view.getUpdateButton().addActionListener(e -> updateProduct());
        view.getDeleteButton().addActionListener(e -> deleteProduct());
        view.getClearButton().addActionListener(e -> view.clearForm());

        view.getProductTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getProductTable().getSelectedRow() != -1) {
                    view.fillFormFromSelectedTableRow();
                }
            }
        });
    }

    public void refreshCategoriesComboBox() {
        try {
            List<Category> categories = productService.getAllCategories();
            Category placeholder = new Category(0, "-- Pilih Kategori --");
            
            ArrayList<Category> comboBoxCategories = new ArrayList<>();
            comboBoxCategories.add(placeholder); 
            if (categories != null) {
                comboBoxCategories.addAll(categories);
            }
            
            view.setCategoryComboBoxModel(comboBoxCategories);
        } catch (Exception e) {
            view.showMessage("Gagal memuat kategori: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // <--- METODE BARU: Untuk memuat Supplier ke ComboBox
    public void refreshSuppliersComboBox() {
        try {
            List<Supplier> suppliers = productService.getAllSuppliers();
            Supplier placeholder = new Supplier(0, "-- Pilih Supplier --", "", ""); 
            
            ArrayList<Supplier> comboBoxSuppliers = new ArrayList<>();
            comboBoxSuppliers.add(placeholder);
            if (suppliers != null) {
                comboBoxSuppliers.addAll(suppliers);
            }
            
            view.setSupplierComboBoxModel(comboBoxSuppliers);
        } catch (Exception e) {
            view.showMessage("Gagal memuat supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    // <--- AKHIR METODE BARU

    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            view.displayProducts(products);
        } catch (Exception e) {
            view.showMessage("Gagal memuat produk: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addProduct() {
        Product product = view.getProductFromForm();
        if (product == null) {
            view.showMessage("Data produk tidak lengkap atau tidak valid. Mohon periksa kembali.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi tambahan untuk ID placeholder, jika tidak ditangani di View atau perlu validasi ganda
        if (product.getCategoryId() == 0) { // Asumsi ID 0 untuk placeholder kategori
            view.showMessage("Silakan pilih kategori produk yang valid.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (product.getSupplierId() == 0) { // <--- BARU: Validasi Supplier ID
            view.showMessage("Silakan pilih supplier produk yang valid.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            productService.addProduct(product);
            view.showMessage("Produk berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProducts();
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal menambahkan produk: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat menambahkan produk: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateProduct() {
        String idText = view.getProductIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih produk dari tabel untuk diupdate.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = view.getProductFromForm();
        if (product == null) {
             view.showMessage("Data produk tidak lengkap atau tidak valid. Mohon periksa kembali.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (product.getCategoryId() == 0) { // Asumsi ID 0 untuk placeholder kategori
            view.showMessage("Silakan pilih kategori produk yang valid.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (product.getSupplierId() == 0) { // <--- BARU: Validasi Supplier ID
            view.showMessage("Silakan pilih supplier produk yang valid.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            productService.updateProduct(product);
            view.showMessage("Produk berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadProducts();
            view.clearForm();
        } catch (ServiceException se) {
            view.showMessage("Gagal memperbarui produk: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memperbarui produk: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteProduct() {
        String idText = view.getProductIdField().getText();
        if (idText.isEmpty()) {
            view.showMessage("Pilih produk dari tabel untuk dihapus.", "Hapus Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            view.showMessage("ID produk tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Apakah Anda yakin ingin menghapus produk ini (ID: " + productId + ")?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(productId);
                view.showMessage("Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                view.clearForm();
            } catch (ServiceException se) {
                view.showMessage("Gagal menghapus produk: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat menghapus produk: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
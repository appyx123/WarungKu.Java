package com.warungkupos.controller;

import com.warungkupos.model.Product; // Untuk menyimpan Product object di keranjang
import com.warungkupos.model.Transaction;
import com.warungkupos.model.TransactionDetail;
import com.warungkupos.model.User;
import com.warungkupos.service.ProductManagementService;
import com.warungkupos.service.ServiceException;
import com.warungkupos.service.TransactionHandlingService;
import com.warungkupos.view.customer.ProductDisplayPanel;
import com.warungkupos.view.customer.TransactionHistoryPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal; // <--- BARIS INI DITAMBAHKAN
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerController {

    private ProductDisplayPanel productDisplayView;
    private TransactionHistoryPanel transactionHistoryView;
    private ProductManagementService productManagementService;
    private TransactionHandlingService transactionHandlingService;
    private User loggedInUser;
    private NumberFormat currencyFormatter;

    private Map<Integer, Product> productsInCartById;
    private Map<Integer, Integer> quantitiesInCartById;

    public CustomerController(ProductDisplayPanel productDisplayView,
                              TransactionHistoryPanel transactionHistoryView,
                              ProductManagementService productManagementService,
                              TransactionHandlingService transactionHandlingService,
                              User loggedInUser) {
        this.productDisplayView = productDisplayView;
        this.transactionHistoryView = transactionHistoryView;
        this.productManagementService = productManagementService;
        this.transactionHandlingService = transactionHandlingService;
        this.loggedInUser = loggedInUser;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        this.productsInCartById = new HashMap<>();
        this.quantitiesInCartById = new HashMap<>();

        if (loggedInUser == null) {
            handleCriticalError("Error: Tidak ada informasi pengguna yang login.");
            return;
        }

        attachListeners();
        initializeView();
    }

    private void handleCriticalError(String message) {
        JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        if (this.productDisplayView != null && SwingUtilities.getWindowAncestor(this.productDisplayView) != null) {
            SwingUtilities.getWindowAncestor(this.productDisplayView).dispose();
        }
        if (this.transactionHistoryView != null && SwingUtilities.getWindowAncestor(this.transactionHistoryView) != null) {
            if (SwingUtilities.getWindowAncestor(this.productDisplayView) != SwingUtilities.getWindowAncestor(this.transactionHistoryView)){
                SwingUtilities.getWindowAncestor(this.transactionHistoryView).dispose();
            }
        }
    }


    private void initializeView() {
        if (productDisplayView != null) {
            loadProductsToDisplay();
            productDisplayView.clearAddToCartForm();
            refreshCartDisplay();
        }
        if (transactionHistoryView != null) {
            loadTransactionHistory();
            transactionHistoryView.getViewDetailsButton().setEnabled(false);
        }
    }

    private void attachListeners() {
        if (productDisplayView != null) {
            productDisplayView.getProductDisplayTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    if (productDisplayView.getProductDisplayTable().getSelectedRow() != -1) {
                        productDisplayView.fillAddToCartFormFromSelectedProduct();
                    } else {
                        productDisplayView.clearAddToCartForm();
                    }
                }
            });

            productDisplayView.getAddToCartButton().addActionListener(e -> handleAddToCart());
            productDisplayView.getClearSelectionButton().addActionListener(e -> productDisplayView.clearAddToCartForm());

            productDisplayView.getRemoveFromCartButton().addActionListener(e -> handleRemoveFromCart());
            productDisplayView.getCheckoutButton().addActionListener(e -> handleCheckout());
        }

        if (transactionHistoryView != null) {
            transactionHistoryView.getRefreshButton().addActionListener(e -> loadTransactionHistory());
            transactionHistoryView.getViewDetailsButton().addActionListener(e -> viewSelectedTransactionHistoryDetails());

            transactionHistoryView.getHistoryTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    boolean rowSelected = transactionHistoryView.getHistoryTable().getSelectedRow() != -1;
                    transactionHistoryView.getViewDetailsButton().setEnabled(rowSelected);
                }
            });
        }
    }

    private void loadProductsToDisplay() {
        if (productDisplayView == null) return;
        try {
            List<Product> products = productManagementService.getAllProducts();
            productDisplayView.displayProducts(products);
        } catch (Exception e) {
            productDisplayView.showMessage("Gagal memuat daftar produk: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshCartDisplay() {
        Map<Product, Integer> displayMap = new HashMap<>();
        for (Map.Entry<Integer, Product> entry : productsInCartById.entrySet()) {
            displayMap.put(entry.getValue(), quantitiesInCartById.get(entry.getKey()));
        }
        productDisplayView.displayCartItems(displayMap);
    }

    private void handleAddToCart() {
        if (productDisplayView == null) return;

        Map<String, Integer> formData = productDisplayView.getProductAndQuantityForCart();
        if (formData == null) {
            return;
        }

        int productId = formData.get("productId");
        int quantity = formData.get("quantity");
        
        try {
            Product productToAdd = productManagementService.getProductById(productId);
            if (productToAdd == null) {
                productDisplayView.showMessage("Produk tidak ditemukan.", "Tambah ke Keranjang Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (productToAdd.getStock() < quantity) {
                productDisplayView.showMessage("Stok produk '" + productToAdd.getName() + "' tidak mencukupi. Sisa: " + productToAdd.getStock(), "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                return;
            }

            quantitiesInCartById.put(productId, quantitiesInCartById.getOrDefault(productId, 0) + quantity);
            productsInCartById.put(productId, productToAdd);

            refreshCartDisplay();
            productDisplayView.clearAddToCartForm();
            productDisplayView.showMessage("Produk berhasil ditambahkan ke keranjang!", "Keranjang Belanja", JOptionPane.INFORMATION_MESSAGE);

        } catch (ServiceException se) {
            productDisplayView.showMessage("Gagal menambahkan ke keranjang: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            productDisplayView.showMessage("Terjadi kesalahan sistem saat menambahkan ke keranjang: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleRemoveFromCart() {
        if (productDisplayView == null) return;
        int selectedCartRow = productDisplayView.getCartTable().getSelectedRow();
        if (selectedCartRow == -1) {
            productDisplayView.showMessage("Pilih item dari keranjang untuk dihapus.", "Hapus dari Keranjang", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productIdToRemove = (Integer) productDisplayView.getCartTable().getModel().getValueAt(selectedCartRow, 0);

        productsInCartById.remove(productIdToRemove);
        quantitiesInCartById.remove(productIdToRemove);
        
        refreshCartDisplay();
        productDisplayView.showMessage("Produk berhasil dihapus dari keranjang.", "Keranjang Belanja", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCheckout() {
        if (productDisplayView == null) return;
        if (quantitiesInCartById.isEmpty()) {
            productDisplayView.showMessage("Keranjang belanja kosong. Silakan tambahkan produk terlebih dahulu.", "Checkout Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal confirmationTotal = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : quantitiesInCartById.entrySet()) {
            Product p = productsInCartById.get(entry.getKey());
            Integer qty = entry.getValue();
            confirmationTotal = confirmationTotal.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        int confirm = JOptionPane.showConfirmDialog(productDisplayView,
                "Total belanja Anda: " + currencyFormatter.format(confirmationTotal) + ". Lanjutkan checkout?",
                "Konfirmasi Checkout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Map<Integer, Integer> itemsToPurchaseForService = new HashMap<>(quantitiesInCartById);

            try {
                transactionHandlingService.createTransaction(loggedInUser.getUsername(), itemsToPurchaseForService);
                productDisplayView.showMessage("Checkout berhasil! Transaksi Anda telah diproses.", "Checkout Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                productsInCartById.clear();
                quantitiesInCartById.clear();
                refreshCartDisplay();
                loadProductsToDisplay();
                
                if (transactionHistoryView != null) {
                    loadTransactionHistory();
                }

            } catch (ServiceException se) {
                productDisplayView.showMessage("Checkout gagal: " + se.getMessage(), "Error Checkout", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                productDisplayView.showMessage("Terjadi kesalahan sistem saat checkout: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    private void loadTransactionHistory() {
        if (transactionHistoryView == null || loggedInUser == null) return;
        try {
            List<Transaction> history = transactionHandlingService.getTransactionHistoryByUsername(loggedInUser.getUsername());
            transactionHistoryView.displayTransactionHistory(history);
        } catch (Exception e) {
            transactionHistoryView.showMessage("Gagal memuat riwayat transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewSelectedTransactionHistoryDetails() {
        if (transactionHistoryView == null) return;
        int selectedRow = transactionHistoryView.getHistoryTable().getSelectedRow();
        if (selectedRow == -1) {
            transactionHistoryView.showMessage("Pilih transaksi dari riwayat untuk dilihat detailnya.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transactionId = (Integer) transactionHistoryView.getHistoryTable().getModel().getValueAt(
            transactionHistoryView.getHistoryTable().convertRowIndexToModel(selectedRow), 0);

        try {
            List<TransactionDetail> details = transactionHandlingService.getTransactionDetails(transactionId);
            if (details.isEmpty()) {
                transactionHistoryView.showMessage("Tidak ada detail untuk transaksi ini.", "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(transactionHistoryView), "Detail Riwayat Transaksi ID: " + transactionId, true);
            detailDialog.setSize(500, 350);
            detailDialog.setLocationRelativeTo(transactionHistoryView);
            
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
            transactionHistoryView.showMessage("Gagal memuat detail riwayat transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
package com.warungkupos.view.customer;

import com.warungkupos.model.Product;
import com.warungkupos.model.User; 
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.DateFormatter; 
import com.warungkupos.util.InputValidator; 

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat; 
import java.util.HashMap; 
import java.util.List;
import java.util.Locale; 
import java.util.Map;

public class ProductDisplayPanel extends JPanel {

    private JTable productDisplayTable;
    private DefaultTableModel productTableModel; 
    private JScrollPane productScrollPane;

    private JLabel selectedProductNameLabel;
    private JTextField selectedProductIdField; 
    private JSpinner quantitySpinner;
    private JButton addToCartButton; 
    private JButton clearSelectionButton; 

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    public JLabel cartTotalLabel; 
    private JButton removeFromCartButton;
    private JButton checkoutButton;

    private User loggedInUser; 
    private NumberFormat currencyFormatter;

    public ProductDisplayPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }
    
    private void initComponents() {
        JPanel productListPanel = new JPanel(new BorderLayout());
        productListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Daftar Produk Tersedia", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), AppConstants.COLOR_PRIMARY_BLUE
        ));

        String[] productColumnNames = {"ID", "Nama Produk", "Kategori", "Harga (Rp)", "Stok"};
        productTableModel = new DefaultTableModel(productColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 4: return Integer.class; 
                    case 3: return Double.class; 
                    default: return String.class;
                }
            }
        };
        productDisplayTable = new JTable(productTableModel);
        productDisplayTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productDisplayTable.setRowHeight(25);
        productDisplayTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productDisplayTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        productDisplayTable.getTableHeader().setForeground(Color.WHITE);
        productDisplayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productDisplayTable.setAutoCreateRowSorter(true);

        productDisplayTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer()); 
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        productDisplayTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); 
        productDisplayTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); 
        
        productDisplayTable.getColumnModel().getColumn(0).setPreferredWidth(50);  
        productDisplayTable.getColumnModel().getColumn(1).setPreferredWidth(250); 
        productDisplayTable.getColumnModel().getColumn(2).setPreferredWidth(150); 
        productDisplayTable.getColumnModel().getColumn(3).setPreferredWidth(120); 
        productDisplayTable.getColumnModel().getColumn(4).setPreferredWidth(70);  

        productScrollPane = new JScrollPane(productDisplayTable);
        productListPanel.add(productScrollPane, BorderLayout.CENTER);

        JPanel addToCartPanel = new JPanel(new GridBagLayout());
        addToCartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Tambah ke Keranjang", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        selectedProductNameLabel = new JLabel("Pilih produk dari tabel di atas");
        selectedProductNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectedProductNameLabel.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        addToCartPanel.add(selectedProductNameLabel, gbc);
        
        selectedProductIdField = new JTextField(5);
        selectedProductIdField.setVisible(false); 

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        addToCartPanel.add(new JLabel("Jumlah Beli:"), gbc);
        
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1); 
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.RIGHT);
        spinnerEditor.getTextField().setColumns(3); 
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        addToCartPanel.add(quantitySpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,5));
        addToCartButton = new JButton("Tambah ke Keranjang"); 
        styleButton(addToCartButton, AppConstants.COLOR_PRIMARY_BLUE);
        
        clearSelectionButton = new JButton("Batal Pilih");
        styleButton(clearSelectionButton, Color.GRAY);

        buttonPanel.add(addToCartButton);
        buttonPanel.add(clearSelectionButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(15,5,5,5);
        addToCartPanel.add(buttonPanel, gbc);
        
        enableAddToCartForm(false);


        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Keranjang Belanja", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), AppConstants.COLOR_PRIMARY_BLUE
        ));

        String[] cartColumnNames = {"ID Produk", "Nama Produk", "Harga Satuan", "Kuantitas", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 3: return Integer.class; 
                    case 2: case 4: return Double.class; 
                    default: return String.class;
                }
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.setRowHeight(25);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setAutoCreateRowSorter(true);

        cartTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer()); 
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer()); 
        cartTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); 
        cartTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); 

        cartTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        // --- PERBAIKAN: Layout untuk cartSummaryPanel ---
        JPanel cartSummaryPanel = new JPanel(new GridBagLayout()); // <--- UBAH INI KE GridBagLayout
        GridBagConstraints gbcSummary = new GridBagConstraints();
        gbcSummary.insets = new Insets(5, 5, 5, 5); // Padding
        gbcSummary.fill = GridBagConstraints.HORIZONTAL; // Isi ruang horizontal

        // Label Total Keranjang
        cartTotalLabel = new JLabel("Total Keranjang: " + currencyFormatter.format(0));
        cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cartTotalLabel.setForeground(new Color(0, 100, 0));
        gbcSummary.gridx = 0; // Kolom pertama
        gbcSummary.gridy = 0; // Baris pertama
        gbcSummary.weightx = 1.0; // Beri bobot agar mengisi ruang kosong ke kanan
        gbcSummary.anchor = GridBagConstraints.WEST; // Rata kiri
        cartSummaryPanel.add(cartTotalLabel, gbcSummary);

        // Panel Tombol Aksi Keranjang
        JPanel cartActionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cartActionButtonsPanel.setOpaque(false); // Transparan agar background panel induk terlihat
        removeFromCartButton = new JButton("Hapus dari Keranjang");
        styleButton(removeFromCartButton, AppConstants.COLOR_ERROR_RED);
        checkoutButton = new JButton("Checkout");
        styleButton(checkoutButton, AppConstants.COLOR_PRIMARY_BLUE);
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cartActionButtonsPanel.add(removeFromCartButton);
        cartActionButtonsPanel.add(checkoutButton);
        gbcSummary.gridx = 1; // Kolom kedua
        gbcSummary.gridy = 0; // Baris pertama
        gbcSummary.weightx = 0; // Jangan beri bobot, agar tetap di kanan
        gbcSummary.anchor = GridBagConstraints.EAST; // Rata kanan
        cartSummaryPanel.add(cartActionButtonsPanel, gbcSummary);
        // --- AKHIR PERBAIKAN ---

        cartPanel.add(cartSummaryPanel, BorderLayout.SOUTH);
        
        enableCartActions(false); 

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, productListPanel, addToCartPanel);
        leftSplitPane.setResizeWeight(0.7); 
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.setContinuousLayout(true);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, cartPanel); 
        mainSplitPane.setResizeWeight(0.6); 
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setContinuousLayout(true);

        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void enableAddToCartForm(boolean enabled) {
        quantitySpinner.setEnabled(enabled);
        addToCartButton.setEnabled(enabled);
        clearSelectionButton.setEnabled(enabled);
        if (!enabled) {
            selectedProductNameLabel.setText("Pilih produk dari tabel di atas");
            selectedProductIdField.setText("");
            quantitySpinner.setValue(1); 
        }
    }

    public void enableCartActions(boolean enabled) {
        removeFromCartButton.setEnabled(enabled);
        checkoutButton.setEnabled(enabled);
    }

    public void displayProducts(List<Product> products) {
        productTableModel.setRowCount(0); 
        if (products != null) {
            for (Product product : products) {
                Object[] row = new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getCategoryName(),
                        product.getPrice().doubleValue(), 
                        product.getStock()
                };
                productTableModel.addRow(row);
            }
        }
    }
    
    public void fillAddToCartFormFromSelectedProduct() {
        int selectedRow = productDisplayTable.getSelectedRow();
        if (selectedRow != -1) {
            String productId = productTableModel.getValueAt(selectedRow, 0).toString();
            String productName = productTableModel.getValueAt(selectedRow, 1).toString();
            int stock = (Integer) productTableModel.getValueAt(selectedRow, 4);

            selectedProductIdField.setText(productId);
            selectedProductNameLabel.setText("Tambah: " + productName);
            
            if (stock > 0) {
                 enableAddToCartForm(true);
                 ((SpinnerNumberModel) quantitySpinner.getModel()).setMaximum(stock); 
                 quantitySpinner.setValue(1); 
            } else {
                 selectedProductNameLabel.setText(productName + " (Stok Habis)");
                 enableAddToCartForm(false); 
            }
        } else {
            clearAddToCartForm();
        }
    }

    public void clearAddToCartForm() {
        productDisplayTable.clearSelection();
        enableAddToCartForm(false);
    }

    public void displayCartItems(Map<Product, Integer> cartItems) { 
        cartTableModel.setRowCount(0);
        BigDecimal currentTotal = BigDecimal.ZERO;
        if (cartItems != null) {
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
                currentTotal = currentTotal.add(subtotal);

                cartTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getPrice().doubleValue(), 
                    quantity,
                    subtotal.doubleValue() 
                });
            }
        }
        cartTotalLabel.setText("Total Keranjang: " + currencyFormatter.format(currentTotal));
        enableCartActions(!cartItems.isEmpty()); 
    }
    
    public void clearCartDisplay() {
        cartTableModel.setRowCount(0);
        cartTotalLabel.setText("Total Keranjang: " + currencyFormatter.format(0));
        enableCartActions(false);
    }
    
    public Map<String, Integer> getProductAndQuantityForCart() {
        String productIdStr = selectedProductIdField.getText();
        String quantityObj = quantitySpinner.getValue().toString(); 

        if (!InputValidator.isNonEmpty(productIdStr, "ID Produk")) {
            showMessage("Silakan pilih produk dari tabel terlebih dahulu.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!InputValidator.isValidInteger(quantityObj)) {
            showMessage("Kuantitas tidak valid. Mohon gunakan angka.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            quantitySpinner.requestFocusInWindow(); 
            return null;
        }
        int quantity = Integer.parseInt(quantityObj);
        if (quantity <= 0) {
            showMessage("Kuantitas harus lebih dari 0.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            quantitySpinner.requestFocusInWindow();
            return null;
        }
        
        int selectedRow = productDisplayTable.getSelectedRow();
        if (selectedRow == -1) { 
            showMessage("Produk tidak terpilih di tabel.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int availableStock = (Integer) productTableModel.getValueAt(selectedRow, 4);

        if (quantity > availableStock) {
            showMessage("Kuantitas yang diminta melebihi stok yang tersedia (" + availableStock + ").", "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
            quantitySpinner.requestFocusInWindow();
            return null;
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("productId", Integer.parseInt(productIdStr));
        result.put("quantity", quantity);
        return result;
    }
    
    public JTable getProductDisplayTable() { return productDisplayTable; }
    public JSpinner getQuantitySpinner() { return quantitySpinner; }
    public JButton getAddToCartButton() { return addToCartButton; }
    public JButton getClearSelectionButton() { return clearSelectionButton; }
    public String getSelectedProductIdInForm() { return selectedProductIdField.getText(); }
    public User getLoggedInUser() { return loggedInUser; }

    public JTable getCartTable() { return cartTable; } 
    public JButton getRemoveFromCartButton() { return removeFromCartButton; }
    public JButton getCheckoutButton() { return checkoutButton; }


    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private class CurrencyRenderer extends DefaultTableCellRenderer {
        private NumberFormat localCurrencyFormatter;

        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
            localCurrencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        }

        @Override 
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Number) {
                value = localCurrencyFormatter.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
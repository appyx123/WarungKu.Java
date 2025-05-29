package com.warungkupos.view.admin;

import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier; // <--- PASTIKAN IMPORT INI ADA
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.InputValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductManagementPanel extends JPanel {

    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField productPriceField;
    private JTextField productStockField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Supplier> supplierComboBox; // <--- FIELD BARU: Dropdown untuk Supplier

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable productTable;
    private DefaultTableModel tableModel;

    public ProductManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Produk", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("ID Produk:"), gbc);
        productIdField = new JTextField(5);
        productIdField.setEditable(false);
        productIdField.setBackground(new Color(230,230,230));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(productIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Nama Produk:"), gbc);
        productNameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(productNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Harga (Rp):"), gbc);
        productPriceField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        formPanel.add(productPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Stok:"), gbc);
        productStockField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        formPanel.add(productStockField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Kategori:"), gbc);
        categoryComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        formPanel.add(categoryComboBox, gbc);

        // <--- FIELD BARU: Dropdown untuk Supplier
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Supplier:"), gbc);
        supplierComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        formPanel.add(supplierComboBox, gbc);
        // <--- AKHIR FIELD BARU

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Tambah");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Hapus");
        clearButton = new JButton("Clear Form");
        
        styleButton(addButton, AppConstants.COLOR_PRIMARY_BLUE);
        styleButton(updateButton, new Color(255, 165, 0));
        styleButton(deleteButton, AppConstants.COLOR_ERROR_RED);
        styleButton(clearButton, Color.GRAY);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // PERBAIKAN: gbc.gridy disesuaikan karena ada field baru di atasnya
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);
        
        // PERBAIKAN: Tambahkan kolom "Supplier" di tabel produk
        String[] columnNames = {"ID", "Nama Produk", "Harga", "Stok", "Kategori", "Supplier"}; 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 3: return Integer.class;
                    case 2: return Double.class;
                    default: return Object.class;
                }
            }
        };
        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);

        // PERBAIKAN: Sesuaikan lebar kolom karena ada kolom baru
        productTable.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama Produk
        productTable.getColumnModel().getColumn(2).setPreferredWidth(90);  // Harga
        productTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Stok
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Kategori
        productTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Supplier (NEW)

        JScrollPane scrollPane = new JScrollPane(productTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void clearForm() {
        productIdField.setText("");
        productNameField.setText("");
        productPriceField.setText("");
        productStockField.setText("");
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0); 
        }
        // <--- BARU: Reset dropdown supplier
        if (supplierComboBox.getItemCount() > 0) {
            supplierComboBox.setSelectedIndex(0);
        }
        // <--- AKHIR BARU
        productNameField.requestFocus(); 
        productTable.clearSelection(); 
    }

    public void setCategoryComboBoxModel(List<Category> categories) {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        if (categories == null || categories.isEmpty() || categories.get(0).getId() != 0) {
             model.addElement(new Category(0, "-- Pilih Kategori --"));
        }
        if (categories != null) {
            for (Category category : categories) {
                if (category.getId() != 0) {
                    model.addElement(category);
                }
            }
        }
        categoryComboBox.setModel(model);
    }
    
    // <--- METODE BARU: Untuk mengisi dropdown Supplier
    public void setSupplierComboBoxModel(List<Supplier> suppliers) {
        DefaultComboBoxModel<Supplier> model = new DefaultComboBoxModel<>();
        // Tambahkan placeholder
        if (suppliers == null || suppliers.isEmpty() || suppliers.get(0).getId() != 0) {
            model.addElement(new Supplier(0, "-- Pilih Supplier --", "", ""));
        }
        if (suppliers != null) {
            for (Supplier supplier : suppliers) {
                if (supplier.getId() != 0) { // Pastikan placeholder tidak ditambahkan dua kali
                    model.addElement(supplier);
                }
            }
        }
        supplierComboBox.setModel(model);
    }
    // <--- AKHIR METODE BARU
    
    public void displayProducts(List<Product> products) {
        tableModel.setRowCount(0); 
        if (products != null) {
            for (Product product : products) {
                // <--- PERBAIKAN: Tambahkan supplierName ke baris tabel
                Object[] row = new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getPrice(), 
                        product.getStock(),
                        product.getCategoryName(),
                        product.getSupplierName() // <--- FIELD BARU
                };
                tableModel.addRow(row);
            }
        }
    }

    public Product getProductFromForm() {
        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim();
        String stockStr = productStockField.getText().trim();
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem(); // <--- BARU: Ambil Supplier terpilih

        if (!InputValidator.isNonEmpty(name, "Nama Produk")) {
            showMessage("Nama Produk tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productNameField.requestFocusInWindow();
            return null;
        }
        if (!InputValidator.isNonEmpty(priceStr, "Harga")) {
            showMessage("Harga tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productPriceField.requestFocusInWindow();
            return null;
        }
        if (!InputValidator.isValidBigDecimal(priceStr)) {
            showMessage("Format harga tidak valid (gunakan angka).", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productPriceField.requestFocusInWindow();
            return null;
        }
        BigDecimal price = new BigDecimal(priceStr); 
        if (price.compareTo(BigDecimal.ZERO) < 0) { 
            showMessage("Harga tidak boleh negatif.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productPriceField.requestFocusInWindow();
            return null;
        }

        if (!InputValidator.isNonEmpty(stockStr, "Stok")) {
            showMessage("Stok tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productStockField.requestFocusInWindow();
            return null;
        }
        if (!InputValidator.isValidInteger(stockStr)) {
            showMessage("Format stok tidak valid (gunakan angka bulat).", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productStockField.requestFocusInWindow();
            return null;
        }
        int stock = Integer.parseInt(stockStr); 
        if (stock < 0) { 
            showMessage("Stok tidak boleh negatif.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            productStockField.requestFocusInWindow();
            return null;
        }

        if (selectedCategory == null || selectedCategory.getId() == 0) { 
            showMessage("Silakan pilih kategori produk yang valid.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            categoryComboBox.requestFocusInWindow();
            return null;
        }
        // <--- BARU: Validasi Supplier
        if (selectedSupplier == null || selectedSupplier.getId() == 0) {
            showMessage("Silakan pilih supplier produk yang valid.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            supplierComboBox.requestFocusInWindow();
            return null;
        }
        // <--- AKHIR BARU
        
        Product product = new Product();
        if (!productIdField.getText().isEmpty()) {
            try {
                product.setId(Integer.parseInt(productIdField.getText()));
            } catch (NumberFormatException e) {
            }
        }
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(selectedCategory.getId());
        product.setCategoryName(selectedCategory.getName()); 
        product.setSupplierId(selectedSupplier.getId()); // <--- BARU: Set Supplier ID
        product.setSupplierName(selectedSupplier.getName()); // <--- BARU: Set Supplier Name
        
        return product;
    }

    public void fillFormFromSelectedTableRow() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            productIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            productNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            productPriceField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            productStockField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            
            // Set Kategori terpilih
            String categoryNameFromTable = tableModel.getValueAt(selectedRow, 4).toString();
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                Category categoryItem = categoryComboBox.getItemAt(i);
                if (categoryItem != null && categoryItem.getName() != null && categoryItem.getName().equals(categoryNameFromTable)) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // <--- BARU: Set Supplier terpilih
            String supplierNameFromTable = tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "";
            for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                Supplier supplierItem = supplierComboBox.getItemAt(i);
                if (supplierItem != null && supplierItem.getName() != null && supplierItem.getName().equals(supplierNameFromTable)) {
                    supplierComboBox.setSelectedIndex(i);
                    break;
                }
            }
            // <--- AKHIR BARU
        }
    }
    
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getClearButton() { return clearButton; }
    public JTable getProductTable() { return productTable; }
    public JTextField getProductIdField() { return productIdField; } 
    
    // <--- BARU: Getter untuk JComboBox Supplier
    public JComboBox<Supplier> getSupplierComboBox() { return supplierComboBox; }
    // <--- AKHIR BARU

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
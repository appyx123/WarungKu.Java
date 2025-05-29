package com.warungkupos.view.admin;

import com.warungkupos.model.Supplier;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.InputValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierManagementPanel extends JPanel {

    private JTextField supplierIdField;
    private JTextField supplierNameField;
    private JTextField supplierContactField;
    private JTextArea supplierAddressArea;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Supplier", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("ID Supplier:"), gbc);
        supplierIdField = new JTextField(5);
        supplierIdField.setEditable(false);
        supplierIdField.setBackground(new Color(230,230,230));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(supplierIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Nama Supplier:"), gbc);
        supplierNameField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(supplierNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Kontak:"), gbc);
        supplierContactField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        formPanel.add(supplierContactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST; 
        gbc.weightx = 0;
        formPanel.add(new JLabel("Alamat:"), gbc);
        supplierAddressArea = new JTextArea(3, 20); 
        supplierAddressArea.setLineWrap(true);
        supplierAddressArea.setWrapStyleWord(true);
        supplierAddressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane addressScrollPane = new JScrollPane(supplierAddressArea);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; 
        gbc.weighty = 1.0; 
        formPanel.add(addressScrollPane, gbc);

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

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.weighty = 0; 
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"ID", "Nama Supplier", "Kontak", "Alamat"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };
        supplierTable = new JTable(tableModel);
        supplierTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        supplierTable.setRowHeight(25);
        supplierTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        supplierTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        supplierTable.getTableHeader().setForeground(Color.WHITE);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setAutoCreateRowSorter(true);

        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(40);  
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(120); 
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(250); 

        JScrollPane scrollPane = new JScrollPane(supplierTable);

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
        supplierIdField.setText("");
        supplierNameField.setText("");
        supplierContactField.setText("");
        supplierAddressArea.setText("");
        supplierNameField.requestFocus();
        supplierTable.clearSelection();
    }

    public void displaySuppliers(List<Supplier> suppliers) {
        tableModel.setRowCount(0); 
        if (suppliers != null) {
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContact(),
                        supplier.getAddress()
                });
            }
        }
    }

    // Perbaikan: Anotasi @Override DIHAPUS karena ini bukan override dari JPanel
    public Supplier getSupplierFromForm() {
        String name = supplierNameField.getText().trim();
        String contact = supplierContactField.getText().trim();
        String address = supplierAddressArea.getText().trim();

        if (!InputValidator.isNonEmpty(name, "Nama Supplier")) {
            showMessage("Nama Supplier tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            supplierNameField.requestFocusInWindow();
            return null;
        }
        
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContact(contact);
        supplier.setAddress(address);

        if (!supplierIdField.getText().isEmpty()) {
            try {
                supplier.setId(Integer.parseInt(supplierIdField.getText()));
            } catch (NumberFormatException e) {
            }
        }
        return supplier;
    }

    public void fillFormFromSelectedTableRow() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow != -1) {
            supplierIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            supplierNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            
            Object contactObj = tableModel.getValueAt(selectedRow, 2);
            supplierContactField.setText(contactObj != null ? contactObj.toString() : "");
            
            Object addressObj = tableModel.getValueAt(selectedRow, 3);
            supplierAddressArea.setText(addressObj != null ? addressObj.toString() : "");
        }
    }

    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getClearButton() { return clearButton; }
    public JTable getSupplierTable() { return supplierTable; }
    public JTextField getSupplierIdField() { return supplierIdField; }
    public JTextField getSupplierNameField() { return supplierNameField; }
    public JTextField getSupplierContactField() { return supplierContactField; }
    public JTextArea getSupplierAddressArea() { return supplierAddressArea; }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
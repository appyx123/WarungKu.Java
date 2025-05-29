package com.warungkupos.view.admin;

import com.warungkupos.model.Transaction;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.DateFormatter;
import com.warungkupos.util.InputValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer; // <--- PASTIKAN IMPORT INI ADA
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat; // <--- PASTIKAN IMPORT INI ADA
import java.util.Date;
import java.util.List;
import java.util.Locale; // <--- PASTIKAN IMPORT INI ADA

public class TransactionManagementPanel extends JPanel {

    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField usernameFilterField; 
    private JButton filterButton;
    private JButton resetButton; 

    private JTable transactionTable;
    private DefaultTableModel tableModel;

    private JButton viewDetailsButton;
    private JButton moveToRecycleBinButton;
    
    private NumberFormat currencyFormatter;


    public TransactionManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initComponents();
    }

    private void initComponents() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filter Transaksi", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));

        filterPanel.add(new JLabel("Tgl Mulai (yyyy-MM-dd):"));
        startDateField = new JTextField(10);
        filterPanel.add(startDateField);

        filterPanel.add(new JLabel("Tgl Akhir (yyyy-MM-dd):"));
        endDateField = new JTextField(10);
        filterPanel.add(endDateField);
        
        filterButton = new JButton("Filter");
        styleButton(filterButton, AppConstants.COLOR_PRIMARY_BLUE);
        filterPanel.add(filterButton);
        
        resetButton = new JButton("Reset Filter");
        styleButton(resetButton, Color.GRAY);
        filterPanel.add(resetButton);

        String[] columnNames = {"ID Transaksi", "Tanggal", "Username", "Total (Rp)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: 
                        return Integer.class;
                    case 1: 
                        return String.class; 
                    case 2: 
                        return String.class;
                    case 3: 
                        return Double.class; 
                    default:
                        return Object.class;
                }
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setAutoCreateRowSorter(true); 

        transactionTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        transactionTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); 

        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(80);  
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150); 
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(150); 
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(120); 

        JScrollPane scrollPane = new JScrollPane(transactionTable);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        viewDetailsButton = new JButton("Lihat Detail");
        moveToRecycleBinButton = new JButton("Pindah ke Recycle Bin");
        
        styleButton(viewDetailsButton, new Color(0, 150, 136)); 
        styleButton(moveToRecycleBinButton, AppConstants.COLOR_ERROR_RED);

        actionPanel.add(viewDetailsButton);
        actionPanel.add(moveToRecycleBinButton);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void displayTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0); 
        if (transactions != null) {
            for (Transaction tx : transactions) {
                Object[] row = new Object[]{
                        tx.getId(),
                        DateFormatter.formatDateTimeForDisplay(tx.getTransactionDate()),
                        tx.getUsername(),
                        tx.getTotalAmount().doubleValue() 
                };
                tableModel.addRow(row);
            }
        }
    }
    
    public String getStartDateFieldText() { return startDateField.getText().trim(); }
    public String getEndDateFieldText() { return endDateField.getText().trim(); }

    public JTextField getStartDateField() { return startDateField; }
    public JTextField getEndDateField() { return endDateField; }
    
    public boolean validateDateInputs() {
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();

        if (startDateStr.isEmpty() && endDateStr.isEmpty()) {
            return true;
        }

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            showMessage("Mohon isi kedua field tanggal atau kosongkan keduanya untuk filter.", "Input Tanggal Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            if (startDateStr.isEmpty()) startDateField.requestFocusInWindow(); else endDateField.requestFocusInWindow();
            return false;
        }

        Date startDate = DateFormatter.parseDateFromStorage(startDateStr);
        Date endDate = DateFormatter.parseDateFromStorage(endDateStr);

        if (startDate == null) {
            showMessage("Format Tanggal Mulai tidak valid (yyyy-MM-dd).", "Format Tanggal Salah", JOptionPane.WARNING_MESSAGE);
            startDateField.requestFocusInWindow();
            return false;
        }
        if (endDate == null) {
            showMessage("Format Tanggal Akhir tidak valid (yyyy-MM-dd).", "Format Tanggal Salah", JOptionPane.WARNING_MESSAGE);
            endDateField.requestFocusInWindow();
            return false;
        }

        if (startDate.after(endDate)) {
            showMessage("Tanggal Mulai tidak boleh setelah Tanggal Akhir.", "Rentang Tanggal Salah", JOptionPane.WARNING_MESSAGE);
            startDateField.requestFocusInWindow(); 
            return false;
        }

        return true; 
    }

    public JButton getFilterButton() { return filterButton; }
    public JButton getResetButton() { return resetButton; }
    public JButton getViewDetailsButton() { return viewDetailsButton; }
    public JButton getMoveToRecycleBinButton() { return moveToRecycleBinButton; }
    public JTable getTransactionTable() { return transactionTable; }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // Perbaikan: Inner class harus import DefaultTableCellRenderer dari javax.swing.table.*
    // dan NumberFormat/Locale dari java.text/java.util
    private class CurrencyRenderer extends DefaultTableCellRenderer {
        private NumberFormat localCurrencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID")); // Buat instance di sini

        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT); 
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Number) {
                value = localCurrencyFormatter.format(value); // Gunakan instance lokal
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
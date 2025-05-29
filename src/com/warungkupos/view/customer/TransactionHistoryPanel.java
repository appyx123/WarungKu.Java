package com.warungkupos.view.customer;

import com.warungkupos.model.Transaction;
import com.warungkupos.model.User;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.DateFormatter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton viewDetailsButton;
    private JButton refreshButton;

    private User loggedInUser;
    private NumberFormat currencyFormatter;

    public TransactionHistoryPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // Judul Panel
        String titleText = "Riwayat Transaksi Saya";
        if (loggedInUser != null) {
            titleText += " - " + (loggedInUser.getFullName() != null && !loggedInUser.getFullName().isEmpty() ? loggedInUser.getFullName() : loggedInUser.getUsername());
        }
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // === Panel Tabel Riwayat Transaksi ===
        String[] columnNames = {"ID Transaksi", "Tanggal Transaksi", "Total Pembelian (Rp)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // ID Transaksi
                        return Integer.class;
                    case 2: // Total Pembelian
                        return Double.class;
                    default: // Tanggal (String)
                        return String.class;
                }
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setAutoCreateRowSorter(true);

        // Renderer & Alignment
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer()); // Total
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); // ID

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tanggal
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Total

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // === Panel Aksi Bawah ===
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        refreshButton = new JButton("Refresh Riwayat");
        styleButton(refreshButton, AppConstants.COLOR_PRIMARY_BLUE);
        
        viewDetailsButton = new JButton("Lihat Detail Transaksi");
        styleButton(viewDetailsButton, new Color(0, 150, 136)); // Teal

        actionPanel.add(refreshButton);
        actionPanel.add(viewDetailsButton);
        
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

    // --- Metode untuk diisi oleh Controller nantinya ---

    public void displayTransactionHistory(List<Transaction> transactions) {
        tableModel.setRowCount(0); // Hapus baris lama
        if (transactions != null) {
            for (Transaction tx : transactions) {
                tableModel.addRow(new Object[]{
                        tx.getId(),
                        DateFormatter.formatDateTimeForDisplay(tx.getTransactionDate()),
                        tx.getTotalAmount().doubleValue() // Untuk sorting dan rendering
                });
            }
        }
    }

    // Getter untuk komponen UI
    public JTable getHistoryTable() { return historyTable; }
    public JButton getViewDetailsButton() { return viewDetailsButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public User getLoggedInUser() { return loggedInUser; } // Controller mungkin perlu ini

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Number) {
                value = currencyFormatter.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
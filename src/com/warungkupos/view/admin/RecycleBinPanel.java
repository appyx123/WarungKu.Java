package com.warungkupos.view.admin;

import com.warungkupos.model.RecycleBinTransaction;
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

public class RecycleBinPanel extends JPanel {

    // Tabel untuk Menampilkan Transaksi di Recycle Bin
    private JTable recycleBinTable;
    private DefaultTableModel tableModel;

    // Tombol Aksi
    private JButton viewRecycledDetailsButton;
    private JButton restoreTransactionButton;
    private JButton deletePermanentlyButton;
    private JButton refreshButton;
    
    private NumberFormat currencyFormatter;

    public RecycleBinPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initComponents();
    }

    private void initComponents() {
        // Judul Panel
        JLabel titleLabel = new JLabel("Recycle Bin - Transaksi Dihapus");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // === Panel Tabel Recycle Bin ===
        String[] columnNames = {"ID Asli", "Tgl Transaksi Asli", "Username Asli", "Total Asli (Rp)", "Tgl Dihapus", "Dihapus Oleh"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // ID Asli
                        return Integer.class;
                    case 3: // Total Asli
                        return Double.class;
                    default:
                        return String.class; // Tanggal dan Username/Dihapus Oleh akan jadi String
                }
            }
        };
        recycleBinTable = new JTable(tableModel);
        recycleBinTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recycleBinTable.setRowHeight(25);
        recycleBinTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        recycleBinTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        recycleBinTable.getTableHeader().setForeground(Color.WHITE);
        recycleBinTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recycleBinTable.setAutoCreateRowSorter(true);

        // Renderer untuk kolom mata uang
        recycleBinTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        // Renderer untuk rata kanan ID
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        recycleBinTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);


        recycleBinTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID Asli
        recycleBinTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Tgl Transaksi Asli
        recycleBinTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Username Asli
        recycleBinTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Total Asli
        recycleBinTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Tgl Dihapus
        recycleBinTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Dihapus Oleh

        JScrollPane scrollPane = new JScrollPane(recycleBinTable);
        add(scrollPane, BorderLayout.CENTER);

        // === Panel Aksi Bawah ===
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        refreshButton = new JButton("Refresh Data");
        viewRecycledDetailsButton = new JButton("Lihat Detail (Dihapus)");
        restoreTransactionButton = new JButton("Restore Transaksi");
        deletePermanentlyButton = new JButton("Hapus Permanen");
        
        styleButton(refreshButton, AppConstants.COLOR_PRIMARY_BLUE);
        styleButton(viewRecycledDetailsButton, new Color(0, 150, 136)); // Teal
        styleButton(restoreTransactionButton, new Color(60, 179, 113));  // MediumSeaGreen
        styleButton(deletePermanentlyButton, AppConstants.COLOR_ERROR_RED);

        actionPanel.add(refreshButton);
        actionPanel.add(viewRecycledDetailsButton);
        actionPanel.add(restoreTransactionButton);
        actionPanel.add(deletePermanentlyButton);
        
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
    
    public void displayRecycledTransactions(List<RecycleBinTransaction> transactions) {
        tableModel.setRowCount(0); // Hapus baris lama
        if (transactions != null) {
            for (RecycleBinTransaction tx : transactions) {
                Object[] row = new Object[]{
                        tx.getId(),
                        DateFormatter.formatDateTimeForDisplay(tx.getOriginalTransactionDate()),
                        tx.getOriginalUsername(),
                        tx.getOriginalTotalAmount().doubleValue(),
                        DateFormatter.formatDateTimeForDisplay(tx.getDeletedDate()),
                        tx.getDeletedBy()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getViewRecycledDetailsButton() { return viewRecycledDetailsButton; }
    public JButton getRestoreTransactionButton() { return restoreTransactionButton; }
    public JButton getDeletePermanentlyButton() { return deletePermanentlyButton; }
    public JTable getRecycleBinTable() { return recycleBinTable; }

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
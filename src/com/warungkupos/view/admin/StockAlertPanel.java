package com.warungkupos.view.admin;

import com.warungkupos.model.StockAlert; // Menggunakan model StockAlert
import com.warungkupos.util.AppConstants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockAlertPanel extends JPanel {

    private JTable alertTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JLabel infoLabel; // Untuk menampilkan info seperti threshold yang digunakan

    public StockAlertPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // Panel Atas untuk Info dan Tombol Refresh
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        infoLabel = new JLabel("Menampilkan produk dengan stok di bawah atau sama dengan batas tertentu.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        topPanel.add(infoLabel, BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Peringatan Stok");
        styleButton(refreshButton, AppConstants.COLOR_PRIMARY_BLUE);
        JPanel refreshButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButtonPanel.add(refreshButton);
        topPanel.add(refreshButtonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // === Panel Tabel Peringatan Stok ===
        String[] columnNames = {"ID Produk", "Nama Produk", "Kategori", "Stok Saat Ini", "Batas Stok"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // ID Produk
                    case 3: // Stok Saat Ini
                    case 4: // Batas Stok
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        alertTable = new JTable(tableModel);
        alertTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertTable.setRowHeight(25);
        alertTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        alertTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        alertTable.getTableHeader().setForeground(Color.WHITE);
        alertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Biasanya tidak perlu aksi per baris
        alertTable.setAutoCreateRowSorter(true);

        // Renderer untuk rata kanan kolom numerik
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        alertTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); // ID
        alertTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Stok Saat Ini
        alertTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Batas Stok

        alertTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        alertTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        alertTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        alertTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        alertTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(alertTable);
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

    // --- Metode untuk diisi oleh Controller nantinya ---
    
    public void displayStockAlerts(List<StockAlert> alerts) {
        tableModel.setRowCount(0); // Hapus baris lama
        if (alerts != null) {
            for (StockAlert alert : alerts) {
                tableModel.addRow(new Object[]{
                        alert.getProductId(),
                        alert.getProductName(),
                        alert.getCategoryName(),
                        alert.getCurrentStock(),
                        alert.getMinimumStockLevel() // Threshold yang digunakan saat alert di-generate
                });
            }
        }
        // Update info label jika perlu
        if (!alerts.isEmpty()) {
            infoLabel.setText("Peringatan untuk stok <= " + alerts.get(0).getMinimumStockLevel() + " unit.");
        } else {
            infoLabel.setText("Tidak ada produk dengan stok rendah saat ini (berdasarkan threshold default).");
        }
    }
    
    public void setInfoLabel(String text) {
        infoLabel.setText(text);
    }
    
    public JButton getRefreshButton() { return refreshButton; }
    // public JTable getAlertTable() { return alertTable; } // Jika controller perlu akses tabel

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
package com.warungkupos.view.admin;

import com.warungkupos.util.AppConstants;

import javax.swing.*;
import javax.swing.border.Border; // Import Border
import javax.swing.border.EmptyBorder; // Import EmptyBorder
import javax.swing.border.CompoundBorder; // Import CompoundBorder
import javax.swing.border.LineBorder; // Import LineBorder
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List; // Jika ada list produk dll yang ditampilkan


public class AdminMainDashboardPanel extends JPanel {

    // Statistik Produk/Inventaris
    private JLabel totalProductsLabel;
    private JLabel totalCategoriesLabel;
    private JLabel totalSuppliersLabel;
    private JLabel lowStockProductsLabel;

    // Statistik Penjualan/Transaksi
    private JLabel totalSalesTodayLabel;
    private JLabel totalSalesMonthLabel;
    private JLabel totalTransactionsTodayLabel;
    private JLabel totalTransactionsMonthLabel;

    private JButton refreshButton;

    private NumberFormat currencyFormatter;

    private JLabel totalUsersLabel;
    private JPanel generalPanel; // Panel untuk Total Pengguna

    public AdminMainDashboardPanel() {
        setLayout(new BorderLayout(15, 15)); // Margin besar antar komponen
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding panel
        setBackground(AppConstants.COLOR_BACKGROUND_LIGHT); // Warna background panel

        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initComponents();
    }

    private void initComponents() {
        // === Panel Utama untuk Statistik (Menggunakan GridBagLayout untuk fleksibilitas) ===
        // Beri border untuk keseluruhan area statistik agar lebih menonjol
        JPanel statsMainContainer = new JPanel(new BorderLayout());
        statsMainContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(AppConstants.COLOR_PRIMARY_BLUE, 2, true), // Border garis tebal dengan sudut membulat (jika true)
            "Statistik Utama Dasbor", // Judul besar untuk seluruh bagian statistik
            TitledBorder.CENTER, TitledBorder.TOP, // Judul di tengah atas
            new Font("Segoe UI", Font.BOLD, 20), AppConstants.COLOR_PRIMARY_BLUE
        ));
        statsMainContainer.setOpaque(false); // Pastikan transparan agar background panel utama terlihat


        JPanel statsGridPanel = new JPanel(new GridBagLayout()); // Mengganti statsMainPanel
        statsGridPanel.setOpaque(false); // Agar background terlihat
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Padding antar panel statistik
        gbc.fill = GridBagConstraints.BOTH; // Memenuhi ruang yang tersedia

        // === Panel Ringkasan Inventaris ===
        JPanel inventoryPanel = createStatsPanel("Ringkasan Inventaris");
        inventoryPanel.setLayout(new GridLayout(4, 1, 8, 8)); // Padding internal
        totalProductsLabel = createStatLabel("Total Produk:", "0");
        totalCategoriesLabel = createStatLabel("Total Kategori:", "0");
        totalSuppliersLabel = createStatLabel("Total Supplier:", "0");
        lowStockProductsLabel = createStatLabel("Produk Stok Rendah:", "0"); // Default text
        inventoryPanel.add(totalProductsLabel);
        inventoryPanel.add(totalCategoriesLabel);
        inventoryPanel.add(totalSuppliersLabel);
        inventoryPanel.add(lowStockProductsLabel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5; gbc.weighty = 0.5;
        statsGridPanel.add(inventoryPanel, gbc);

        // === Panel Ringkasan Penjualan Hari Ini ===
        JPanel salesTodayPanel = createStatsPanel("Penjualan Hari Ini");
        salesTodayPanel.setLayout(new GridLayout(2, 1, 8, 8));
        totalSalesTodayLabel = createStatLabel("Total Penjualan:", currencyFormatter.format(0));
        totalTransactionsTodayLabel = createStatLabel("Jumlah Transaksi:", "0");
        salesTodayPanel.add(totalSalesTodayLabel);
        salesTodayPanel.add(totalTransactionsTodayLabel);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5; gbc.weighty = 0.5;
        statsGridPanel.add(salesTodayPanel, gbc);

        // === Panel Ringkasan Penjualan Bulan Ini ===
        JPanel salesMonthPanel = createStatsPanel("Penjualan Bulan Ini");
        salesMonthPanel.setLayout(new GridLayout(2, 1, 8, 8));
        totalSalesMonthLabel = createStatLabel("Total Penjualan:", currencyFormatter.format(0));
        totalTransactionsMonthLabel = createStatLabel("Jumlah Transaksi:", "0");
        salesMonthPanel.add(totalSalesMonthLabel);
        salesMonthPanel.add(totalTransactionsMonthLabel);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5; gbc.weighty = 0.5;
        statsGridPanel.add(salesMonthPanel, gbc);

        // === Panel Ringkasan Pengguna ===
        generalPanel = createStatsPanel("Ringkasan Pengguna");
        generalPanel.setLayout(new GridLayout(1, 1, 8, 8)); // Contoh: hanya 1 stat user
        totalUsersLabel = createStatLabel("Total Pengguna:", "0");
        generalPanel.add(totalUsersLabel);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5; gbc.weighty = 0.5;
        statsGridPanel.add(generalPanel, gbc);
        
        statsMainContainer.add(statsGridPanel, BorderLayout.CENTER); // Tambahkan grid ke container utama
        
        // --- Tombol Refresh ---
        JPanel refreshButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButtonPanel.setOpaque(false);
        refreshButton = new JButton("Refresh Statistik");
        styleButton(refreshButton, AppConstants.COLOR_PRIMARY_BLUE);
        refreshButtonPanel.add(refreshButton);
        
        add(statsMainContainer, BorderLayout.CENTER); // Tambahkan container utama ke panel ini
        add(refreshButtonPanel, BorderLayout.SOUTH);
    }

    /** Helper method untuk membuat panel statistik dengan border dan judul */
    private JPanel createStatsPanel(String title) {
        JPanel panel = new JPanel();
        // Menggunakan CompoundBorder untuk border yang lebih kompleks (padding + garis)
        panel.setBorder(new CompoundBorder(
            new LineBorder(AppConstants.COLOR_PRIMARY_BLUE, 1, true), // Garis luar
            new TitledBorder( // Judul dalam
                new EmptyBorder(5, 5, 5, 5), // Padding di dalam border sebelum judul
                title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_TEXT_DARK
            )
        ));
        panel.setBackground(Color.WHITE); // Background putih untuk kartu stat
        return panel;
    }

    /** Helper method untuk membuat JLabel untuk menampilkan statistik dengan format HTML */
    // <--- METODE BARU: createStatLabel dengan dua parameter
    private JLabel createStatLabel(String description, String value) {
        JLabel label = new JLabel("<html><body style='text-align:left;'>" +
                                  "<span style='font-size:12px;color:#666;'>" + description + "</span><br>" +
                                  "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + value + "</span>" +
                                  "</html>");
        label.setVerticalAlignment(SwingConstants.TOP); // Agar teks deskripsi di atas nilai
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    // <--- METODE LAMA: createStatLabel dengan satu parameter (bisa dihapus atau diganti)
    // private JLabel createStatLabel(String text) {
    //     JLabel label = new JLabel(text);
    //     label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    //     label.setForeground(AppConstants.COLOR_TEXT_DARK);
    //     return label;
    // }
    // <--- AKHIR METODE LAMA

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- Metode untuk diisi oleh Controller nantinya ---
    // Metode untuk mengupdate nilai statistik di View (semua diperbarui untuk HTML)
    public void setTotalProducts(int count) {
        totalProductsLabel.setText("<html><body style='text-align:left;'>" +
                                   "<span style='font-size:12px;color:#666;'>Total Produk:</span><br>" +
                                   "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                   "</html>");
    }
    public void setTotalCategories(int count) {
        totalCategoriesLabel.setText("<html><body style='text-align:left;'>" +
                                     "<span style='font-size:12px;color:#666;'>Total Kategori:</span><br>" +
                                     "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                     "</html>");
    }
    public void setTotalSuppliers(int count) {
        totalSuppliersLabel.setText("<html><body style='text-align:left;'>" +
                                    "<span style='font-size:12px;color:#666;'>Total Supplier:</span><br>" +
                                    "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                    "</html>");
    }
    public void setLowStockProducts(int count) {
        String colorCode = String.format("#%02x%02x%02x", (count > 0 ? AppConstants.COLOR_ERROR_RED : AppConstants.COLOR_PRIMARY_BLUE).getRed(),
                                      (count > 0 ? AppConstants.COLOR_ERROR_RED : AppConstants.COLOR_PRIMARY_BLUE).getGreen(),
                                      (count > 0 ? AppConstants.COLOR_ERROR_RED : AppConstants.COLOR_PRIMARY_BLUE).getBlue());

        lowStockProductsLabel.setText("<html><body style='text-align:left;'>" +
                                      "<span style='font-size:12px;color:#666;'>Produk Stok Rendah:</span><br>" +
                                      "<span style='font-size:18px;font-weight:bold;color:" + colorCode + ";'>" + count + "</span>" +
                                      "</html>");
    }
    public void setTotalSalesToday(BigDecimal amount) {
        totalSalesTodayLabel.setText("<html><body style='text-align:left;'>" +
                                     "<span style='font-size:12px;color:#666;'>Total Penjualan:</span><br>" +
                                     "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + currencyFormatter.format(amount) + "</span>" +
                                     "</html>");
    }
    public void setTotalSalesMonth(BigDecimal amount) {
        totalSalesMonthLabel.setText("<html><body style='text-align:left;'>" +
                                     "<span style='font-size:12px;color:#666;'>Total Penjualan:</span><br>" +
                                     "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + currencyFormatter.format(amount) + "</span>" +
                                     "</html>");
    }
    public void setTotalTransactionsToday(int count) {
        totalTransactionsTodayLabel.setText("<html><body style='text-align:left;'>" +
                                           "<span style='font-size:12px;color:#666;'>Jumlah Transaksi:</span><br>" +
                                           "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                           "</html>");
    }
    public void setTotalTransactionsMonth(int count) {
        totalTransactionsMonthLabel.setText("<html><body style='text-align:left;'>" +
                                           "<span style='font-size:12px;color:#666;'>Jumlah Transaksi:</span><br>" +
                                           "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                           "</html>");
    }
    public void setTotalUsers(int count) {
        totalUsersLabel.setText("<html><body style='text-align:left;'>" +
                                "<span style='font-size:12px;color:#666;'>Total Pengguna:</span><br>" +
                                "<span style='font-size:18px;font-weight:bold;color:" + String.format("#%02x%02x%02x", AppConstants.COLOR_PRIMARY_BLUE.getRed(), AppConstants.COLOR_PRIMARY_BLUE.getGreen(), AppConstants.COLOR_PRIMARY_BLUE.getBlue()) + ";'>" + count + "</span>" +
                                "</html>");
    }

    public JButton getRefreshButton() { return refreshButton; }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
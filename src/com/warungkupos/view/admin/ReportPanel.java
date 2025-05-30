package com.warungkupos.view.admin;

import com.warungkupos.model.Product;
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
import java.util.List;
import java.util.Locale;
import java.util.Date; // Tambahkan import ini

public class ReportPanel extends JPanel {

    private JTextField startDateField;
    private JTextField endDateField;
    private JButton generateSalesReportButton;
    private JLabel totalSalesLabel;

    private JTable productStockTable;
    private DefaultTableModel stockTableModel;
    private JButton refreshStockReportButton;
    
    private NumberFormat currencyFormatter;

    public ReportPanel() {
        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        initComponents();
    }

    private void initComponents() {
        JPanel salesReportPanel = new JPanel(new BorderLayout(10,10));
        salesReportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Laporan Penjualan Total", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), AppConstants.COLOR_PRIMARY_BLUE
        ));

        JPanel salesFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        salesFilterPanel.add(new JLabel("Dari Tanggal (yyyy-MM-dd):"));
        startDateField = new JTextField(10);
        salesFilterPanel.add(startDateField);

        salesFilterPanel.add(new JLabel("Sampai Tanggal (yyyy-MM-dd):"));
        endDateField = new JTextField(10);
        salesFilterPanel.add(endDateField);

        generateSalesReportButton = new JButton("Tampilkan Total Penjualan");
        styleButton(generateSalesReportButton, AppConstants.COLOR_PRIMARY_BLUE);
        salesFilterPanel.add(generateSalesReportButton);
        
        salesReportPanel.add(salesFilterPanel, BorderLayout.NORTH);

        JPanel totalSalesDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,10));
        totalSalesDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
        JLabel salesInfoLabel = new JLabel("Total Penjualan pada periode terpilih:");
        salesInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalSalesLabel = new JLabel(currencyFormatter.format(0)); 
        totalSalesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalSalesLabel.setForeground(new Color(0, 100, 0)); 
        
        totalSalesDisplayPanel.add(salesInfoLabel);
        totalSalesDisplayPanel.add(totalSalesLabel);
        salesReportPanel.add(totalSalesDisplayPanel, BorderLayout.CENTER);


        JPanel stockReportPanel = new JPanel(new BorderLayout(10,10));
        stockReportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Ringkasan Stok Produk", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), AppConstants.COLOR_PRIMARY_BLUE
        ));
        
        JPanel stockButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshStockReportButton = new JButton("Refresh Stok Produk");
        styleButton(refreshStockReportButton, AppConstants.COLOR_PRIMARY_BLUE);
        stockButtonPanel.add(refreshStockReportButton);
        stockReportPanel.add(stockButtonPanel, BorderLayout.NORTH);

        String[] stockColumnNames = {"ID Produk", "Nama Produk", "Kategori", "Stok Saat Ini"};
        stockTableModel = new DefaultTableModel(stockColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: case 3: return Integer.class;
                    default: return String.class;
                }
            }
        };
        productStockTable = new JTable(stockTableModel);
        productStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productStockTable.setRowHeight(25);
        productStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productStockTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        productStockTable.getTableHeader().setForeground(Color.WHITE);
        productStockTable.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        productStockTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer); 
        productStockTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); 

        productStockTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        productStockTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        productStockTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        productStockTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane stockScrollPane = new JScrollPane(productStockTable);
        stockReportPanel.add(stockScrollPane, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, salesReportPanel, stockReportPanel);
        splitPane.setResizeWeight(0.3); 
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void displayTotalSales(BigDecimal totalSales) {
        if (totalSales != null) {
            totalSalesLabel.setText(currencyFormatter.format(totalSales));
        } else {
            totalSalesLabel.setText(currencyFormatter.format(0));
        }
    }

    public void displayProductStockSummary(List<Product> products) {
        stockTableModel.setRowCount(0); 
        if (products != null) {
            for (Product product : products) {
                stockTableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getCategoryName(), 
                        product.getStock()
                });
            }
        }
    }
    
    public String getStartDateFieldText() { return startDateField.getText().trim(); }
    public String getEndDateFieldText() { return endDateField.getText().trim(); }
    
    // <--- METODE BARU: Setter untuk field tanggal
    public void setStartDateField(String dateString) {
        this.startDateField.setText(dateString);
    }

    public void setEndDateField(String dateString) {
        this.endDateField.setText(dateString);
    }
    // <--- AKHIR METODE BARU
    
    public boolean validateDateInputs() {
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) { // Cek jika ada yang kosong
            showMessage("Tanggal Mulai dan Tanggal Akhir harus diisi untuk laporan penjualan.", "Input Tanggal Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
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

    public JButton getGenerateSalesReportButton() { return generateSalesReportButton; }
    public JButton getRefreshStockReportButton() { return refreshStockReportButton; }
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
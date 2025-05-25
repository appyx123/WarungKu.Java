package controller;

import Models.Category;
import Models.DataManager;
import Models.Product;
import Models.Report;
import Models.Supplier;
import Models.Transaction;
import view.AdminView;
import view.CustomerView;
import view.LoginView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AppController {
    private DataManager dataManager;
    private LoginView loginView;
    private AdminView adminView;
    private CustomerView customerView;
    private String currentUser;

    public AppController() {
        try {
            dataManager = new DataManager();
            loginView = new LoginView();
            setupLoginView();
            loginView.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memulai aplikasi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupLoginView() {
        loginView.getLoginButton().addActionListener(e -> {
            String role = (String) loginView.getRoleCombo().getSelectedItem();
            String username = loginView.getUsernameField().getText();
            String password = new String(loginView.getPasswordField().getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Username dan password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (dataManager.authenticate(username, password, role)) {
                    currentUser = username;
                    loginView.setVisible(false);
                    if (role.equals("Admin")) {
                        adminView = new AdminView();
                        setupAdminView();
                        adminView.setVisible(true);
                    } else {
                        customerView = new CustomerView();
                        setupCustomerView();
                        customerView.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(loginView, "Username atau password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(loginView, "Error database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginView.getRegisterButton().addActionListener(e -> {
            loginView.showRegisterDialog();
        });
    }

    private void setupAdminView() {
        try {
            refreshProductTable();
            refreshCategoryTable();
            refreshTransactionTable();
            refreshSupplierTable();
            refreshRecycleBinTable();

            adminView.getProductAddButton().addActionListener(e -> {
                try {
                    String name = adminView.getProductNameField().getText();
                    double price = Double.parseDouble(adminView.getProductPriceField().getText());
                    int stock = Integer.parseInt(adminView.getProductStockField().getText());
                    int categoryId = Integer.parseInt(adminView.getProductCategoryIdField().getText());
                    dataManager.addProduct(name, price, stock, categoryId);
                    refreshProductTable();
                    clearProductFields();
                    checkStockWarning(getLatestProductId());
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getProductUpdateButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getProductIdField().getText());
                    String name = adminView.getProductNameField().getText();
                    double price = Double.parseDouble(adminView.getProductPriceField().getText());
                    int stock = Integer.parseInt(adminView.getProductStockField().getText());
                    int categoryId = Integer.parseInt(adminView.getProductCategoryIdField().getText());
                    dataManager.updateProduct(id, name, price, stock, categoryId);
                    refreshProductTable();
                    checkStockWarning(id);
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getProductDeleteButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getProductIdField().getText());
                    dataManager.deleteProduct(id);
                    refreshProductTable();
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getCategoryAddButton().addActionListener(e -> {
                try {
                    String Xie = adminView.getCategoryNameField().getText();
                    dataManager.addCategory(adminView.getCategoryId(), Xie);
                    refreshCategoryTable();
                    adminView.getCategoryNameField().setText("");
                } catch (SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getCategoryUpdateButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getCategoryIdField().getText());
                    String name = adminView.getCategoryNameField().getText();
                    dataManager.updateCategory(id, name);
                    refreshCategoryTable();
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getCategoryDeleteButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getCategoryIdField().getText());
                    dataManager.deleteCategory(id);
                    refreshCategoryTable();
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getTransactionAddButton().addActionListener(e -> {
                try {
                    int productId = Integer.parseInt(adminView.getTransactionProductIdField().getText());
                    int quantity = Integer.parseInt(adminView.getTransactionQuantityField().getText());
                    if (quantity <= 0) throw new IllegalArgumentException("Kuantitas harus positif");
                    Map<Integer, Integer> productQuantities = new HashMap<>();
                    productQuantities.put(productId, quantity);
                    dataManager.addTransaction(productQuantities, currentUser);
                    refreshTransactionTable();
                    refreshProductTable();
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getTransactionDeleteButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getTransactionIdField().getText());
                    dataManager.deleteTransaction(id);
                    refreshTransactionTable();
                    refreshRecycleBinTable();
                    refreshProductTable();
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getSupplierAddButton().addActionListener(e -> {
                try {
                    String name = adminView.getSupplierNameField().getText();
                    String contact = adminView.getSupplierContactField().getText();
                    String address = adminView.getSupplierAddressField().getText();
                    dataManager.addSupplier(name, contact, address);
                    refreshSupplierTable();
                    clearSupplierFields();
                } catch (SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getSupplierUpdateButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getSupplierIdField().getText());
                    String name = adminView.getSupplierNameField().getText();
                    String contact = adminView.getSupplierContactField().getText();
                    String address = adminView.getSupplierAddressField().getText();
                    dataManager.updateSupplier(id, name, contact, address);
                    refreshSupplierTable();
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getSupplierDeleteButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getSupplierIdField().getText());
                    dataManager.deleteSupplier(id);
                    refreshSupplierTable();
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getGenerateReportButton().addActionListener(e -> {
                try {
                    Report report = dataManager.generateReport();
                    StringBuilder reportText = new StringBuilder();
                    reportText.append("Laporan Penjualan dan Stok\n");
                    reportText.append("Tanggal: ").append(report.getDate()).append("\n");
                    reportText.append("Total Penjualan: Rp").append(String.format("%,.2f", report.getTotalSales())).append("\n");
                    reportText.append("Ringkasan Stok:\n");
                    report.getStockSummary().forEach((id, stock) -> {
                        try {
                            for (Product p : dataManager.getProducts()) {
                                if (p.getId() == id) {
                                    reportText.append("- ").append(p.getName()).append(": ").append(stock).append(" unit\n");
                                    break;
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                    adminView.getReportArea().setText(reportText.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error menghasilkan laporan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getRestoreButton().addActionListener(e -> {
                try {
                    int id = Integer.parseInt(adminView.getRecycleBinIdField().getText());
                    dataManager.restoreTransaction(id);
                    refreshTransactionTable();
                    refreshRecycleBinTable();
                    refreshProductTable();
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(adminView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            adminView.getLogoutButton().addActionListener(e -> {
                adminView.setVisible(false);
                loginView.setVisible(true);
                currentUser = null;
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(adminView, "Error database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupCustomerView() {
        try {
            refreshCustomerProductTable();
            refreshCustomerTransactionTable();

            customerView.getBuyButton().addActionListener(e -> {
                try {
                    int productId = Integer.parseInt(customerView.getProductIdField().getText());
                    int quantity = Integer.parseInt(customerView.getQuantityField().getText());
                    if (quantity <= 0) throw new IllegalArgumentException("Kuantitas harus positif");
                    Map<Integer, Integer> productQuantities = new HashMap<>();
                    productQuantities.put(productId, quantity);
                    dataManager.addTransaction(productQuantities, currentUser);
                    refreshCustomerProductTable();
                    refreshCustomerTransactionTable();
                    customerView.getProductIdField().setText("");
                    customerView.getQuantityField().setText("");
                } catch (NumberFormatException | SQLException | IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(customerView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            customerView.getLogoutButton().addActionListener(e -> {
                customerView.setVisible(false);
                loginView.setVisible(true);
                currentUser = null;
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(customerView, "Error database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshProductTable() throws SQLException {
        adminView.getProductTableModel().setRowCount(0);
        for (Product p : dataManager.getProducts()) {
            adminView.getProductTableModel().addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getCategoryId()
            });
        }
    }

    private void refreshCategoryTable() throws SQLException {
        adminView.getCategoryTableModel().setRowCount(0);
        for (Category c : dataManager.getCategories()) {
            adminView.getCategoryTableModel().addRow(new Object[]{c.getId(), c.getName()});
        }
    }

    private void refreshTransactionTable() throws SQLException {
        adminView.getTransactionTableModel().setRowCount(0);
        for (Transaction t : dataManager.getTransactions()) {
            String products = t.getDetails().stream()
                    .map(d -> "ID:" + d.getProductId() + "(" + d.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            adminView.getTransactionTableModel().addRow(new Object[]{
                    t.getId(), t.getDate(), products, t.getTotal(), t.getUsername()
            });
        }
    }

    private void refreshSupplierTable() throws SQLException {
        adminView.getSupplierTableModel().setRowCount(0);
        for (Supplier s : dataManager.getSuppliers()) {
            adminView.getSupplierTableModel().addRow(new Object[]{
                    s.getId(), s.getName(), s.getContact(), s.getAddress()
            });
        }
    }

    private void refreshRecycleBinTable() throws SQLException {
        adminView.getRecycleBinTableModel().setRowCount(0);
        for (Transaction t : dataManager.getRecycleBin()) {
            String products = t.getDetails().stream()
                    .map(d -> "ID:" + d.getProductId() + "(" + d.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            adminView.getRecycleBinTableModel().addRow(new Object[]{
                    t.getId(), t.getDate(), products, t.getTotal(), t.getUsername()
            });
        }
    }

    private void refreshCustomerProductTable() throws SQLException {
        customerView.getProductTableModel().setRowCount(0);
        for (Product p : dataManager.getProducts()) {
            customerView.getProductTableModel().addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getCategoryId()
            });
        }
    }

    private void refreshCustomerTransactionTable() throws SQLException {
        customerView.getTransactionTableModel().setRowCount(0);
        for (Transaction t : dataManager.getUserTransactions(currentUser)) {
            String products = t.getDetails().stream()
                    .map(d -> "ID:" + d.getProductId() + "(" + d.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            customerView.getTransactionTableModel().addRow(new Object[]{
                    t.getId(), t.getDate(), products, t.getTotal()
            });
        }
    }

    private void clearProductFields() {
        adminView.getProductIdField().setText("");
        adminView.getProductNameField().setText("");
        adminView.getProductPriceField().setText("");
        adminView.getProductStockField().setText("");
        adminView.getProductCategoryIdField().setText("");
    }

    private void clearSupplierFields() {
        adminView.getSupplierIdField().setText("");
        adminView.getSupplierNameField().setText("");
        adminView.getSupplierContactField().setText("");
        adminView.getSupplierAddressField().setText("");
    }

    private int getLatestProductId() throws SQLException {
        int latestId = -1;
        for (Product p : dataManager.getProducts()) {
            if (p.getId() > latestId) {
                latestId = p.getId();
            }
        }
        return latestId;
    }

    private void checkStockWarning(int productId) throws SQLException {
        int stock = dataManager.checkStock(productId);
        if (stock >= 0 && stock < 50) {
            JOptionPane.showMessageDialog(adminView,
                    "Peringatan: Produk ID " + productId + " memiliki stok rendah (" + stock + " unit)!",
                    "Peringatan Stok Rendah",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
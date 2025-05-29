package com.warungkupos.view.admin;

import com.warungkupos.controller.*; // Impor semua controller yang relevan
import com.warungkupos.service.ProductManagementService;
import com.warungkupos.service.TransactionHandlingService;
import com.warungkupos.service.RecycleBinService;
import com.warungkupos.service.ReportGenerationService;
import com.warungkupos.service.StockNotificationService; 
import com.warungkupos.service.AuthenticationService; // Tambahkan import ini
import com.warungkupos.service.*; // Impor semua implementasi service
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.UIManagerSetup;
import com.warungkupos.view.auth.LoginView;
import com.warungkupos.model.User; 
import com.warungkupos.dao.UserDao; // Tambahkan import ini untuk UserDao
import com.warungkupos.dao.impl.UserDaoImpl; // Tambahkan import ini untuk UserDaoImpl


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminDashboardView extends JFrame {

    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private User loggedInAdmin; 

    // Instance Services (diinisialisasi di konstruktor AdminDashboardView)
    private ProductManagementService productManagementService;
    private TransactionHandlingService transactionHandlingService;
    private RecycleBinService recycleBinService;
    private ReportGenerationService reportGenerationService;
    private StockNotificationService stockNotificationService; 
    private AuthenticationService authenticationService; // Tambahkan field ini
    private UserDao userDao; // Tambahkan field ini

    // Referensi Controller yang mungkin perlu diakses dari AdminDashboardView
    private ProductController productController; 
    // Anda bisa tambahkan referensi controller lain jika perlu interaksi langsung dari dashboard

    public AdminDashboardView() { 
        // Anda bisa tambahkan User admin sebagai parameter jika perlu
        // this.loggedInAdmin = loggedInAdmin; // Jika Anda passing User saat membuat AdminDashboardView
        
        setTitle(AppConstants.ADMIN_DASHBOARD_TITLE + (loggedInAdmin != null ? " - User: " + loggedInAdmin.getUsername() : ""));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720)); 
        setLocationRelativeTo(null);

        // --- Inisialisasi semua Service dan DAO yang dibutuhkan ---
        this.productManagementService = new ProductManagementServiceImpl();
        this.transactionHandlingService = new TransactionHandlingServiceImpl();
        this.recycleBinService = new RecycleBinServiceImpl();
        this.reportGenerationService = new ReportGenerationServiceImpl();
        this.stockNotificationService = new StockNotificationServiceImpl(); 
        this.authenticationService = new AuthenticationServiceImpl(); // Inisialisasi AuthenticationService
        this.userDao = new UserDaoImpl(); // Inisialisasi UserDao
        // --------------------------------------------------------

        initComponents();
        addListeners(); 
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(AppConstants.COLOR_PRIMARY_BLUE);

        JLabel titleLabel = new JLabel("Dasbor Admin - " + AppConstants.APPLICATION_NAME);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConstants.COLOR_PRIMARY_BLUE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,0));
        logoutButtonPanel.setOpaque(false);
        logoutButtonPanel.add(logoutButton);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // --- Mengisi Tab dengan Panel Manajemen ---

        // 1. Tab Produk
        ProductManagementPanel productPanel = new ProductManagementPanel();
        this.productController = new ProductController(productPanel, productManagementService); 
        tabbedPane.addTab("Produk", productPanel);

        // 2. Tab Kategori
        CategoryManagementPanel categoryPanel = new CategoryManagementPanel();
        new CategoryController(categoryPanel, productManagementService);
        tabbedPane.addTab("Kategori", categoryPanel);

        // 3. Tab Supplier
        SupplierManagementPanel supplierPanel = new SupplierManagementPanel();
        new SupplierController(supplierPanel, productManagementService);
        tabbedPane.addTab("Supplier", supplierPanel);

        // 4. Tab Transaksi
        TransactionManagementPanel transactionPanel = new TransactionManagementPanel();
        new TransactionController(transactionPanel, transactionHandlingService, recycleBinService);
        tabbedPane.addTab("Transaksi", transactionPanel);

        // 5. Tab Recycle Bin
        RecycleBinPanel recycleBinPanel = new RecycleBinPanel();
        new RecycleBinController(recycleBinPanel, recycleBinService);
        tabbedPane.addTab("Recycle Bin", recycleBinPanel);

        // 6. Tab Laporan
        ReportPanel reportPanel = new ReportPanel();
        new ReportController(reportPanel, reportGenerationService);
        tabbedPane.addTab("Laporan", reportPanel);
        
        // 7. Tab Peringatan Stok
        StockAlertPanel stockAlertPanel = new StockAlertPanel();
        new StockAlertController(stockAlertPanel, stockNotificationService); 
        tabbedPane.addTab("Peringatan Stok", stockAlertPanel);

        // 8. Tab Pengguna (User Management) - BARU DITAMBAHKAN
        UserManagementPanel userManagementPanel = new UserManagementPanel();
        // Berikan AuthenticationService dan UserDao ke UserController
        new UserController(userManagementPanel, authenticationService, userDao); 
        tabbedPane.addTab("Pengguna", userManagementPanel);


        add(tabbedPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> handleLogout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(AdminDashboardView.this,
                        AppConstants.MSG_CONFIRM_LOGOUT + " dan keluar dari aplikasi?",
                        "Konfirmasi Logout & Keluar",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = sourceTabbedPane.getSelectedIndex();
                String selectedTitle = sourceTabbedPane.getTitleAt(selectedIndex);

                // --- Refresh data saat tab tertentu dipilih ---
                if ("Produk".equals(selectedTitle) && productController != null) {
                    productController.refreshCategoriesComboBox(); // Refresh kategori di tab Produk
                    // productController.loadProducts(); // Anda bisa juga refresh produk di sini
                } 
                // Jika Anda ingin refresh data di tab lain saat tabnya dipilih, tambahkan logika di sini.
                // Misal:
                // if ("Kategori".equals(selectedTitle)) {
                //      ((CategoryController)getControllerForTab(sourceTabbedPane, selectedTitle)).loadCategories();
                // }
                // Ini memerlukan AdminDashboardView menyimpan referensi ke semua controllernya atau mencari controller berdasarkan panel
            }
        });
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(this,
                AppConstants.MSG_CONFIRM_LOGOUT, "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            this.dispose();
            EventQueue.invokeLater(() -> {
                LoginView loginView = new LoginView();
                // Pastikan AuthController mendapatkan AuthenticationService yang sudah diinisialisasi
                loginView.setAuthController(new AuthController(new AuthenticationServiceImpl()));
                loginView.setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        UIManagerSetup.setupLookAndFeel();
        EventQueue.invokeLater(() -> {
            // Untuk testing, Anda bisa membuat user admin dummy jika diperlukan
            // User adminUser = new User(); adminUser.setUsername("testAdmin"); adminUser.setRole(AppConstants.ROLE_ADMIN);
            AdminDashboardView adminDashboard = new AdminDashboardView(/* adminUser */);
            adminDashboard.setVisible(true);
        });
    }
}
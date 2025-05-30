package com.warungkupos.view.customer;

import com.warungkupos.controller.AuthController;
import com.warungkupos.controller.CustomerController;
import com.warungkupos.model.User;
import com.warungkupos.service.ProductManagementService;
import com.warungkupos.service.TransactionHandlingService;
import com.warungkupos.service.AuthenticationServiceImpl;
import com.warungkupos.service.ProductManagementServiceImpl;
import com.warungkupos.service.TransactionHandlingServiceImpl;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.UIManagerSetup;
import com.warungkupos.view.auth.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.EventQueue;


public class CustomerDashboardView extends JFrame {

    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private User loggedInUser;

    // Instance Services
    private ProductManagementService productManagementService;
    private TransactionHandlingService transactionHandlingService;

    public CustomerDashboardView(User user) {
        this.loggedInUser = user;
        if (user == null) {
            JOptionPane.showMessageDialog(null, "Error: Data pengguna tidak valid.", "Error Kritis", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        String title = AppConstants.CUSTOMER_DASHBOARD_TITLE;
        if (user.getFullName() != null && !user.getFullName().isEmpty()) { // <--- PERBAIKAN DI SINI
            title = "Selamat Datang, " + user.getFullName() + "! - " + AppConstants.APPLICATION_NAME;
        } else {
            title = "Selamat Datang, " + user.getUsername() + "! - " + AppConstants.APPLICATION_NAME;
        }
        setTitle(title);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        this.productManagementService = new ProductManagementServiceImpl();
        this.transactionHandlingService = new TransactionHandlingServiceImpl();

        initComponents();
        addListeners();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(AppConstants.COLOR_PRIMARY_BLUE);

        String welcomeMessage = "Pelanggan Dashboard";
        if (loggedInUser.getFullName() != null && !loggedInUser.getFullName().isEmpty()) { // <--- PERBAIKAN DI SINI
            welcomeMessage = loggedInUser.getFullName();
        } else {
             welcomeMessage = loggedInUser.getUsername();
        }
        
        JLabel titleLabel = new JLabel(welcomeMessage);
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

        // --- Mengisi Tab dengan Panel Customer ---

        // 1. Tab Transaksi (sebelumnya "Lihat Produk")
        ProductDisplayPanel productDisplayPanel = new ProductDisplayPanel(this.loggedInUser);
        // 2. Tab Riwayat Transaksi
        TransactionHistoryPanel transactionHistoryPanel = new TransactionHistoryPanel(this.loggedInUser);

        // Buat CustomerController dan berikan kedua panel serta service
        new CustomerController(productDisplayPanel, transactionHistoryPanel, 
                               productManagementService, transactionHandlingService, 
                               this.loggedInUser);
        
        tabbedPane.addTab("Transaksi", productDisplayPanel);
        tabbedPane.addTab("Riwayat Transaksi Saya", transactionHistoryPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> handleLogout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(CustomerDashboardView.this,
                        AppConstants.MSG_CONFIRM_LOGOUT + " dan keluar dari aplikasi?",
                        "Konfirmasi Logout & Keluar",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
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
                loginView.setAuthController(new AuthController(new AuthenticationServiceImpl()));
                loginView.setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        UIManagerSetup.setupLookAndFeel();
        EventQueue.invokeLater(() -> {
            User testUser = new User(1, "customerTest", "hashedpassword", AppConstants.ROLE_CUSTOMER, "Pelanggan Uji Coba");
            CustomerDashboardView customerDashboard = new CustomerDashboardView(testUser);
            customerDashboard.setVisible(true);
        });
    }
}
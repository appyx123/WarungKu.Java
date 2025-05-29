package com.warungkupos.view.auth;

import com.warungkupos.controller.AuthController;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.UIManagerSetup; // Untuk main method standalone
import com.warungkupos.util.InputValidator; // Import InputValidator

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpView extends JFrame {

    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signUpButton;
    private JButton loginButton;

    private AuthController authController;

    public SignUpView() {
        setTitle(AppConstants.SIGNUP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(750, 550)); 
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        initComponents();
        addListeners();
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    private void initComponents() {
        // === Panel Kiri (Branding) ===
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridwidth = GridBagConstraints.REMAINDER;
        gbcLeft.fill = GridBagConstraints.HORIZONTAL;
        gbcLeft.insets = new Insets(10, 20, 10, 20);

        JLabel appNameLabel = new JLabel(AppConstants.APPLICATION_NAME);
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(appNameLabel, gbcLeft);

        JLabel appSloganLabel = new JLabel("Buat Akun Baru Anda");
        appSloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        appSloganLabel.setForeground(Color.WHITE);
        appSloganLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(appSloganLabel, gbcLeft);
        
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(200,200,200,100));
        gbcLeft.insets = new Insets(20, 50, 20, 50);
        leftPanel.add(separator, gbcLeft);

        // === Panel Kanan (Form Registrasi) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(AppConstants.COLOR_WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(8, 10, 8, 10); 
        gbcRight.fill = GridBagConstraints.HORIZONTAL;

        JLabel signUpTitleLabel = new JLabel("REGISTRASI AKUN");
        signUpTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        signUpTitleLabel.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        signUpTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.gridwidth = 2; 
        gbcRight.weightx = 1.0;
        gbcRight.insets = new Insets(15, 10, 15, 10);
        rightPanel.add(signUpTitleLabel, gbcRight);

        gbcRight.gridwidth = 1; 
        gbcRight.insets = new Insets(8, 10, 8, 10); 

        // Nama Lengkap
        JLabel fullNameLabel = new JLabel("Nama Lengkap (Opsional):");
        fullNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 1;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 0;
        rightPanel.add(fullNameLabel, gbcRight);

        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 1;
        gbcRight.weightx = 1.0;
        rightPanel.add(fullNameField, gbcRight);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 2;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 0;
        rightPanel.add(usernameLabel, gbcRight);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 2;
        gbcRight.weightx = 1.0;
        rightPanel.add(usernameField, gbcRight);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 3;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 0;
        rightPanel.add(passwordLabel, gbcRight);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 3;
        gbcRight.weightx = 1.0;
        rightPanel.add(passwordField, gbcRight);

        // Konfirmasi Password
        JLabel confirmPasswordLabel = new JLabel("Konfirmasi Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 4;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 0;
        rightPanel.add(confirmPasswordLabel, gbcRight);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 4;
        gbcRight.weightx = 1.0;
        rightPanel.add(confirmPasswordField, gbcRight);
        
        // Role
        JLabel roleLabel = new JLabel("Daftar sebagai:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 5;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.weightx = 0;
        rightPanel.add(roleLabel, gbcRight);

        String[] roles = {AppConstants.ROLE_CUSTOMER, AppConstants.ROLE_ADMIN};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 5;
        gbcRight.weightx = 1.0;
        rightPanel.add(roleComboBox, gbcRight);


        // Tombol Daftar
        signUpButton = new JButton("Daftar Akun");
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signUpButton.setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        gbcRight.gridx = 0;
        gbcRight.gridy = 6;
        gbcRight.gridwidth = 2; 
        gbcRight.anchor = GridBagConstraints.CENTER;
        gbcRight.fill = GridBagConstraints.NONE;
        gbcRight.insets = new Insets(20, 10, 5, 10); 
        rightPanel.add(signUpButton, gbcRight);

        // Panel untuk link Login
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginPanel.setBackground(AppConstants.COLOR_WHITE);
        JLabel alreadyHaveAccountLabel = new JLabel("Sudah punya akun? ");
        alreadyHaveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        loginButton = new JButton("Login di sini");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginPanel.add(alreadyHaveAccountLabel);
        loginPanel.add(loginButton);
        
        gbcRight.gridx = 0;
        gbcRight.gridy = 7; 
        gbcRight.gridwidth = 2;
        gbcRight.anchor = GridBagConstraints.CENTER;
        gbcRight.insets = new Insets(5, 10, 20, 10);
        rightPanel.add(loginPanel, gbcRight);

        add(leftPanel);
        add(rightPanel);
    }

    private void addListeners() {
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
        
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authController != null) {
                    authController.showLoginView();
                    SignUpView.this.dispose(); 
                } else {
                     JOptionPane.showMessageDialog(SignUpView.this,
                        "AuthController belum diinisialisasi.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void handleSignUp() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (authController == null) {
            JOptionPane.showMessageDialog(this,
                    "AuthController belum diinisialisialisasi. Tidak bisa mendaftar.",
                    "Error Sistem", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Validasi Input di Sisi View ---
        if (!InputValidator.isNonEmpty(username, "Username")) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocusInWindow();
            return;
        }
        if (!InputValidator.hasMinLength(username, 3)) {
            JOptionPane.showMessageDialog(this, "Username minimal 3 karakter.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocusInWindow();
            return;
        }
        if (!InputValidator.isNonEmpty(password, "Password")) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocusInWindow();
            return;
        }
        if (!InputValidator.hasMinLength(password, 6)) {
            JOptionPane.showMessageDialog(this, "Password minimal 6 karakter.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocusInWindow();
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password dan Konfirmasi Password tidak cocok.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.requestFocusInWindow();
            return;
        }
        if (role == null || (!role.equals(AppConstants.ROLE_ADMIN) && !role.equals(AppConstants.ROLE_CUSTOMER))) {
            JOptionPane.showMessageDialog(this, "Role tidak valid.", "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // --- Akhir Validasi Input di Sisi View ---

        authController.handleRegistration(username, password, confirmPassword, role, fullName, this);
    }

    // Untuk dijalankan secara independen saat development (opsional)
    public static void main(String[] args) {
        UIManagerSetup.setupLookAndFeel();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SignUpView signUpView = new SignUpView();
                signUpView.setVisible(true);
            }
        });
    }
}
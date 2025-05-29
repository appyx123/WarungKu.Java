package com.warungkupos.view.auth;

import com.warungkupos.controller.AuthController; // Akan kita buat setelah ini
import com.warungkupos.model.User;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.UIManagerSetup;
import com.warungkupos.view.admin.AdminDashboardView; // Akan kita buat nanti
import com.warungkupos.view.customer.CustomerDashboardView; // Akan kita buat nanti

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton; // Tombol untuk navigasi ke SignUpView

    private AuthController authController; // Controller untuk handle logic

    public LoginView() {
        // Inisialisasi AuthController (akan menggunakan AuthenticationService)
        // Kita akan buat AuthController setelah ini.
        // Untuk sementara, kita bisa buat dummy atau tunggu sampai AuthController siap.
        // authController = new AuthController(new AuthenticationServiceImpl()); // Contoh
        // Untuk sekarang, kita akan set controllernya nanti.

        setTitle(AppConstants.LOGIN_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 450)); // Ukuran minimum frame
        setLocationRelativeTo(null); // Tampilkan di tengah layar
        setLayout(new GridLayout(1, 2)); // Layout utama: 1 baris, 2 kolom

        initComponents();
        addListeners();
    }
    
    // Metode untuk meng-set controller dari luar jika diperlukan
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }


    private void initComponents() {
        // === Panel Kiri (Branding) ===
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        leftPanel.setLayout(new GridBagLayout()); // Untuk menengahkan konten
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridwidth = GridBagConstraints.REMAINDER;
        gbcLeft.fill = GridBagConstraints.HORIZONTAL;
        gbcLeft.insets = new Insets(10, 20, 10, 20);

        JLabel appNameLabel = new JLabel(AppConstants.APPLICATION_NAME);
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(appNameLabel, gbcLeft);

        JLabel appSloganLabel = new JLabel("Sistem Manajemen Toko Online Anda");
        appSloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        appSloganLabel.setForeground(Color.WHITE);
        appSloganLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(appSloganLabel, gbcLeft);
        
        // Tambahan pemisah atau dekorasi teks jika diinginkan
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(200,200,200,100)); // Warna separator semi-transparan
        gbcLeft.insets = new Insets(20, 50, 20, 50); // Atur margin separator
        leftPanel.add(separator, gbcLeft);


        // === Panel Kanan (Form Login) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(AppConstants.COLOR_WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(10, 10, 10, 10);
        gbcRight.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginTitleLabel = new JLabel("LOGIN PENGGUNA");
        loginTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitleLabel.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        loginTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.gridwidth = 2; // Span 2 kolom
        gbcRight.weightx = 1.0;
        gbcRight.insets = new Insets(20, 10, 20, 10);
        rightPanel.add(loginTitleLabel, gbcRight);

        gbcRight.gridwidth = 1; // Kembali ke 1 kolom
        gbcRight.insets = new Insets(10, 10, 10, 10); // Reset insets

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 1;
        gbcRight.anchor = GridBagConstraints.WEST;
        rightPanel.add(usernameLabel, gbcRight);

        usernameField = new JTextField(20); // Lebar field sekitar 20 karakter
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 1;
        gbcRight.weightx = 1.0; // Agar field bisa melebar
        rightPanel.add(usernameField, gbcRight);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 0;
        gbcRight.gridy = 2;
        gbcRight.weightx = 0; // Reset weightx
        gbcRight.anchor = GridBagConstraints.WEST;
        rightPanel.add(passwordLabel, gbcRight);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbcRight.gridx = 1;
        gbcRight.gridy = 2;
        gbcRight.weightx = 1.0;
        rightPanel.add(passwordField, gbcRight);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false); // Hilangkan border fokus
        gbcRight.gridx = 0;
        gbcRight.gridy = 3;
        gbcRight.gridwidth = 2; // Span 2 kolom
        gbcRight.anchor = GridBagConstraints.CENTER;
        gbcRight.fill = GridBagConstraints.NONE; // Agar tombol tidak melebar penuh
        gbcRight.insets = new Insets(20, 10, 5, 10); // Margin atas lebih besar
        rightPanel.add(loginButton, gbcRight);

        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Panel untuk teks dan tombol Sign Up
        signUpPanel.setBackground(AppConstants.COLOR_WHITE); // Samakan background
        JLabel noAccountLabel = new JLabel("Belum punya akun? ");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        signUpButton = new JButton("Daftar di sini");
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signUpButton.setForeground(AppConstants.COLOR_PRIMARY_BLUE);
        // Styling agar terlihat seperti link
        signUpButton.setBorderPainted(false);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setOpaque(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpPanel.add(noAccountLabel);
        signUpPanel.add(signUpButton);
        
        gbcRight.gridx = 0;
        gbcRight.gridy = 4;
        gbcRight.gridwidth = 2;
        gbcRight.anchor = GridBagConstraints.CENTER;
        gbcRight.insets = new Insets(5, 10, 20, 10);
        rightPanel.add(signUpPanel, gbcRight);

        // Menambahkan kedua panel ke frame utama
        add(leftPanel);
        add(rightPanel);
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Tambahkan listener untuk enter di password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pindahkan fokus ke password field jika enter ditekan di username field
                passwordField.requestFocusInWindow();
            }
        });


        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigasi ke SignUpView
                if (authController != null) {
                    authController.showSignUpView();
                    LoginView.this.dispose(); // Tutup LoginView
                } else {
                     JOptionPane.showMessageDialog(LoginView.this,
                        "AuthController belum diinisialisasi.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (authController == null) {
            JOptionPane.showMessageDialog(this,
                    "AuthController belum diinisialisasi. Tidak bisa login.",
                    "Error Sistem", JOptionPane.ERROR_MESSAGE);
            return;
        }

        authController.handleLogin(username, password, this);
    }
    
    public void openAdminDashboard() {
        AdminDashboardView adminView = new AdminDashboardView(); // Akan dibuat nanti
        adminView.setVisible(true);
        this.dispose();
    }

    public void openCustomerDashboard(User user) {
        CustomerDashboardView customerView = new CustomerDashboardView(user); // Akan dibuat nanti
        customerView.setVisible(true);
        this.dispose();
    }


    // Untuk dijalankan secara independen saat development (opsional)
    public static void main(String[] args) {
        UIManagerSetup.setupLookAndFeel(); // Panggil setup L&F
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoginView loginView = new LoginView();
                // Untuk testing tanpa AuthController dulu, bisa di-set null atau dummy
                // loginView.setAuthController(new AuthController(null)); // Dummy AuthController jika diperlukan
                loginView.setVisible(true);
            }
        });
    }
}
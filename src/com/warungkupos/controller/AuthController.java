package com.warungkupos.controller;

import com.warungkupos.model.User;
import com.warungkupos.service.AuthenticationService;
import com.warungkupos.service.AuthenticationException;
import com.warungkupos.service.ServiceException; // Jika diperlukan untuk error lain dari service
import com.warungkupos.util.AppConstants;
import com.warungkupos.view.auth.LoginView;
import com.warungkupos.view.auth.SignUpView;
// Dashboard views akan di-import saat dibutuhkan
// import com.warungkupos.view.admin.AdminDashboardView;
// import com.warungkupos.view.customer.CustomerDashboardView;

import javax.swing.JOptionPane;
import java.awt.EventQueue;

public class AuthController {

    private AuthenticationService authenticationService;
    private LoginView loginView; // Opsional, bisa juga dibuat on-demand
    private SignUpView signUpView; // Opsional, bisa juga dibuat on-demand

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void showLoginView() {
        // Pastikan GUI dijalankan di EDT
        EventQueue.invokeLater(() -> {
            if (loginView == null || !loginView.isDisplayable()) {
                loginView = new LoginView();
                loginView.setAuthController(this); // Set controller untuk view ini
            }
            loginView.setVisible(true);
            // Jika ada view lain yang terbuka (misal SignUpView), tutup
            if (signUpView != null && signUpView.isDisplayable()) {
                signUpView.dispose();
            }
        });
    }

    public void showSignUpView() {
        EventQueue.invokeLater(() -> {
            if (signUpView == null || !signUpView.isDisplayable()) {
                signUpView = new SignUpView();
                signUpView.setAuthController(this); // Set controller untuk view ini
            }
            signUpView.setVisible(true);
            // Jika ada view lain yang terbuka (misal LoginView), tutup
            if (loginView != null && loginView.isDisplayable()) {
                loginView.dispose();
            }
        });
    }

    public void handleLogin(String username, String password, LoginView currentView) {
        // Validasi input dasar bisa juga dilakukan di sini atau di View sebelum memanggil controller
        if (username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(currentView, "Username dan Password tidak boleh kosong.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = authenticationService.login(username, password);
            // Jika login berhasil
            JOptionPane.showMessageDialog(currentView, "Login berhasil! Selamat datang " + (user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername()) + ".", "Login Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Navigasi ke dashboard yang sesuai
            if (AppConstants.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
                currentView.openAdminDashboard(); // Panggil metode di LoginView
            } else if (AppConstants.ROLE_CUSTOMER.equalsIgnoreCase(user.getRole())) {
                currentView.openCustomerDashboard(user); // Panggil metode di LoginView
            } else {
                // Role tidak diketahui, mungkin tampilkan pesan error atau default dashboard
                 JOptionPane.showMessageDialog(currentView, "Role pengguna tidak dikenali.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
            // currentView.dispose(); // LoginView akan di-dispose oleh metode open...Dashboard() di dalamnya

        } catch (AuthenticationException e) {
            JOptionPane.showMessageDialog(currentView, e.getMessage(), "Login Gagal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Untuk error tak terduga lainnya (misal, masalah koneksi DB dari DAO)
            e.printStackTrace(); // Sebaiknya log error ini
            JOptionPane.showMessageDialog(currentView, "Terjadi kesalahan sistem: " + e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleRegistration(String username, String password, String confirmPassword, String role, String fullName, SignUpView currentView) {
        // Validasi input dasar
        if (username.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            JOptionPane.showMessageDialog(currentView, "Semua field yang wajib (Username, Password, Konfirmasi Password, Role) harus diisi.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(currentView, "Password dan Konfirmasi Password tidak cocok.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            authenticationService.register(username, password, role, fullName);
            // Jika registrasi berhasil
            JOptionPane.showMessageDialog(currentView, AppConstants.MSG_REGISTRATION_SUCCESS, "Registrasi Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            // Tutup SignUpView dan buka LoginView
            currentView.dispose();
            showLoginView();

        } catch (AuthenticationException e) {
            JOptionPane.showMessageDialog(currentView, e.getMessage(), "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace(); // Log error
            JOptionPane.showMessageDialog(currentView, "Terjadi kesalahan sistem saat registrasi: " + e.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
        }
    }
}
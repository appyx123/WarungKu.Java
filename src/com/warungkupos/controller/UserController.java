package com.warungkupos.controller;

import com.warungkupos.model.User;
import com.warungkupos.dao.UserDao;
import com.warungkupos.dao.impl.UserDaoImpl;
import com.warungkupos.service.AuthenticationService;
import com.warungkupos.service.AuthenticationException;
import com.warungkupos.service.ServiceException;
import com.warungkupos.view.admin.UserManagementPanel;
import com.warungkupos.util.PasswordUtil;
import com.warungkupos.dao.TransactionDao; // <--- PASTIKAN IMPORT INI ADA
import com.warungkupos.dao.impl.TransactionDaoImpl; // <--- PASTIKAN IMPORT INI ADA


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.List;

public class UserController {

    private UserManagementPanel view;
    private AuthenticationService authenticationService;
    private UserDao userDao;
    private TransactionDao transactionDao; // <--- FIELD BARU

    public UserController(UserManagementPanel view, AuthenticationService authenticationService, UserDao userDao) {
        this.view = view;
        this.authenticationService = authenticationService;
        this.userDao = userDao;
        this.transactionDao = new TransactionDaoImpl(); // <--- INISIALISASI BARU
        attachListeners();
        initializeView();
    }
    
    // Konstruktor default jika tidak ada DI
    public UserController(UserManagementPanel view, AuthenticationService authenticationService) {
        this(view, authenticationService, new UserDaoImpl()); 
        // this.transactionDao = new TransactionDaoImpl(); // Sudah diinisialisasi di konstruktor utama
    }


    private void initializeView() {
        loadUsers();
        view.clearForm(); 
        view.getUsernameField().setEditable(true);
    }

    private void attachListeners() {
        view.getAddButton().addActionListener(e -> addUser());
        view.getUpdateButton().addActionListener(e -> updateUser());
        view.getDeleteButton().addActionListener(e -> deleteUser());
        view.getResetPasswordButton().addActionListener(e -> resetUserPassword()); 
        view.getClearButton().addActionListener(e -> view.clearForm());

        view.getUserTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (view.getUserTable().getSelectedRow() != -1) {
                    view.fillFormFromSelectedTableRow();
                    view.getUsernameField().setEditable(false); 
                } else {
                    view.clearForm();
                    view.getUsernameField().setEditable(true);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            List<User> users = userDao.getAllUsers(); 
            view.displayUsers(users);
        } catch (Exception e) {
            view.showMessage("Gagal memuat daftar pengguna: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addUser() {
        User newUser = view.getUserFromForm();
        if (newUser == null) { 
            return;
        }

        try {
            authenticationService.register(
                newUser.getUsername(), 
                newUser.getPassword(), 
                newUser.getRole(), 
                newUser.getFullName()
            );
            view.showMessage("Pengguna '" + newUser.getUsername() + "' berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadUsers(); 
            view.clearForm();
        } catch (AuthenticationException ae) { 
            view.showMessage("Gagal menambah pengguna: " + ae.getMessage(), "Error Registrasi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat menambah pengguna: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateUser() {
        String userIdText = view.getUserIdField().getText();
        if (userIdText.isEmpty()) {
            view.showMessage("Pilih pengguna dari tabel untuk diupdate.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User updatedUser = view.getUserFromForm();
        if (updatedUser == null) { 
            return;
        }
        
        int userId;
        try {
            userId = Integer.parseInt(userIdText);
            updatedUser.setId(userId); 
        } catch (NumberFormatException e) {
            view.showMessage("ID Pengguna tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String newPassword = new String(view.getPasswordField().getPassword());
            if (!newPassword.isEmpty()) {
                String hashedPassword = com.warungkupos.util.PasswordUtil.hashPassword(newPassword); 
                updatedUser.setPassword(hashedPassword);
            } else {
                User existingUser = userDao.getUserById(userId);
                if (existingUser != null) {
                    updatedUser.setPassword(existingUser.getPassword());
                } else {
                    throw new ServiceException("Pengguna tidak ditemukan saat update.");
                }
            }

            boolean success = userDao.updateUser(updatedUser); 
            if (success) {
                view.showMessage("Pengguna '" + updatedUser.getUsername() + "' berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
                view.clearForm();
            } else {
                view.showMessage("Gagal memperbarui pengguna. Pengguna mungkin tidak ditemukan.", "Update Gagal", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ServiceException se) {
            view.showMessage("Gagal memperbarui pengguna: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Terjadi kesalahan sistem saat memperbarui pengguna: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void resetUserPassword() {
        String userIdText = view.getUserIdField().getText();
        if (userIdText.isEmpty()) {
            view.showMessage("Pilih pengguna dari tabel untuk reset password.", "Reset Password Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdText);
        } catch (NumberFormatException e) {
            view.showMessage("ID Pengguna tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newPassword = new String(view.getPasswordField().getPassword());
        if (!newPassword.isEmpty()) { 
            if (!com.warungkupos.util.InputValidator.hasMinLength(newPassword, 6)) { 
                view.showMessage("Password baru minimal 6 karakter.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                view.getPasswordField().requestFocusInWindow();
                return;
            }
        }
        if (newPassword.isEmpty()) {
            view.showMessage("Mohon masukkan password baru untuk direset.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            view.getPasswordField().requestFocusInWindow();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Anda yakin ingin mereset password pengguna '" + view.getUsernameField().getText() + "'?",
                "Konfirmasi Reset Password",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                User userToReset = userDao.getUserById(userId);
                if (userToReset == null) {
                    throw new ServiceException("Pengguna tidak ditemukan.");
                }
                String hashedPassword = com.warungkupos.util.PasswordUtil.hashPassword(newPassword); 
                userToReset.setPassword(hashedPassword);
                userToReset.setFullName(userToReset.getFullName()); 
                userToReset.setRole(userToReset.getRole());

                boolean success = userDao.updateUser(userToReset);
                if (success) {
                    view.showMessage("Password pengguna '" + userToReset.getUsername() + "' berhasil direset!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    view.clearForm(); 
                } else {
                    view.showMessage("Gagal mereset password pengguna.", "Reset Password Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ServiceException se) {
                view.showMessage("Gagal mereset password: " + se.getMessage(), "Error Servis", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat mereset password: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    private void deleteUser() {
        String userIdText = view.getUserIdField().getText();
        if (userIdText.isEmpty()) {
            view.showMessage("Pilih pengguna dari tabel untuk dihapus.", "Hapus Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdText);
        } catch (NumberFormatException e) {
            view.showMessage("ID Pengguna tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String usernameToDelete = view.getUsernameField().getText(); 

        int confirm = JOptionPane.showConfirmDialog(view,
                "Apakah Anda yakin ingin menghapus pengguna '" + usernameToDelete + "' (ID: " + userId + ")?\nOperasi ini tidak dapat dibatalkan!",
                "Konfirmasi Hapus Pengguna",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // <--- Pengecekan baru: Apakah user punya transaksi?
                if (transactionDao.hasTransactionsByUsername(usernameToDelete)) {
                    view.showMessage("Pengguna ini tidak dapat dihapus karena masih memiliki riwayat transaksi.", "Hapus Gagal", JOptionPane.ERROR_MESSAGE);
                    return; // Hentikan operasi jika ada transaksi
                }
                // <--- Akhir pengecekan baru

                boolean success = userDao.deleteUser(usernameToDelete); 
                if (success) {
                    view.showMessage("Pengguna '" + usernameToDelete + "' berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers(); 
                    view.clearForm();
                } else {
                    view.showMessage("Gagal menghapus pengguna. Pengguna mungkin tidak ditemukan.", "Hapus Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                view.showMessage("Terjadi kesalahan sistem saat menghapus pengguna: " + ex.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
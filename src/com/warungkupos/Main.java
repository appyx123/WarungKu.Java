// File: src/main/java/com/warungkupos/Main.java (MODIFIKASI)
package com.warungkupos;

import com.warungkupos.controller.AuthController;
import com.warungkupos.service.AuthenticationService;
import com.warungkupos.service.AuthenticationServiceImpl; // Implementasi service
import com.warungkupos.util.UIManagerSetup;
// import com.warungkupos.view.auth.LoginView; // Tidak perlu import LoginView lagi di sini

import java.awt.EventQueue;

public class Main {
    

    public static void main(String[] args) {
        UIManagerSetup.setupLookAndFeel();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Buat instance service dan controller
                    AuthenticationService authService = new AuthenticationServiceImpl();
                    AuthController authController = new AuthController(authService);
                    
                    // Mulai aplikasi dengan menampilkan LoginView melalui controller
                    authController.showLoginView();
                    
                } catch (Exception e) {
                    System.err.println("Gagal memulai aplikasi: " + e.getMessage());
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null, 
                       "Gagal memulai aplikasi.\n" + e.getMessage(), 
                       "Error Kritis", 
                       javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
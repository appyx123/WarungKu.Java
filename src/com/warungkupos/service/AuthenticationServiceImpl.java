package com.warungkupos.service; // Sesuai struktur Anda, tidak di sub-package impl

import com.warungkupos.dao.UserDao;
import com.warungkupos.dao.impl.UserDaoImpl; // Menggunakan implementasi DAO
import com.warungkupos.model.User;
import com.warungkupos.util.PasswordUtil; // Akan dibuat nanti

public class AuthenticationServiceImpl implements AuthenticationService {

    private UserDao userDao;

    // Konstruktor default yang menginisialisasi DAO.
    // Untuk proyek yang lebih besar, pertimbangkan Dependency Injection.
    public AuthenticationServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    // Konstruktor alternatif untuk memungkinkan injeksi DAO (berguna untuk testing)
    public AuthenticationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String username, String password) throws AuthenticationException, Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username tidak boleh kosong.");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password tidak boleh kosong.");
        }

        User user;
        try {
            user = userDao.getUserByUsername(username);
        } catch (Exception e) {
            // Tangani atau log SQLException dari DAO jika perlu, lalu lemparkan sebagai Exception umum
            throw new Exception("Gagal mengakses data pengguna: " + e.getMessage(), e);
        }

        if (user == null) {
            throw new AuthenticationException("Username tidak ditemukan.");
        }

        // Asumsikan PasswordUtil.verifyPassword adalah metode statis
        // dan akan kita buat nanti.
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new AuthenticationException("Password salah.");
        }

        // Login berhasil
        return user;
    }

    @Override
    public User register(String username, String password, String role, String fullName) throws AuthenticationException, Exception {
        // Validasi input dasar
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username tidak boleh kosong.");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password tidak boleh kosong.");
        }
        if (role == null || (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Customer"))) {
            throw new AuthenticationException("Role tidak valid. Harus 'Admin' atau 'Customer'.");
        }
        if (username.length() < 3) {
             throw new AuthenticationException("Username minimal 3 karakter.");
        }
        if (password.length() < 6) {
             throw new AuthenticationException("Password minimal 6 karakter.");
        }
        // Normalisasi nama lengkap jika kosong
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = ""; // Atau null jika kolom DB memperbolehkan dan itu preferensi Anda
        }


        try {
            // Cek apakah username sudah ada
            if (userDao.CekUser(username)) {
                throw new AuthenticationException("Username '" + username + "' sudah digunakan.");
            }

            // Hash password sebelum disimpan
            // Asumsikan PasswordUtil.hashPassword adalah metode statis
            String hashedPassword = PasswordUtil.hashPassword(password);

            User newUser = new User(username, hashedPassword, role, fullName);
            
            userDao.addUser(newUser); // addUser di UserDaoImpl sudah diatur untuk tabel Users
                                      // dengan kolom (username, password, role, full_name)

            // Ambil user yang baru ditambahkan untuk mendapatkan ID jika di-generate oleh DB
            // dan untuk memastikan data tersimpan dengan benar.
            User registeredUser = userDao.getUserByUsername(username); 
            if(registeredUser == null) {
                // Ini seharusnya tidak terjadi jika addUser berhasil tanpa error SQL
                throw new Exception("Gagal mengambil data pengguna setelah registrasi.");
            }
            return registeredUser;

        } catch (AuthenticationException ae) {
            throw ae; // Lemparkan kembali AuthenticationException
        } catch (Exception e) {
            // Tangani atau log SQLException dari DAO atau error lain
            throw new Exception("Terjadi kesalahan saat proses registrasi: " + e.getMessage(), e);
        }
    }
}
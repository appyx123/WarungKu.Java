package com.warungkupos.service;

import com.warungkupos.model.User;

public interface AuthenticationService {
    /**
     * Melakukan login pengguna.
     * @param username Username pengguna.
     * @param password Password pengguna (plain text).
     * @return Objek User jika login berhasil.
     * @throws AuthenticationException Jika login gagal (username tidak ditemukan, password salah).
     * @throws Exception Jika terjadi error lain (misalnya, masalah database dari DAO).
     */
    User login(String username, String password) throws AuthenticationException, Exception;

    /**
     * Melakukan registrasi pengguna baru.
     * @param username Username baru.
     * @param password Password baru (plain text).
     * @param role Peran pengguna ("Admin" atau "Customer").
     * @param fullName Nama lengkap pengguna (opsional).
     * @return Objek User yang baru diregistrasi.
     * @throws AuthenticationException Jika registrasi gagal (username sudah ada, input tidak valid).
     * @throws Exception Jika terjadi error lain (misalnya, masalah database dari DAO).
     */
    User register(String username, String password, String role, String fullName) throws AuthenticationException, Exception;
}
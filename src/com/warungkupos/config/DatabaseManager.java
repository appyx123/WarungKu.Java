package com.warungkupos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // Detail koneksi database Anda
    // TODO: Untuk aplikasi produksi, sebaiknya detail ini disimpan di file konfigurasi eksternal
    //       dan tidak di-hardcode di dalam kode.
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver"; // Untuk MySQL Connector/J 8.x+
    // private static final String DB_DRIVER = "com.mysql.jdbc.Driver"; // Untuk MySQL Connector/J 5.x
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko_online"; // Ganti 'toko_online' dengan nama database Anda
    private static final String DB_USERNAME = "root"; // Ganti dengan username database Anda
    private static final String DB_PASSWORD = ""; // Ganti dengan password database Anda

    private static Connection connection = null;

    // Blok statis untuk me-load driver JDBC saat kelas ini dimuat
    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Gagal memuat JDBC Driver: " + e.getMessage());
            // Dalam aplikasi GUI, ini sebaiknya ditampilkan sebagai dialog error yang lebih user-friendly
            // dan mungkin menghentikan aplikasi jika koneksi database adalah kritikal.
            throw new RuntimeException("Gagal memuat JDBC Driver. Aplikasi tidak dapat berjalan.", e);
        }
    }

    /**
     * Mendapatkan koneksi ke database.
     * Metode ini akan membuat koneksi baru jika belum ada atau jika koneksi sebelumnya ditutup/tidak valid.
     * Namun, untuk penggunaan di DAO yang sering, lebih baik setiap panggilan getConnection()
     * menghasilkan koneksi baru agar dikelola oleh pemanggil (misalnya dengan try-with-resources).
     * * Untuk aplikasi desktop sederhana, satu koneksi yang di-reuse bisa saja cukup,
     * tapi ini bisa menimbulkan masalah jika koneksi terputus atau tidak thread-safe.
     *
     * Versi yang lebih aman adalah selalu membuat koneksi baru:
     * public static Connection getConnection() throws SQLException {
     * return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
     * }
     * * Mari kita gunakan pendekatan di mana setiap panggilan menghasilkan koneksi baru
     * agar lebih aman dan dikelola oleh pemanggil (DAO/Service) menggunakan try-with-resources.
     *
     * @return Objek Connection yang baru.
     * @throws SQLException Jika terjadi error saat membuat koneksi.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    /**
     * Metode untuk menutup koneksi (jika Anda tidak menggunakan try-with-resources).
     * Namun, sangat direkomendasikan untuk menggunakan try-with-resources di mana koneksi digunakan.
     * @param conn Koneksi yang akan ditutup.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi: " + e.getMessage());
            }
        }
    }

    // Metode main untuk menguji koneksi (opsional)
    public static void main(String[] args) {
        try (Connection testConnection = DatabaseManager.getConnection()) {
            if (testConnection != null && !testConnection.isClosed()) {
                System.out.println("Koneksi ke database '" + DB_URL + "' berhasil!");
                // Anda bisa menambahkan query sederhana di sini untuk menguji lebih lanjut
            } else {
                System.err.println("Gagal membuat koneksi ke database.");
            }
        } catch (SQLException e) {
            System.err.println("Koneksi database gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
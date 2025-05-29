package com.warungkupos.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000; // Jumlah iterasi, bisa ditingkatkan untuk keamanan lebih
    private static final int KEY_LENGTH = 256;   // Panjang kunci dalam bit
    private static final int SALT_LENGTH = 16;   // Panjang salt dalam byte (128 bit)

    /**
     * Menghasilkan salt acak untuk hashing password.
     * @return byte array yang berisi salt.
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Melakukan hashing pada password plain text menggunakan salt yang diberikan.
     * @param password Password plain text.
     * @param salt Salt untuk hashing.
     * @return Hash password dalam bentuk byte array.
     * @throws NoSuchAlgorithmException Jika algoritma tidak ditemukan.
     * @throws InvalidKeySpecException Jika spesifikasi kunci tidak valid.
     */
    private static byte[] hash(char[] password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Menghash password plain text. Salt akan digenerate secara otomatis dan
     * digabungkan dengan hash password.
     * Format penyimpanan: Base64(salt):Base64(hashedPassword)
     * @param plainPassword Password plain text.
     * @return String yang berisi salt dan hash password, dipisahkan oleh titik dua.
     * @throws RuntimeException Jika terjadi error saat hashing.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        try {
            byte[] salt = generateSalt();
            byte[] hashedPassword = hash(plainPassword.toCharArray(), salt);
            // Gabungkan salt dan hash, lalu encode ke Base64
            // Format: salt:hash
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedHashedPassword = Base64.getEncoder().encodeToString(hashedPassword);
            return encodedSalt + ":" + encodedHashedPassword;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Sebaiknya log error ini
            throw new RuntimeException("Error saat hashing password: " + e.getMessage(), e);
        }
    }

    /**
     * Memverifikasi password plain text dengan storedPassword (yang berisi salt dan hash).
     * @param plainPassword Password plain text yang akan diverifikasi.
     * @param storedPassword String password yang tersimpan di database (format: Base64(salt):Base64(hash)).
     * @return true jika password cocok, false jika tidak.
     * @throws RuntimeException Jika terjadi error saat verifikasi.
     */
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || plainPassword.isEmpty() || 
            storedPassword == null || storedPassword.isEmpty()) {
            return false; // Atau throw IllegalArgumentException
        }

        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            // Format storedPassword tidak valid
            // Sebaiknya log warning/error di sini
            System.err.println("Format storedPassword tidak valid: " + storedPassword);
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

            byte[] actualHash = hash(plainPassword.toCharArray(), salt);

            // Bandingkan hash secara aman untuk mencegah timing attacks (meskipun untuk PBKDF2 mungkin kurang krusial
            // dibandingkan perbandingan hash langsung, ini adalah praktik yang baik)
            int diff = expectedHash.length ^ actualHash.length;
            for (int i = 0; i < expectedHash.length && i < actualHash.length; i++) {
                diff |= expectedHash[i] ^ actualHash[i];
            }
            return diff == 0;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            // IllegalArgumentException bisa terjadi jika Base64 string tidak valid
            // Sebaiknya log error ini
            System.err.println("Error saat verifikasi password: " + e.getMessage());
            return false; // Jika ada error saat proses, anggap tidak cocok
        }
    }

    // Contoh penggunaan (opsional, bisa dihapus atau dikomentari)
    public static void main(String[] args) {
        String passwordAsli = "Pa$$wOrd123";
        String hashedPassword = hashPassword(passwordAsli);
        System.out.println("Password Asli: " + passwordAsli);
        System.out.println("Hashed Password (Salt:Hash): " + hashedPassword);

        // Verifikasi
        System.out.println("Verifikasi dengan password benar: " + verifyPassword(passwordAsli, hashedPassword));
        System.out.println("Verifikasi dengan password salah: " + verifyPassword("passwordSalah", hashedPassword));
        System.out.println("Verifikasi dengan password salah lainnya: " + verifyPassword("Pa$$wOrd1234", hashedPassword));

        // Tes dengan input tidak valid
        // System.out.println("Verifikasi dengan stored password format salah: " + verifyPassword(passwordAsli, "saltSaja"));
        // System.out.println("Verifikasi dengan stored password base64 salah: " + verifyPassword(passwordAsli, "!!!!:####"));

    }
}
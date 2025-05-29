package com.warungkupos.util;

import java.util.regex.Pattern;

public class InputValidator {

    // Pola regex untuk validasi email sederhana
    // Untuk validasi email yang lebih ketat (sesuai RFC 5322), regex akan jauh lebih kompleks.
    // Pola ini cukup untuk kebanyakan kasus penggunaan umum.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Pola regex untuk username: alfanumerik, underscore, titik. Minimal 3 karakter.
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._]{3,}$"
    );
    
    // Pola regex untuk password: minimal 6 karakter, setidaknya satu huruf besar, 
    // satu huruf kecil, dan satu angka. Bisa ditambah karakter spesial jika perlu.
    // Ini contoh, bisa disesuaikan dengan kebijakan password Anda.
    // private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    //         "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"
    // );


    /**
     * Memeriksa apakah sebuah string tidak null dan tidak kosong (setelah di-trim).
     * @param input String yang akan divalidasi.
     * @param fieldName Nama field (untuk pesan error jika diperlukan, tidak digunakan di sini).
     * @return true jika string valid (tidak null dan tidak kosong), false sebaliknya.
     */
    public static boolean isNonEmpty(String input, String fieldName) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Memeriksa apakah sebuah string hanya berisi angka.
     * @param input String yang akan divalidasi.
     * @return true jika string valid (hanya angka dan tidak kosong), false sebaliknya.
     */
    public static boolean isNumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return input.trim().matches("\\d+");
    }
    
    /**
     * Memeriksa apakah sebuah string dapat diubah menjadi integer.
     * @param input String yang akan divalidasi.
     * @return true jika string bisa di-parse menjadi integer, false sebaliknya.
     */
    public static boolean isValidInteger(String input) {
        if (!isNonEmpty(input, "input angka")) return false;
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Memeriksa apakah sebuah string dapat diubah menjadi double.
     * @param input String yang akan divalidasi.
     * @return true jika string bisa di-parse menjadi double, false sebaliknya.
     */
    public static boolean isValidDouble(String input) {
        if (!isNonEmpty(input, "input angka desimal")) return false;
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Memeriksa apakah sebuah string dapat diubah menjadi BigDecimal.
     * @param input String yang akan divalidasi.
     * @return true jika string bisa di-parse menjadi BigDecimal, false sebaliknya.
     */
    public static boolean isValidBigDecimal(String input) {
        if (!isNonEmpty(input, "input angka desimal (BigDecimal)")) return false;
        try {
            new java.math.BigDecimal(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * Memeriksa apakah sebuah string memiliki panjang minimal tertentu.
     * @param input String yang akan divalidasi.
     * @param minLength Panjang minimal yang diizinkan.
     * @return true jika panjang string >= minLength, false sebaliknya.
     */
    public static boolean hasMinLength(String input, int minLength) {
        if (input == null) {
            return false;
        }
        return input.length() >= minLength;
    }
    
    /**
     * Memeriksa apakah sebuah string memiliki panjang dalam rentang tertentu.
     * @param input String yang akan divalidasi.
     * @param minLength Panjang minimal.
     * @param maxLength Panjang maksimal.
     * @return true jika panjang string berada dalam rentang [minLength, maxLength].
     */
    public static boolean hasLengthBetween(String input, int minLength, int maxLength) {
        if (input == null) {
            return false;
        }
        int length = input.length();
        return length >= minLength && length <= maxLength;
    }


    /**
     * Memeriksa apakah sebuah string cocok dengan format email sederhana.
     * @param email String email yang akan divalidasi.
     * @return true jika format email valid, false sebaliknya.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Memeriksa apakah sebuah username valid (alfanumerik, underscore, titik, min 3 karakter).
     * @param username String username yang akan divalidasi.
     * @return true jika format username valid, false sebaliknya.
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Memeriksa apakah sebuah password memenuhi kriteria tertentu (contoh).
     * Anda bisa mengaktifkan dan menyesuaikan PASSWORD_PATTERN jika perlu.
     * @param password String password yang akan divalidasi.
     * @param minLength Panjang minimal password.
     * @return true jika password memenuhi kriteria.
     */
    public static boolean isValidPasswordFormat(String password, int minLength) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() < minLength) {
            return false;
        }
        // Tambahkan pengecekan regex jika PASSWORD_PATTERN diaktifkan
        // return PASSWORD_PATTERN.matcher(password).matches();
        return true; // Validasi sederhana hanya berdasarkan panjang minimal untuk saat ini
    }


    // Contoh penggunaan (opsional, bisa dihapus atau dikomentari)
    public static void main(String[] args) {
        System.out.println("--- NonEmpty ---");
        System.out.println("Input 'test': " + isNonEmpty("test", "Test Field")); // true
        System.out.println("Input '  ': " + isNonEmpty("  ", "Test Field"));   // false
        System.out.println("Input null: " + isNonEmpty(null, "Test Field"));   // false

        System.out.println("\n--- Numeric & Integer ---");
        System.out.println("Input '123': " + isNumeric("123") + ", isValidInteger: " + isValidInteger("123")); // true, true
        System.out.println("Input '123.4': " + isNumeric("123.4") + ", isValidInteger: " + isValidInteger("123.4")); // false, false
        System.out.println("Input 'abc': " + isNumeric("abc") + ", isValidInteger: " + isValidInteger("abc"));   // false, false
        System.out.println("Input '-10': " + isNumeric("-10") + ", isValidInteger: " + isValidInteger("-10")); // false (isNumeric hanya digit), true

        System.out.println("\n--- Double & BigDecimal ---");
        System.out.println("Input '123.45': isValidDouble: " + isValidDouble("123.45") + ", isValidBigDecimal: " + isValidBigDecimal("123.45")); // true, true
        System.out.println("Input '123': isValidDouble: " + isValidDouble("123") + ", isValidBigDecimal: " + isValidBigDecimal("123")); // true, true

        System.out.println("\n--- Length ---");
        System.out.println("Input 'abcde', minLength 3: " + hasMinLength("abcde", 3)); // true
        System.out.println("Input 'ab', minLength 3: " + hasMinLength("ab", 3));     // false
        System.out.println("Input 'abcde', between 3-5: " + hasLengthBetween("abcde", 3, 5)); // true
        System.out.println("Input 'abcdef', between 3-5: " + hasLengthBetween("abcdef", 3, 5)); // false


        System.out.println("\n--- Email ---");
        System.out.println("Email 'test@example.com': " + isValidEmail("test@example.com"));     // true
        System.out.println("Email 'test.user@sub.example.co.id': " + isValidEmail("test.user@sub.example.co.id")); // true
        System.out.println("Email 'test@example': " + isValidEmail("test@example"));           // false
        System.out.println("Email 'test@.com': " + isValidEmail("test@.com"));                 // false
        System.out.println("Email '@example.com': " + isValidEmail("@example.com"));           // false

        System.out.println("\n--- Username ---");
        System.out.println("Username 'user123': " + isValidUsername("user123"));       // true
        System.out.println("Username 'user.name': " + isValidUsername("user.name"));   // true
        System.out.println("Username 'user_name': " + isValidUsername("user_name"));   // true
        System.out.println("Username 'us': " + isValidUsername("us"));                 // false (minimal 3)
        System.out.println("Username 'user!': " + isValidUsername("user!"));           // false (karakter tidak valid)

        System.out.println("\n--- Password Format (simple length check) ---");
        System.out.println("Password 'pass123', min 6: " + isValidPasswordFormat("pass123", 6)); // true
        System.out.println("Password 'pass', min 6: " + isValidPasswordFormat("pass", 6));     // false
    }
}
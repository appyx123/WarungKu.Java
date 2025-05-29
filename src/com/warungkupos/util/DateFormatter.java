package com.warungkupos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone; // Opsional, jika ingin spesifik timezone

public class DateFormatter {

    // Definisikan format tanggal yang umum digunakan sebagai konstanta
    // Format untuk tampilan di GUI (lebih mudah dibaca manusia)
    public static final String DISPLAY_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd-MM-yyyy";
    public static final String DISPLAY_TIME_FORMAT = "HH:mm:ss";

    // Format untuk penyimpanan atau pertukaran data (seringkali ISO 8601 atau mirip)
    public static final String STORAGE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String STORAGE_DATE_FORMAT = "yyyy-MM-dd";

    // TimeZone default bisa diatur di sini jika perlu konsistensi, misal TimeZone.getTimeZone("Asia/Makassar")
    // Jika tidak di-set, akan menggunakan TimeZone default JVM.
    // Untuk aplikasi desktop yang umumnya berjalan di zona waktu pengguna, default JVM biasanya OK.
    // private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC"); 

    /**
     * Memformat objek Date menjadi String menggunakan format default tampilan (dd-MM-yyyy HH:mm:ss).
     * @param date Objek Date yang akan diformat.
     * @return String representasi tanggal dan waktu, atau string kosong jika input date null.
     */
    public static String formatDateTimeForDisplay(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_TIME_FORMAT);
        // sdf.setTimeZone(DEFAULT_TIME_ZONE); // Uncomment jika ingin menggunakan timezone spesifik
        return sdf.format(date);
    }

    /**
     * Memformat objek Date menjadi String (hanya tanggal) menggunakan format default tampilan (dd-MM-yyyy).
     * @param date Objek Date yang akan diformat.
     * @return String representasi tanggal, atau string kosong jika input date null.
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Memformat objek Date menjadi String menggunakan format kustom.
     * @param date Objek Date yang akan diformat.
     * @param format Pola format tanggal (misalnya, "yyyy/MM/dd").
     * @return String representasi tanggal dan waktu, atau string kosong jika input date null.
     */
    public static String formatCustom(Date date, String format) {
        if (date == null || format == null || format.isEmpty()) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Mem-parse String tanggal dan waktu (format: yyyy-MM-dd HH:mm:ss) menjadi objek Date.
     * @param dateString String tanggal dan waktu.
     * @return Objek Date, atau null jika parsing gagal atau input string null/kosong.
     */
    public static Date parseDateTimeFromStorage(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(STORAGE_DATE_TIME_FORMAT);
            return sdf.parse(dateString.trim());
        } catch (ParseException e) {
            System.err.println("Gagal mem-parse tanggal-waktu dari storage: '" + dateString + "' - " + e.getMessage());
            return null; // Atau throw exception jika itu preferensi Anda
        }
    }
    
    /**
     * Mem-parse String tanggal (format: yyyy-MM-dd) menjadi objek Date.
     * @param dateString String tanggal.
     * @return Objek Date, atau null jika parsing gagal atau input string null/kosong.
     */
    public static Date parseDateFromStorage(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(STORAGE_DATE_FORMAT);
            return sdf.parse(dateString.trim());
        } catch (ParseException e) {
            System.err.println("Gagal mem-parse tanggal dari storage: '" + dateString + "' - " + e.getMessage());
            return null;
        }
    }

    /**
     * Mem-parse String tanggal menggunakan format kustom menjadi objek Date.
     * @param dateString String tanggal.
     * @param format Pola format tanggal yang digunakan oleh dateString.
     * @return Objek Date, atau null jika parsing gagal atau input string/format null/kosong.
     */
    public static Date parseCustom(String dateString, String format) {
        if (dateString == null || dateString.trim().isEmpty() || format == null || format.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateString.trim());
        } catch (ParseException e) {
            System.err.println("Gagal mem-parse tanggal kustom: '" + dateString + "' dengan format '" + format + "' - " + e.getMessage());
            return null;
        }
    }

    // Contoh penggunaan (opsional, bisa dihapus atau dikomentari)
    public static void main(String[] args) {
        Date now = new Date();

        System.out.println("--- Formatting ---");
        System.out.println("Current DateTime (Display): " + formatDateTimeForDisplay(now));
        System.out.println("Current Date (Display): " + formatDateForDisplay(now));
        System.out.println("Current DateTime (Storage): " + formatCustom(now, STORAGE_DATE_TIME_FORMAT));
        System.out.println("Current Date (Storage): " + formatCustom(now, STORAGE_DATE_FORMAT));
        System.out.println("Custom Format (E, dd MMM yyyy): " + formatCustom(now, "E, dd MMM yyyy HH:mm:ss z"));


        System.out.println("\n--- Parsing ---");
        String dateTimeStr = "2025-05-28 10:30:00";
        Date parsedDateTime = parseDateTimeFromStorage(dateTimeStr);
        if (parsedDateTime != null) {
            System.out.println("Parsed DateTime '" + dateTimeStr + "': " + formatDateTimeForDisplay(parsedDateTime));
        }

        String dateStr = "2025-12-31";
        Date parsedDate = parseDateFromStorage(dateStr);
        if (parsedDate != null) {
            System.out.println("Parsed Date '" + dateStr + "': " + formatDateForDisplay(parsedDate));
        }
        
        String customDateStr = "28/05/2025";
        Date parsedCustomDate = parseCustom(customDateStr, "dd/MM/yyyy");
        if(parsedCustomDate != null) {
            System.out.println("Parsed Custom Date '" + customDateStr + "': " + formatDateForDisplay(parsedCustomDate));
        }

        System.out.println("Parsing invalid date: " + parseDateTimeFromStorage("ini bukan tanggal"));
    }
}
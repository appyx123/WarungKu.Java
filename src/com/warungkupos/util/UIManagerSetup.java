package com.warungkupos.util;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font; // Opsional: untuk mengatur font default
import javax.swing.plaf.FontUIResource; // Opsional: untuk mengatur font default
import java.util.Enumeration; // Opsional: untuk mengatur font default

public class UIManagerSetup {

    private static final String NIMBUS_LF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    // Alternatif lain:
    // private static final String METAL_LF = UIManager.getCrossPlatformLookAndFeelClassName();
    // private static final String SYSTEM_LF = UIManager.getSystemLookAndFeelClassName();

    /**
     * Private constructor untuk mencegah instansiasi kelas utilitas.
     */
    private UIManagerSetup() {
        // Kelas ini tidak untuk diinstansiasi
    }

    /**
     * Mengatur Look and Feel global untuk aplikasi.
     * Metode ini sebaiknya dipanggil di awal method main() aplikasi Anda.
     */
    public static void setupLookAndFeel() {
        try {
            // Coba atur Nimbus Look and Feel (tampilan modern)
            UIManager.setLookAndFeel(NIMBUS_LF);

            // --- OPSIONAL: Mengatur Font Default Global ---
            // Jika Anda ingin font default yang berbeda dari bawaan Nimbus.
            // setGlobalFont(new Font("Segoe UI", Font.PLAIN, 13)); // Ganti dengan font dan ukuran yang Anda inginkan

        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Nimbus Look and Feel tidak didukung: " + e.getMessage());
            // Jika Nimbus gagal, coba atur System Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Gagal mengatur System Look and Feel: " + ex.getMessage());
                // Jika semua gagal, Swing akan menggunakan L&F default (biasanya Metal)
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Kelas Nimbus Look and Feel tidak ditemukan: " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("Gagal membuat instance Nimbus Look and Feel: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Gagal mengakses Nimbus Look and Feel: " + e.getMessage());
        }
    }

    /**
     * OPSIONAL: Metode untuk mengatur font default global untuk semua komponen Swing.
     * Perhatian: Ini bisa memiliki efek yang luas dan mungkin tidak selalu diinginkan
     * atau bekerja sempurna dengan semua Look and Feel. Gunakan dengan hati-hati.
     * @param font Objek Font yang akan dijadikan default.
     */
    public static void setGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }

    // Metode main untuk menguji (opsional, hanya untuk demonstrasi cepat)
    // public static void main(String[] args) {
    //     setupLookAndFeel(); // Panggil setup
    //
    //     // Buat JFrame sederhana untuk melihat hasilnya
    //     javax.swing.JFrame frame = new javax.swing.JFrame("Tes Look and Feel");
    //     frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    //     frame.setSize(400, 300);
    //     javax.swing.JPanel panel = new javax.swing.JPanel();
    //     panel.add(new javax.swing.JLabel("Halo Dunia!"));
    //     panel.add(new javax.swing.JTextField("Input teks..."));
    //     panel.add(new javax.swing.JButton("Tombol"));
    //     panel.add(new javax.swing.JCheckBox("Centang Saya"));
    //     panel.add(new javax.swing.JComboBox<>(new String[]{"Pilihan 1", "Pilihan 2"}));
    //     frame.add(panel);
    //     frame.setLocationRelativeTo(null);
    //     frame.setVisible(true);
    // }
}

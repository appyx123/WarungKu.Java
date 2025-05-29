// File: src/main/java/com/warungkupos/model/Transaction.java
package com.warungkupos.model;

import java.math.BigDecimal;
import java.util.Date; // Atau java.sql.Timestamp jika Anda preferensi spesifik untuk DB

public class Transaction {
    private int id;
    private Date transactionDate; // Tanggal dan waktu transaksi
    private BigDecimal totalAmount; // Total harga transaksi
    private String username; // Username customer atau kasir yang melakukan transaksi
    // Anda bisa menambahkan field lain seperti status, metode pembayaran, dll. jika perlu

    // Konstruktor Kosong
    public Transaction() {
    }

    // Konstruktor untuk membuat objek lengkap (misalnya saat mengambil dari DB)
    public Transaction(int id, Date transactionDate, BigDecimal totalAmount, String username) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.username = username;
    }

    // Konstruktor untuk menambah transaksi baru (ID biasanya auto-generated)
    public Transaction(Date transactionDate, BigDecimal totalAmount, String username) {
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.username = username;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", transactionDate=" + transactionDate +
               ", totalAmount=" + totalAmount +
               ", username='" + username + '\'' +
               '}';
    }
}
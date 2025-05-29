// File: src/main/java/com/warungkupos/model/TransactionDetail.java
package com.warungkupos.model;

import java.math.BigDecimal;

public class TransactionDetail {
    private int id; // Primary key untuk tabel TransactionDetails (opsional, bisa juga composite key)
    private int transactionId; // Foreign key ke tabel Transactions
    private int productId; // Foreign key ke tabel Products
    private int quantity;
    private BigDecimal unitPrice; // Harga satuan produk pada saat transaksi (penting untuk historis harga)
    private BigDecimal subtotal; // quantity * unitPrice

    // Opsional: untuk menampilkan informasi produk langsung
    private String productName;

    // Konstruktor Kosong
    public TransactionDetail() {
    }

    // Konstruktor utama
    public TransactionDetail(int id, int transactionId, int productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }
    
    // Konstruktor tanpa ID (untuk insert data baru dimana ID auto generated atau tidak ada PK tunggal)
    public TransactionDetail(int transactionId, int productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "TransactionDetail{" +
               "id=" + id +
               ", transactionId=" + transactionId +
               ", productId=" + productId +
               (productName != null ? ", productName='" + productName + '\'' : "") +
               ", quantity=" + quantity +
               ", unitPrice=" + unitPrice +
               ", subtotal=" + subtotal +
               '}';
    }
}
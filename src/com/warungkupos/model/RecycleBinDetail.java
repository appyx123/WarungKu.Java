// File: src/main/java/com/warungkupos/model/RecycleBinDetail.java
package com.warungkupos.model;

import java.math.BigDecimal;

public class RecycleBinDetail {
    private int id; // ID asli dari TransactionDetails, atau PK baru untuk tabel RecycleBinDetails
    private int recycleBinTransactionId; // Foreign key ke tabel RecycleBinTransactions (merujuk ke ID asli transaksi)
    private int originalProductId;
    private int originalQuantity;
    private BigDecimal originalUnitPrice;
    private BigDecimal originalSubtotal;

    // Opsional: untuk menampilkan informasi produk langsung
    private String originalProductName;

    // Konstruktor Kosong
    public RecycleBinDetail() {
    }

    // Konstruktor untuk membuat objek lengkap
    public RecycleBinDetail(int id, int recycleBinTransactionId, int originalProductId, String originalProductName, int originalQuantity, BigDecimal originalUnitPrice, BigDecimal originalSubtotal) {
        this.id = id;
        this.recycleBinTransactionId = recycleBinTransactionId;
        this.originalProductId = originalProductId;
        this.originalProductName = originalProductName;
        this.originalQuantity = originalQuantity;
        this.originalUnitPrice = originalUnitPrice;
        this.originalSubtotal = originalSubtotal;
    }
    
    // Konstruktor tanpa product name jika diambil dari query terpisah
     public RecycleBinDetail(int id, int recycleBinTransactionId, int originalProductId, int originalQuantity, BigDecimal originalUnitPrice, BigDecimal originalSubtotal) {
        this.id = id;
        this.recycleBinTransactionId = recycleBinTransactionId;
        this.originalProductId = originalProductId;
        this.originalQuantity = originalQuantity;
        this.originalUnitPrice = originalUnitPrice;
        this.originalSubtotal = originalSubtotal;
    }


    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecycleBinTransactionId() {
        return recycleBinTransactionId;
    }

    public void setRecycleBinTransactionId(int recycleBinTransactionId) {
        this.recycleBinTransactionId = recycleBinTransactionId;
    }

    public int getOriginalProductId() {
        return originalProductId;
    }

    public void setOriginalProductId(int originalProductId) {
        this.originalProductId = originalProductId;
    }

    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public BigDecimal getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public BigDecimal getOriginalSubtotal() {
        return originalSubtotal;
    }

    public void setOriginalSubtotal(BigDecimal originalSubtotal) {
        this.originalSubtotal = originalSubtotal;
    }

    public String getOriginalProductName() {
        return originalProductName;
    }

    public void setOriginalProductName(String originalProductName) {
        this.originalProductName = originalProductName;
    }

    @Override
    public String toString() {
        return "RecycleBinDetail{" +
               "id=" + id +
               ", recycleBinTransactionId=" + recycleBinTransactionId +
               ", originalProductId=" + originalProductId +
               (originalProductName != null ? ", originalProductName='" + originalProductName + '\'' : "") +
               ", originalQuantity=" + originalQuantity +
               ", originalUnitPrice=" + originalUnitPrice +
               ", originalSubtotal=" + originalSubtotal +
               '}';
    }
}
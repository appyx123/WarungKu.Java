// File: src/main/java/com/warungkupos/model/RecycleBinTransaction.java
package com.warungkupos.model;

import java.math.BigDecimal;
import java.util.Date; // Atau java.sql.Timestamp

public class RecycleBinTransaction {
    private int id; // ID asli dari tabel Transactions sebelum dihapus
    private Date originalTransactionDate;
    private BigDecimal originalTotalAmount;
    private String originalUsername;
    private Date deletedDate; // Tanggal kapan transaksi ini dihapus
    private String deletedBy; // Username admin yang menghapus (opsional)

    // Konstruktor Kosong
    public RecycleBinTransaction() {
    }

    // Konstruktor untuk membuat objek lengkap
    public RecycleBinTransaction(int id, Date originalTransactionDate, BigDecimal originalTotalAmount, String originalUsername, Date deletedDate, String deletedBy) {
        this.id = id;
        this.originalTransactionDate = originalTransactionDate;
        this.originalTotalAmount = originalTotalAmount;
        this.originalUsername = originalUsername;
        this.deletedDate = deletedDate;
        this.deletedBy = deletedBy;
    }
    
    // Konstruktor minimal jika deletedBy tidak disimpan
    public RecycleBinTransaction(int id, Date originalTransactionDate, BigDecimal originalTotalAmount, String originalUsername, Date deletedDate) {
        this.id = id;
        this.originalTransactionDate = originalTransactionDate;
        this.originalTotalAmount = originalTotalAmount;
        this.originalUsername = originalUsername;
        this.deletedDate = deletedDate;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOriginalTransactionDate() {
        return originalTransactionDate;
    }

    public void setOriginalTransactionDate(Date originalTransactionDate) {
        this.originalTransactionDate = originalTransactionDate;
    }

    public BigDecimal getOriginalTotalAmount() {
        return originalTotalAmount;
    }

    public void setOriginalTotalAmount(BigDecimal originalTotalAmount) {
        this.originalTotalAmount = originalTotalAmount;
    }

    public String getOriginalUsername() {
        return originalUsername;
    }

    public void setOriginalUsername(String originalUsername) {
        this.originalUsername = originalUsername;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String toString() {
        return "RecycleBinTransaction{" +
               "id=" + id +
               ", originalTransactionDate=" + originalTransactionDate +
               ", originalTotalAmount=" + originalTotalAmount +
               ", originalUsername='" + originalUsername + '\'' +
               ", deletedDate=" + deletedDate +
               (deletedBy != null ? ", deletedBy='" + deletedBy + '\'' : "") +
               '}';
    }
}
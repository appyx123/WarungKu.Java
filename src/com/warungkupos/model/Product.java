// File: src/main/java/com/warungkupos/model/Product.java
package com.warungkupos.model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private BigDecimal price;
    private int stock;
    private int categoryId; // Foreign key ke tabel Categories
    private String categoryName; // Opsional, untuk menampilkan nama kategori saat join
    private int supplierId; // <--- FIELD BARU: Foreign key ke tabel Suppliers
    private String supplierName; // <--- FIELD BARU: Opsional, untuk menampilkan nama supplier saat join

    // Konstruktor Kosong
    public Product() {
    }

    // Konstruktor untuk membuat objek lengkap (misalnya saat mengambil dari DB dengan join)
    // <--- KONSTRUKTOR DIPERBARUI
    public Product(int id, String name, BigDecimal price, int stock, int categoryId, String categoryName, int supplierId, String supplierName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.supplierId = supplierId; // <--- FIELD BARU
        this.supplierName = supplierName; // <--- FIELD BARU
    }
    
    // Konstruktor untuk membuat objek saat mengambil dari tabel Product saja (tanpa join category/supplier name)
    // <--- KONSTRUKTOR DIPERBARUI
    public Product(int id, String name, BigDecimal price, int stock, int categoryId, int supplierId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.supplierId = supplierId; // <--- FIELD BARU
    }

    // Konstruktor untuk menambah produk baru (ID biasanya auto-generated)
    // <--- KONSTRUKTOR DIPERBARUI
    public Product(String name, BigDecimal price, int stock, int categoryId, int supplierId) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.supplierId = supplierId; // <--- FIELD BARU
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // <--- GETTER DAN SETTER BARU
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    // <--- AKHIR GETTER DAN SETTER BARU

    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", categoryId=" + categoryId +
               (categoryName != null ? ", categoryName='" + categoryName + '\'' : "") +
               ", supplierId=" + supplierId + // <--- Ditambahkan ke toString
               (supplierName != null ? ", supplierName='" + supplierName + '\'' : "") + // <--- Ditambahkan ke toString
               '}';
    }
}
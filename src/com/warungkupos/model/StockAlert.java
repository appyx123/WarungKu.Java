// File: src/main/java/com/warungkupos/model/StockAlert.java
package com.warungkupos.model;

public class StockAlert {
    private int productId;
    private String productName;
    private int currentStock;
    private int minimumStockLevel; // Level stok minimum yang memicu alert
    private String categoryName; // Opsional, untuk info tambahan

    // Konstruktor Kosong
    public StockAlert() {
    }

    // Konstruktor untuk membuat objek lengkap
    public StockAlert(int productId, String productName, String categoryName, int currentStock, int minimumStockLevel) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.minimumStockLevel = minimumStockLevel;
    }
    
    // Konstruktor tanpa categoryName
    public StockAlert(int productId, String productName, int currentStock, int minimumStockLevel) {
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.minimumStockLevel = minimumStockLevel;
    }


    // Getter dan Setter
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(int minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "StockAlert{" +
               "productId=" + productId +
               ", productName='" + productName + '\'' +
               (categoryName != null ? ", categoryName='" + categoryName + '\'' : "") +
               ", currentStock=" + currentStock +
               ", minimumStockLevel=" + minimumStockLevel +
               '}';
    }
}
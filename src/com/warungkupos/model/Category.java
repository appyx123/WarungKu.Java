// File: src/main/java/com/warungkupos/model/Category.java
package com.warungkupos.model;

public class Category {
    private int id;
    private String name;

    // Konstruktor Kosong
    public Category() {
    }

    // Konstruktor dengan id dan nama (untuk mengambil data dari DB)
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Konstruktor dengan nama saja (untuk membuat kategori baru dimana id auto generated)
    public Category(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        // Ini penting agar JComboBox bisa menampilkan nama kategori dengan benar
        return name; 
    }
}
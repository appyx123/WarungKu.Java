// File: src/main/java/com/warungkupos/model/Supplier.java
package com.warungkupos.model;

public class Supplier {
    private int id;
    private String name;
    private String contact; // Bisa nomor telepon atau email
    private String address;

    // Konstruktor Kosong
    public Supplier() {
    }

    // Konstruktor untuk membuat objek lengkap (misalnya saat mengambil dari DB)
    public Supplier(int id, String name, String contact, String address) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    // Konstruktor untuk menambah supplier baru (ID biasanya auto-generated)
    public Supplier(String name, String contact, String address) {
        this.name = name;
        this.contact = contact;
        this.address = address;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        // Berguna untuk JComboBox jika ingin menampilkan nama supplier
        return name;
    }
}
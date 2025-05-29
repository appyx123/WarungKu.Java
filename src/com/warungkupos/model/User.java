// File: src/main/java/com/warungkupos/model/User.java
package com.warungkupos.model;

public class User {
    private int id; // Opsional, jika Anda punya ID auto-increment di tabel Users
    private String username;
    private String password; // Ini akan berupa hash password, bukan plain text
    private String role; // Misalnya "Admin" atau "Customer"
    private String fullName; // Opsional, sesuai deskripsi awal

    // Konstruktor Kosong (diperlukan oleh beberapa framework/library)
    public User() {
    }

    // Konstruktor dengan parameter dasar
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Konstruktor dengan semua parameter termasuk fullName dan id
    public User(int id, String username, String password, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Konstruktor tanpa id (untuk kasus insert data baru dimana id auto generated)
    public User(String username, String password, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               ", fullName='" + fullName + '\'' +
               '}';
    }
}
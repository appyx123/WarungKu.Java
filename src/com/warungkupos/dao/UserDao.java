// File: src/main/java/com/warungkupos/dao/UserDao.java
package com.warungkupos.dao;

import com.warungkupos.model.User;
import java.util.List;

public interface UserDao {
    void addUser(User user) throws Exception; // Menggunakan Exception untuk error SQL
    User getUserByUsername(String username) throws Exception;
    User getUserById(int id) throws Exception; // Jika diperlukan
    List<User> getAllUsers() throws Exception;
    boolean updateUser(User user) throws Exception;
    boolean deleteUser(String username) throws Exception;
    boolean CekUser(String username) throws Exception; // Untuk memeriksa apakah username sudah ada
}
// File: src/main/java/com/warungkupos/dao/CategoryDao.java
package com.warungkupos.dao;

import com.warungkupos.model.Category;
import java.util.List;

public interface CategoryDao {
    void addCategory(Category category) throws Exception;
    Category getCategoryById(int id) throws Exception;
    Category getCategoryByName(String name) throws Exception;
    List<Category> getAllCategories() throws Exception;
    boolean updateCategory(Category category) throws Exception;
    boolean deleteCategory(int id) throws Exception;
    boolean isCategoryInUse(int categoryId) throws Exception; // Untuk cek sebelum delete
}
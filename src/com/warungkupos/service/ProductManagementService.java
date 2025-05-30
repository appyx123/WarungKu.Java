package com.warungkupos.service;

import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier;
import java.util.List;

public interface ProductManagementService {

    // --- Category Management ---
    void addCategory(Category category) throws ServiceException, Exception;
    Category getCategoryById(int categoryId) throws Exception;
    List<Category> getAllCategories() throws Exception;
    void updateCategory(Category category) throws ServiceException, Exception;
    void deleteCategory(int categoryId) throws ServiceException, Exception;

    // --- Supplier Management ---
    void addSupplier(Supplier supplier) throws ServiceException, Exception;
    Supplier getSupplierById(int supplierId) throws Exception;
    List<Supplier> getAllSuppliers() throws Exception;
    void updateSupplier(Supplier supplier) throws ServiceException, Exception;
    // deleteSupplier sekarang akan memanggil pengecekan isSupplierInUse di ProductDao
    void deleteSupplier(int supplierId) throws ServiceException, Exception;

    // --- Product Management ---
    void addProduct(Product product) throws ServiceException, Exception;
    Product getProductById(int productId) throws Exception;
    List<Product> getAllProducts() throws Exception;
    List<Product> searchProducts(String keyword) throws Exception;
    List<Product> getProductsByCategoryId(int categoryId) throws Exception;
    void updateProduct(Product product) throws ServiceException, Exception;
    void deleteProduct(int productId) throws ServiceException, Exception;
    void updateProductStock(int productId, int newStock) throws ServiceException, Exception;
}
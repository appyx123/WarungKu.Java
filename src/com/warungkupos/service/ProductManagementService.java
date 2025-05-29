package com.warungkupos.service;

import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier;
import java.util.List;

public interface ProductManagementService {

    // --- Category Management ---
    /**
     * Menambahkan kategori baru.
     * @param category Objek Category yang akan ditambahkan.
     * @throws ServiceException Jika nama kategori sudah ada atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void addCategory(Category category) throws ServiceException, Exception;
    Category getCategoryById(int categoryId) throws Exception;
    List<Category> getAllCategories() throws Exception;
    /**
     * Memperbarui kategori yang ada.
     * @param category Objek Category dengan data terbaru.
     * @throws ServiceException Jika nama kategori baru sudah digunakan oleh kategori lain atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void updateCategory(Category category) throws ServiceException, Exception;
    /**
     * Menghapus kategori.
     * @param categoryId ID kategori yang akan dihapus.
     * @throws ServiceException Jika kategori masih digunakan oleh produk atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void deleteCategory(int categoryId) throws ServiceException, Exception;

    // --- Supplier Management ---
    /**
     * Menambahkan supplier baru.
     * @param supplier Objek Supplier yang akan ditambahkan.
     * @throws ServiceException Jika nama supplier sudah ada atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void addSupplier(Supplier supplier) throws ServiceException, Exception;
    Supplier getSupplierById(int supplierId) throws Exception;
    List<Supplier> getAllSuppliers() throws Exception;
    /**
     * Memperbarui supplier yang ada.
     * @param supplier Objek Supplier dengan data terbaru.
     * @throws ServiceException Jika nama supplier baru sudah digunakan oleh supplier lain atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void updateSupplier(Supplier supplier) throws ServiceException, Exception;
    /**
     * Menghapus supplier.
     * @param supplierId ID supplier yang akan dihapus.
     * @throws ServiceException Jika supplier masih terkait (misalnya dengan produk, jika ada relasi) atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void deleteSupplier(int supplierId) throws ServiceException, Exception;

    // --- Product Management ---
    /**
     * Menambahkan produk baru.
     * @param product Objek Product yang akan ditambahkan.
     * @throws ServiceException Jika nama produk sudah ada atau kategori tidak valid, atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void addProduct(Product product) throws ServiceException, Exception;
    Product getProductById(int productId) throws Exception;
    List<Product> getAllProducts() throws Exception;
    List<Product> searchProducts(String keyword) throws Exception;
    List<Product> getProductsByCategoryId(int categoryId) throws Exception;
    /**
     * Memperbarui produk yang ada.
     * @param product Objek Product dengan data terbaru.
     * @throws ServiceException Jika nama produk baru sudah digunakan oleh produk lain, kategori tidak valid, atau terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void updateProduct(Product product) throws ServiceException, Exception;
    /**
     * Menghapus produk.
     * @param productId ID produk yang akan dihapus.
     * @throws ServiceException Jika terjadi error.
     * @throws Exception Error umum dari DAO.
     */
    void deleteProduct(int productId) throws ServiceException, Exception;
    /**
     * Memperbarui stok produk.
     * @param productId ID produk.
     * @param newStock Jumlah stok baru.
     * @throws ServiceException Jika produk tidak ditemukan atau stok baru tidak valid.
     * @throws Exception Error umum dari DAO.
     */
    void updateProductStock(int productId, int newStock) throws ServiceException, Exception;
}
package com.warungkupos.service; // Sesuai struktur Anda

import com.warungkupos.config.DatabaseManager;
import com.warungkupos.dao.CategoryDao;
import com.warungkupos.dao.ProductDao; // Pastikan ProductDao ini adalah versi yang sudah diupdate
import com.warungkupos.dao.SupplierDao;
import com.warungkupos.dao.impl.CategoryDaoImpl;
import com.warungkupos.dao.impl.ProductDaoImpl; // Pastikan implementasinya juga versi terbaru
import com.warungkupos.dao.impl.SupplierDaoImpl;
import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductManagementServiceImpl implements ProductManagementService {

    private final CategoryDao categoryDao;
    private final SupplierDao supplierDao;
    private final ProductDao productDao; // Ini harus versi yang sudah diupdate

    public ProductManagementServiceImpl() {
        this.categoryDao = new CategoryDaoImpl();
        this.supplierDao = new SupplierDaoImpl();
        this.productDao = new ProductDaoImpl(); // Pastikan ProductDaoImpl yang diinisialisasi adalah versi terbaru
    }

    public ProductManagementServiceImpl(CategoryDao categoryDao, SupplierDao supplierDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.supplierDao = supplierDao;
        this.productDao = productDao;
    }

    // --- Category Management Implementation ---
    @Override
    public void addCategory(Category category) throws ServiceException, Exception {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ServiceException("Nama kategori tidak boleh kosong.");
        }
        if (categoryDao.getCategoryByName(category.getName().trim()) != null) {
            throw new ServiceException("Kategori dengan nama '" + category.getName().trim() + "' sudah ada.");
        }
        category.setName(category.getName().trim());
        categoryDao.addCategory(category);
    }

    @Override
    public Category getCategoryById(int categoryId) throws Exception {
        return categoryDao.getCategoryById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() throws Exception {
        return categoryDao.getAllCategories();
    }

    @Override
    public void updateCategory(Category category) throws ServiceException, Exception {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ServiceException("Nama kategori tidak boleh kosong.");
        }
        Category existingCategoryWithSameName = categoryDao.getCategoryByName(category.getName().trim());
        if (existingCategoryWithSameName != null && existingCategoryWithSameName.getId() != category.getId()) {
            throw new ServiceException("Kategori dengan nama '" + category.getName().trim() + "' sudah digunakan oleh kategori lain.");
        }
        category.setName(category.getName().trim());
        if (!categoryDao.updateCategory(category)) {
            throw new ServiceException("Gagal memperbarui kategori. Kategori mungkin tidak ditemukan.");
        }
    }

    @Override
    public void deleteCategory(int categoryId) throws ServiceException, Exception {
        if (categoryDao.isCategoryInUse(categoryId)) {
            throw new ServiceException("Kategori tidak dapat dihapus karena masih digunakan oleh produk.");
        }
        if (!categoryDao.deleteCategory(categoryId)) {
            throw new ServiceException("Gagal menghapus kategori. Kategori mungkin tidak ditemukan.");
        }
    }

    // --- Supplier Management Implementation ---
    @Override
    public void addSupplier(Supplier supplier) throws ServiceException, Exception {
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ServiceException("Nama supplier tidak boleh kosong.");
        }
        if (supplierDao.getSupplierByName(supplier.getName().trim()) != null) {
             throw new ServiceException("Supplier dengan nama '" + supplier.getName().trim() + "' sudah ada.");
        }
        supplier.setName(supplier.getName().trim());
        supplierDao.addSupplier(supplier);
    }

    @Override
    public Supplier getSupplierById(int supplierId) throws Exception {
        return supplierDao.getSupplierById(supplierId);
    }

    @Override
    public List<Supplier> getAllSuppliers() throws Exception {
        return supplierDao.getAllSuppliers();
    }

    @Override
    public void updateSupplier(Supplier supplier) throws ServiceException, Exception {
         if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ServiceException("Nama supplier tidak boleh kosong.");
        }
        Supplier existingSupplierWithSameName = supplierDao.getSupplierByName(supplier.getName().trim());
        if (existingSupplierWithSameName != null && existingSupplierWithSameName.getId() != supplier.getId()) {
            throw new ServiceException("Supplier dengan nama '" + supplier.getName().trim() + "' sudah digunakan oleh supplier lain.");
        }
        supplier.setName(supplier.getName().trim());
        if(!supplierDao.updateSupplier(supplier)){
            throw new ServiceException("Gagal memperbarui supplier. Supplier mungkin tidak ditemukan.");
        }
    }

    @Override
    public void deleteSupplier(int supplierId) throws ServiceException, Exception {
        // <--- PERBAIKAN: Pengecekan apakah supplier sedang digunakan oleh produk
        if (productDao.isSupplierInUse(supplierId)) { // Menggunakan productDao
            throw new ServiceException("Supplier tidak dapat dihapus karena masih digunakan oleh produk.");
        }
        // <--- AKHIR PERBAIKAN

        if(!supplierDao.deleteSupplier(supplierId)){
             throw new ServiceException("Gagal menghapus supplier. Supplier mungkin tidak ditemukan.");
        }
    }

    // --- Product Management Implementation ---
    @Override
    public void addProduct(Product product) throws ServiceException, Exception {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ServiceException("Nama produk tidak boleh kosong.");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new ServiceException("Harga produk tidak valid.");
        }
        if (product.getStock() < 0) {
            throw new ServiceException("Stok produk tidak boleh negatif.");
        }
        if (categoryDao.getCategoryById(product.getCategoryId()) == null) {
            throw new ServiceException("Kategori untuk produk tidak ditemukan atau tidak valid.");
        }
        // Validasi Supplier: Pastikan supplier yang dipilih valid (ID tidak 0)
        if (product.getSupplierId() == 0 || supplierDao.getSupplierById(product.getSupplierId()) == null) {
             throw new ServiceException("Supplier untuk produk tidak ditemukan atau tidak valid.");
        }

        if (productDao.getProductByName(product.getName().trim()) != null) {
            throw new ServiceException("Produk dengan nama '" + product.getName().trim() + "' sudah ada.");
        }
        product.setName(product.getName().trim());
        productDao.addProduct(product);
    }

    @Override
    public Product getProductById(int productId) throws Exception {
        return productDao.getProductById(productId);
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        return productDao.getAllProducts();
    }
    
    @Override
    public List<Product> searchProducts(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDao.searchProducts(keyword.trim());
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) throws Exception {
        if (categoryDao.getCategoryById(categoryId) == null) {
            throw new ServiceException("Kategori dengan ID " + categoryId + " tidak ditemukan.");
        }
        return productDao.getProductsByCategoryId(categoryId);
    }

    @Override
    public void updateProduct(Product product) throws ServiceException, Exception {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ServiceException("Nama produk tidak boleh kosong.");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new ServiceException("Harga produk tidak valid.");
        }
        if (product.getStock() < 0) {
            throw new ServiceException("Stok produk tidak boleh negatif.");
        }
        if (categoryDao.getCategoryById(product.getCategoryId()) == null) {
            throw new ServiceException("Kategori untuk produk tidak ditemukan atau tidak valid.");
        }
        // Validasi Supplier: Pastikan supplier yang dipilih valid (ID tidak 0)
        if (product.getSupplierId() == 0 || supplierDao.getSupplierById(product.getSupplierId()) == null) {
             throw new ServiceException("Supplier untuk produk tidak ditemukan atau tidak valid.");
        }

        Product existingProductWithSameName = productDao.getProductByName(product.getName().trim());
        if (existingProductWithSameName != null && existingProductWithSameName.getId() != product.getId()) {
            throw new ServiceException("Produk dengan nama '" + product.getName().trim() + "' sudah digunakan oleh produk lain.");
        }
        product.setName(product.getName().trim());
        if(!productDao.updateProduct(product)){
            throw new ServiceException("Gagal memperbarui produk. Produk mungkin tidak ditemukan.");
        }
    }

    @Override
    public void deleteProduct(int productId) throws ServiceException, Exception {
        if(!productDao.deleteProduct(productId)){
            throw new ServiceException("Gagal menghapus produk. Produk mungkin tidak ditemukan.");
        }
    }
    
    @Override
    public void updateProductStock(int productId, int newStock) throws ServiceException, Exception {
        Connection conn = null;
        try {
            Product product = productDao.getProductById(productId);
            if (product == null) {
                throw new ServiceException("Produk dengan ID " + productId + " tidak ditemukan.");
            }
            if (newStock < 0) {
                throw new ServiceException("Stok baru tidak boleh negatif.");
            }

            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            if (!productDao.updateProductStock(productId, newStock, conn)) {
                throw new ServiceException("Gagal memperbarui stok produk di database.");
            }
            conn.commit();
        } catch (SQLException sqlEx) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) { }
            }
            throw new ServiceException("Gagal memperbarui stok produk karena kesalahan database: " + sqlEx.getMessage(), sqlEx);
        } catch (Exception e) {
             if (conn != null) { 
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) { }
            }
            if (e instanceof ServiceException) throw (ServiceException) e;
            throw new Exception("Gagal memperbarui stok produk: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { }
            }
        }
    }
}
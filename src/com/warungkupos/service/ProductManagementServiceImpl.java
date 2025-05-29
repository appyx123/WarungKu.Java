package com.warungkupos.service;

import com.warungkupos.config.DatabaseManager; // Tambahkan import ini
import com.warungkupos.dao.CategoryDao;
import com.warungkupos.dao.ProductDao;
import com.warungkupos.dao.SupplierDao;
import com.warungkupos.dao.impl.CategoryDaoImpl;
import com.warungkupos.dao.impl.ProductDaoImpl;
import com.warungkupos.dao.impl.SupplierDaoImpl;
import com.warungkupos.model.Category;
import com.warungkupos.model.Product;
import com.warungkupos.model.Supplier;

import java.sql.Connection; // Tambahkan import ini
import java.sql.SQLException; // Tambahkan import ini
import java.util.List;

public class ProductManagementServiceImpl implements ProductManagementService {

    private final CategoryDao categoryDao;
    private final SupplierDao supplierDao;
    private final ProductDao productDao;

    // Konstruktor default
    public ProductManagementServiceImpl() {
        this.categoryDao = new CategoryDaoImpl();
        this.supplierDao = new SupplierDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    // Konstruktor untuk dependency injection (lebih baik untuk testing)
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
        if (newStock < 0) {
            throw new ServiceException("Stok baru tidak boleh negatif.");
        }
        // Operasi update stok tunggal ini sebaiknya juga dikelola dalam transaksi
        Connection conn = null;
        try {
            Product product = productDao.getProductById(productId); // Baca info produk (bisa pakai koneksi sendiri DAO)
            if (product == null) {
                throw new ServiceException("Produk dengan ID " + productId + " tidak ditemukan.");
            }

            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            if (!productDao.updateProductStock(productId, newStock, conn)) { // Panggil versi DAO dengan Connection
                throw new ServiceException("Gagal memperbarui stok produk di database.");
            }
            conn.commit();
        } catch (SQLException sqlEx) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Log error rollback jika perlu
                }
            }
            throw new ServiceException("Gagal memperbarui stok produk karena kesalahan database: " + sqlEx.getMessage(), sqlEx);
        } catch (Exception e) { // Termasuk ServiceException dari getProductById
             if (conn != null) { // Jika error terjadi setelah koneksi dibuka tapi sebelum commit
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Log error rollback
                }
            }
            if (e instanceof ServiceException) throw (ServiceException) e; // Lemparkan kembali ServiceException
            throw new Exception("Gagal memperbarui stok produk: " + e.getMessage(), e); // Lemparkan sebagai Exception umum
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan ke default
                    conn.close();
                } catch (SQLException ex) {
                    // Log error penutupan koneksi
                }
            }
        }
    }
}
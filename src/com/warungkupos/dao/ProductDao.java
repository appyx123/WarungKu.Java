// File: src/main/java/com/warungkupos/dao/ProductDao.java
package com.warungkupos.dao;

import com.warungkupos.model.Product;
import java.sql.Connection;
import java.util.List;

public interface ProductDao {
    // addProduct sekarang menerima Product yang memiliki supplierId
    void addProduct(Product product) throws Exception;

    // getProductById sekarang akan mengembalikan Product dengan categoryName dan supplierName
    Product getProductById(int id) throws Exception;

    // getProductByName juga akan mengembalikan Product dengan categoryName dan supplierName
    Product getProductByName(String name) throws Exception;

    // getAllProducts akan mengembalikan Product dengan categoryName dan supplierName
    List<Product> getAllProducts() throws Exception;

    // getProductsByCategoryId juga akan mengembalikan Product dengan categoryName dan supplierName
    List<Product> getProductsByCategoryId(int categoryId) throws Exception;

    // searchProducts juga akan mengembalikan Product dengan categoryName dan supplierName
    List<Product> searchProducts(String keyword) throws Exception;

    // updateProduct sekarang menerima Product yang memiliki supplierId
    boolean updateProduct(Product product) throws Exception;

    boolean deleteProduct(int id) throws Exception;

    // updateProductStock (metode ini tidak berubah karena hanya memengaruhi stok)
    boolean updateProductStock(int productId, int newStock, Connection conn) throws Exception;
}
// File: src/main/java/com/warungkupos/dao/SupplierDao.java
package com.warungkupos.dao;

import com.warungkupos.model.Supplier;
import java.util.List;

public interface SupplierDao {
    void addSupplier(Supplier supplier) throws Exception;
    Supplier getSupplierById(int id) throws Exception;
    Supplier getSupplierByName(String name) throws Exception; // Berguna untuk validasi
    List<Supplier> getAllSuppliers() throws Exception;
    boolean updateSupplier(Supplier supplier) throws Exception;
    boolean deleteSupplier(int id) throws Exception;
    boolean isSupplierInUse(int supplierId) throws Exception; // Opsional: Cek jika supplier terkait dengan produk atau entitas lain
}
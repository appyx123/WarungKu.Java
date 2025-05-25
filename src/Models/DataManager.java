package Models;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private Connection conn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko_online1?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public DataManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal menginisialisasi database: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password, String role) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean registerUser(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                return false;
            }
            throw e;
        }
    }

    public void addProduct(String name, double price, int stock, int categoryId) throws SQLException {
        if (name.isEmpty()) throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (price < 0) throw new IllegalArgumentException("Harga tidak boleh negatif");
        if (stock < 0) throw new IllegalArgumentException("Stok tidak boleh negatif");
        if (!categoryExists(categoryId)) throw new IllegalArgumentException("ID Kategori tidak valid");

        String sql = "INSERT INTO Products (name, price, stock, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, categoryId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateProduct(int id, String name, double price, int stock, int categoryId) throws SQLException {
        if (name.isEmpty()) throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (price < 0) throw new IllegalArgumentException("Harga tidak boleh negatif");
        if (stock < 0) throw new IllegalArgumentException("Stok tidak boleh negatif");
        if (!categoryExists(categoryId)) throw new IllegalArgumentException("ID Kategori tidak valid");
        if (!productExists(id)) throw new IllegalArgumentException("ID Produk tidak ditemukan");

        String sql = "UPDATE Products SET name = ?, price = ?, stock = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, categoryId);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteProduct(int id) throws SQLException {
        if (!productExists(id)) throw new IllegalArgumentException("ID Produk tidak ditemukan");

        String sql = "DELETE FROM Products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<Product> getProducts() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return products;
    }

    public int checkStock(int productId) throws SQLException {
        String sql = "SELECT stock FROM Products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean productExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM Products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void addCategory(String name) throws SQLException {
        if (name.isEmpty()) throw new IllegalArgumentException("Nama tidak boleh kosong");

        String sql = "INSERT INTO Categories (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateCategory(int id, String name) throws SQLException {
        if (name.isEmpty()) throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (!categoryExists(id)) throw new IllegalArgumentException("ID Kategori tidak ditemukan");

        String sql = "UPDATE Categories SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteCategory(int id) throws SQLException {
        String sql = "SELECT 1 FROM Products WHERE category_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                throw new IllegalArgumentException("Tidak dapat menghapus kategori yang terkait dengan produk");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        sql = "DELETE FROM Categories WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<Category> getCategories() throws SQLException {
        ArrayList<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return categories;
    }

    private boolean categoryExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM Categories WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void addTransaction(Map<Integer, Integer> productQuantities, String username) throws SQLException {
        conn.setAutoCommit(false);
        try {
            double total = 0;
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();
                String sql = "SELECT price, stock FROM Products WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, productId);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("ID Produk " + productId + " tidak ditemukan");
                    }
                    if (rs.getInt("stock") < quantity) {
                        throw new IllegalArgumentException("Stok tidak cukup untuk produk ID " + productId);
                    }
                    total += rs.getDouble("price") * quantity;
                }
            }

            String sql = "INSERT INTO Transactions (date, total, username) VALUES (NOW(), ?, ?)";
            int transactionId;
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, total);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                rs.next();
                transactionId = rs.getInt(1);
            }

            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();
                sql = "UPDATE Products SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, quantity);
                    pstmt.setInt(2, productId);
                    pstmt.executeUpdate();
                }
                sql = "INSERT INTO TransactionDetails (transaction_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, transactionId);
                    pstmt.setInt(2, productId);
                    pstmt.setInt(3, quantity);
                    pstmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void deleteTransaction(int id) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sql = "SELECT date, total, username FROM Transactions WHERE id = ?";
            Timestamp date;
            double total;
            String username;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    throw new IllegalArgumentException("ID Transaksi tidak ditemukan");
                }
                date = rs.getTimestamp("date");
                total = rs.getDouble("total");
                username = rs.getString("username");
            }

            List<TransactionDetail> details = new ArrayList<>();
            sql = "SELECT product_id, quantity FROM TransactionDetails WHERE transaction_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    details.add(new TransactionDetail(id, rs.getInt("product_id"), rs.getInt("quantity")));
                }
            }

            sql = "INSERT INTO RecycleBin (id, date, total, username) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.setTimestamp(2, date);
                pstmt.setDouble(3, total);
                pstmt.setString(4, username);
                pstmt.executeUpdate();
            }

            for (TransactionDetail detail : details) {
                sql = "INSERT INTO RecycleBinDetails (transaction_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, detail.getProductId());
                    pstmt.setInt(3, detail.getQuantity());
                    pstmt.executeUpdate();
                }
            }

            sql = "DELETE FROM Transactions WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void restoreTransaction(int id) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sql = "SELECT date, total, username FROM RecycleBin WHERE id = ?";
            Timestamp date;
            double total;
            String username;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    throw new IllegalArgumentException("ID Transaksi tidak ditemukan di Recycle Bin");
                }
                date = rs.getTimestamp("date");
                total = rs.getDouble("total");
                username = rs.getString("username");
            }

            List<TransactionDetail> details = new ArrayList<>();
            sql = "SELECT product_id, quantity FROM RecycleBinDetails WHERE transaction_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    details.add(new TransactionDetail(id, rs.getInt("product_id"), rs.getInt("quantity")));
                }
            }

            for (TransactionDetail detail : details) {
                sql = "SELECT stock FROM Products WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, detail.getProductId());
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("ID Produk " + detail.getProductId() + " tidak ditemukan");
                    }
                    int currentStock = rs.getInt("stock");
                    if (currentStock < detail.getQuantity()) {
                        throw new IllegalArgumentException("Stok tidak cukup untuk produk ID " + detail.getProductId());
                    }
                }
            }

            sql = "INSERT INTO Transactions (id, date, total, username) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.setTimestamp(2, date);
                pstmt.setDouble(3, total);
                pstmt.setString(4, username);
                pstmt.executeUpdate();
            }

            for (TransactionDetail detail : details) {
                sql = "INSERT INTO TransactionDetails (transaction_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, detail.getProductId());
                    pstmt.setInt(3, detail.getQuantity());
                    pstmt.executeUpdate();
                }
                sql = "UPDATE Products SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, detail.getQuantity());
                    pstmt.setInt(2, detail.getProductId());
                    pstmt.executeUpdate();
                }
            }

            sql = "DELETE FROM RecycleBin WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public ArrayList<Transaction> getTransactions() throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                List<TransactionDetail> details = getTransactionDetails(id);
                transactions.add(new Transaction(
                        id,
                        rs.getTimestamp("date"),
                        details,
                        rs.getDouble("total"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return transactions;
    }

    public ArrayList<Transaction> getUserTransactions(String username) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                List<TransactionDetail> details = getTransactionDetails(id);
                transactions.add(new Transaction(
                        id,
                        rs.getTimestamp("date"),
                        details,
                        rs.getDouble("total"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return transactions;
    }

    public ArrayList<Transaction> getRecycleBin() throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM RecycleBin";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                List<TransactionDetail> details = getRecycleBinDetails(id);
                transactions.add(new Transaction(
                        id,
                        rs.getTimestamp("date"),
                        details,
                        rs.getDouble("total"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return transactions;
    }

    private List<TransactionDetail> getTransactionDetails(int transactionId) throws SQLException {
        List<TransactionDetail> details = new ArrayList<>();
        String sql = "SELECT product_id, quantity FROM TransactionDetails WHERE transaction_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                details.add(new TransactionDetail(transactionId, rs.getInt("product_id"), rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return details;
    }

    private List<TransactionDetail> getRecycleBinDetails(int transactionId) throws SQLException {
        List<TransactionDetail> details = new ArrayList<>();
        String sql = "SELECT product_id, quantity FROM RecycleBinDetails WHERE transaction_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                details.add(new TransactionDetail(transactionId, rs.getInt("product_id"), rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return details;
    }

    public void addSupplier(String name, String contact, String address) throws SQLException {
        if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            throw new IllegalArgumentException("Semua kolom harus diisi");
        }

        String sql = "INSERT INTO Suppliers (name, contact, address) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateSupplier(int id, String name, String contact, String address) throws SQLException {
        if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            throw new IllegalArgumentException("Semua kolom harus diisi");
        }
        String sql = "UPDATE Suppliers SET name = ?, contact = ?, address = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.setInt(4, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("ID Supplier tidak ditemukan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteSupplier(int id) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<Supplier> getSuppliers() throws SQLException {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return suppliers;
    }

    public Report generateReport() throws SQLException {
        double totalSales = 0;
        String sql = "SELECT SUM(total) as total_sales FROM Transactions";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        Map<Integer, Integer> stockSummary = new HashMap<>();
        sql = "SELECT id, stock FROM Products";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stockSummary.put(rs.getInt("id"), rs.getInt("stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return new Report(totalSales, stockSummary);
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
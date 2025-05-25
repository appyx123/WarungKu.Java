package Models;

public class TransactionDetail {
    private int transactionId;
    private int productId;
    private int quantity;

    public TransactionDetail(int transactionId, int productId, int quantity) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getTransactionId() { return transactionId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
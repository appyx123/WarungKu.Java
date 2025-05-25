package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
    private int id;
    private Date date;
    private List<TransactionDetail> details;
    private double total;
    private String username;

    public Transaction(int id, Date date, List<TransactionDetail> details, double total, String username) {
        this.id = id;
        this.date = date;
        this.details = details != null ? details : new ArrayList<>();
        this.total = total;
        this.username = username;
    }

    public int getId() { return id; }
    public Date getDate() { return date; }
    public List<TransactionDetail> getDetails() { return details; }
    public double getTotal() { return total; }
    public String getUsername() { return username; }
}
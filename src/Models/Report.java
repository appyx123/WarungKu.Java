package Models;

import java.util.Date;
import java.util.Map;

public class Report {
    private Date date;
    private double totalSales;
    private Map<Integer, Integer> stockSummary;

    public Report(double totalSales, Map<Integer, Integer> stockSummary) {
        this.date = new Date();
        this.totalSales = totalSales;
        this.stockSummary = stockSummary;
    }

    public Date getDate() { return date; }
    public double getTotalSales() { return totalSales; }
    public Map<Integer, Integer> getStockSummary() { return stockSummary; }
}
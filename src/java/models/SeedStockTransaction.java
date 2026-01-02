package models;

import java.sql.Timestamp;

public class SeedStockTransaction {
    private int id;
    private int stockId;
    private String type; // IN / OUT
    private int qty;
    private String note;
    private Timestamp createdAt;

    public SeedStockTransaction() {}

    public SeedStockTransaction(int id, int stockId, String type, int qty, String note, Timestamp createdAt) {
        this.id = id;
        this.stockId = stockId;
        this.type = type;
        this.qty = qty;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getStockId() { return stockId; }
    public String getType() { return type; }
    public int getQty() { return qty; }
    public String getNote() { return note; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setStockId(int stockId) { this.stockId = stockId; }
    public void setType(String type) { this.type = type; }
    public void setQty(int qty) { this.qty = qty; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

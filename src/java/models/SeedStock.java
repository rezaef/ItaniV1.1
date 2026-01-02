package models;

import java.sql.Timestamp;

public class SeedStock {
    private int id;
    private String name;
    private String unit;
    private int stock;
    private Timestamp updatedAt;

    public SeedStock() {}

    public SeedStock(int id, String name, String unit, int stock, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stock = stock;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public int getStock() { return stock; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStock(int stock) { this.stock = stock; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

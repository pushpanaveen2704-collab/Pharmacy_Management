package com.pharmacy.model;

import java.time.LocalDate;

public class Stock {
    private int stockId;
    private String medicineId;
    private String medicineName;
    private String category;
    private int currentStock;
    private int reorderLevel;
    private LocalDate expiryDate;
    private String status; // AVAILABLE, LOW STOCK, OUT OF STOCK

    public Stock() {}

    public Stock(int stockId, String medicineId, String medicineName, String category, int currentStock, int reorderLevel, LocalDate expiryDate) {
        this.stockId = stockId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.category = category;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.status = computeStatus();
    }

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { 
        this.currentStock = currentStock;
        this.status = computeStatus();
    }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { 
        this.reorderLevel = reorderLevel;
        this.status = computeStatus();
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { 
        if (status == null) status = computeStatus();
        return status; 
    }
    public void setStatus(String status) { this.status = status; }

    public String computeStatus() {
        if (currentStock == 0) return "OUT OF STOCK";
        if (currentStock <= reorderLevel) return "LOW STOCK";
        return "AVAILABLE";
    }
}

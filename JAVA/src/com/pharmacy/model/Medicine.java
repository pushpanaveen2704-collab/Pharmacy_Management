package com.pharmacy.model;

import java.time.LocalDate;

public class Medicine {
    private String medicineId;
    private String name;
    private String category;
    private String manufacturer;
    private double unitPrice;
    private LocalDate expiryDate;
    private int reorderLevel;

    public Medicine() {}

    public Medicine(String medicineId, String name, String category, String manufacturer, double unitPrice, LocalDate expiryDate, int reorderLevel) {
        this.medicineId = medicineId;
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
        this.reorderLevel = reorderLevel;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isNearExpiry(int daysThreshold) {
        if (expiryDate == null) return false;
        LocalDate today = LocalDate.now();
        return !expiryDate.isBefore(today) && !expiryDate.isAfter(today.plusDays(daysThreshold));
    }
}

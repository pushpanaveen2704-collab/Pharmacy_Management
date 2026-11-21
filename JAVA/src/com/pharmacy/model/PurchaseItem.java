package com.pharmacy.model;

import java.time.LocalDate;

public class PurchaseItem {
    private int purchaseItemId;
    private String purchaseId;
    private String medicineId;
    private String medicineName;
    private String batchNo;
    private LocalDate expiryDate;
    private int quantity;
    private double purchasePrice;

    public PurchaseItem() {}

    public PurchaseItem(String medicineId, String batchNo, LocalDate expiryDate, int quantity, double purchasePrice) {
        this.medicineId = medicineId;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public int getPurchaseItemId() { return purchaseItemId; }
    public void setPurchaseItemId(int purchaseItemId) { this.purchaseItemId = purchaseItemId; }

    public String getPurchaseId() { return purchaseId; }
    public void setPurchaseId(String purchaseId) { this.purchaseId = purchaseId; }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public double getSubtotal() { return quantity * purchasePrice; }
}

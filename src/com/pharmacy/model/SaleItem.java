package com.pharmacy.model;

public class SaleItem {
    private int saleItemId;
    private String saleId;
    private String medicineId;
    private String medicineName;
    private int quantitySold;
    private double sellingPrice;
    private double subtotal;

    public SaleItem() {}

    public SaleItem(String medicineId, String medicineName, int quantitySold, double sellingPrice) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantitySold = quantitySold;
        this.sellingPrice = sellingPrice;
        this.subtotal = quantitySold * sellingPrice;
    }

    public int getSaleItemId() { return saleItemId; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }

    public String getSaleId() { return saleId; }
    public void setSaleId(String saleId) { this.saleId = saleId; }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { 
        this.quantitySold = quantitySold; 
        this.subtotal = quantitySold * this.sellingPrice;
    }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { 
        this.sellingPrice = sellingPrice; 
        this.subtotal = this.quantitySold * sellingPrice;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}

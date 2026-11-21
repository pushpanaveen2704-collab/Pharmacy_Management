package com.pharmacy.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private String saleId;
    private LocalDate saleDate;
    private String customerName;
    private double totalAmount;
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public Sale(String saleId, LocalDate saleDate, String customerName, double totalAmount) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public String getSaleId() { return saleId; }
    public void setSaleId(String saleId) { this.saleId = saleId; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public void addItem(SaleItem item) { this.items.add(item); }
}

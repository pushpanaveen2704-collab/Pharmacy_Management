package com.pharmacy.model;

import java.util.ArrayList;
import java.util.List;

public class DashboardSummary {
    // Overall Summary
    private int totalMedicinesRegistered;
    private int totalSuppliers;
    private int totalPurchaseTransactions;
    private int totalSalesTransactions;
    private int totalAvailableStock;
    private int totalMedicinesSold;
    private double totalRevenue;

    // Stock Overview
    private int availableMedicines;
    private int lowStockMedicines;
    private int outOfStockMedicines;
    private int expiredMedicines;
    private int nearExpiryMedicines;

    // Sales Overview (Today's performance)
    private int todayTransactions;
    private int todayMedicinesSold;
    private double todayRevenue;

    // Top Selling Medicine
    private String topSellingMedicineName = "N/A";
    private int topSellingQuantitySold = 0;
    private double topSellingRevenue = 0.0;

    // Quick Alerts
    private List<String> quickAlerts = new ArrayList<>();

    public DashboardSummary() {}

    public int getTotalMedicinesRegistered() { return totalMedicinesRegistered; }
    public void setTotalMedicinesRegistered(int totalMedicinesRegistered) { this.totalMedicinesRegistered = totalMedicinesRegistered; }

    public int getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(int totalSuppliers) { this.totalSuppliers = totalSuppliers; }

    public int getTotalPurchaseTransactions() { return totalPurchaseTransactions; }
    public void setTotalPurchaseTransactions(int totalPurchaseTransactions) { this.totalPurchaseTransactions = totalPurchaseTransactions; }

    public int getTotalSalesTransactions() { return totalSalesTransactions; }
    public void setTotalSalesTransactions(int totalSalesTransactions) { this.totalSalesTransactions = totalSalesTransactions; }

    public int getTotalAvailableStock() { return totalAvailableStock; }
    public void setTotalAvailableStock(int totalAvailableStock) { this.totalAvailableStock = totalAvailableStock; }

    public int getTotalMedicinesSold() { return totalMedicinesSold; }
    public void setTotalMedicinesSold(int totalMedicinesSold) { this.totalMedicinesSold = totalMedicinesSold; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getAvailableMedicines() { return availableMedicines; }
    public void setAvailableMedicines(int availableMedicines) { this.availableMedicines = availableMedicines; }

    public int getLowStockMedicines() { return lowStockMedicines; }
    public void setLowStockMedicines(int lowStockMedicines) { this.lowStockMedicines = lowStockMedicines; }

    public int getOutOfStockMedicines() { return outOfStockMedicines; }
    public void setOutOfStockMedicines(int outOfStockMedicines) { this.outOfStockMedicines = outOfStockMedicines; }

    public int getExpiredMedicines() { return expiredMedicines; }
    public void setExpiredMedicines(int expiredMedicines) { this.expiredMedicines = expiredMedicines; }

    public int getNearExpiryMedicines() { return nearExpiryMedicines; }
    public void setNearExpiryMedicines(int nearExpiryMedicines) { this.nearExpiryMedicines = nearExpiryMedicines; }

    public int getTodayTransactions() { return todayTransactions; }
    public void setTodayTransactions(int todayTransactions) { this.todayTransactions = todayTransactions; }

    public int getTodayMedicinesSold() { return todayMedicinesSold; }
    public void setTodayMedicinesSold(int todayMedicinesSold) { this.todayMedicinesSold = todayMedicinesSold; }

    public double getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(double todayRevenue) { this.todayRevenue = todayRevenue; }

    public String getTopSellingMedicineName() { return topSellingMedicineName; }
    public void setTopSellingMedicineName(String topSellingMedicineName) { this.topSellingMedicineName = topSellingMedicineName; }

    public int getTopSellingQuantitySold() { return topSellingQuantitySold; }
    public void setTopSellingQuantitySold(int topSellingQuantitySold) { this.topSellingQuantitySold = topSellingQuantitySold; }

    public double getTopSellingRevenue() { return topSellingRevenue; }
    public void setTopSellingRevenue(double topSellingRevenue) { this.topSellingRevenue = topSellingRevenue; }

    public List<String> getQuickAlerts() { return quickAlerts; }
    public void setQuickAlerts(List<String> quickAlerts) { this.quickAlerts = quickAlerts; }
    public void addQuickAlert(String alert) { this.quickAlerts.add(alert); }
}

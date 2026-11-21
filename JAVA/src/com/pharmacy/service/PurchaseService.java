package com.pharmacy.service;

import com.pharmacy.dao.MedicineDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Purchase;
import com.pharmacy.model.PurchaseItem;
import com.pharmacy.model.Supplier;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.CurrencyUtil;
import com.pharmacy.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PurchaseService {
    private final PurchaseDAO purchaseDAO = new PurchaseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    public void recordPurchase(Scanner scanner) {
        ConsoleUtil.printHeader("RECORD MEDICINE PURCHASE");
        try {
            String purId = ConsoleUtil.readString(scanner, "Enter Purchase ID (e.g. PUR106): ", false).toUpperCase();
            if (purchaseDAO.isPurchaseExists(purId)) {
                ConsoleUtil.printError("Purchase transaction " + purId + " already exists.");
                return;
            }

            String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID (e.g. S101): ", false).toUpperCase();
            Supplier supplier = supplierDAO.getSupplierById(supId);
            if (supplier == null) {
                ConsoleUtil.printError("Supplier " + supId + " does not exist. Please register the supplier first.");
                return;
            }
            System.out.println("Selected Supplier: " + supplier.getSupplierName());

            String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID (e.g. MED101): ", false).toUpperCase();
            Medicine med = medicineDAO.getMedicineById(medId);
            if (med == null) {
                ConsoleUtil.printError("Medicine " + medId + " does not exist. Please add the medicine first.");
                return;
            }
            System.out.println("Selected Medicine: " + med.getName());

            String batchNo = ConsoleUtil.readString(scanner, "Enter Batch Number (e.g. BAT2611): ", false).toUpperCase();
            LocalDate expiryDate = ConsoleUtil.readDate(scanner, "Enter Batch Expiry Date (YYYY-MM-DD): ", false);
            int qty = ConsoleUtil.readInt(scanner, "Enter Purchase Quantity (units): ", 1, 100000);
            double purchasePrice = ConsoleUtil.readDouble(scanner, "Enter Purchase Price per unit (Rs.): ", 0.01);
            LocalDate purDate = ConsoleUtil.readDate(scanner, "Enter Purchase Date (YYYY-MM-DD, press Enter for Today): ", true);

            double totalAmount = qty * purchasePrice;
            Purchase p = new Purchase(purId, supId, purDate, totalAmount);
            PurchaseItem item = new PurchaseItem(medId, batchNo, expiryDate, qty, purchasePrice);
            p.addItem(item);

            purchaseDAO.recordPurchase(p);
            ConsoleUtil.printSuccess("Purchase recorded successfully.");
            ConsoleUtil.printSuccess("Stock for medicine '" + med.getName() + "' updated automatically by +" + qty + " units.");
            System.out.println("Total Purchase Amount: " + CurrencyUtil.format(totalAmount));
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to record purchase transaction: " + e.getMessage());
        }
    }

    public void viewAllPurchases() {
        ConsoleUtil.printHeader("ALL PURCHASE TRANSACTIONS");
        try {
            List<Purchase> list = purchaseDAO.getAllPurchases();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No purchase transactions recorded.");
                return;
            }
            displayPurchaseTable(list);
            System.out.println("\nTotal Purchase Transactions: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to load purchase records: " + e.getMessage());
        }
    }

    public void searchPurchase(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH PURCHASE RECORD");
        String purId = ConsoleUtil.readString(scanner, "Enter Purchase ID: ", false).toUpperCase();
        try {
            Purchase p = purchaseDAO.getPurchaseById(purId);
            if (p == null) {
                ConsoleUtil.printWarning("Purchase transaction " + purId + " not found.");
                return;
            }
            displayPurchaseDetails(p);
        } catch (SQLException e) {
            ConsoleUtil.printError("Error searching purchase: " + e.getMessage());
        }
    }

    public void viewSupplierWisePurchases(Scanner scanner) {
        ConsoleUtil.printHeader("SUPPLIER-WISE PURCHASES");
        String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID (e.g. S101): ", false).toUpperCase();
        try {
            List<Purchase> list = purchaseDAO.getPurchasesBySupplier(supId);
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No purchase records found for supplier: " + supId);
                return;
            }
            displayPurchaseTable(list);
            double total = list.stream().mapToDouble(Purchase::getTotalAmount).sum();
            System.out.println("\nTotal Orders from Supplier " + supId + ": " + list.size());
            System.out.println("Total Purchase Outlay: " + CurrencyUtil.format(total));
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to retrieve supplier purchase records: " + e.getMessage());
        }
    }

    public void generatePurchaseReport() {
        ConsoleUtil.printHeader("PURCHASE PROCUREMENT SUMMARY REPORT");
        try {
            List<Purchase> list = purchaseDAO.getAllPurchases();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No purchase records available for report generation.");
                return;
            }
            displayPurchaseTable(list);
            double grandTotal = list.stream().mapToDouble(Purchase::getTotalAmount).sum();
            System.out.println("\n" + ConsoleUtil.LINE_DASHES);
            System.out.printf("%-35s : %d\n", "Total Purchase Transactions", list.size());
            System.out.printf("%-35s : %s\n", "Total Procurement Expenditure", CurrencyUtil.format(grandTotal));
            System.out.printf("%-35s : %s\n", "Average Purchase Order Value", CurrencyUtil.format(grandTotal / list.size()));
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Report generation failed: " + e.getMessage());
        }
    }

    private void displayPurchaseTable(List<Purchase> list) {
        System.out.println("-------------------- PURCHASE DETAILS --------------------");
        System.out.printf("%-12s %-12s %-14s %-10s %-12s\n", "Purchase ID", "Supplier ID", "Total (Rs.)", "Items", "Date");
        System.out.println("----------------------------------------------------------");
        for (Purchase p : list) {
            System.out.printf("%-12s %-12s %-14s %-10d %-12s\n", 
                p.getPurchaseId(), 
                p.getSupplierId(), 
                CurrencyUtil.formatPlain(p.getTotalAmount()), 
                p.getItems().isEmpty() ? 1 : p.getItems().size(),
                DateUtil.format(p.getPurchaseDate()));
        }
        System.out.println("----------------------------------------------------------");
    }

    private void displayPurchaseDetails(Purchase p) {
        System.out.println("\n" + ConsoleUtil.LINE_DASHES);
        System.out.printf("%-20s : %s\n", "Purchase ID", p.getPurchaseId());
        System.out.printf("%-20s : %s (%s)\n", "Supplier", p.getSupplierName(), p.getSupplierId());
        System.out.printf("%-20s : %s\n", "Purchase Date", DateUtil.format(p.getPurchaseDate()));
        System.out.printf("%-20s : %s\n", "Total Amount", CurrencyUtil.format(p.getTotalAmount()));
        System.out.println(ConsoleUtil.LINE_DASHES);
        System.out.println("Items in Purchase:");
        System.out.printf("%-12s %-18s %-10s %-8s %-10s %-10s\n", "Medicine ID", "Name", "Batch", "Qty", "Cost", "Subtotal");
        for (PurchaseItem pi : p.getItems()) {
            System.out.printf("%-12s %-18s %-10s %-8d %-10.2f %-10.2f\n",
                pi.getMedicineId(),
                truncate(pi.getMedicineName(), 17),
                pi.getBatchNo(),
                pi.getQuantity(),
                pi.getPurchasePrice(),
                pi.getSubtotal());
        }
        System.out.println(ConsoleUtil.LINE_DASHES);
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

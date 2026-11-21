package com.pharmacy.service;

import com.pharmacy.dao.StockDAO;
import com.pharmacy.model.Stock;
import com.pharmacy.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StockService {
    private final StockDAO stockDAO = new StockDAO();

    public void viewAvailableStock() {
        ConsoleUtil.printHeader("AVAILABLE MEDICINE STOCK");
        try {
            List<Stock> list = stockDAO.getAllStock();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No inventory stock records found.");
                return;
            }
            displayStockTable(list);
            System.out.println("\nTotal Medicine Types: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch stock records: " + e.getMessage());
        }
    }

    public void updateStock(Scanner scanner) {
        ConsoleUtil.printHeader("MANUAL STOCK ADJUSTMENT");
        String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID to adjust stock: ", false).toUpperCase();
        try {
            Stock s = stockDAO.getStockByMedicineId(medId);
            if (s == null) {
                ConsoleUtil.printError("No stock record found for medicine ID: " + medId);
                return;
            }

            System.out.println("Medicine: " + s.getMedicineName() + " | Current Stock: " + s.getCurrentStock() + " units");
            int newStock = ConsoleUtil.readInt(scanner, "Enter New Stock Quantity: ", 0, 100000);
            stockDAO.updateStock(medId, newStock);
            ConsoleUtil.printSuccess("Stock updated successfully.");
            System.out.println("New Stock Level for " + s.getMedicineName() + ": " + newStock + " units");
        } catch (SQLException e) {
            ConsoleUtil.printError("Stock update failed: " + e.getMessage());
        }
    }

    public void viewLowStockMedicines() {
        ConsoleUtil.printHeader("LOW STOCK WARNING REPORT");
        try {
            List<Stock> list = stockDAO.getLowStockMedicines();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No low stock medicines detected. All inventory levels healthy.");
                return;
            }
            displayStockTable(list);
            ConsoleUtil.printWarning(list.size() + " medicine(s) have reached or fallen below reorder threshold!");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to query low stock medicines: " + e.getMessage());
        }
    }

    public void viewOutOfStockMedicines() {
        ConsoleUtil.printHeader("OUT OF STOCK CRITICAL REPORT");
        try {
            List<Stock> list = stockDAO.getOutOfStockMedicines();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("Zero medicines are out of stock.");
                return;
            }
            displayStockTable(list);
            ConsoleUtil.printAlert(list.size() + " medicine(s) are completely out of stock!");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to query out-of-stock medicines: " + e.getMessage());
        }
    }

    public void viewStockSummary() {
        ConsoleUtil.printHeader("INVENTORY STOCK SUMMARY");
        try {
            List<Stock> all = stockDAO.getAllStock();
            int totalUnits = all.stream().mapToInt(Stock::getCurrentStock).sum();
            long availableCount = all.stream().filter(s -> "AVAILABLE".equals(s.getStatus())).count();
            long lowCount = all.stream().filter(s -> "LOW STOCK".equals(s.getStatus())).count();
            long outCount = all.stream().filter(s -> "OUT OF STOCK".equals(s.getStatus())).count();

            System.out.println(ConsoleUtil.LINE_DASHES);
            System.out.printf("%-30s : %d types\n", "Total Registered Medicine Types", all.size());
            System.out.printf("%-30s : %d units\n", "Total Available Inventory Units", totalUnits);
            System.out.printf("%-30s : %d types\n", "Medicines with Optimal Stock", availableCount);
            System.out.printf("%-30s : %d types\n", "Medicines in Low Stock", lowCount);
            System.out.printf("%-30s : %d types\n", "Medicines Out of Stock", outCount);
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to calculate stock summary: " + e.getMessage());
        }
    }

    public void searchMedicineStock(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH MEDICINE STOCK");
        String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID: ", false).toUpperCase();
        try {
            Stock s = stockDAO.getStockByMedicineId(medId);
            if (s == null) {
                ConsoleUtil.printWarning("Medicine " + medId + " not found in inventory.");
                return;
            }
            System.out.println(ConsoleUtil.LINE_DASHES);
            System.out.printf("%-20s : %s\n", "Medicine ID", s.getMedicineId());
            System.out.printf("%-20s : %s\n", "Medicine Name", s.getMedicineName());
            System.out.printf("%-20s : %s\n", "Category", s.getCategory());
            System.out.printf("%-20s : %d units\n", "Current Stock", s.getCurrentStock());
            System.out.printf("%-20s : %d units\n", "Reorder Level", s.getReorderLevel());
            System.out.printf("%-20s : %s\n", "Inventory Status", s.getStatus());
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Stock search error: " + e.getMessage());
        }
    }

    private void displayStockTable(List<Stock> list) {
        System.out.println("-------------------- AVAILABLE STOCK --------------------");
        System.out.printf("%-12s %-20s %-10s %-15s\n", "Medicine ID", "Medicine Name", "Quantity", "Status");
        System.out.println("---------------------------------------------------------");
        for (Stock s : list) {
            System.out.printf("%-12s %-20s %-10d %-15s\n", 
                s.getMedicineId(), 
                truncate(s.getMedicineName(), 19), 
                s.getCurrentStock(), 
                s.getStatus());
        }
        System.out.println("---------------------------------------------------------");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

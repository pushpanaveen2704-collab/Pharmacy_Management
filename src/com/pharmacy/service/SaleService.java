package com.pharmacy.service;

import com.pharmacy.dao.MedicineDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.StockDAO;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;
import com.pharmacy.model.Stock;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.CurrencyUtil;
import com.pharmacy.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class SaleService {
    private final SaleDAO saleDAO = new SaleDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final StockDAO stockDAO = new StockDAO();

    public void recordMedicineSale(Scanner scanner) {
        ConsoleUtil.printHeader("RECORD MEDICINE SALE");
        try {
            String saleId = ConsoleUtil.readString(scanner, "Enter Sale ID (e.g. SL109): ", false).toUpperCase();
            if (saleDAO.isSaleExists(saleId)) {
                ConsoleUtil.printError("Sale transaction " + saleId + " already recorded.");
                return;
            }

            String customerName = ConsoleUtil.readString(scanner, "Enter Customer Name (press Enter for 'Walk-in Customer'): ", true);
            if (customerName.isEmpty()) customerName = "Walk-in Customer";

            String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID (e.g. MED101): ", false).toUpperCase();
            Medicine med = medicineDAO.getMedicineById(medId);
            if (med == null) {
                ConsoleUtil.printError("Invalid medicine ID: " + medId);
                return;
            }

            // Expiry Check
            if (med.isExpired()) {
                ConsoleUtil.printError("Expired medicine cannot be sold! (Expired on: " + DateUtil.format(med.getExpiryDate()) + ")");
                return;
            }
            if (med.isNearExpiry(30)) {
                ConsoleUtil.printWarning("Caution: Medicine is nearing expiry (" + DateUtil.format(med.getExpiryDate()) + ").");
            }

            // Stock Check
            Stock stock = stockDAO.getStockByMedicineId(medId);
            int available = stock != null ? stock.getCurrentStock() : 0;
            if (available <= 0) {
                ConsoleUtil.printError("Medicine is OUT OF STOCK. Cannot dispense.");
                return;
            }

            System.out.println("Medicine: " + med.getName() + " | Price: " + CurrencyUtil.format(med.getUnitPrice()) + 
                               " | Available Stock: " + available + " units");

            int qty = ConsoleUtil.readInt(scanner, "Enter Quantity to Sell: ", 1, 10000);
            if (qty > available) {
                ConsoleUtil.printError("Insufficient stock available. Available: " + available + ", Requested: " + qty);
                return;
            }

            LocalDate saleDate = ConsoleUtil.readDate(scanner, "Enter Sale Date (YYYY-MM-DD, press Enter for Today): ", true);

            double subtotal = qty * med.getUnitPrice();
            Sale sale = new Sale(saleId, saleDate, customerName, subtotal);
            SaleItem item = new SaleItem(medId, med.getName(), qty, med.getUnitPrice());
            sale.addItem(item);

            saleDAO.recordSale(sale);
            ConsoleUtil.printSuccess("Medicine sale completed successfully.");
            ConsoleUtil.printSuccess("Stock automatically reduced by " + qty + " units. Remaining: " + (available - qty));

            // Generate receipt immediately
            displaySalesReceipt(sale);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to process medicine sale: " + e.getMessage());
        }
    }

    public void viewAllSales() {
        ConsoleUtil.printHeader("ALL MEDICINE SALES TRANSACTIONS");
        try {
            List<Sale> list = saleDAO.getAllSales();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No sales transactions recorded.");
                return;
            }
            displaySalesTable(list);
            double total = list.stream().mapToDouble(Sale::getTotalAmount).sum();
            System.out.println("\nTotal Sales Transactions: " + list.size());
            System.out.println("Cumulative Revenue: " + CurrencyUtil.format(total));
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch sales records: " + e.getMessage());
        }
    }

    public void searchSale(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH SALE TRANSACTION");
        String saleId = ConsoleUtil.readString(scanner, "Enter Sale ID: ", false).toUpperCase();
        try {
            Sale s = saleDAO.getSaleById(saleId);
            if (s == null) {
                ConsoleUtil.printWarning("Sale transaction " + saleId + " not found.");
                return;
            }
            displaySalesReceipt(s);
        } catch (SQLException e) {
            ConsoleUtil.printError("Search error: " + e.getMessage());
        }
    }

    public void viewDailySales(Scanner scanner) {
        ConsoleUtil.printHeader("DAILY SALES TRANSACTIONS");
        LocalDate date = ConsoleUtil.readDate(scanner, "Enter Date (YYYY-MM-DD, press Enter for Today): ", true);
        try {
            List<Sale> list = saleDAO.getDailySales(date);
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No sales transactions found for date: " + DateUtil.format(date));
                return;
            }
            displaySalesTable(list);
            double total = list.stream().mapToDouble(Sale::getTotalAmount).sum();
            System.out.println("\nDate: " + DateUtil.format(date));
            System.out.println("Daily Transactions: " + list.size());
            System.out.println("Total Daily Revenue: " + CurrencyUtil.format(total));
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to load daily sales: " + e.getMessage());
        }
    }

    public void generateSalesReceipt(Scanner scanner) {
        searchSale(scanner);
    }

    public void displaySalesReceipt(Sale s) {
        System.out.println("\n" + ConsoleUtil.LINE_EQUALS);
        System.out.println(ConsoleUtil.centerText("PHARMACY DISPENSING RECEIPT", 60));
        System.out.println(ConsoleUtil.LINE_EQUALS);
        System.out.printf("%-18s : %s\n", "Receipt No", s.getSaleId());
        System.out.printf("%-18s : %s\n", "Date", DateUtil.format(s.getSaleDate()));
        System.out.printf("%-18s : %s\n", "Customer", s.getCustomerName());
        System.out.println(ConsoleUtil.LINE_DASHES);
        System.out.println("-------------------- MEDICINE SALES --------------------");
        System.out.printf("%-10s %-12s %-18s %-6s %-10s\n", "Sale ID", "Medicine ID", "Medicine Name", "Qty", "Amount");
        System.out.println("--------------------------------------------------------");
        for (SaleItem item : s.getItems()) {
            System.out.printf("%-10s %-12s %-18s %-6d %-10.2f\n",
                s.getSaleId(),
                item.getMedicineId(),
                truncate(item.getMedicineName(), 17),
                item.getQuantitySold(),
                item.getSubtotal());
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("%-30s : %s\n", "TOTAL BILLED AMOUNT", CurrencyUtil.format(s.getTotalAmount()));
        System.out.println(ConsoleUtil.LINE_EQUALS);
    }

    private void displaySalesTable(List<Sale> list) {
        System.out.println("-------------------- MEDICINE SALES --------------------");
        System.out.printf("%-10s %-14s %-20s %-12s\n", "Sale ID", "Sale Date", "Customer", "Amount (Rs.)");
        System.out.println("--------------------------------------------------------");
        for (Sale s : list) {
            System.out.printf("%-10s %-14s %-20s %-12s\n",
                s.getSaleId(),
                DateUtil.format(s.getSaleDate()),
                truncate(s.getCustomerName(), 19),
                CurrencyUtil.formatPlain(s.getTotalAmount()));
        }
        System.out.println("--------------------------------------------------------");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

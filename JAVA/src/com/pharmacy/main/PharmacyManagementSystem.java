package com.pharmacy.main;

import com.pharmacy.dao.DatabaseConnection;
import com.pharmacy.dao.ReportDAO;
import com.pharmacy.model.DashboardSummary;
import com.pharmacy.service.*;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.CurrencyUtil;
import com.pharmacy.util.TeePrintStream;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class PharmacyManagementSystem {

    private static final MedicineService medicineService = new MedicineService();
    private static final SupplierService supplierService = new SupplierService();
    private static final PurchaseService purchaseService = new PurchaseService();
    private static final StockService stockService = new StockService();
    private static final SaleService saleService = new SaleService();
    private static final ReportService reportService = new ReportService();
    private static final ExpiryService expiryService = new ExpiryService();
    private static final ReportDAO reportDAO = new ReportDAO();

    public static void main(String[] args) {
        // Initialize dual-logging to Windows CMD console and PharmacyManagement_Output.txt
        TeePrintStream.install("PharmacyManagement_Output.txt");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("C:\\PharmacySystem> java PharmacyManagementSystem");

        while (running) {
            displayMainMenu();
            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 11);

            switch (choice) {
                case 1:
                    handleDashboard(scanner);
                    break;
                case 2:
                    handleMedicineManagement(scanner);
                    break;
                case 3:
                    handleSupplierManagement(scanner);
                    break;
                case 4:
                    handlePurchaseManagement(scanner);
                    break;
                case 5:
                    handleStockManagement(scanner);
                    break;
                case 6:
                    handleSalesManagement(scanner);
                    break;
                case 7:
                    handleSalesAnalysis(scanner);
                    break;
                case 8:
                    handleExpiryManagement(scanner);
                    break;
                case 9:
                    handlePythonAnalysis(scanner);
                    break;
                case 10:
                    handleDatabaseManagement(scanner);
                    break;
                case 11:
                    System.out.println();
                    ConsoleUtil.printInfo("Exiting Pharmacy Stock Management and Medicine Sales Analysis System. Goodbye!");
                    running = false;
                    break;
                default:
                    ConsoleUtil.printWarning("Invalid option. Please choose a valid number from the menu.");
            }
        }
        scanner.close();
    }

    public static void displayMainMenu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PHARMACY MANAGEMENT SYSTEM");
        System.out.println();
        System.out.println("         PHARMACY STOCK & SALES ANALYSIS");
        System.out.println("============================================================");
        System.out.println();
        System.out.println("                MAIN MENU");
        System.out.println();
        System.out.println("1. Dashboard");
        System.out.println("2. Medicine Management");
        System.out.println("3. Supplier Management");
        System.out.println("4. Purchase Management");
        System.out.println("5. Stock Management");
        System.out.println("6. Medicine Sales Management");
        System.out.println("7. Sales Analysis & Reports");
        System.out.println("8. Expiry Management");
        System.out.println("9. Python Data Analysis");
        System.out.println("10. Database Management");
        System.out.println("11. Exit");
        System.out.println();
        System.out.println("============================================================");
    }

    // ============================================================
    // 1. DASHBOARD MODULE
    // ============================================================
    public static void handleDashboard(Scanner scanner) {
        boolean inDashboard = true;
        while (inDashboard) {
            renderDashboard();

            System.out.println("---");
            System.out.println();
            System.out.println("1. Refresh Dashboard");
            System.out.println("2. View Detailed Stock Report");
            System.out.println("3. View Detailed Sales Report");
            System.out.println("4. View Expiry Report");
            System.out.println("5. Return to Main Menu");
            System.out.println();

            int dashChoice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 5);
            switch (dashChoice) {
                case 1:
                    ConsoleUtil.printSuccess("Refreshing Dashboard data from database...");
                    break;
                case 2:
                    stockService.viewStockSummary();
                    stockService.viewAvailableStock();
                    ConsoleUtil.pause(scanner);
                    break;
                case 3:
                    reportService.generateDailySalesReport(scanner);
                    ConsoleUtil.pause(scanner);
                    break;
                case 4:
                    expiryService.generateExpiryReport();
                    ConsoleUtil.pause(scanner);
                    break;
                case 5:
                    inDashboard = false;
                    break;
            }
        }
    }

    public static void renderDashboard() {
        try {
            DashboardSummary ds = reportDAO.getDashboardSummary();

            System.out.println();
            System.out.println("============================================================");
            System.out.println("PHARMACY DASHBOARD");
            System.out.println();
            System.out.println("                OVERALL SUMMARY");
            System.out.println();
            System.out.printf("%-33s : %d\n", "Total Medicines Registered", ds.getTotalMedicinesRegistered());
            System.out.printf("%-33s : %d\n", "Total Suppliers", ds.getTotalSuppliers());
            System.out.printf("%-33s : %d\n", "Total Purchase Transactions", ds.getTotalPurchaseTransactions());
            System.out.printf("%-33s : %d\n", "Total Sales Transactions", ds.getTotalSalesTransactions());
            System.out.printf("%-33s : %d\n", "Total Available Stock", ds.getTotalAvailableStock());
            System.out.printf("%-33s : %d\n", "Total Medicines Sold", ds.getTotalMedicinesSold());
            System.out.printf("%-33s : %s\n", "Total Revenue", CurrencyUtil.format(ds.getTotalRevenue()));
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.println("                STOCK OVERVIEW");
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.printf("%-33s : %d\n", "Available Medicines", ds.getAvailableMedicines());
            System.out.printf("%-33s : %d\n", "Low Stock Medicines", ds.getLowStockMedicines());
            System.out.printf("%-33s : %d\n", "Out of Stock Medicines", ds.getOutOfStockMedicines());
            System.out.printf("%-33s : %d\n", "Expired Medicines", ds.getExpiredMedicines());
            System.out.printf("%-33s : %d\n", "Near Expiry Medicines", ds.getNearExpiryMedicines());
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.println("                SALES OVERVIEW");
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.printf("%-33s : %d\n", "Today's Transactions", ds.getTodayTransactions());
            System.out.printf("%-33s : %d\n", "Today's Medicines Sold", ds.getTodayMedicinesSold());
            System.out.printf("%-33s : %s\n", "Today's Revenue", CurrencyUtil.format(ds.getTodayRevenue()));
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.println("             TOP SELLING MEDICINE");
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.printf("%-33s : %s\n", "Medicine Name", ds.getTopSellingMedicineName());
            System.out.printf("%-33s : %d\n", "Total Quantity Sold", ds.getTopSellingQuantitySold());
            System.out.printf("%-33s : %s\n", "Total Revenue", CurrencyUtil.format(ds.getTopSellingRevenue()));
            System.out.println();
            System.out.println("---");
            System.out.println();
            System.out.println("                QUICK ALERTS");
            System.out.println();
            System.out.println("---");
            System.out.println();
            for (String alert : ds.getQuickAlerts()) {
                System.out.println(alert);
            }
            System.out.println();
        } catch (Exception e) {
            ConsoleUtil.printError("Error loading live dashboard: " + e.getMessage());
        }
    }

    // ============================================================
    // 2. MEDICINE MANAGEMENT
    // ============================================================
    public static void handleMedicineManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("MEDICINE MANAGEMENT");
            System.out.println();
            System.out.println("1. Add Medicine");
            System.out.println("2. View All Medicines");
            System.out.println("3. Search Medicine");
            System.out.println("4. Update Medicine");
            System.out.println("5. Delete Medicine");
            System.out.println("6. View Medicine Details");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1: medicineService.addMedicine(scanner); ConsoleUtil.pause(scanner); break;
                case 2: medicineService.viewAllMedicines(); ConsoleUtil.pause(scanner); break;
                case 3: medicineService.searchMedicine(scanner); ConsoleUtil.pause(scanner); break;
                case 4: medicineService.updateMedicine(scanner); ConsoleUtil.pause(scanner); break;
                case 5: medicineService.deleteMedicine(scanner); ConsoleUtil.pause(scanner); break;
                case 6: medicineService.viewMedicineDetails(scanner); ConsoleUtil.pause(scanner); break;
                case 7: back = true; break;
            }
        }
    }

    // ============================================================
    // 3. SUPPLIER MANAGEMENT
    // ============================================================
    public static void handleSupplierManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("SUPPLIER MANAGEMENT");
            System.out.println();
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Search Supplier");
            System.out.println("4. Update Supplier");
            System.out.println("5. Delete Supplier");
            System.out.println("6. View Supplier Details");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1: supplierService.addSupplier(scanner); ConsoleUtil.pause(scanner); break;
                case 2: supplierService.viewAllSuppliers(); ConsoleUtil.pause(scanner); break;
                case 3: supplierService.searchSupplier(scanner); ConsoleUtil.pause(scanner); break;
                case 4: supplierService.updateSupplier(scanner); ConsoleUtil.pause(scanner); break;
                case 5: supplierService.deleteSupplier(scanner); ConsoleUtil.pause(scanner); break;
                case 6: supplierService.viewSupplierDetails(scanner); ConsoleUtil.pause(scanner); break;
                case 7: back = true; break;
            }
        }
    }

    // ============================================================
    // 4. PURCHASE MANAGEMENT
    // ============================================================
    public static void handlePurchaseManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("PURCHASE MANAGEMENT");
            System.out.println();
            System.out.println("1. Record Medicine Purchase");
            System.out.println("2. View All Purchases");
            System.out.println("3. Search Purchase");
            System.out.println("4. View Purchase History");
            System.out.println("5. View Supplier-wise Purchases");
            System.out.println("6. Generate Purchase Report");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1: purchaseService.recordPurchase(scanner); ConsoleUtil.pause(scanner); break;
                case 2: purchaseService.viewAllPurchases(); ConsoleUtil.pause(scanner); break;
                case 3: purchaseService.searchPurchase(scanner); ConsoleUtil.pause(scanner); break;
                case 4: purchaseService.viewAllPurchases(); ConsoleUtil.pause(scanner); break;
                case 5: purchaseService.viewSupplierWisePurchases(scanner); ConsoleUtil.pause(scanner); break;
                case 6: purchaseService.generatePurchaseReport(); ConsoleUtil.pause(scanner); break;
                case 7: back = true; break;
            }
        }
    }

    // ============================================================
    // 5. STOCK MANAGEMENT
    // ============================================================
    public static void handleStockManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("STOCK MANAGEMENT");
            System.out.println();
            System.out.println("1. View Available Stock");
            System.out.println("2. Update Stock");
            System.out.println("3. View Low Stock Medicines");
            System.out.println("4. View Out of Stock Medicines");
            System.out.println("5. View Stock Summary");
            System.out.println("6. Search Medicine Stock");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1: stockService.viewAvailableStock(); ConsoleUtil.pause(scanner); break;
                case 2: stockService.updateStock(scanner); ConsoleUtil.pause(scanner); break;
                case 3: stockService.viewLowStockMedicines(); ConsoleUtil.pause(scanner); break;
                case 4: stockService.viewOutOfStockMedicines(); ConsoleUtil.pause(scanner); break;
                case 5: stockService.viewStockSummary(); ConsoleUtil.pause(scanner); break;
                case 6: stockService.searchMedicineStock(scanner); ConsoleUtil.pause(scanner); break;
                case 7: back = true; break;
            }
        }
    }

    // ============================================================
    // 6. MEDICINE SALES MANAGEMENT
    // ============================================================
    public static void handleSalesManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("MEDICINE SALES MANAGEMENT");
            System.out.println();
            System.out.println("1. Record Medicine Sale");
            System.out.println("2. View All Sales");
            System.out.println("3. Search Sale");
            System.out.println("4. View Daily Sales");
            System.out.println("5. View Sales History");
            System.out.println("6. Generate Sales Receipt");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1: saleService.recordMedicineSale(scanner); ConsoleUtil.pause(scanner); break;
                case 2: saleService.viewAllSales(); ConsoleUtil.pause(scanner); break;
                case 3: saleService.searchSale(scanner); ConsoleUtil.pause(scanner); break;
                case 4: saleService.viewDailySales(scanner); ConsoleUtil.pause(scanner); break;
                case 5: saleService.viewAllSales(); ConsoleUtil.pause(scanner); break;
                case 6: saleService.generateSalesReceipt(scanner); ConsoleUtil.pause(scanner); break;
                case 7: back = true; break;
            }
        }
    }

    // ============================================================
    // 7. SALES ANALYSIS AND REPORTS
    // ============================================================
    public static void handleSalesAnalysis(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("SALES ANALYSIS & REPORTS");
            System.out.println();
            System.out.println("1. Daily Sales Report");
            System.out.println("2. Weekly Sales Report");
            System.out.println("3. Monthly Sales Report");
            System.out.println("4. Most Sold Medicines");
            System.out.println("5. Least Sold Medicines");
            System.out.println("6. Revenue Analysis");
            System.out.println("7. Medicine-wise Sales Report");
            System.out.println("8. Generate Complete Sales Report");
            System.out.println("9. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 9);
            switch (choice) {
                case 1: reportService.generateDailySalesReport(scanner); ConsoleUtil.pause(scanner); break;
                case 2: reportService.generateWeeklySalesReport(); ConsoleUtil.pause(scanner); break;
                case 3: reportService.generateMonthlySalesReport(); ConsoleUtil.pause(scanner); break;
                case 4: reportService.viewTopSellingMedicines(); ConsoleUtil.pause(scanner); break;
                case 5: reportService.viewLeastSellingMedicines(); ConsoleUtil.pause(scanner); break;
                case 6: reportService.viewRevenueAnalysis(); ConsoleUtil.pause(scanner); break;
                case 7: reportService.viewMedicineWiseSalesReport(); ConsoleUtil.pause(scanner); break;
                case 8: reportService.generateCompleteSalesReport(); ConsoleUtil.pause(scanner); break;
                case 9: back = true; break;
            }
        }
    }

    // ============================================================
    // 8. EXPIRY MANAGEMENT
    // ============================================================
    public static void handleExpiryManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("EXPIRY MANAGEMENT");
            System.out.println();
            System.out.println("1. View All Expiry Details");
            System.out.println("2. View Near Expiry Medicines");
            System.out.println("3. View Expired Medicines");
            System.out.println("4. View Medicines Expiring This Month");
            System.out.println("5. Generate Expiry Report");
            System.out.println("6. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 6);
            switch (choice) {
                case 1: expiryService.viewAllExpiryDetails(); ConsoleUtil.pause(scanner); break;
                case 2: expiryService.viewNearExpiryMedicines(scanner); ConsoleUtil.pause(scanner); break;
                case 3: expiryService.viewExpiredMedicines(); ConsoleUtil.pause(scanner); break;
                case 4: expiryService.viewMedicinesExpiringThisMonth(); ConsoleUtil.pause(scanner); break;
                case 5: expiryService.generateExpiryReport(); ConsoleUtil.pause(scanner); break;
                case 6: back = true; break;
            }
        }
    }

    // ============================================================
    // 9. PYTHON DATA ANALYSIS
    // ============================================================
    public static void handlePythonAnalysis(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("PYTHON DATA ANALYSIS");
            System.out.println();
            System.out.println("1. Medicine Sales Visualization");
            System.out.println("2. Daily Revenue Visualization");
            System.out.println("3. Stock Analysis Visualization");
            System.out.println("4. Top Selling Medicine Analysis");
            System.out.println("5. Low Stock Analysis");
            System.out.println("6. Expiry Analysis");
            System.out.println("7. Complete Data Analysis");
            System.out.println("8. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 8);
            if (choice == 8) {
                back = true;
            } else {
                PythonService.executeAnalysis(String.valueOf(choice));
                ConsoleUtil.pause(scanner);
            }
        }
    }

    // ============================================================
    // 10. DATABASE MANAGEMENT
    // ============================================================
    public static void handleDatabaseManagement(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("DATABASE MANAGEMENT");
            System.out.println();
            System.out.println("1. Test Database Connection");
            System.out.println("2. View Database Summary");
            System.out.println("3. View Medicine Records");
            System.out.println("4. View Supplier Records");
            System.out.println("5. View Purchase Records");
            System.out.println("6. View Sales Records");
            System.out.println("7. Return to Main Menu");
            System.out.println();

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice: ", 1, 7);
            switch (choice) {
                case 1:
                    ConsoleUtil.printInfo("Connecting to Database at: " + DatabaseConnection.getDatabaseLocation());
                    if (DatabaseConnection.testConnection()) {
                        ConsoleUtil.printSuccess("Database connection established successfully. Status: ONLINE");
                    } else {
                        ConsoleUtil.printError("Database connection failed.");
                    }
                    ConsoleUtil.pause(scanner);
                    break;
                case 2:
                    viewDatabaseSummary();
                    ConsoleUtil.pause(scanner);
                    break;
                case 3:
                    medicineService.viewAllMedicines();
                    ConsoleUtil.pause(scanner);
                    break;
                case 4:
                    supplierService.viewAllSuppliers();
                    ConsoleUtil.pause(scanner);
                    break;
                case 5:
                    purchaseService.viewAllPurchases();
                    ConsoleUtil.pause(scanner);
                    break;
                case 6:
                    saleService.viewAllSales();
                    ConsoleUtil.pause(scanner);
                    break;
                case 7:
                    back = true;
                    break;
            }
        }
    }

    private static void viewDatabaseSummary() {
        ConsoleUtil.printHeader("DATABASE SCHEMA & STORAGE METRICS");
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Database Engine    : " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver Name        : " + meta.getDriverName() + " " + meta.getDriverVersion());
            System.out.println("Storage Location   : " + DatabaseConnection.getDatabaseLocation());
            System.out.println(ConsoleUtil.LINE_DASHES);
            System.out.println("Table Record Counts:");
            try (Statement st = conn.createStatement()) {
                String[] tables = {"medicines", "suppliers", "stock", "purchases", "purchase_items", "sales", "sale_items"};
                for (String t : tables) {
                    try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t)) {
                        if (rs.next()) {
                            System.out.printf("  %-18s : %d records\n", t, rs.getInt(1));
                        }
                    }
                }
            }
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (Exception e) {
            ConsoleUtil.printError("Database diagnostic error: " + e.getMessage());
        }
    }
}

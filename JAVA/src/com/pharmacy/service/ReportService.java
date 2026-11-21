package com.pharmacy.service;

import com.pharmacy.dao.ReportDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.CurrencyUtil;
import com.pharmacy.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    public void generateDailySalesReport(Scanner scanner) {
        ConsoleUtil.printHeader("DAILY SALES REPORT");
        LocalDate date = ConsoleUtil.readDate(scanner, "Enter Report Date (YYYY-MM-DD, press Enter for Today): ", true);
        try {
            List<Sale> sales = saleDAO.getDailySales(date);
            int totalTxn = sales.size();
            int totalQty = 0;
            double totalRevenue = 0.0;

            for (Sale s : sales) {
                totalRevenue += s.getTotalAmount();
                for (SaleItem item : s.getItems()) {
                    totalQty += item.getQuantitySold();
                }
            }

            double avgTxn = totalTxn > 0 ? (totalRevenue / totalTxn) : 0.0;

            System.out.println("-------------------- DAILY SALES REPORT --------------------");
            System.out.printf("%-27s : %s\n", "Date", DateUtil.format(date));
            System.out.printf("%-27s : %d\n", "Total Transactions", totalTxn);
            System.out.printf("%-27s : %d\n", "Total Medicines Sold", totalQty);
            System.out.printf("%-27s : %s\n", "Total Revenue", CurrencyUtil.format(totalRevenue));
            System.out.printf("%-27s : %s\n", "Average Transaction Value", CurrencyUtil.format(avgTxn));
            System.out.println("------------------------------------------------------------");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to generate daily sales report: " + e.getMessage());
        }
    }

    public void generateWeeklySalesReport() {
        ConsoleUtil.printHeader("WEEKLY SALES REPORT");
        try {
            List<Map<String, Object>> analysis = reportDAO.getDailyRevenueAnalysis();
            double weeklyTotal = 0.0;
            int weeklyTxn = 0;
            System.out.println("-------------------- PAST 7 DAYS ACTIVITY ------------------");
            System.out.printf("%-15s %-15s %-20s\n", "Date", "Transactions", "Revenue");
            System.out.println("------------------------------------------------------------");
            int count = 0;
            for (Map<String, Object> m : analysis) {
                if (count++ >= 7) break;
                int tx = (int) m.get("transactions");
                double rev = (double) m.get("revenue");
                weeklyTxn += tx;
                weeklyTotal += rev;
                System.out.printf("%-15s %-15d %-20s\n", m.get("date"), tx, CurrencyUtil.format(rev));
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-25s : %d\n", "Total Weekly Transactions", weeklyTxn);
            System.out.printf("%-25s : %s\n", "Total Weekly Revenue", CurrencyUtil.format(weeklyTotal));
            System.out.println("------------------------------------------------------------");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to calculate weekly sales: " + e.getMessage());
        }
    }

    public void generateMonthlySalesReport() {
        ConsoleUtil.printHeader("MONTHLY SALES REPORT");
        try {
            List<Map<String, Object>> analysis = reportDAO.getDailyRevenueAnalysis();
            double monthlyTotal = 0.0;
            int monthlyTxn = 0;
            for (Map<String, Object> m : analysis) {
                monthlyTxn += (int) m.get("transactions");
                monthlyTotal += (double) m.get("revenue");
            }
            System.out.println("-------------------- CURRENT MONTH REPORT ------------------");
            System.out.printf("%-27s : %s\n", "Reporting Period", LocalDate.now().getMonth() + " " + LocalDate.now().getYear());
            System.out.printf("%-27s : %d\n", "Total Sales Transactions", monthlyTxn);
            System.out.printf("%-27s : %s\n", "Gross Monthly Revenue", CurrencyUtil.format(monthlyTotal));
            System.out.printf("%-27s : %s\n", "Average Daily Revenue", CurrencyUtil.format(monthlyTotal / 30.0));
            System.out.println("------------------------------------------------------------");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to calculate monthly sales: " + e.getMessage());
        }
    }

    public void viewTopSellingMedicines() {
        ConsoleUtil.printHeader("TOP SELLING MEDICINES");
        try {
            List<Map<String, Object>> list = reportDAO.getRankedMedicines(true, 5);
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No sales records available.");
                return;
            }
            displayRankedTable("TOP SELLING MEDICINES", list);
            String best = (String) list.get(0).get("medicine_name");
            System.out.println("\nBEST SELLING MEDICINE: " + best.toUpperCase());
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to query top-selling medicines: " + e.getMessage());
        }
    }

    public void viewLeastSellingMedicines() {
        ConsoleUtil.printHeader("LEAST SELLING MEDICINES");
        try {
            List<Map<String, Object>> list = reportDAO.getRankedMedicines(false, 5);
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No sales records available.");
                return;
            }
            displayRankedTable("LEAST SELLING MEDICINES", list);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to query least-selling medicines: " + e.getMessage());
        }
    }

    public void viewRevenueAnalysis() {
        ConsoleUtil.printHeader("REVENUE TREND ANALYSIS");
        try {
            List<Map<String, Object>> analysis = reportDAO.getDailyRevenueAnalysis();
            System.out.println("-------------------- DAILY REVENUE TREND -------------------");
            System.out.printf("%-15s %-15s %-20s\n", "Date", "Transactions", "Total Revenue");
            System.out.println("------------------------------------------------------------");
            double cumulative = 0;
            for (Map<String, Object> m : analysis) {
                double r = (double) m.get("revenue");
                cumulative += r;
                System.out.printf("%-15s %-15d %-20s\n", m.get("date"), (int) m.get("transactions"), CurrencyUtil.format(r));
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-30s : %s\n", "CUMULATIVE SYSTEM REVENUE", CurrencyUtil.format(cumulative));
            System.out.println("------------------------------------------------------------");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to analyze revenue: " + e.getMessage());
        }
    }

    public void viewMedicineWiseSalesReport() {
        ConsoleUtil.printHeader("MEDICINE-WISE SALES REPORT");
        try {
            List<Map<String, Object>> list = reportDAO.getMedicineWiseSales();
            displayRankedTable("MEDICINE-WISE SALES REPORT", list);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to generate medicine-wise sales: " + e.getMessage());
        }
    }

    public void generateCompleteSalesReport() {
        ConsoleUtil.printHeader("COMPLETE EXECUTIVE SALES & REVENUE REPORT");
        generateDailySalesReport(new Scanner(System.in));
        generateWeeklySalesReport();
        viewTopSellingMedicines();
        viewRevenueAnalysis();
    }

    private void displayRankedTable(String title, List<Map<String, Object>> list) {
        System.out.println("-------------------- " + title + " --------------------");
        System.out.printf("%-6s %-20s %-15s %-15s\n", "Rank", "Medicine Name", "Quantity Sold", "Revenue");
        System.out.println("------------------------------------------------------------");
        for (Map<String, Object> m : list) {
            System.out.printf("%-6d %-20s %-15d %-15.2f\n",
                (int) m.get("rank"),
                truncate((String) m.get("medicine_name"), 19),
                (int) m.get("quantity_sold"),
                (double) m.get("revenue"));
        }
        System.out.println("------------------------------------------------------------");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

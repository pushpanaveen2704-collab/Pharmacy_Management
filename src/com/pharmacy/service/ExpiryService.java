package com.pharmacy.service;

import com.pharmacy.dao.MedicineDAO;
import com.pharmacy.dao.ReportDAO;
import com.pharmacy.model.Medicine;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ExpiryService {
    private final ReportDAO reportDAO = new ReportDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    public void viewAllExpiryDetails() {
        ConsoleUtil.printHeader("MEDICINE EXPIRY SURVEILLANCE");
        try {
            List<Medicine> list = medicineDAO.getAllMedicines();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No medicine records found.");
                return;
            }
            displayExpiryTable(list);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to retrieve expiry details: " + e.getMessage());
        }
    }

    public void viewNearExpiryMedicines(Scanner scanner) {
        ConsoleUtil.printHeader("NEAR EXPIRY MEDICINES ALERT");
        System.out.print("Enter threshold in days (press Enter for default 30 days): ");
        String in = scanner.nextLine().trim();
        int days = 30;
        if (!in.isEmpty()) {
            try {
                days = Integer.parseInt(in);
            } catch (NumberFormatException ignored) {}
        }

        try {
            List<Medicine> list = reportDAO.getNearExpiryMedicines(days);
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No medicines are nearing expiry within " + days + " days.");
                return;
            }
            displayExpiryTable(list);
            ConsoleUtil.printWarning(list.size() + " medicine(s) are nearing expiry within " + days + " days!");
        } catch (SQLException e) {
            ConsoleUtil.printError("Error fetching near-expiry medicines: " + e.getMessage());
        }
    }

    public void viewExpiredMedicines() {
        ConsoleUtil.printHeader("EXPIRED MEDICINES QUARANTINE LIST");
        try {
            List<Medicine> list = reportDAO.getExpiredMedicines();
            if (list.isEmpty()) {
                ConsoleUtil.printSuccess("Zero expired medicines in inventory. All active stocks valid.");
                return;
            }
            displayExpiryTable(list);
            ConsoleUtil.printAlert(list.size() + " medicine(s) are EXPIRED and must be removed from dispensing racks immediately!");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch expired medicines: " + e.getMessage());
        }
    }

    public void viewMedicinesExpiringThisMonth() {
        ConsoleUtil.printHeader("MEDICINES EXPIRING THIS MONTH");
        try {
            List<Medicine> list = reportDAO.getMedicinesExpiringThisMonth();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No medicines expiring in current calendar month.");
                return;
            }
            displayExpiryTable(list);
            ConsoleUtil.printWarning(list.size() + " medicine(s) will expire this month!");
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch monthly expiring medicines: " + e.getMessage());
        }
    }

    public void generateExpiryReport() {
        ConsoleUtil.printHeader("COMPREHENSIVE EXPIRY AUDIT REPORT");
        try {
            List<Medicine> all = medicineDAO.getAllMedicines();
            long expiredCount = all.stream().filter(m -> m.getExpiryDate().isBefore(LocalDate.now())).count();
            long nearExpiryCount = all.stream().filter(m -> !m.getExpiryDate().isBefore(LocalDate.now()) && 
                                                           !m.getExpiryDate().isAfter(LocalDate.now().plusDays(30))).count();
            long validCount = all.size() - expiredCount - nearExpiryCount;

            displayExpiryTable(all);

            System.out.println("\n" + ConsoleUtil.LINE_DASHES);
            System.out.printf("%-30s : %d\n", "Total Medicines Inspected", all.size());
            System.out.printf("%-30s : %d\n", "Expired Medicines", expiredCount);
            System.out.printf("%-30s : %d\n", "Near Expiry Medicines (<= 30d)", nearExpiryCount);
            System.out.printf("%-30s : %d\n", "Valid / Healthy Medicines", validCount);
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to generate expiry report: " + e.getMessage());
        }
    }

    private void displayExpiryTable(List<Medicine> list) {
        System.out.println("-------------------- EXPIRY ALERT REPORT --------------------");
        System.out.printf("%-12s %-20s %-14s %-15s\n", "Medicine ID", "Medicine Name", "Expiry Date", "Status");
        System.out.println("-------------------------------------------------------------");
        LocalDate today = LocalDate.now();
        int nearCount = 0;
        int expCount = 0;

        for (Medicine m : list) {
            String status;
            if (m.getExpiryDate().isBefore(today)) {
                status = "EXPIRED";
                expCount++;
            } else if (!m.getExpiryDate().isAfter(today.plusDays(30))) {
                status = "NEAR EXPIRY";
                nearCount++;
            } else {
                status = "VALID";
            }
            System.out.printf("%-12s %-20s %-14s %-15s\n",
                m.getMedicineId(),
                truncate(m.getName(), 19),
                DateUtil.format(m.getExpiryDate()),
                status);
        }
        System.out.println("-------------------------------------------------------------");
        System.out.println("Near Expiry Medicines: " + nearCount);
        System.out.println("Expired Medicines: " + expCount);
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

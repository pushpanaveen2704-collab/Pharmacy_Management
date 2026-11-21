package com.pharmacy.service;

import com.pharmacy.dao.MedicineDAO;
import com.pharmacy.model.Medicine;
import com.pharmacy.util.ConsoleUtil;
import com.pharmacy.util.CurrencyUtil;
import com.pharmacy.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MedicineService {
    private final MedicineDAO medicineDAO = new MedicineDAO();

    public void addMedicine(Scanner scanner) {
        ConsoleUtil.printHeader("ADD NEW MEDICINE");
        try {
            String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID (e.g. MED116): ", false).toUpperCase();
            if (medicineDAO.isMedicineExists(medId)) {
                ConsoleUtil.printError("Medicine with ID " + medId + " already exists!");
                return;
            }

            String name = ConsoleUtil.readString(scanner, "Enter Medicine Name: ", false);
            String category = ConsoleUtil.readString(scanner, "Enter Category (Tablet/Syrup/Antibiotic/etc.): ", false);
            String manufacturer = ConsoleUtil.readString(scanner, "Enter Manufacturer: ", false);
            double price = ConsoleUtil.readDouble(scanner, "Enter Unit Price (Rs.): ", 0.01);
            LocalDate expiry = ConsoleUtil.readDate(scanner, "Enter Expiry Date (YYYY-MM-DD): ", false);
            int reorder = ConsoleUtil.readInt(scanner, "Enter Reorder Alert Level (e.g. 15): ", 1, 1000);

            Medicine m = new Medicine(medId, name, category, manufacturer, price, expiry, reorder);
            medicineDAO.addMedicine(m);
            ConsoleUtil.printSuccess("Medicine registered successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database error during registration: " + e.getMessage());
        }
    }

    public void viewAllMedicines() {
        ConsoleUtil.printHeader("ALL REGISTERED MEDICINES");
        try {
            List<Medicine> list = medicineDAO.getAllMedicines();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No medicine records registered in the system.");
                return;
            }
            displayMedicineTable(list);
            System.out.println("\nTotal Medicines Registered: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to retrieve medicine records: " + e.getMessage());
        }
    }

    public void searchMedicine(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH MEDICINE");
        String keyword = ConsoleUtil.readString(scanner, "Enter Medicine ID, Name, or Category to search: ", false);
        try {
            List<Medicine> list = medicineDAO.searchMedicines(keyword);
            if (list.isEmpty()) {
                ConsoleUtil.printWarning("No matching medicines found for query: " + keyword);
                return;
            }
            displayMedicineTable(list);
            System.out.println("\nMatching Medicines Found: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Search operation failed: " + e.getMessage());
        }
    }

    public void updateMedicine(Scanner scanner) {
        ConsoleUtil.printHeader("UPDATE MEDICINE INFORMATION");
        String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID to update: ", false).toUpperCase();
        try {
            Medicine m = medicineDAO.getMedicineById(medId);
            if (m == null) {
                ConsoleUtil.printError("Medicine with ID " + medId + " does not exist.");
                return;
            }

            System.out.println("Current details for " + m.getName() + ":");
            System.out.println("Category: " + m.getCategory() + " | Price: " + CurrencyUtil.format(m.getUnitPrice()) + 
                               " | Expiry: " + DateUtil.format(m.getExpiryDate()));

            String newName = ConsoleUtil.readString(scanner, "Enter New Name (leave blank to keep current): ", true);
            if (!newName.isEmpty()) m.setName(newName);

            String newCategory = ConsoleUtil.readString(scanner, "Enter New Category (leave blank to keep current): ", true);
            if (!newCategory.isEmpty()) m.setCategory(newCategory);

            String newMfg = ConsoleUtil.readString(scanner, "Enter New Manufacturer (leave blank to keep current): ", true);
            if (!newMfg.isEmpty()) m.setManufacturer(newMfg);

            System.out.print("Update Unit Price? (y/N): ");
            String ansP = scanner.nextLine().trim();
            if (ansP.equalsIgnoreCase("y")) {
                m.setUnitPrice(ConsoleUtil.readDouble(scanner, "Enter New Unit Price: ", 0.01));
            }

            System.out.print("Update Expiry Date? (y/N): ");
            String ansE = scanner.nextLine().trim();
            if (ansE.equalsIgnoreCase("y")) {
                m.setExpiryDate(ConsoleUtil.readDate(scanner, "Enter New Expiry Date (YYYY-MM-DD): ", false));
            }

            medicineDAO.updateMedicine(m);
            ConsoleUtil.printSuccess("Medicine information updated successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Update failed: " + e.getMessage());
        }
    }

    public void deleteMedicine(Scanner scanner) {
        ConsoleUtil.printHeader("DELETE MEDICINE RECORD");
        String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID to delete: ", false).toUpperCase();
        try {
            Medicine m = medicineDAO.getMedicineById(medId);
            if (m == null) {
                ConsoleUtil.printError("Medicine with ID " + medId + " does not exist.");
                return;
            }

            System.out.print("Are you sure you want to delete '" + m.getName() + "' (" + medId + ")? (y/N): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                medicineDAO.deleteMedicine(medId);
                ConsoleUtil.printSuccess("Medicine record deleted successfully.");
            } else {
                ConsoleUtil.printInfo("Deletion cancelled.");
            }
        } catch (SQLException e) {
            ConsoleUtil.printError("Deletion rejected (record is linked to transaction history): " + e.getMessage());
        }
    }

    public void viewMedicineDetails(Scanner scanner) {
        ConsoleUtil.printHeader("VIEW MEDICINE DETAILS");
        String medId = ConsoleUtil.readString(scanner, "Enter Medicine ID: ", false).toUpperCase();
        try {
            Medicine m = medicineDAO.getMedicineById(medId);
            if (m == null) {
                ConsoleUtil.printError("Medicine with ID " + medId + " not found.");
                return;
            }

            System.out.println(ConsoleUtil.LINE_DASHES);
            System.out.printf("%-20s : %s\n", "Medicine ID", m.getMedicineId());
            System.out.printf("%-20s : %s\n", "Medicine Name", m.getName());
            System.out.printf("%-20s : %s\n", "Category", m.getCategory());
            System.out.printf("%-20s : %s\n", "Manufacturer", m.getManufacturer());
            System.out.printf("%-20s : %s\n", "Unit Price", CurrencyUtil.format(m.getUnitPrice()));
            System.out.printf("%-20s : %s\n", "Expiry Date", DateUtil.format(m.getExpiryDate()));
            System.out.printf("%-20s : %d units\n", "Reorder Level", m.getReorderLevel());
            String status = m.isExpired() ? "[EXPIRED]" : (m.isNearExpiry(30) ? "[NEAR EXPIRY]" : "[VALID]");
            System.out.printf("%-20s : %s\n", "Expiry Status", status);
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch medicine details: " + e.getMessage());
        }
    }

    private void displayMedicineTable(List<Medicine> list) {
        System.out.println("-------------------- MEDICINE DETAILS --------------------");
        System.out.printf("%-12s %-20s %-15s %-10s\n", "Medicine ID", "Medicine Name", "Category", "Price");
        System.out.println("----------------------------------------------------------");
        for (Medicine m : list) {
            System.out.printf("%-12s %-20s %-15s %-10.2f\n", 
                m.getMedicineId(), 
                truncate(m.getName(), 19), 
                truncate(m.getCategory(), 14), 
                m.getUnitPrice());
        }
        System.out.println("----------------------------------------------------------");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

package com.pharmacy.service;

import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Supplier;
import com.pharmacy.util.ConsoleUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SupplierService {
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public void addSupplier(Scanner scanner) {
        ConsoleUtil.printHeader("REGISTER NEW SUPPLIER");
        try {
            String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID (e.g. S106): ", false).toUpperCase();
            if (supplierDAO.isSupplierExists(supId)) {
                ConsoleUtil.printError("Supplier with ID " + supId + " already registered.");
                return;
            }

            String name = ConsoleUtil.readString(scanner, "Enter Supplier Name: ", false);
            String phone = ConsoleUtil.readString(scanner, "Enter Contact Phone Number: ", false);
            String email = ConsoleUtil.readString(scanner, "Enter Email Address: ", false);
            String address = ConsoleUtil.readString(scanner, "Enter Postal / Office Address: ", false);

            Supplier s = new Supplier(supId, name, phone, email, address);
            supplierDAO.addSupplier(s);
            ConsoleUtil.printSuccess("Supplier registered successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Database error registering supplier: " + e.getMessage());
        }
    }

    public void viewAllSuppliers() {
        ConsoleUtil.printHeader("ALL REGISTERED SUPPLIERS");
        try {
            List<Supplier> list = supplierDAO.getAllSuppliers();
            if (list.isEmpty()) {
                ConsoleUtil.printInfo("No supplier records found.");
                return;
            }
            displaySupplierTable(list);
            System.out.println("\nTotal Registered Suppliers: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to retrieve suppliers: " + e.getMessage());
        }
    }

    public void searchSupplier(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH SUPPLIER");
        String keyword = ConsoleUtil.readString(scanner, "Enter Supplier ID, Name, or Phone: ", false);
        try {
            List<Supplier> list = supplierDAO.searchSuppliers(keyword);
            if (list.isEmpty()) {
                ConsoleUtil.printWarning("No suppliers matched: " + keyword);
                return;
            }
            displaySupplierTable(list);
            System.out.println("\nMatching Suppliers: " + list.size());
        } catch (SQLException e) {
            ConsoleUtil.printError("Supplier search failed: " + e.getMessage());
        }
    }

    public void updateSupplier(Scanner scanner) {
        ConsoleUtil.printHeader("UPDATE SUPPLIER DETAILS");
        String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID to update: ", false).toUpperCase();
        try {
            Supplier s = supplierDAO.getSupplierById(supId);
            if (s == null) {
                ConsoleUtil.printError("Supplier with ID " + supId + " not found.");
                return;
            }

            System.out.println("Current Name: " + s.getSupplierName() + " | Phone: " + s.getPhone());
            String newName = ConsoleUtil.readString(scanner, "Enter New Name (leave blank to keep current): ", true);
            if (!newName.isEmpty()) s.setSupplierName(newName);

            String newPhone = ConsoleUtil.readString(scanner, "Enter New Phone (leave blank to keep current): ", true);
            if (!newPhone.isEmpty()) s.setPhone(newPhone);

            String newEmail = ConsoleUtil.readString(scanner, "Enter New Email (leave blank to keep current): ", true);
            if (!newEmail.isEmpty()) s.setEmail(newEmail);

            String newAddress = ConsoleUtil.readString(scanner, "Enter New Address (leave blank to keep current): ", true);
            if (!newAddress.isEmpty()) s.setAddress(newAddress);

            supplierDAO.updateSupplier(s);
            ConsoleUtil.printSuccess("Supplier details updated successfully.");
        } catch (SQLException e) {
            ConsoleUtil.printError("Update failed: " + e.getMessage());
        }
    }

    public void deleteSupplier(Scanner scanner) {
        ConsoleUtil.printHeader("DELETE SUPPLIER");
        String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID to delete: ", false).toUpperCase();
        try {
            Supplier s = supplierDAO.getSupplierById(supId);
            if (s == null) {
                ConsoleUtil.printError("Supplier with ID " + supId + " not found.");
                return;
            }

            System.out.print("Are you sure you want to delete supplier '" + s.getSupplierName() + "'? (y/N): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                supplierDAO.deleteSupplier(supId);
                ConsoleUtil.printSuccess("Supplier removed successfully.");
            } else {
                ConsoleUtil.printInfo("Deletion cancelled.");
            }
        } catch (SQLException e) {
            ConsoleUtil.printError("Cannot delete supplier with active purchase transaction history: " + e.getMessage());
        }
    }

    public void viewSupplierDetails(Scanner scanner) {
        ConsoleUtil.printHeader("SUPPLIER PROFILE");
        String supId = ConsoleUtil.readString(scanner, "Enter Supplier ID: ", false).toUpperCase();
        try {
            Supplier s = supplierDAO.getSupplierById(supId);
            if (s == null) {
                ConsoleUtil.printError("Supplier with ID " + supId + " not found.");
                return;
            }

            System.out.println(ConsoleUtil.LINE_DASHES);
            System.out.printf("%-18s : %s\n", "Supplier ID", s.getSupplierId());
            System.out.printf("%-18s : %s\n", "Supplier Name", s.getSupplierName());
            System.out.printf("%-18s : %s\n", "Contact Phone", s.getPhone());
            System.out.printf("%-18s : %s\n", "Email Address", s.getEmail());
            System.out.printf("%-18s : %s\n", "Office Address", s.getAddress());
            System.out.println(ConsoleUtil.LINE_DASHES);
        } catch (SQLException e) {
            ConsoleUtil.printError("Failed to fetch supplier profile: " + e.getMessage());
        }
    }

    private void displaySupplierTable(List<Supplier> list) {
        System.out.println("-------------------- SUPPLIER DETAILS --------------------");
        System.out.printf("%-12s %-25s %-15s\n", "Supplier ID", "Supplier Name", "Contact");
        System.out.println("----------------------------------------------------------");
        for (Supplier s : list) {
            System.out.printf("%-12s %-25s %-15s\n", 
                s.getSupplierId(), 
                truncate(s.getSupplierName(), 24), 
                s.getPhone());
        }
        System.out.println("----------------------------------------------------------");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() <= maxLen ? val : val.substring(0, maxLen - 2) + "..";
    }
}

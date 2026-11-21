package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class SupplierManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("SupplierManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java SupplierManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleSupplierManagement(scanner);
        scanner.close();
    }
}

package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class SalesManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("SalesManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java SalesManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleSalesManagement(scanner);
        scanner.close();
    }
}

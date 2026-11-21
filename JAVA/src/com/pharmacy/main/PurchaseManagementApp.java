package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class PurchaseManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("PurchaseManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java PurchaseManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handlePurchaseManagement(scanner);
        scanner.close();
    }
}

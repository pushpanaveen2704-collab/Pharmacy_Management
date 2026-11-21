package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class StockManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("StockManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java StockManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleStockManagement(scanner);
        scanner.close();
    }
}

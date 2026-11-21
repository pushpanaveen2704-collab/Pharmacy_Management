package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class DatabaseManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("DatabaseManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java DatabaseManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleDatabaseManagement(scanner);
        scanner.close();
    }
}

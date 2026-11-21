package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class ExpiryManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("ExpiryManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java ExpiryManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleExpiryManagement(scanner);
        scanner.close();
    }
}

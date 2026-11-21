package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class MedicineManagementApp {
    public static void main(String[] args) {
        TeePrintStream.install("MedicineManagement_Output.txt");
        System.out.println("C:\\PharmacySystem> java MedicineManagementApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleMedicineManagement(scanner);
        scanner.close();
    }
}

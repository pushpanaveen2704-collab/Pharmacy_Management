package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class PharmacyAnalysisApp {
    public static void main(String[] args) {
        TeePrintStream.install("PharmacyAnalysis_Output.txt");
        System.out.println("C:\\PharmacySystem> java PharmacyAnalysisApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handleSalesAnalysis(scanner);
        scanner.close();
    }
}

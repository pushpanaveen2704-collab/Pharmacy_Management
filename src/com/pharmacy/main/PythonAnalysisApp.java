package com.pharmacy.main;

import com.pharmacy.util.TeePrintStream;
import java.util.Scanner;

public class PythonAnalysisApp {
    public static void main(String[] args) {
        TeePrintStream.install("PythonAnalysis_Output.txt");
        System.out.println("C:\\PharmacySystem> java PythonAnalysisApp");
        Scanner scanner = new Scanner(System.in);
        PharmacyManagementSystem.handlePythonAnalysis(scanner);
        scanner.close();
    }
}

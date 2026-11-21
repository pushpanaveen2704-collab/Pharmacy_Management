package com.pharmacy.service;

import com.pharmacy.util.ConsoleUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PythonService {

    public static String detectPython() {
        String[] candidates = {
            "C:\\Users\\Anusuya\\AppData\\Local\\Programs\\Python\\Python37\\python.exe",
            "python",
            "py",
            "python3"
        };

        for (String cmd : candidates) {
            try {
                Process p = new ProcessBuilder(cmd, "--version").start();
                int code = p.waitFor();
                if (code == 0) {
                    return cmd;
                }
            } catch (Exception ignored) {}
        }
        return "python";
    }

    public static String detectScriptPath() {
        String[] candidates = {
            "FDS/data_analysis.py",
            "python/data_analysis.py",
            "../FDS/data_analysis.py",
            "../python/data_analysis.py"
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }
        return new File("FDS/data_analysis.py").getAbsolutePath();
    }

    public static boolean executeAnalysis(String mode) {
        String pythonExe = detectPython();
        String script = detectScriptPath();

        ConsoleUtil.printInfo("Launching Python Analytics Engine...");
        ConsoleUtil.printInfo("Interpreter: " + pythonExe);
        ConsoleUtil.printInfo("Target Script: " + script);

        try {
            List<String> command = new ArrayList<>();
            command.add(pythonExe);
            command.add(script);
            command.add(mode);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ConsoleUtil.printSuccess("Python Data Analysis routine completed successfully.");
                return true;
            } else {
                ConsoleUtil.printError("Python process exited with code " + exitCode);
                return false;
            }
        } catch (Exception e) {
            ConsoleUtil.printError("Failed to invoke Python environment: " + e.getMessage());
            return false;
        }
    }
}

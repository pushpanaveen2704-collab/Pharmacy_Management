package com.pharmacy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * TeePrintStream duplicates all standard output and standard error
 * simultaneously to the live Windows Command Prompt console AND to an output text file.
 * This guarantees interactive console operation without suppressing user prompts or inputs.
 */
public class TeePrintStream extends PrintStream {
    private final PrintStream second;
    private static PrintStream currentLogStream = null;

    public TeePrintStream(OutputStream main, PrintStream second) {
        super(main, true);
        this.second = second;
    }

    public static synchronized void install(String outputFileName) {
        try {
            File outFile = new File(outputFileName);
            // Overwrite existing or start fresh session
            FileOutputStream fos = new FileOutputStream(outFile, false);
            PrintStream fileOut = new PrintStream(fos, true, "UTF-8");
            currentLogStream = fileOut;

            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;

            TeePrintStream teeOut = new TeePrintStream(originalOut, fileOut);
            TeePrintStream teeErr = new TeePrintStream(originalErr, fileOut);

            System.setOut(teeOut);
            System.setErr(teeErr);
        } catch (IOException e) {
            System.err.println("[WARNING] Could not initialize file logging for " + outputFileName + ": " + e.getMessage());
        }
    }

    public static synchronized void logInput(String input) {
        if (currentLogStream != null) {
            currentLogStream.println("[USER INPUT] > " + input);
            currentLogStream.flush();
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        if (second != null) {
            try {
                second.write(buf, off, len);
                second.flush();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void write(int b) {
        super.write(b);
        if (second != null) {
            try {
                second.write(b);
                second.flush();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void flush() {
        super.flush();
        if (second != null) {
            second.flush();
        }
    }

    @Override
    public void close() {
        super.close();
        if (second != null) {
            second.close();
        }
    }
}

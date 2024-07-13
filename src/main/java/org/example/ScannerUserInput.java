package org.example;
import java.util.Scanner;

public class ScannerUserInput implements UserInput {
    private final Scanner scanner;

    public ScannerUserInput(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public int nextInt() {
        return scanner.nextInt();
    }

    @Override
    public void close() {
        scanner.close();
    }
}

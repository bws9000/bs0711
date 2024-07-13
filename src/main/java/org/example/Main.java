package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserInput userInput = new ScannerUserInput(new Scanner(System.in));
        App app = new App(userInput);
        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();
    }
}
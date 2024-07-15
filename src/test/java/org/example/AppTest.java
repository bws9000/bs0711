package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppTest {
    private App app;
    private UserInput userInputMock;
    NumberFormat numberUSFormat;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        userInputMock = Mockito.mock(UserInput.class);
        numberUSFormat = NumberFormat.getNumberInstance(Locale.US);
        numberUSFormat.setMinimumFractionDigits(2);
        numberUSFormat.setMaximumFractionDigits(2);
        app = new App(userInputMock);
    }
    @AfterEach
    public void tearDown() {}

    @Test
    public void testOne() {
        when(userInputMock.nextLine())
                .thenReturn("CHNS")
                .thenReturn("")
                .thenReturn("09/03/2015");
        when(userInputMock.nextInt()).thenReturn(5);
        app.getToolCode();
        app.getRentalDays();
        app.getCheckoutDate();

        Tool selectedTool = app.toolSelected;
        assertEquals("CHNS", selectedTool.getToolCode());
        assertEquals("Chainsaw", selectedTool.getToolType());
        assertEquals("Stihl", selectedTool.getBrand());
        assertEquals(LocalDate.of(2015, 9, 3), app.date);
        assertEquals(5, app.daysToRent);
        verify(userInputMock, times(3)).nextLine();
        verify(userInputMock, times(1)).nextInt();
    }

    @Test
    public void testOnePercentageOutOfRange() {
        when(userInputMock.nextInt()).thenReturn(101).thenReturn(50);
        when(userInputMock.nextLine()).thenReturn("");
        app.getDiscountPercentage();
        assertEquals(50, app.discountPercentage);
        String[] outputLines = outContent.toString().split("\n");

        assertEquals(app.PROMPT_GET_DISCOUNT_PERCENTAGE, outputLines[0].trim());
        assertEquals(app.NUMBER_100_ERROR, outputLines[1].trim());
        assertEquals(app.PROMPT_GET_DISCOUNT_PERCENTAGE, outputLines[2].trim());
        verify(userInputMock, times(2)).nextInt();
        verify(userInputMock, times(2)).nextLine();
    }


    @Test
    public void testTwo() {
        when(userInputMock.nextLine())
                .thenReturn("LADW")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("07/02/2020");
        when(userInputMock.nextInt())
                .thenReturn(3)
                .thenReturn(10);

        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();

        Tool selectedTool = app.toolSelected;
        assertEquals("LADW", selectedTool.getToolCode());
        assertEquals("Ladder", selectedTool.getToolType());
        assertEquals("Werner", selectedTool.getBrand());
        assertEquals(LocalDate.of(2020, 7, 2), app.date);
        assertEquals(3, app.daysToRent);
        assertEquals(10,app.discountPercentage);
        assertEquals(2, app.getChargeDays());
        assertEquals(0.40, Double.parseDouble(numberUSFormat.format(app.getDiscountAmount())), 0.001);
        assertEquals(3.58, Double.parseDouble(numberUSFormat.format(app.getDiscount(app.getBill()))), 0.001);
        verify(userInputMock, times(4)).nextLine();
        verify(userInputMock, times(2)).nextInt();
    }

    @Test
    public void testThree() {
        when(userInputMock.nextLine())
                .thenReturn("CHNS")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("07/02/2015");
        when(userInputMock.nextInt())
                .thenReturn(5)
                .thenReturn(25);

        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();

        Tool selectedTool = app.toolSelected;
        assertEquals("CHNS", selectedTool.getToolCode());
        assertEquals("Chainsaw", selectedTool.getToolType());
        assertEquals("Stihl", selectedTool.getBrand());
        assertEquals(LocalDate.of(2015, 7, 2), app.date);
        assertEquals(5, app.daysToRent);
        assertEquals(25,app.discountPercentage);
        assertEquals(4, app.getChargeDays());
        assertEquals(1.49, Double.parseDouble(numberUSFormat.format(app.getDiscountAmount())), 0.001);
        assertEquals(4.47, Double.parseDouble(numberUSFormat.format(app.getDiscount(app.getBill()))), 0.001);
        verify(userInputMock, times(4)).nextLine();
        verify(userInputMock, times(2)).nextInt();
    }

    @Test
    public void testFour() {
        when(userInputMock.nextLine())
                .thenReturn("JAKD")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("09/03/2015");
        when(userInputMock.nextInt())
                .thenReturn(6)
                .thenReturn(0);

        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();

        Tool selectedTool = app.toolSelected;
        assertEquals("JAKD", selectedTool.getToolCode());
        assertEquals("Jackhammer", selectedTool.getToolType());
        assertEquals("DeWalt", selectedTool.getBrand());
        assertEquals(LocalDate.of(2015, 9, 3), app.date);
        assertEquals(6, app.daysToRent);
        assertEquals(0,app.discountPercentage);
        assertEquals(3, app.getChargeDays());
        assertEquals(0.00, Double.parseDouble(numberUSFormat.format(app.getDiscountAmount())), 0.001);
        assertEquals(8.97, Double.parseDouble(numberUSFormat.format(app.getDiscount(app.getBill()))), 0.001);
        verify(userInputMock, times(4)).nextLine();
        verify(userInputMock, times(2)).nextInt();
    }

    @Test
    public void testFive() {
        when(userInputMock.nextLine())
                .thenReturn("JAKR")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("07/02/2015");
        when(userInputMock.nextInt())
                .thenReturn(9)
                .thenReturn(0);

        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();

        Tool selectedTool = app.toolSelected;
        assertEquals("JAKR", selectedTool.getToolCode());
        assertEquals("Jackhammer", selectedTool.getToolType());
        assertEquals("Ridgid", selectedTool.getBrand());
        assertEquals(LocalDate.of(2015, 7, 2), app.date);
        assertEquals(9, app.daysToRent);
        assertEquals(0,app.discountPercentage);
        assertEquals(7, app.getChargeDays());
        assertEquals(0.00, Double.parseDouble(numberUSFormat.format(app.getDiscountAmount())), 0.001);
        assertEquals(20.93, Double.parseDouble(numberUSFormat.format(app.getDiscount(app.getBill()))), 0.001);
        verify(userInputMock, times(4)).nextLine();
        verify(userInputMock, times(2)).nextInt();
    }

    @Test
    public void testSix() {
        when(userInputMock.nextLine())
                .thenReturn("JAKR")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("07/02/2020");
        when(userInputMock.nextInt())
                .thenReturn(4)
                .thenReturn(50);

        app.getToolCode();
        app.getRentalDays();
        app.getDiscountPercentage();
        app.getCheckoutDate();
        app.displayResult();

        Tool selectedTool = app.toolSelected;
        assertEquals("JAKR", selectedTool.getToolCode());
        assertEquals("Jackhammer", selectedTool.getToolType());
        assertEquals("Ridgid", selectedTool.getBrand());
        assertEquals(LocalDate.of(2020, 7, 2), app.date);
        assertEquals(4, app.daysToRent);
        assertEquals(50,app.discountPercentage);
        assertEquals(2, app.getChargeDays());
        assertEquals(2.99, Double.parseDouble(numberUSFormat.format(app.getDiscountAmount())), 0.001);
        assertEquals(2.99, Double.parseDouble(numberUSFormat.format(app.getDiscount(app.getBill()))), 0.001);
        verify(userInputMock, times(4)).nextLine();
        verify(userInputMock, times(2)).nextInt();
    }
}




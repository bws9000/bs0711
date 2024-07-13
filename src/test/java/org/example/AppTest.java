package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppTest {
    private App app;
    private UserInput userInputMock;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        userInputMock = Mockito.mock(UserInput.class);
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
}




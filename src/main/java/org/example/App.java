package org.example;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class App implements PrimaryPrompts {

    public final String NUMBER_INPUT_ERROR = "*** Please enter a number greater than 0. ***";
    public final String NUMBER_100_ERROR = "*** Please enter a number between 0-100. ***";
    public final String PROMPT_GET_TOOL_CODE = "Enter tool code:";
    public final String PROMPT_GET_TOOL_CODE_ERROR = "*** Tool not found ***";
    public final String PROMPT_GET_USER_RENTAL_DAYS = "Enter how many days you want to rent tool:";
    public final String PROMPT_GET_DISCOUNT_PERCENTAGE = "Enter a Discount Percentage between 1-100:";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String PROMPT_GET_DATE = "Enter the date (format: " + DATE_FORMAT + "): ";
    private static final String PROMPT_DATE_ERROR = "Invalid date format. Please enter the date in the format " + DATE_FORMAT + ".";

    private Map<String, Tool> toolCollection;
    private List<LocalDate> range;
    Tool toolSelected;
    int daysToRent;
    int discountPercentage;
    double discountAmount;
    LocalDate date = null;
    Scanner scanner;
    NumberFormat numberUSFormat;
    double bill;
    int chargeDays = 0;
    UserInput userInput;

    App(UserInput userInput) {
        initializeCollection();
        numberUSFormat = NumberFormat.getNumberInstance(Locale.US);
        numberUSFormat.setMinimumFractionDigits(2);
        numberUSFormat.setMaximumFractionDigits(2);
        this.userInput = userInput;
    }

    public int getChargeDays() {
        return chargeDays;
    }

    public void setChargeDays(int chargeDays) {
        this.chargeDays = chargeDays;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }

    public void setToolSelected(Tool toolSelected) {
        this.toolSelected = toolSelected;
    }

    public Tool getTool(String id){
        return toolCollection.get(id);
    }

    public void initializeCollection(){
        toolCollection = new HashMap<>();
        toolCollection.put("CHNS", new Tool("CHNS", "Chainsaw", "Stihl", "$1.49", true, false, true));
        toolCollection.put("LADW", new Tool("LADW", "Ladder", "Werner", "$1.99", true, true, false));
        toolCollection.put("JAKD", new Tool("JAKD", "Jackhammer", "DeWalt", "$2.99", true, false, false));
        toolCollection.put("JAKR", new Tool("JAKR", "Jackhammer", "Ridgid", "$2.99", true, false, false));
    }

    @Override
    public void getToolCode() {
        Tool tool;
        do {
            System.out.println(PROMPT_GET_TOOL_CODE);
            String toolCodeIn = this.userInput.nextLine();
            tool = getTool(toolCodeIn.trim());
            if(tool != null){
                setToolSelected(tool);
            }else{
                System.out.println(PROMPT_GET_TOOL_CODE_ERROR);
            }
        }while(tool == null);
    }

    @Override
    public void getRentalDays() {
        do {
            System.out.println(PROMPT_GET_USER_RENTAL_DAYS);
            try {
                daysToRent = this.userInput.nextInt();
                if(daysToRent < 1) System.out.println(NUMBER_INPUT_ERROR);
            }catch(Exception e){
                System.out.println(NUMBER_INPUT_ERROR);
                scanner.next();
            }
            this.userInput.nextLine();
        }while(daysToRent == 0);
    }

    @Override
    public void getDiscountPercentage() {
        do {
            System.out.println(PROMPT_GET_DISCOUNT_PERCENTAGE);
            try {
                discountPercentage = this.userInput.nextInt();
                if(discountPercentage < 0) System.out.println(NUMBER_INPUT_ERROR);
                if(discountPercentage > 100) System.out.println(NUMBER_100_ERROR);
            }catch(Exception e){
                System.out.println(NUMBER_INPUT_ERROR);
                discountPercentage = -1;
                scanner.next();
            }
            this.userInput.nextLine();
        }while(discountPercentage > 100 || discountPercentage < 0);
    }

    @Override
    public void getCheckoutDate() {
        while (this.date == null) {
            System.out.println(PROMPT_GET_DATE);
            try {
                String dateIn = this.userInput.nextLine().trim();
                this.setDate(LocalDate.parse(dateIn, DATE_FORMATTER));
                range = this.generateDateRange();
            } catch (Exception e) {
                System.out.println(PROMPT_DATE_ERROR);
            }
        }
    }


    @Override
    public void displayResult() {
        this.generateBill();
        System.out.println("\n\nTool code: " + toolSelected.getToolCode());
        System.out.println("Tool type: " + toolSelected.getToolType());
        System.out.println("Final charge: $" + numberUSFormat.format(getDiscount(getBill())));
        System.out.println("Tool brand: " + toolSelected.getBrand());
        System.out.println("Rental days: " + daysToRent);
        System.out.println("Checkout date: " + getDate());
        System.out.println("Due date: " + getFormattedDueDate());
        System.out.println("Daily rental charge: " + toolSelected.getDailyCharge());
        System.out.println("Charge days: " + this.getChargeDays());
        System.out.println("Pre-discount charge: $" + numberUSFormat.format(getBill()));
        System.out.println("Discount Percent: " + discountPercentage + "%");
        System.out.println("Discount Amount: $" + numberUSFormat.format(getDiscountAmount()));
        this.userInput.close();
    }

    private void generateBill() {
        double dailyCharge = 0.00;
        int chargedDays = 0;

        for (LocalDate date : range) {
            boolean chargeIt = false;
            if(!isHoliday(date)) {
                if (!this.isWeekend(date) && toolSelected.isWeekdayCharge()) {
                    chargeIt = true;
                }
                if (this.isWeekend(date) && toolSelected.isWeekendCharge()) {
                    chargeIt = true;
                }
            } else if (toolSelected.isHolidayCharge()) {
                chargeIt = true;
            }

            if (chargeIt) {
                dailyCharge += convertStringToDouble(toolSelected.getDailyCharge());
                chargedDays++;
            }
        }
        this.setChargeDays(chargedDays);
        this.setBill(dailyCharge);
    }

    private double getDiscount(double totalCharge){
        double discount = (double) discountPercentage / 100;
        double totalAfterDiscount = totalCharge * (1 - discount);
        this.setDiscountAmount(totalCharge - totalAfterDiscount);
        return totalAfterDiscount;
    }

    private double convertStringToDouble(String stringCurrNumber) {
        String sanitizedString = stringCurrNumber.replace("$", "").replace(",", "");
        return Double.parseDouble(sanitizedString);
    }

    private LocalDate generateDueDate(){
        String formattedDate = this.getDate();
        LocalDate parsedDate = LocalDate.parse(formattedDate, DATE_FORMATTER);
        return parsedDate.plusDays(daysToRent);
    }
    private String getFormattedDueDate() {
        LocalDate dueDate = this.generateDueDate();
        return dueDate.format(DATE_FORMATTER);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isJulyFourth(LocalDate date) {
        return date.getMonth() == Month.JULY && date.getDayOfMonth() == 4;
    }

    private static boolean isLaborDay(LocalDate date) {
        if (date.getMonth() != Month.SEPTEMBER) {
            return false;
        }
        LocalDate firstMondayOfSeptember = LocalDate.of(date.getYear(), Month.SEPTEMBER, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        return date.equals(firstMondayOfSeptember);
    }

    private boolean isHoliday(LocalDate date) {
        return isJulyFourth(date) || isLaborDay(date);
    }

    private List<LocalDate> generateDateRange() {
        LocalDate startDate = this.date;
        LocalDate endDate = startDate.plusDays(daysToRent - 1);

        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dateList;
    }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

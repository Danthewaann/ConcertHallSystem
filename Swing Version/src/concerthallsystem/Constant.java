package concerthallsystem;

import java.awt.Color;
import java.text.DecimalFormat;

/**
 * This is a constants class that provides all the constant variables that the
 * rest of the classes within this package can use. 
 * More colours, seat rows, and seat numbers can be added here if the seating plan
 * changes in the future, such as every row will now have 12 seats, 
 * or more rows are added to make a new seat section etc. 
 * 
 * @author Daniel Black, George Bingham, Rebecca Curtis, Minyu Lei
 */

public class Constant 
{
    public static final Color GOLD = new Color(255, 223, 0);
    public static final Color SILVER = new Color(192, 192, 192);
    public static final Color BRONZE = new Color(205, 127, 50);
    public static final Color RED = new Color(255, 0, 0);
    public static final String[] SEAT_SECTIONS = {"GOLD", "SILVER", "BRONZE"};
    public static final String[] SEAT_ROWS = {"A","B","C","D","E","F","G","H","I"};
    public static final int[] SEAT_NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int TOTAL_SEATS = Constant.SEAT_ROWS.length * Constant.SEAT_NUMBERS.length;
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.00");
}

package concerthallsystem;

import java.awt.Color;
import java.awt.Cursor;
import java.text.DecimalFormat;

/**
 * This is a constants class that provides all the constant variables that the
 * rest of the classes within this package can use, such as the concerts
 * directory and concert_list file and the colours that all the seatIcons can use 
 *   
 * @author Daniel Black
 */

public class Constant 
{
    public static final Color GOLD = new Color(255, 223, 0);
    public static final Color SILVER = new Color(192, 192, 192);
    public static final Color BRONZE = new Color(205, 127, 50);
    public static final Color RED = new Color(255, 0, 0);
    public static final String DIRECTORY = "Concerts";
    public static final String CONCERTS_FILE = "Concert_list.txt";    
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.00");
    public static final Cursor SELECT_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    
    //Used to capitalize any string e.g "daniel black" becomes "Daniel Black"
    public static String capitalize(String name)
    {
        String result = "";
        char[] nameChars = name.toLowerCase().toCharArray();                  
        for(int i = 0; i < nameChars.length; i++)
        {
            if(i == 0)
            {
                result += Character.toUpperCase(nameChars[i]);
            }
            else if(Character.isWhitespace(nameChars[i-1]))
            {
                result += Character.toUpperCase(nameChars[i]);
            }
            else
            {
                result += nameChars[i];
            }            
        }
        return result;
    }
}

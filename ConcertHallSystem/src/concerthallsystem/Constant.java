package concerthallsystem;

/**
 * This is a constants class that provides all the constant variables that the
 * rest of the classes within this package can use, such as the concerts
 * directory and concert_list file and the colours that all the seatIcons can use 
 *   
 * @author Daniel Black
 */

public class Constant 
{             
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

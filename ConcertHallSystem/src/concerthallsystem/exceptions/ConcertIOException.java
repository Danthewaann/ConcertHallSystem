package concerthallsystem.exceptions;

/**
 * This class is thrown when a concert fails to load from file
 * 
 * @author Daniel
 */

public class ConcertIOException extends RuntimeException 
{
  
    public ConcertIOException(int lineNum)
    {
        super("Failed to load concert on line " + lineNum);
    }
    
}

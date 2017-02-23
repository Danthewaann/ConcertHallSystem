package concerthallsystem.exceptions;

/**
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

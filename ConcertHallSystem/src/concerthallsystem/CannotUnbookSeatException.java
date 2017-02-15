package concerthallsystem;

/**
 *
 * @author Daniel Black
 */

public class CannotUnbookSeatException extends RuntimeException 
{
    public CannotUnbookSeatException(String seatPos, String reason) 
    {
        super("Cannot unbook seat " + "(" + seatPos + ")\n" + reason);
    }   
}

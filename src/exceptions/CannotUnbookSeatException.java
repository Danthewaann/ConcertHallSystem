package exceptions;

/**
 * This class is thrown when a user tries to un-book a seat that throws this
 * exception in its unBook() method
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

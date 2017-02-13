package concerthallsystem;

/**
 *
 * @author Daniel Black
 */

public class ConcertAlreadyExistsException extends RuntimeException
{
    public ConcertAlreadyExistsException()
    {
        super("This concert already exists, do you want to override it?");
    }
}

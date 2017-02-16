package concerthallsystem;

/**
 *
 * @author Daniel Black
 */

public class ConcertAlreadyExistsException extends RuntimeException
{
    public ConcertAlreadyExistsException(Concert concert)
    {
        super(
            "Concert: " + concert.getName() + " " + concert.getDateWithSlashes() 
            + " already exists, do you want to override it?"
        );
    }
}

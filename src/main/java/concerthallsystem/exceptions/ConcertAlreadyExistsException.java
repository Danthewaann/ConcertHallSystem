package concerthallsystem.exceptions;

import concerthallsystem.main.Concert;

/**
 * This class is thrown when a concert is loaded into the same that has
 * the same name and date that a currently loaded concert in the system has,
 * also when the user tries to create a new concert that also already exists in the system
 *
 * @author Daniel Black
 */

public class ConcertAlreadyExistsException extends RuntimeException
{
    public ConcertAlreadyExistsException(Concert concert)
    {
        super(
                "Concert: " + concert + " already exists, do you want to override it?"
        );
    }

    public ConcertAlreadyExistsException(String concertList, String location)
    {
        super(
                "Found two or more concerts stored on file that are the same:\n" + concertList
                        + "\n...in location " + location + "\n" + "Change the name/date of them, then restart the program"
        );
    }
}

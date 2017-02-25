package concerthallsystem.exceptions;

import concerthallsystem.Concert;

/**
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

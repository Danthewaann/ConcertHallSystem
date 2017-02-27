package concerthallsystem.exceptions;

import concerthallsystem.Concert;
import java.io.File;

/**
 * This exception is still a work in progress, still trying to figure out how
 * to send information to this class so I can accurately report the errors that
 * occur when a seat fails to load/save
 * 
 * @author Daniel Black
 */

public class SeatIOException extends RuntimeException 
{
    private Concert concert;
    private String seatRow;
    private int seatNum;
    private int seatIndex;
    private String bookee;
    private int seatLineNum;
   
    public SeatIOException(Concert concert)
    {
        this.concert = concert;
    }

    public SeatIOException(Concert concert, String seatRow, int seatNum, int seatIndex, String bookee, int seatLineNum) 
    {
        this.concert = concert;
        this.seatRow = seatRow;
        this.seatNum = seatNum;
        this.seatIndex = seatIndex;
        this.bookee = bookee;
        this.seatLineNum = seatLineNum;
    }

    @Override
    public String getMessage() 
    {              
        boolean invalidIndex = false;
        boolean invalidRow = false;
        boolean invalidNum = false;
              
        if(this.seatIndex < 0 || this.seatIndex > Concert.TOTAL_SEATS - 1)
        {
            invalidIndex = true;
            return "Seat Index of a seat for concert " + this.concert + " is invalid";
        }    
        if(this.seatRow == null) 
        {
            return( 
                "Seat (" + this.seatRow + this.seatNum + ") for concert " 
                + this.concert + " isn't an actual seat in location " + "Concerts" + File.separator 
                + this.concert + File.separator + "Booked_seats.txt"
            );
        }
        else
        {
            return "";
        }
        
    }
    
}

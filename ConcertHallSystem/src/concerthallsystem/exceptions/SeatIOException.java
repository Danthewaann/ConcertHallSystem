package concerthallsystem.exceptions;

import concerthallsystem.Concert;
import concerthallsystem.Constant;
import java.io.File;

/**
 *
 * @author Daniel
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
            return "Seat Index of a seat for concert " + this.concert.getName() + " " + this.concert.getDateWithSlashes() + " is invalid";
        }    
        if(this.seatRow == null) 
        {
            return( 
                "Seat (" + this.seatRow + this.seatNum + ") for concert " 
                + this.concert.getName() + " " + this.concert.getDateWithSlashes()
                + " isn't an actual seat in location " + Constant.DIRECTORY + File.separator 
                + this.concert.getName() + this.concert.getDate() + File.separator + "Booked_seats.txt"
            );
        }
        else
        {
            return "";
        }
        
    }
    
}

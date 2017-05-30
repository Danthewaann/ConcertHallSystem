package concerthallsystem.exceptions;

import concerthallsystem.Concert;

public class ConcertIOException extends RuntimeException
{
    private Concert concert;

    public ConcertIOException(Concert concert, String errorReport, int lineNum)
    {
        super("\tFailed to fully load a concert on line " + lineNum + "\n" + errorReport);
        this.concert = concert;
    }

    public Concert getConcert()
    {
        return this.concert;
    }
}

package concerthallsystem.exceptions;

import concerthallsystem.Concert;

import java.util.List;

public class ConcertIOException extends RuntimeException
{
    private Concert concert;
    private List<RuntimeException> errors;

    public ConcertIOException(Concert concert, List<RuntimeException> errors)
    {
        this.concert = concert;
        this.errors = errors;
    }

    public Concert getConcert()
    {
        return this.concert;
    }

    @Override
    public String getMessage()
    {
        String errorReport = "";
        for(RuntimeException error : this.errors) {
            errorReport += error.getMessage();
        }
        return "\tFailed to fully load a concert on line " + concert.getLinePosition() + "\n" + errorReport;
    }
}

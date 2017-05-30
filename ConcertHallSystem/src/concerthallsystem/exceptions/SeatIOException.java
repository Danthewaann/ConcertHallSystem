package concerthallsystem.exceptions;

import java.io.File;

public class SeatIOException extends RuntimeException
{
    public SeatIOException(File seatsFile, int lineNum)
    {
        super("\t\tFailed to load seat on line " + lineNum
            + " ...in location " + seatsFile + "\n");
    }
}

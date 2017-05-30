package concerthallsystem.exceptions;

import java.io.File;

public class CustomerIOException extends RuntimeException
{
    public CustomerIOException(File customersFile, int lineNum)
    {
        super("\t\tFailed to load customer on line " + lineNum
                + " ...in location " + customersFile + "\n");
    }
}

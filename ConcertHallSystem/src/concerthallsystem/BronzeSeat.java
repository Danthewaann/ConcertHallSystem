package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;

/**
 * This class is a derived class of the Seat abstract class
 * 
 * @author Daniel Black
 */

public class BronzeSeat extends Seat
{           
    public BronzeSeat(String row, int num)
    {
        super(row, num);
    }

    public BronzeSeat(Seat seat)
    {
        super(seat);
    }

    @Override
    public void unBook(Customer customer) throws CannotUnbookSeatException
    {
        throw new CannotUnbookSeatException(
            this.toString(), "Reason: seat (" + this.toString() + ") is in the Bronze Section"
        );
    }       
}


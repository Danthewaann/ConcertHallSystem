package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;

public class BronzeSeat extends Seat
{   
    public BronzeSeat(String row, int num, int index)
    {
        super(row, num, index);
    }   
    
    public BronzeSeat(String row, int num)
    {
        super(row, num);
    }

    @Override
    public void unBook(Customer customer) throws CannotUnbookSeatException
    {
        throw new CannotUnbookSeatException(
            this.getPosition(), "Reason: seat (" + this.getPosition() + ") is in the Bronze Section"
        );
    }       
}


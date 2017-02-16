package concerthallsystem;

public class BronzeSeat extends Seat
{   
    public BronzeSeat(String row, int num, int index)
    {
        super(row, num, index);
    }   

    @Override
    public void unBook(Customer customer) throws CannotUnbookSeatException
    {
        throw new CannotUnbookSeatException(
            this.getPosition(), "Reason: seat (" + this.getPosition() + ") is in the Bronze section"
        );
    }       
}


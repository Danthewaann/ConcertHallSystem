package concerthallsystem;

/**
 * This class is a derived class of the Seat abstract class
 * 
 * @author Daniel Black
 */

public class GoldSeat extends Seat
{          
    public GoldSeat(String row, int num)
    {
        super(row, num);
    }

    public GoldSeat(Seat seat)
    {
        super(seat);
    }

    @Override
    public void book(Customer customer) 
    {
        super.book(customer); 
        customer.setEntitlement(this);
    }
}

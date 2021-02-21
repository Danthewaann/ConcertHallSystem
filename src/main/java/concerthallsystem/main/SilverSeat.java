package concerthallsystem.main;

/**
 * This class is a derived class of the main.Seat abstract class
 *
 * @author Daniel Black
 */

public class SilverSeat extends Seat
{
    public SilverSeat(String row, int num)
    {
        super(row, num);
    }

    public SilverSeat(Seat seat)
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

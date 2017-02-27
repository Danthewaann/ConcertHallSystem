package concerthallsystem;

/**
 * This class is a derived class of the Seat abstract class
 * 
 * @author Daniel Black
 */

public class SilverSeat extends Seat
{      
    public SilverSeat(String row, int num)
    {
        super(row, num);
    }
    
    @Override
    public void book(Customer customer) 
    {
        super.book(customer); 
        customer.setEntitlement(this);
    }  
}

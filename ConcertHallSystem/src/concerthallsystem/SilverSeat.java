package concerthallsystem;

public class SilverSeat extends Seat
{
    public SilverSeat(String row, int num, int index)
    {
        super(row, num, index);
    }  
    
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

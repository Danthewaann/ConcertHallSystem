package concerthallsystem;

public class GoldSeat extends Seat
{          
    public GoldSeat(String row, int num)
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

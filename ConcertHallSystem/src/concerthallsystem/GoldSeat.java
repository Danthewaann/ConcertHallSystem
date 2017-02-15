package concerthallsystem;

public class GoldSeat extends Seat
{   
    public GoldSeat(String row, int num, int index)
    {
        super(row, num, index);
    } 

    @Override
    public void book(Customer customer) 
    {
        super.book(customer); 
        customer.setEntitlement(this);
    }  
}

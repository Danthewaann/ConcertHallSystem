package concerthallsystem;

public class SilverSeat extends Seat
{
    public SilverSeat(String row, int num)
    {
        super(row, num);
    }
    
    @Override
    public void book(String name)
    {
        this.setBookee(name);
        this.setStatus(true);
        this.setEntitled(true);
    }
        
    @Override
    public String getEntitlement()
    {       
        return this.getBookee() + " is eligible for a free programme";                       
    }
    
    @Override
    public void unBook()
    {
        this.setStatus(false);
        this.setBookee(null);
    }
}

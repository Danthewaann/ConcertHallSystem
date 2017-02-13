package concerthallsystem;

public class GoldSeat extends Seat
{   
    public GoldSeat(String row, int num, int index)
    {
       super(row, num, index);
    }
    
    @Override
    public void book(String name)
    {               
        this.setBookee(name);
        this.setStatus(true);                                 
    }
               
    @Override
    public void unBook()
    {
        this.setStatus(false);
        this.setBookee(null);
    }     
}

package concerthallsystem;

public class GoldSeat extends Seat
{   
    public GoldSeat(String row, int num)
    {
       super(row, num);
    }
    
    @Override
    public void book(String name)
    {               
        this.setBookee(name);
        this.setStatus(true);           
        this.randomiser();                  
    }
    
    private void randomiser()
    {
        //Selects a random number between 1 and 10
        int randomNum = (int)(Math.random() * 10 + 1 );
        if(randomNum == 1)
        {           
            this.setEntitled(true);
        }
    }
    
    @Override
    public String getEntitlement()
    {
        String entitled;
        if(this.getEntitled())
        {
            entitled = this.getBookee() + " is eligible for a back-stage pass";
        }
        else
        {
            entitled = this.getBookee() + " is ineligible for any entitlements";
        }  
        
        return entitled;
    }
    
    @Override
    public void unBook()
    {
        this.setStatus(false);
        this.setBookee(null);
    } 
}

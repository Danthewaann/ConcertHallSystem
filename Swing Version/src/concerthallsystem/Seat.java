package concerthallsystem;

/**
* The Seat class is an abstract class that takes on the
* form of either the GoldSeat, SilverSeat or BronzeSeat subclasses,
* this class holds the important variables and methods each subclass
* of this class should have, and each subclass can override these methods
* when they need to, or don't even implement them at all.
* The Concert class accesses each individual seat through the Seat class
*
@author Daniel Black, George Bingham, Rebecca Curtis, Minyu Lei
*/

public abstract class Seat
{
    private double price_;
    private boolean isBooked_ = false;
    private boolean isEntitled_ = false;
    private String bookedBy_ = null;
    private String row_;
    private int number_;
    
    public Seat(String row, int num)
    {
        this.row_ = row;
        this.number_ = num;
    }
    
    public void book(String name)
    {
        this.setStatus(true);
        this.setBookee(name);
    }
    
    public void unBook()
    {
    }
   
    public boolean getStatus()
    {
        return this.isBooked_;
    }
    
    public String getBookee()
    {       
        return this.bookedBy_;
    }
    
    public String getSeat()
    {
        return this.row_ + this.number_;
    }
    
    public String getRow()
    {
        return this.row_;
    }
    
    public int getNumber()
    {
        return this.number_;
    }
        
    public String getEntitlement()
    {
        return this.bookedBy_ + " is ineligible for any entitlements";
    }
    
    public double getPrice()
    {
        return this.price_;
    }
    
    public void setPrice(double price)
    {
        this.price_ = price;
    }
           
    public void setStatus(boolean status)
    {
        this.isBooked_ = status;
    }
    
    public void setBookee(String name)
    {
        this.bookedBy_ = name;
    }
    
    public void setEntitled(boolean entitled)
    {
        this.isEntitled_ = entitled;
    }
    
    public boolean getEntitled()
    {
        return this.isEntitled_;
    }
}

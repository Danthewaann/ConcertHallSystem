
package concerthallsystem;

import concerthallsystem.exceptions.CustomerIOException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * The Customer class is used to represent each customer,
 * and each customer holds a list of seats that they have 
 * booked in a particular concert that they are in.
 * This class also allows you to get the entitlement that
 * a particular customer has, depending if they are eligible for one
 * 
 * @author Daniel Black
 */

public class Customer implements Comparable
{
    private final List<Seat> bookedSeats;
    private String name_;
    private boolean goldEntitled_ = false;
    private boolean silverEntitled_ = false;
    
    public Customer(String name)
    {
        this.name_ = name;
        this.bookedSeats = new ArrayList<>();
    }
    
    private Customer()
    {                    
        this.bookedSeats = new ArrayList<>();        
    }
    
    public String getName()
    {
        return this.name_;
    }
         
    public void setEntitlement(Seat seat)
    {        
        if(seat.getClass().getSimpleName().equals("GoldSeat")) {
            //1 in 10 chance of getting a free backstage pass
            int randomNum = ((int)(Math.random() * 10 + 1 ));
            if(randomNum == 1) {           
                this.goldEntitled_ = true;
            }
        }
        else if(seat.getClass().getSimpleName().equals("SilverSeat")) {
            this.silverEntitled_ = true;
        }
    }
       
    public String getEntitlement()
    {
        String result = null;
        if(this.goldEntitled_) {
            result = "a free backstage pass";
        }
        if(this.silverEntitled_) {
            if(result != null) {
                result += " " + "and a free programme";
            }
            else {
                result = "a free programme";
            }
        }
        return result;
    }
    
    public void addSeat(Seat seat)
    {
        this.bookedSeats.add(seat);
        this.bookedSeats.sort(null);
    }
    
    public void removeSeat(Seat seat)
    {
        this.bookedSeats.remove(seat);
        this.bookedSeats.sort(null);              
    }
    
    public boolean hasBookedASeat()
    {
        return this.bookedSeats.size() > 0;
    }
    
    public List<Seat> getBookedSeats()
    {
        return this.bookedSeats;
    }
          
    public boolean save(PrintWriter output)
    {
        try {
            output.println(
                this.name_ + " " + this.goldEntitled_ + " " + this.silverEntitled_
            );
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    //TODO - Need to create a regex for true/false boolean
    public static Customer load(Scanner input, File customersFile, int customerLineNum) throws CustomerIOException
    {
        Customer result = new Customer();
        try {
            result.name_ = input.next();  
            while(!input.hasNextBoolean()) {
                result.name_ += " " + input.next();
            }
            result.goldEntitled_ = input.nextBoolean();
            result.silverEntitled_ = input.nextBoolean();            
        }
        catch(Exception ex) {
            throw new CustomerIOException(customersFile, customerLineNum);
        }
        finally {
            input.nextLine();
        }
        return result;
    }    

    @Override
    public boolean equals(Object obj) 
    {
        if(obj.getClass().isInstance(this)) {
            if(this.hashCode() == obj.hashCode()) {
                return true;
            }
        }
        return false;          
    }
    
    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name_);
        return hash;
    }

    @Override
    public int compareTo(Object obj) 
    {
        if(this.hashCode() < obj.hashCode()) {
            return -1;
        }
        else if(this.hashCode() == obj.hashCode()) {
            return 0;
        }
        else {
            return 1;
        }
    }  
}

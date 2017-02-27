
package concerthallsystem;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Customer class is used to represent each customer,
 * and each customer holds a list of seats that they have 
 * booked in a particular concert that they are in.
 * This class also allows you to get the entitlement that
 * a particular customer has, depending if they are eligible for one
 * 
 * @author Daniel Black
 */

public class Customer 
{
    private final ArrayList<Seat> bookedSeats;
    private int nBookedSeats;
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
        this.nBookedSeats++;
    }
    
    public void removeSeat(Seat seat)
    {
        this.bookedSeats.remove(seat);
        this.bookedSeats.sort(null);
        this.nBookedSeats--;           
    }
    
    public boolean hasBookedASeat()
    {
        return this.nBookedSeats > 0;
    }
    
    public ArrayList<Seat> getBookedSeats()
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
    
    public static Customer load(Scanner input, File customerFile, int customerLineNum)
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
        //If any info is incorrect, detail them then return null
        catch(InputMismatchException ex) {            
            System.out.println(
                "Fatal Error: Failed to load customer on line " + customerLineNum + "...\n"
                + "...in location " + customerFile
            );            
            return null;
        }
        finally {
            input.nextLine();
        }        
        return result;
    }               
}

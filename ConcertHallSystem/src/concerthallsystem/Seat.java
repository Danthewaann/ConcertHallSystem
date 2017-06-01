package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import concerthallsystem.exceptions.SeatIOException;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
* The Seat class is an abstract class that takes on the
* form of either the GoldSeat, SilverSeat or BronzeSeat subclasses,
* this class holds the important variables and methods each subclass
* of this class should have, and each subclass can override these methods
* when they need to, or don't even implement them at all.
* The Concert class accesses each individual seat through the Seat class
*
* @author Daniel Black
*/

public class Seat implements Comparable
{
    private double price_;
    private boolean isBooked_;  
    private String bookedBy_;
    private String row_;
    private int number_;
       
    public Seat(String row, int num)
    {
        this.row_ = row;
        this.number_ = num;
    }

    private Seat(){}

    protected Seat(Seat seat)
    {
        this.row_ = seat.row_;
        this.number_ = seat.number_;
        this.bookedBy_ = seat.bookedBy_;
        this.isBooked_ = true;
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
    
    public void book(Customer customer)
    {
        this.setBookee(customer.getName());
        customer.addSeat(this);        
    }
    
    public void unBook(Customer customer) throws CannotUnbookSeatException
    {
        this.setBookee(null);
        customer.removeSeat(this);
    }
    
    public boolean save(PrintWriter output)
    {             
        try {
            output.println(
                this.row_ + " " + this.number_ + " " + this.bookedBy_
            );  
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }
    
    public static Seat load(Scanner input, File seatsFile, int seatLineNum) throws SeatIOException
    {                                 
        Seat temp = new Seat();
        try {
            temp.row_ = input.next().toUpperCase();
            temp.number_ = input.nextInt();
            temp.bookedBy_= input.next().trim();
            
            //Check if seatRow is an actual row defined by its concert
            int rowIndex = 0;
            while(temp.row_.compareToIgnoreCase(Concert.SEAT_ROWS[rowIndex]) != 0) {
                rowIndex++;
            }

            //Check if seatNum is an actual number defined by its concert
            int numIndex = 0;
            while(temp.number_ != (Concert.SEAT_NUMBERS[numIndex])) {
                numIndex++;
            }

            if(rowIndex < 3) {
                temp = new GoldSeat(temp);
            }
            else if(rowIndex < 6) {
                temp = new SilverSeat(temp);
            }
            else {
                temp = new BronzeSeat(temp);
            }
        }
        catch(ArrayIndexOutOfBoundsException | NoSuchElementException ex) {
            throw new SeatIOException(seatsFile, seatLineNum);
        }
        finally {
            input.nextLine();
        }
        return temp;
    }
      
    public boolean getStatus()
    {
        return this.isBooked_;
    }
    
    public String getBookee()
    {       
        return this.bookedBy_;
    }

    public double getPrice()
    {
        return this.price_;
    }
          
    public void setPrice(double price)
    {
        this.price_ = price;
    }
    
    public void setBookee(String name)
    {
        if(name != null) {
            this.isBooked_ = true;
        }
        else {
            this.isBooked_ = false;
        }
        this.bookedBy_ = name;
    }    
      
    @Override
    public String toString() 
    {
        return this.row_ + this.number_;
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
        hash = 67 * hash + Objects.hashCode(this.row_);
        hash = 67 * hash + this.number_;
        return hash;
    } 
}

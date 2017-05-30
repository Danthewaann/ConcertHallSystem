package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import concerthallsystem.exceptions.SeatIOException;

import java.io.File;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
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

public abstract class Seat implements Comparable
{
    private double price_;
    private boolean isBooked_;  
    private String bookedBy_;
    private final String row_;
    private final int number_;    
       
    public Seat(String row, int num)
    {
        this.row_ = row;
        this.number_ = num;
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
                this.getRow() + " " + this.getNumber() + " " + this.getBookee()                           
            );  
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }
    
    public static Seat load(Scanner input, File seatsFile, int seatLineNum) throws SeatIOException
    {                                 
        Seat result;
        String seatRow, bookee;
        int seatNum;
        int rowIndex = 0;
        int numIndex = 0;
        try {           
            seatRow = input.next(Pattern.compile("[a-iA-I]")).toUpperCase();
            seatNum = input.nextInt();
            bookee = input.nextLine().trim();            
            
            //Check if seatRow is an actual row defined by its concert
            while(seatRow.compareToIgnoreCase(Concert.SEAT_ROWS[rowIndex]) != 0) {
                rowIndex++;
            }

            //Check if seatNum is an actual number defined by its concert
            while(seatNum != (Concert.SEAT_NUMBERS[numIndex])) {
                numIndex++;
            }
           
            //Return the seat type depending on its row
            if(rowIndex < 3) {
                result = new GoldSeat(seatRow, seatNum);
                result.setBookee(bookee);
            }
            else if(rowIndex < 6) {
                result = new SilverSeat(seatRow, seatNum);
                result.setBookee(bookee);
            }
            else {
                result = new BronzeSeat(seatRow, seatNum);
                result.setBookee(bookee);                             
            }
        }
        catch(Exception ex) {
            throw new SeatIOException(seatsFile, seatLineNum);
        }
        return result;
    }
      
    public boolean getStatus()
    {
        return this.isBooked_;
    }
    
    public String getBookee()
    {       
        return this.bookedBy_;
    }
       
    public String getRow()
    {
        return this.row_;
    }
    
    public int getNumber()
    {
        return this.number_;
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
        if(name != null) {
            this.setStatus(true);
        }
        else {
            this.setStatus(false);
        }
        this.bookedBy_ = name;
    }    
      
    @Override
    public String toString() 
    {
        return this.getRow() + this.getNumber();
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

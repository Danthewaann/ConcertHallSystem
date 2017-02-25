package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import java.io.File;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

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
    private boolean isBooked_ = false;   
    private String bookedBy_ = null;
    private String row_;
    private int number_;
    private int index_;
    
    public Seat(String row, int num, int index)
    {
        this.row_ = row;
        this.number_ = num;
        this.index_ = index;
    }
    
    public Seat(String row, int num)
    {
        this.row_ = row;
        this.number_ = num;
    }
    
    private Seat()
    {
        
    }
            
    @Override
    public int compareTo(Object seat)
    {
        if(this.index_ < ((Seat) seat).getIndex())
        {
            return -1;
        }
        else if(this.index_ == ((Seat) seat).getIndex())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
    public void book(Customer customer)
    {
        this.setStatus(true);
        this.setBookee(customer.getName());
        customer.addSeat(this);        
    }
    
    public void unBook(Customer customer) throws CannotUnbookSeatException
    {
        this.setStatus(false);
        this.setBookee(null);
        customer.removeSeat(this);
    }
    
    public boolean save(PrintWriter output)
    {             
        try
        {
            output.println(
                this.getIndex() + " " + this.getRow() + " " + this.getNumber() + " " + this.getBookee()                           
            );  
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    public static Seat load(Scanner input, Concert concert, File seatsFile, int seatLineNum)
    {                                 
        Seat result;
        String seatRow, bookee;
        int seatIndex, seatNum;
        int rowIndex = 0;
        int numIndex = 0;
        try
        {
            seatIndex = input.nextInt();
            seatRow = input.next();
            seatNum = input.nextInt();
            bookee = input.nextLine().trim();            
            
            //Check if seatRow is an actual row defined by its concert
            while(seatRow.compareToIgnoreCase(Concert.SEAT_ROWS[rowIndex]) != 0)
            {
                rowIndex++;
            }

            //Check if seatNum is an actual number defined by its concert
            while(seatNum != (Concert.SEAT_NUMBERS[numIndex]))
            {
                numIndex++;
            }
           
            //Return the seat type depending on its row
            if(rowIndex < 3)
            {
                result = new GoldSeat(seatRow, seatNum, seatIndex);
                result.setStatus(true);
                result.setBookee(bookee);
                result.setPrice(concert.getSectionPrice("Gold"));
                return result;
            }
            else if(rowIndex < 6)
            {
                result = new SilverSeat(seatRow, seatNum, seatIndex);
                result.setStatus(true);
                result.setBookee(bookee);            
                result.setPrice(concert.getSectionPrice("Silver"));
                return result;
            }
            else
            {
                result = new BronzeSeat(seatRow, seatNum, seatIndex);
                result.setStatus(true);
                result.setBookee(bookee);             
                result.setPrice(concert.getSectionPrice("Bronze"));
                return result;
            }
        }
        //If any info is incorrect detail them then return null
        catch(InputMismatchException | ArrayIndexOutOfBoundsException ex)
        {                     
            input.nextLine();
            System.out.println(
                "Fatal Error: Failed to load seat on line " + seatLineNum + "...\n"
                + "...in location " + seatsFile
            ); 
            return null;
        }        
    }
      
    public boolean getStatus()
    {
        return this.isBooked_;
    }
    
    public String getBookee()
    {       
        return this.bookedBy_;
    }
    
    public String getPosition()
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
    
    public int getIndex()
    {
        return this.index_;
    }
}

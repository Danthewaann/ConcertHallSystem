package concerthallsystem.main;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import concerthallsystem.exceptions.SeatIOException;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The main.Seat class is an class that takes on the
 * form of either the main.GoldSeat, main.SilverSeat or main.BronzeSeat subclasses,
 * this class holds the important variables and methods each subclass
 * of this class should have, and each subclass can override these methods
 * when they need to, or don't even implement them at all.
 * The main.Concert class accesses each individual seat through the main.Seat class
 *
 * @author Daniel Black
 */

public class Seat implements Comparable<Seat>
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
        this(seat.row_, seat.number_);
        this.bookedBy_ = seat.bookedBy_;
        this.isBooked_ = true;
    }

    @Override
    public int compareTo(Seat obj)
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
            output.printf("%s %d %s%n", this.row_, this.number_, this.bookedBy_);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    public static Seat load(Scanner input, File seatsFile, int seatLineNum) throws SeatIOException
    {
        Seat temp = new Seat();
        Pattern rowPattern = Pattern.compile("[a-iA-I]");
        Pattern numPattern = Pattern.compile("[1-9]|10");
        try {
            if(input.hasNext(rowPattern)) {
                temp.row_ = input.next().toUpperCase();
            }
            else {
                throw new InputMismatchException();
            }

            if(input.hasNext(numPattern)) {
                temp.number_ = input.nextInt();
            }
            else {
                throw new InputMismatchException();
            }

            while(input.hasNext()) {
                if(!input.hasNext(rowPattern)) {
                    if(temp.bookedBy_ != null) {
                        temp.bookedBy_ += " " + input.next();
                    }
                    else {
                        temp.bookedBy_ = input.next();
                    }
                }
                else {
                    if(temp.bookedBy_.length() > 0) {
                        break;
                    }
                    else {
                        throw new InputMismatchException();
                    }
                }
            }

            if(Pattern.compile("[A-C]").pattern().contains(temp.row_) ) {
                temp = new GoldSeat(temp);
            }
            else if(Pattern.compile("[D-F]").pattern().contains(temp.row_)) {
                temp = new SilverSeat(temp);
            }
            else if(Pattern.compile("[G-I]").pattern().contains(temp.row_)) {
                temp = new BronzeSeat(temp);
            }
        }
        catch(ArrayIndexOutOfBoundsException | NoSuchElementException ex) {
            throw new SeatIOException(seatsFile, seatLineNum);
        }
        finally {
            if(input.hasNextLine()) {
                input.nextLine();
            }
        }
        return temp;
    }

    public String getRow()
    {
        return this.row_;
    }

    public int getNumber()
    {
        return this.number_;
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

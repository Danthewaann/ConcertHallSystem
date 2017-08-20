package main;

import exceptions.CustomerIOException;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The main.Customer class is used to represent each customer,
 * and each customer holds a list of seats that they have
 * booked in a particular concert that they are in.
 * This class also allows you to get the entitlement that
 * a particular customer has, depending if they are eligible for one
 *
 * @author Daniel Black
 */

public class Customer implements Comparable<Customer>
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
        if(seat.getClass().getSimpleName().equals("main.GoldSeat")) {
            //1 in 10 chance of getting a free backstage pass
            int randomNum = ((int)(Math.random() * 10 + 1 ));
            if(randomNum == 1) {
                this.goldEntitled_ = true;
            }
        }
        else if(seat.getClass().getSimpleName().equals("main.SilverSeat")) {
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
            output.printf("%s %b %b%n", this.name_, this.goldEntitled_, this.silverEntitled_);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    public static Customer load(Scanner input, File customersFile, int customerLineNum) throws CustomerIOException
    {
        Customer temp = new Customer();
        Pattern boolPattern = Pattern.compile("true|false|TRUE|FALSE");
        try {
            while(input.hasNext()) {
                if(!input.hasNext(boolPattern)) {
                    if (temp.name_ != null) {
                        temp.name_ += " " + input.next();
                    }
                    else {
                        temp.name_ = input.next();
                    }
                }
                else {
                    if(temp.name_.length() > 0) {
                        break;
                    }
                    else {
                        throw new InputMismatchException();
                    }
                }
            }
            temp.goldEntitled_ = input.nextBoolean();
            temp.silverEntitled_ = input.nextBoolean();
        }
        catch(NoSuchElementException ex) {
            throw new CustomerIOException(customersFile, customerLineNum);
        }
        finally {
            if(input.hasNextLine()) {
                input.nextLine();
            }
        }
        return temp;
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
    public int compareTo(Customer obj)
    {
        return this.name_.compareTo(obj.name_);
    }
}

package concerthallsystem;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The Concert class holds all the important information that can
 * be accessed through the MainGUI. Each concert, when created, automatically 
 * populates the Seat object array with either gold, silver or bronze seats.
 * To access these seats through the GUI, methods can be called on a concert
 * to retrieve important information about each seat from that particular concert,
 * or more general information about the specific concert
 * Each concert also holds a list of customers, which in turn holds a list
 * of seats that they have booked so they can be recalled by the concert.
 * 
 * @author Daniel Black
 */

public class Concert
{
    private Seat[] seats;
    private final ArrayList<Customer> customers;    
    private String name_;
    private String dateWithSlashes_;
    private String date_;
    private int nCustomers_; 
    private int nBookedSeats_;
    private int nAvailableSeats_;
    private double totalSales_;
    private double goldSectionPrice_ = 0.00;
    private double silverSectionPrice_ = 0.00;
    private double bronzeSectionPrice_ = 0.00;
    public static final String[] SEAT_SECTIONS = {"GOLD", "SILVER", "BRONZE"};
    public static final String[] SEAT_ROWS = {"A","B","C","D","E","F","G","H","I"};
    public static final int[] SEAT_NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int TOTAL_SEATS = Concert.SEAT_ROWS.length * Concert.SEAT_NUMBERS.length;
       
    public Concert(String name, int day, int month, int year)
    {
        this.name_ = name;
        this.dateWithSlashes_ = day + "/" + month + "/" + year; 
        this.date_ = day + " " + month + " " + year;
        this.customers = new ArrayList<>();
        this.initializeSeats();       
    }
    
    private Concert()
    {   
        this.customers = new ArrayList<>();
        this.initializeSeats();   
    }
    
    public Concert(String name)
    {
        this(name, 1, 1, 2000);
    }
                  
    //This method creates instances of gold, silver and bronze seats 
    //for a concert, assigning them each a row and a number 
    //depending on their index position in the seats array
    private void initializeSeats() 
    {        
        this.seats = new Seat[Concert.TOTAL_SEATS];
        int seatIndex = 0;
        
        //Go through every row in the concert, and add 10 seats to that row depending
        //on the row e.g. rows 0 to 2 (A to C) will need 10 gold seats each, totaling to 30 seats
        for(int i = 0; i < Concert.SEAT_ROWS.length; i++)           
        {                                  
            if(i < 3)
            {               
                for(int j = 0; j < Concert.SEAT_NUMBERS.length; j++)
                {                                  
                    this.seats[seatIndex] = new GoldSeat(Concert.SEAT_ROWS[i], Concert.SEAT_NUMBERS[j], seatIndex);
                    this.seats[seatIndex].setPrice(this.goldSectionPrice_);
                    seatIndex++;
                }
            }
            else if(i < 6)
            {
                for(int j = 0; j < Concert.SEAT_NUMBERS.length; j++)
                {                                 
                    this.seats[seatIndex] = new SilverSeat(Concert.SEAT_ROWS[i], Concert.SEAT_NUMBERS[j], seatIndex); 
                    this.seats[seatIndex].setPrice(this.silverSectionPrice_);
                    seatIndex++;
                }
            }
            else
            {
                for(int j = 0; j < Concert.SEAT_NUMBERS.length; j++)
                {                                   
                    this.seats[seatIndex] = new BronzeSeat(Concert.SEAT_ROWS[i], Concert.SEAT_NUMBERS[j], seatIndex);
                    this.seats[seatIndex].setPrice(this.bronzeSectionPrice_);
                    seatIndex++;
                }                
            }
        }
    }
    
    public boolean save(PrintWriter concertOutput) 
    {                
        PrintWriter seatOutput = null; 
        PrintWriter customerOutput = null;             
        try
        {                                
            //Save concert to Concert_list file
            concertOutput.println(
                this.getName() + " " + this.getDate() 
                + " " + this.goldSectionPrice_ 
                + " " + this.silverSectionPrice_ 
                + " " + this.bronzeSectionPrice_
            );
            
            //Create directory for current concert
            File concertDirectory = new File(
                Constant.DIRECTORY + File.separator 
                + this.getName() + "_" + this.getDate()
            );
            concertDirectory.mkdir();
            
            //Save all booked seats to file                 
            seatOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Booked_seats.txt")
            );           
            
            for(Seat seat : this.seats) 
            {
                if(seat.getStatus()) 
                {
                    if(seat.save(seatOutput))
                    {
                        System.out.println(
                            "Successfully saved seat " + "(" + seat.getPosition() + ")" 
                            + " for concert " + this.getName() + " " + this.getDateWithSlashes()
                        );
                    }
                    else
                    {
                        System.out.println(
                            "Failed to save seat " + "(" + seat.getPosition() + ")" 
                            + " for concert " + this.getName() + " " + this.getDateWithSlashes()
                        );
                    }
                }
            }
            
            //Save all customers to file            
            customerOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Customers.txt")
            );   
            
            for(Customer customer : this.customers)
            {
                if(customer.save(customerOutput))
                {
                    System.out.println(
                        "Successfully saved customer " + customer.getName() 
                        + " for concert " + this.getName() + " " + this.getDateWithSlashes()
                    );
                }
                else
                {
                    System.out.println(
                        "Failed to save customer " + customer.getName() 
                        + " for concert " + this.getName() + " " + this.getDateWithSlashes()
                    );
                }
            }            
        }
        catch(IOException io)
        {
            System.out.println(io.getMessage());
            return false;
        }
        finally
        {           
            if(seatOutput != null)
            {
                seatOutput.close();
            }
            if(customerOutput != null)
            {
                customerOutput.close();
            }
        }
        return true;
    }
    
    public static Concert load(Scanner concertInput) throws FileNotFoundException
    {
        Scanner seatInput = null;
        Scanner customerInput = null;
        Concert result = new Concert();
        try
        {
            result.name_ = concertInput.next(); 
            while(!concertInput.hasNextInt())
            {
                result.name_ += " " + concertInput.next();
            }        

            int day = concertInput.nextInt();
            int month = concertInput.nextInt();
            int year = concertInput.nextInt();
            result.dateWithSlashes_ = day + "/" + month + "/" + year;
            result.date_ = day + " " + month + " " + year;
            result.goldSectionPrice_ = concertInput.nextDouble();
            result.silverSectionPrice_ = concertInput.nextDouble();
            result.bronzeSectionPrice_ = concertInput.nextDouble();     
                   
            File concertDirectory = new File(
                Constant.DIRECTORY + File.separator + result.getName()
                + "_" + result.getDate()
            );
            concertDirectory.mkdir();
            
            //Load customers from file into the concert
            File customersFile = new File(
                concertDirectory + File.separator + "Customers.txt"
            );
            
            if(customersFile.canRead())               
            {
                customerInput  = new Scanner(customersFile);
                while(customerInput.hasNextLine())
                {                              
                    Customer customer = Customer.load(customerInput);                    
                    if(customer != null)
                    {
                        result.customers.add(customer);
                        result.nCustomers_++;
                    }                    
                }
            }
            else
            {
                customersFile.createNewFile();
            }    
            
            //Load booked seats into concert from file
            File seatsFile = new File(
                concertDirectory + File.separator + "Booked_seats.txt"
            );
            
            if(seatsFile.canRead())
            {
                seatInput = new Scanner(seatsFile);
                while(seatInput.hasNextLine())
                {
                    Seat seat = Seat.load(seatInput, result);
                    if(seat != null)
                    {                                                                     
                        result.seats[seat.getIndex()] = seat;
                        result.nBookedSeats_++;
                        Customer customer = result.findCustomer(seat.getBookee());
                        if(customer != null)
                        {
                            customer.addSeat(seat);
                        }
                        else
                        {
                            Customer newCustomer = new Customer(seat.getBookee());
                            newCustomer.addSeat(seat);
                            result.customers.add(newCustomer);
                        }                                                
                    }                    
                }
            }
            else
            {
                seatsFile.createNewFile();
            }                                                                                                    
        }                   
        catch(IOException io)
        {
            System.out.println(io.getMessage());
            return null;
        }
        finally
        {
            if(seatInput != null)
            {
                seatInput.close();
            }
            if(customerInput != null)
            {
                customerInput.close();
            }
        }                     
        return result;
    }   
    
    public String getName()
    {
        return this.name_;
    }
    
    public String getDateWithSlashes()
    {
        return this.dateWithSlashes_;
    }
    
    public String getDate()
    {
        return this.date_;
    }
    
    public Seat[] getSeats()
    {
        return this.seats;
    }
    
    public ArrayList<Customer> getCustomers()
    {
        return this.customers;
    }
           
    //This method returns a single seat from the concerts seats array     
    public Seat getSeat(String seatRow, int seatNum)
    {     
        int i = 0;
        while(seatRow.compareToIgnoreCase(Concert.SEAT_ROWS[i]) != 0)
        {
            i++;
        }      
        if(i == 0)
        {
            //If the seatRow is A, just return seat at index seatNum - 1
            return this.seats[seatNum-1];
        }
        else
        {
            //If the seatRow is not A, return seat at index (seatNum - 1) + (seatRow index * 10)
            //Each row has 10 seats, so if seatRow is B and seatNum is 5,
            //its index is 14 because seatRow B index is 1 and (5 - 1) + (1 * 10) = 14
            return this.seats[(seatNum-1) + (i*Concert.SEAT_NUMBERS.length)]; 
        }
    }
                  
    public void bookSeat(Seat seat, String name)
    {                        
        String customerName = Constant.capitalize(name);
        Customer customer = this.findCustomer(customerName);
        if(customer != null)
        {           
            this.seats[seat.getIndex()].book(customer);                     
            this.nBookedSeats_++;
        }
        else
        {           
            Customer newCustomer = new Customer(customerName);            
            this.seats[seat.getIndex()].book(newCustomer);            
            this.customers.add(newCustomer);                       
            this.nBookedSeats_++;
            this.nCustomers_++;
        }               
    }
            
    public void unBookSeat(Seat seat) throws CannotUnbookSeatException
    {        
        Customer customer = this.findCustomer(seat.getBookee());
        if(customer != null)
        {           
            this.seats[seat.getIndex()].unBook(customer);
            this.nBookedSeats_--; 
            if(!customer.hasBookedASeat())
            {
                this.customers.remove(customer);
                this.nCustomers_--;
            }
        }                                  
    }
           
    //This method returns the entitlement of the supplied seats bookee
    public String getCustomerEntitlement(Seat seat)
    {               
        Customer customer = this.findCustomer(seat.getBookee());
        if(customer != null)
        {
            if(customer.getEntitlement() == null)
            {
                return null;
            }
            else
            {
                return customer.getName() + " is entitled to " + customer.getEntitlement();
            }
        }
        else
        {
            return null;
        }                       
    }
    
    public double getSectionPrice(String seatSection)
    {              
        int i = 0;
        while(seatSection.compareToIgnoreCase(Concert.SEAT_SECTIONS[i]) != 0)
        {
            i++;
        }
        switch(i)
        {
            case 0:
            {
                return Double.parseDouble(
                    Constant.PRICE_FORMAT.format(this.goldSectionPrice_)
                );                              
            }
            case 1:
            {
                return Double.parseDouble(
                    Constant.PRICE_FORMAT.format(this.silverSectionPrice_)
                );                
            }
            default:
            {
                return Double.parseDouble(
                    Constant.PRICE_FORMAT.format(this.bronzeSectionPrice_)
                );               
            }
        }       
    }

    //This method returns a report, detailing the available seats,
    //booked seats and total sales of the concert
    public String getReport() 
    {             
        for(Seat seat : this.seats) 
        {
            if(seat.getStatus()) 
            {
                this.totalSales_ += seat.getPrice();
            }
        }     
        
        this.nAvailableSeats_ = this.seats.length - this.nBookedSeats_;
        String report = "Available Seats: " + this.nAvailableSeats_ + "\nBooked Seats: " + this.nBookedSeats_;
        report += "\nNumber of Customers: " + this.nCustomers_;
        
        if(this.totalSales_ == 0.00)
        {            
            report += "\nTotal Sales: " + "N/A";
        }
        else
        {                                  
            report += "\nTotal Sales: £" + Constant.PRICE_FORMAT.format(this.totalSales_);                      
        }
        
        this.totalSales_ = 0;
        return report;
    }
    
    //This method returns information about a specific seat,
    //such as if it is booked, and if someone has booked it, 
    //and returns if the person should receive any entitlements
    public String queryBySeat(Seat seat)
    {                         
        if(seat.getStatus())
        {
            Customer customer = this.findCustomer(seat.getBookee());
            if(customer != null)
            {
                if(customer.getEntitlement() == null)
                {
                    return "Selected seat " + "(" + seat.getPosition() 
                            + ")" + " is booked by " + seat.getBookee();
                }
                else
                {
                    return "Selected seat " + "(" + seat.getPosition() + ")" 
                            + " is booked by " + seat.getBookee() + "\n" + seat.getBookee() + " is entitled to " 
                            + customer.getEntitlement();
                }
            }
            else
            {
                return "Could not find customer for " + "(" + seat.getPosition() + ")";
            }
        }
        else
        {
            return "Selected seat " + "(" + seat.getPosition() + ")" + " hasn't been booked";
        }
    }
    
    //This method returns all the seats that the supplied customer has booked
    public String queryByCustomer(String customerName)
    {              
        String returnQuery = "";
        Customer customer = this.findCustomer(customerName);
        if(customer != null)
        {
            if(customer.getEntitlement() != null)
            {
                returnQuery = customer.getName() + " is entitled to " + customer.getEntitlement() + "\n";
            }       
            
            returnQuery += customer.getName() + " has booked " + customer.getBookedSeats().size();
            if(customer.getBookedSeats().size() > 1)
            {
                returnQuery += " seats:\n";
            }
            else
            {
                returnQuery += " seat:\n";
            }
                           
            int counter = 0;
            for(Seat seat : customer.getBookedSeats())
            {
                counter++;
                if(counter < 5)
                {
                    returnQuery += "(" + seat.getPosition() + ") ";
                }
                else
                {
                    returnQuery += "(" + seat.getPosition() + ")\n";
                    counter = 0;
                }                   
            }                        
            return returnQuery;
        }
        else
        {
            return "Customer does not exist";
        }       
    }
    
    public void setSectionPrice(String seatSection, double newPrice) 
    {               
        int i = 0;
        double finalPrice = Double.parseDouble(Constant.PRICE_FORMAT.format(newPrice));        
        while(seatSection.compareToIgnoreCase(Concert.SEAT_SECTIONS[i]) != 0)
        {
            i++;
        }       
        switch(i) 
        {
            case 0:
            {                              
                this.goldSectionPrice_ = finalPrice;                  
                for(int j = 0; j < 30; j++)                       
                {
                    this.seats[j].setPrice(this.goldSectionPrice_);
                }   
                break;
            }                
            case 1:
            {
                this.silverSectionPrice_ = finalPrice;
                for(int j = 30; j < 60; j++)
                {
                    this.seats[j].setPrice(this.silverSectionPrice_);
                }   
                break;
            }
            default:
            {
                this.bronzeSectionPrice_ = finalPrice;
                for(int j = 60; j < 90; j++)
                {
                    this.seats[j].setPrice(this.bronzeSectionPrice_);
                }   
                break;
            }        
        }                               
    }
    
    private Customer findCustomer(String name)
    {
        int i = 0;
        boolean found = false;
        while(i < this.customers.size() && !found)
        {
            if(this.customers.get(i).getName().compareToIgnoreCase(name) == 0)
            {
                found = true;
            }
            else
            {
                i++;
            }
        }
        if(found)
        {
            return this.customers.get(i);
        }
        else
        {
            return null;
        }
    }
}                                


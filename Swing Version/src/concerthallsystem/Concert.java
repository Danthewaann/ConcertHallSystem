package concerthallsystem;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The Concert class holds all the important information that can
 * be accessed through the MainGUI. Each concert, when created, automatically 
 * populates the Seat object array with either gold, silver or bronze seats.
 * To access these seats through the GUI, methods can be called on a concert
 * to retrieve important information about each seat from that particular concert,
 * or more general information about the specific concert
 * 
 * @author Daniel Black, George Bingham, Rebecca Curtis, Minyu Lei
 */

public class Concert
{
    private Seat[] seats;
    private String name_;
    private String dateWithSlashes_;
    private String date_;
    private int bookedSeats_;
    private int availableSeats_;
    private double totalSales_;
    private double goldSectionPrice_ = 0.00;
    private double silverSectionPrice_ = 0.00;
    private double bronzeSectionPrice_ = 0.00;
    
    //This constructor is used to create a new concert within the GUI
    public Concert(String name, int day, int month, int year)
    {
        this.name_ = name;
        this.dateWithSlashes_ = day + "/" + month + "/" + year; 
        this.date_ = day + " " + month + " " + year;
        this.initializeSeats();
    }
    
    //This constructor is used to instantiate a concert from file
    public Concert(Scanner concertInput) throws FileNotFoundException
    {                      
        this.name_ = concertInput.next();        
        while(!concertInput.hasNextInt())
        {
            this.name_ += " " + concertInput.next();
        }        
        int day = concertInput.nextInt();
        int month = concertInput.nextInt();
        int year = concertInput.nextInt();
        this.dateWithSlashes_ = day + "/" + month + "/" + year;
        this.date_ = day + " " + month + " " + year;
        
        this.goldSectionPrice_ = concertInput.nextDouble();
        this.silverSectionPrice_ = concertInput.nextDouble();
        this.bronzeSectionPrice_ = concertInput.nextDouble();
        this.initializeSeats();
        
        //Open up connection to the seats file for the concert and scan  
        //the booked seats into the concert
        Scanner seatInput = new Scanner(new File(
            "CONCERTS_INFO/" + this.name_.toUpperCase() + "_" + this.date_ + "_SEATS.txt"
        ));
        
        while(seatInput.hasNextLine())
        {
            String seatRow = seatInput.next();
            int seatNum = seatInput.nextInt();
            String bookee = seatInput.next();
            while(!seatInput.hasNextBoolean())
            {
                bookee += " " + seatInput.next();
            }              
            boolean isEntitled = seatInput.nextBoolean();
            seatInput.nextLine();
            
            int seatIndex = this.getSeatAsInt(seatRow, seatNum);
            this.seats[seatIndex].setStatus(true);
            this.seats[seatIndex].setBookee(bookee);
            this.seats[seatIndex].setEntitled(isEntitled);
            this.bookedSeats_++;
        }
    }
    
    //This method creates instances of gold, silver and bronze seats 
    //for a concert, assigning them each a row and a number 
    //depending on their index position in the seats array
    private void initializeSeats() 
    {        
        this.seats = new Seat[Constant.TOTAL_SEATS];
        int seatIndex = 0;
        
        //Go through every row in the concert, and add 10 seats to that row depending
        //on the row e.g. rows 0 to 2 (A to C) will need 10 gold seats each, totaling to 30 seats
        for(int i = 0; i < Constant.SEAT_ROWS.length; i++)           
        {                                  
            if(i < 3)
            {               
                for(int j = 0; j < Constant.SEAT_NUMBERS.length; j++)
                {                                  
                    this.seats[seatIndex] = new GoldSeat(Constant.SEAT_ROWS[i], Constant.SEAT_NUMBERS[j]);
                    this.seats[seatIndex].setPrice(this.goldSectionPrice_);
                    seatIndex++;
                }
            }
            else if(i < 6)
            {
                for(int j = 0; j < Constant.SEAT_NUMBERS.length; j++)
                {                                 
                    this.seats[seatIndex] = new SilverSeat(Constant.SEAT_ROWS[i], Constant.SEAT_NUMBERS[j]); 
                    this.seats[seatIndex].setPrice(this.silverSectionPrice_);
                    seatIndex++;
                }
            }
            else
            {
                for(int j = 0; j < Constant.SEAT_NUMBERS.length; j++)
                {                                   
                    this.seats[seatIndex] = new BronzeSeat(Constant.SEAT_ROWS[i], Constant.SEAT_NUMBERS[j]);
                    this.seats[seatIndex].setPrice(this.bronzeSectionPrice_);
                    seatIndex++;
                }                
            }
        }
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
    
    //This method returns an integer representation of a seat
    //e.g. from 0 to 89, which is its position in the seats array.
    //This is used to directly perform operations on the seat, such
    //as book it or return if it is booked etc.    
    public int getSeatAsInt(String seatRow, int seatNum)
    {     
        int i = 0;
        while(seatRow.compareToIgnoreCase(Constant.SEAT_ROWS[i]) != 0)
        {
            i++;
        }      
        if(i == 0)
        {
            //If the seatRow is A, just return seatNum - 1
            return seatNum - 1;
        }
        else
        {
            //If the seatRow is not A, return (seatNum - 1) + (seatRow index * 10)
            //Each row has 10 seats, so if seatRow is B and seatNum is 5,
            //its index is 14 because seatRow B index is 1 and
            //(5 - 1) + (1 * 10) = 14
            return (seatNum - 1) + (i * 10); 
        }
    }
            
    public void bookSeat(String seatRow, int seatNum, String name)
    {       
        int seatIndex = this.getSeatAsInt(seatRow, seatNum);      
        this.seats[seatIndex].book(name);
        this.bookedSeats_++;
    }
    
    public void unBookSeat(String seatRow, int seatNum)
    {
        int seatIndex = this.getSeatAsInt(seatRow, seatNum);        
        this.seats[seatIndex].unBook();
        this.bookedSeats_--;
    }
    
    //This method returns a string representation of a seat e.g A1
    //This is used to display the seat position for the use of the GUI
    public String getSeatAsString(String seatRow, int seatNum)
    {
        int seatIndex = this.getSeatAsInt(seatRow, seatNum);
        String returnQuery = this.seats[seatIndex].getSeat();
        return returnQuery;
    }
    
    //This method returns the entitlement of the supplied seat
    public String getSeatEntitlement(String seatRow, int seatNum)
    {
        int seatIndex = this.getSeatAsInt(seatRow, seatNum);
        String returnQuery = this.seats[seatIndex].getEntitlement();
        return returnQuery;
    }
    
    public double getSectionPrice(String seatSection)
    {              
        int i = 0;
        while(seatSection.compareToIgnoreCase(Constant.SEAT_SECTIONS[i]) != 0)
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
        for(int i = 0; i < Constant.TOTAL_SEATS; i++)
        {
            if(this.seats[i].getStatus())
            {
               this.totalSales_ += this.seats[i].getPrice();
            }
        }     
        
        this.availableSeats_ = Constant.TOTAL_SEATS - this.bookedSeats_;
        String report = "Available Seats: " + this.availableSeats_ + "\nBooked Seats: " + this.bookedSeats_;  
        
        if(this.totalSales_ == 0.00)
        {            
            report += "\nTotal Sales: " + "N/A";
        }
        else
        {                                  
            report += "\nTotal Sales: £" + Constant.PRICE_FORMAT.format(this.totalSales_);                      
        }
        
        this.totalSales_ = 0.00;
        return report;
    }
    
    //This method returns information about a specific seat,
    //such as if it is booked, and if someone has booked it, 
    //returns if the person should receive any entitlements or not
    public String queryBySeat(String seatRow, int seatNum)
    {        
        String returnQuery;
        int seatIndex = this.getSeatAsInt(seatRow, seatNum);        
        
        if(this.seats[seatIndex].getStatus())
        {
            returnQuery = 
                "Selected seat " + "(" + seatRow.toUpperCase() + seatNum + ")" 
                + " is booked by " + this.seats[seatIndex].getBookee() 
                + "\n" + this.seats[seatIndex].getEntitlement();
        }
        else 
        {
            returnQuery = "Selected Seat " + "(" + seatRow + seatNum + ")" + " hasn't been booked";
        }  
        
        return returnQuery;
    }
    
    //This method returns all the seats that the supplied customer has booked
    public String queryByCustomer(String customerName)
    {       
        int i = 0;
        boolean foundCustomer = false;  
        String returnQuery = null;
        
        while(i < Constant.TOTAL_SEATS && !foundCustomer)
        {
            if(this.seats[i].getBookee() == null)
            {
                i++;
            }
            else
            {
                if(customerName.compareToIgnoreCase(this.seats[i].getBookee()) == 0)
                {
                    foundCustomer = true;
                    //Counter is used to insert a \n after every 5 seats just to make
                    //the query information look more structured when returned to the GUI
                    int counter = 0;
                    returnQuery = this.seats[i].getBookee() + " Has booked:\n";
                    
                    while(i < Constant.TOTAL_SEATS)                        
                    {
                        if(this.seats[i].getStatus() && customerName.compareToIgnoreCase(this.seats[i].getBookee()) == 0)
                        {                                                                        
                            counter++;
                            if(counter < 5)
                            {                                
                                returnQuery += "(" + this.seats[i].getSeat() + ") ";                                                             
                            }
                            else
                            {
                                returnQuery += "(" + this.seats[i].getSeat() + ")\n";                               
                                counter = 0;                                
                            }                           
                        }    
                        i++;
                    }
                }
                else
                {
                    i++;
                }
            }           
        }
        if(foundCustomer)
        {
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
        while(seatSection.compareToIgnoreCase(Constant.SEAT_SECTIONS[i]) != 0)
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
                    this.seats[i].setPrice(this.silverSectionPrice_);
                }   
                break;
            }
            default:
            {
                this.bronzeSectionPrice_ = finalPrice;
                for(int j = 60; j < 90; j++)
                {
                    this.seats[i].setPrice(this.bronzeSectionPrice_);
                }   
                break;
            }        
        }                               
    }
}                                


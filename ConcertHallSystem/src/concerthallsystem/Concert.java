package concerthallsystem;

import concerthallsystem.exceptions.ConcertIOException;
import concerthallsystem.exceptions.CannotUnbookSeatException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
    private String date_;
    private int nCustomers_; 
    private int nBookedSeats_;     
    private double silverSectionPrice_;
    private double goldSectionPrice_;
    private double bronzeSectionPrice_;
    public static final String[] SEAT_SECTIONS = {"Gold", "Silver", "Bronze"};
    public static final String[] SEAT_ROWS = {"A","B","C","D","E","F","G","H","I"};
    public static final int[] SEAT_NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int TOTAL_SEATS = SEAT_ROWS.length * SEAT_NUMBERS.length;
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("00.00");
           
    public Concert(String name, String date)
    {
        this.name_ = name;
        this.date_ = date;
        this.customers = new ArrayList<>();
        this.initializeSeats(); 
    }
    
    private Concert()
    {   
        this.customers = new ArrayList<>();           
    }
                         
    //This method creates instances of gold, silver and bronze seats 
    //for a concert, assigning them each a row and a number 
    //depending on their index position in the seats array
    private void initializeSeats() 
    {        
        this.seats = new Seat[TOTAL_SEATS];
        int seatIndex = 0;
        
        //Go through every row in the concert, and add 10 seats to that row depending
        //on the row e.g. rows 0 to 2 (A to C) will need 10 gold seats each, totaling to 30 seats
        for(int i = 0; i < SEAT_ROWS.length; i++) {                                  
            if(i < 3) {               
                for(int j = 0; j < SEAT_NUMBERS.length; j++) {                                  
                    this.seats[seatIndex] = new GoldSeat(SEAT_ROWS[i], SEAT_NUMBERS[j]);
                    this.seats[seatIndex].setPrice(this.goldSectionPrice_);
                    seatIndex++;
                }
            }
            else if(i < 6) {
                for(int j = 0; j < SEAT_NUMBERS.length; j++) {                                 
                    this.seats[seatIndex] = new SilverSeat(SEAT_ROWS[i], SEAT_NUMBERS[j]); 
                    this.seats[seatIndex].setPrice(this.silverSectionPrice_);
                    seatIndex++;
                }
            }
            else {
                for(int j = 0; j < Concert.SEAT_NUMBERS.length; j++) {                                   
                    this.seats[seatIndex] = new BronzeSeat(SEAT_ROWS[i], SEAT_NUMBERS[j]);
                    this.seats[seatIndex].setPrice(this.bronzeSectionPrice_);
                    seatIndex++;
                }                
            }
        }
    }
    
    public boolean save(PrintWriter concertOutput, String directory) 
    {                
        PrintWriter seatOutput = null; 
        PrintWriter customerOutput = null;             
        try {                                
            //Save concert to Concert_list file
            concertOutput.println(
                this + " " + this.goldSectionPrice_ 
                + " " + this.silverSectionPrice_ 
                + " " + this.bronzeSectionPrice_
            );
            
            //Create directory for current concert
            File concertDirectory = new File(
                directory + File.separator + this
            );
            concertDirectory.mkdir();
            
            //Save all booked seats to file                 
            seatOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Booked_seats.txt")
            );           
            
            for(Seat seat : this.seats) {
                if(seat.getStatus()) {
                    if(seat.save(seatOutput)) {
                        System.out.println(
                            "Successfully saved seat " + "(" + seat + ")" 
                            + " for concert " + this
                        );
                    }
                    else {
                        System.out.println(
                            "Failed to save seat " + "(" + seat + ")" 
                            + " for concert " + this
                        );
                    }
                }
            }
            
            //Save all customers to file            
            customerOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Customers.txt")
            );   
            
            for(Customer customer : this.customers) {
                if(customer.save(customerOutput)) {
                    System.out.println(
                        "Successfully saved customer " + customer.getName() 
                        + " for concert " + this
                    );
                }
                else {
                    System.out.println(
                        "Failed to save customer " + customer.getName() 
                        + " for concert " + this
                    );
                }
            }            
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
            return false;
        }
        finally {           
            if(seatOutput != null) {
                seatOutput.close();
            }
            if(customerOutput != null) {
                customerOutput.close();
            }
        }
        return true;
    }
    
    //Load in concert from file, populating it with its customers and booked seats
    public static Concert load(Scanner concertInput, String mainDirectory, int concertLineNum) throws FileNotFoundException
    {
        Scanner seatInput = null;
        Scanner customerInput = null;
        Concert tempConcert = new Concert();
        try {      
            /* LOAD IN CONCERT INFO */
            tempConcert.name_ = concertInput.next(); 
            
            Pattern dateRegex = Pattern.compile("[\\d]{4}[-][\\d]{2}[-][\\d]{2}");           
            while(!concertInput.hasNext(dateRegex)) {
                tempConcert.name_ += " " + concertInput.next();
            }                 
            
            tempConcert.date_ = concertInput.next();
            tempConcert.goldSectionPrice_ = concertInput.nextDouble();
            tempConcert.silverSectionPrice_ = concertInput.nextDouble();
            tempConcert.bronzeSectionPrice_ = concertInput.nextDouble();
            tempConcert.initializeSeats();          
            /* END OF LOADING IN CONCERT INFO */           
                   
            //CREATE DIRECTORY FOR THE CURRENTLY LOADED CONCERT. 
            //THIS IS WHERE ALL THE CURRENT CONCERT'S INFO SHOULD BE
            //STORED, TWO FILES, ONE FOR BOOKED SEATS, ONE FOR CUSTOMERS 
            File concertDirectory = new File(
                mainDirectory + File.separator + tempConcert                                  
            );
            
            //If the current concert's directory doesn't exist, create it
            concertDirectory.mkdir();
            
            /* LOAD IN CUSTOMER INFO */
            File customersFile = new File(
                concertDirectory + File.separator + "Customers.txt"
            );
            
            //If the customers file exists, start loading in the customers 
            if(customersFile.canRead()) {                
                int customerLineNum = 1;
                customerInput = new Scanner(customersFile);
                
                while(customerInput.hasNextLine()) {                              
                    Customer tempCustomer = Customer.load(customerInput, customersFile, customerLineNum++);                    
                    if(tempCustomer != null) {
                        tempConcert.customers.add(tempCustomer);
                        tempConcert.nCustomers_++;
                    }                       
                }
            }
            else {
                customersFile.createNewFile();
            } 
            /* END OF LOADING IN CUSTOMER INFO */
            
            /* LOAD IN SEAT INFO */
            File seatsFile = new File(
                concertDirectory + File.separator + "Booked_seats.txt"
            );
            
            //If the seats file exists, start loading in the seats
            if(seatsFile.canRead()) {
                int seatLineNum = 1;
                seatInput = new Scanner(seatsFile);
                
                while(seatInput.hasNextLine()) {                               
                    Seat tempSeat = Seat.load(seatInput, seatsFile, seatLineNum++);
                    Seat actualSeat = tempConcert.findSeat(tempSeat);
                    
                    if(actualSeat != null) {
                        actualSeat.setStatus(true);                       
                        actualSeat.setBookee(tempSeat.getBookee());
                        tempConcert.nBookedSeats_++;
                        Customer tempCustomer = tempConcert.findCustomer(actualSeat.getBookee());
                        if(tempCustomer != null) {
                            tempCustomer.addSeat(actualSeat);
                        }
                        else {
                            Customer newCustomer = new Customer(actualSeat.getBookee());
                            newCustomer.addSeat(actualSeat);
                            tempConcert.customers.add(newCustomer);
                        }     
                    }
                    else {
                        System.out.println("Error: tried to load a seat that doesn't exist");
                    }
                                         
                }
            }
            else {
                seatsFile.createNewFile();
            }   
            /* END OF LOADING IN SEAT INFO */
        }        
        catch(InputMismatchException | IOException io) {
            throw new ConcertIOException(concertLineNum);                                   
        }       
        finally {
            if(seatInput != null) {
                seatInput.close();
            }
            if(customerInput != null) {
                customerInput.close();
            }
            concertInput.nextLine();
        }                     
        return tempConcert;
    }   
    
    public String getName()
    {
        return this.name_;
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
        while(seatRow.compareToIgnoreCase(Concert.SEAT_ROWS[i]) != 0) {
            i++;
        }    
        
        if(i == 0) {
            //If the seatRow is A, just return seat at index seatNum - 1
            return this.seats[seatNum-1];
        }
        else {
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
        
        if(customer != null) {           
            Seat temp = this.findSeat(seat);
            temp.book(customer);                         
            this.nBookedSeats_++;
        }
        else {           
            Customer newCustomer = new Customer(customerName); 
            Seat temp = this.findSeat(seat);
            temp.book(newCustomer);                       
            this.customers.add(newCustomer);                       
            this.nBookedSeats_++;
            this.nCustomers_++;
        }               
    }
            
    public void unBookSeat(Seat seat) throws CannotUnbookSeatException
    {        
        Customer customer = this.findCustomer(seat.getBookee());
        Seat temp = this.findSeat(seat);
        
        if(customer != null ) {               
            temp.unBook(customer);           
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
        
        if(customer != null)  {
            if(customer.getEntitlement() == null) {
                return null;
            }
            else {
                return customer.getName() + " is entitled to " + customer.getEntitlement();
            }
        }
        else {
            return null;
        }                       
    }
    
    public double getSectionPrice(String seatSection)
    {              
        int i = 0;       
        while(seatSection.compareToIgnoreCase(Concert.SEAT_SECTIONS[i]) != 0) {
            i++;
        }
        
        switch(i) {
            case 0: {
                return Double.parseDouble(
                    PRICE_FORMAT.format(this.goldSectionPrice_)
                );                              
            }
            case 1: {
                return Double.parseDouble(
                    PRICE_FORMAT.format(this.silverSectionPrice_)
                );                
            }
            default: {
                return Double.parseDouble(
                   PRICE_FORMAT.format(this.bronzeSectionPrice_)
                );               
            }
        }       
    }

    //This method returns a report, detailing the available seats,
    //booked seats and total sales of the concert
    public List<String> getReport() 
    {             
        List<String> fullReport = new ArrayList<>();
        double totalSales = 0;
        
        for(Seat seat : this.seats) {
            if(seat.getStatus()) {
                totalSales += seat.getPrice();
            }
        }  
        
        fullReport.add("Available Seats: " + String.valueOf(this.seats.length - this.nBookedSeats_));      
        fullReport.add("Booked Seats: " + String.valueOf(this.nBookedSeats_));   
        fullReport.add("Customers: " + String.valueOf(this.nCustomers_));
        fullReport.add("GoldSeat Price: £" + PRICE_FORMAT.format(this.goldSectionPrice_));
        fullReport.add("SilverSeat Price: £" + PRICE_FORMAT.format(this.silverSectionPrice_));
        fullReport.add("BronzeSeat Price: £" + PRICE_FORMAT.format(this.bronzeSectionPrice_));                                  
        fullReport.add("Total Sales: £" + PRICE_FORMAT.format(totalSales));         
        return fullReport;
    }
    
    //This method returns information about a specific seat,
    //such as if it is booked, and if someone has booked it, 
    //and returns if the person should receive any entitlements
    public String queryBySeat(Seat seat)
    {                         
        if(seat.getStatus()) {
            Customer customer = this.findCustomer(seat.getBookee());
            if(customer != null)  {
                if(customer.getEntitlement() == null) {
                    return "Selected seat " + "(" + seat 
                            + ")" + " is booked by " + seat.getBookee();
                }
                else {
                    return "Selected seat " + "(" + seat + ")" 
                            + " is booked by " + seat.getBookee() + "\n" 
                            + seat.getBookee() + " is entitled to " 
                            + customer.getEntitlement();
                }
            }
            else {
                return "Could not find customer for " + "(" + seat + ")";
            }
        }
        else {
            return "Selected seat " + "(" + seat + ")" + " hasn't been booked";
        }        
    }
    
    //This method returns all the seats that the supplied customer has booked
    public String queryByCustomer(String customerName)
    {              
        String returnQuery = "";
        Customer customer = this.findCustomer(customerName);
        
        if(customer != null) {
            if(customer.getEntitlement() != null) {
                returnQuery = customer.getName() + " is entitled to " + customer.getEntitlement() + "\n";
            }       
            
            returnQuery += customer.getName() + " has booked " + customer.getBookedSeats().size();
            
            if(customer.getBookedSeats().size() > 1) {
                returnQuery += " seats:\n";
            }
            else {
                returnQuery += " seat:\n";
            }
                           
            int counter = 0;
            for(Seat seat : customer.getBookedSeats()) {
                counter++;
                if(counter < 5){
                    returnQuery += "(" + seat + ") ";
                }
                else {
                    returnQuery += "(" + seat + ")\n";
                    counter = 0;
                }                   
            }                        
            return returnQuery;
        }
        else {
            return "Customer does not exist";
        }       
    }
    
    public void setSectionPrice(String seatSection, double newPrice) 
    {               
        int i = 0;
        double finalPrice = Double.parseDouble(PRICE_FORMAT.format(newPrice)); 
        
        while(seatSection.compareToIgnoreCase(Concert.SEAT_SECTIONS[i]) != 0) {
            i++;
        }    
        
        switch(i) {
            case 0: {                              
                this.goldSectionPrice_ = finalPrice;                  
                for(int j = 0; j < 30; j++) {
                    this.seats[j].setPrice(this.goldSectionPrice_);
                }   
                break;
            }                
            case 1: {
                this.silverSectionPrice_ = finalPrice;
                for(int j = 30; j < 60; j++) {
                    this.seats[j].setPrice(this.silverSectionPrice_);
                }   
                break;
            }
            default: {
                this.bronzeSectionPrice_ = finalPrice;
                for(int j = 60; j < 90; j++) {
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
        
        while(i < this.customers.size() && !found) {
            if(this.customers.get(i).getName().compareToIgnoreCase(name) == 0) {
                found = true;
            }
            else {
                i++;
            }
        }
        if(found) {
            return this.customers.get(i);
        }
        else {
            return null;
        }
    }
    
    private Seat findSeat(Seat seat)
    {
        int i = 0;
        boolean found = false;
        
        while(i < this.seats.length && !found) {
            if(this.seats[i].equals(seat)) {
                found = true;
            }
            else {
                i++;
            }
        }
        if(found) {
            return this.seats[i];
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() 
    {
        return this.getName() + " " + this.getDate();
    }

    @Override
    public boolean equals(Object obj) 
    {   
        if(obj.getClass().isInstance(this)) {
            if(this.hashCode() == ((Concert) obj).hashCode()) {
                return true;
            }
        }                      
        return false;       
    }

    @Override
    public int hashCode() 
    {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.name_);
        hash = 61 * hash + Objects.hashCode(this.date_);
        return hash;
    }         
}                                


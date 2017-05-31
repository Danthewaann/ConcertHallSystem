package concerthallsystem;

import concerthallsystem.exceptions.ConcertIOException;
import concerthallsystem.exceptions.CannotUnbookSeatException;
import concerthallsystem.exceptions.CustomerIOException;
import concerthallsystem.exceptions.SeatIOException;

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
import java.util.Arrays;

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

public class Concert implements Comparable
{
    private Seat[] seats;    
    private String name_;
    private String date_;   
    private int nBookedSeats_;
    private int linePosition;
    private double silverSectionPrice_;
    private double goldSectionPrice_;
    private double bronzeSectionPrice_;
    private boolean recentlyChanged = false;
    private final ArrayList<Customer> customers;    
    public static final String[] SEAT_SECTIONS = {"Gold", "Silver", "Bronze"};
    public static final String[] SEAT_ROWS = {"A","B","C","D","E","F","G","H","I"};
    public static final int[] SEAT_NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int TOTAL_SEATS = SEAT_ROWS.length * SEAT_NUMBERS.length;
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("##0.00");
           
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
                for(int j = 0; j < SEAT_NUMBERS.length; j++) {                                   
                    this.seats[seatIndex] = new BronzeSeat(SEAT_ROWS[i], SEAT_NUMBERS[j]);
                    this.seats[seatIndex].setPrice(this.bronzeSectionPrice_);
                    seatIndex++;
                }                
            }
        }
    }
    
    //Saves the current concert along with its info, booked seats and customers, to file
    public boolean save(PrintWriter concertOutput, String directory) throws FileNotFoundException
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
              
            //Open outputstream to Booked_seats.txt file
            seatOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Booked_seats.txt")
            );           
            
            //Save all booked seats to file 
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
            
            //Open outputstream to Customers.txt file                       
            customerOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Customers.txt")
            );   
            
            //Save all customers to file 
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
        finally { //Close all outputstreams when done
            if(seatOutput != null) {
                seatOutput.close();
            }
            if(customerOutput != null) {
                customerOutput.close();
            }
        }
        this.recentlyChanged = false;
        return true;
    }
    
    //Load in a concert from file, populating it with its customers and booked seats, 
    //and returns it for the ConcertController to manage
    public static Concert load(Scanner concertInput, String mainDirectory, int concertLineNum) throws ConcertIOException
    {
        Scanner seatInput = null;
        Scanner customerInput = null;
        Concert tempConcert = new Concert();
        String errorReport = "";
        try {
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

            //Create directory for currently loaded concert
            File concertDirectory = new File(
                mainDirectory + File.separator + tempConcert
            );

            //If the current concert's directory doesn't exist, create it
            concertDirectory.mkdir();

            //Load in customer information from file
            File customersFile = new File(
                concertDirectory + File.separator + "Customers.txt"
            );
            if(customersFile.canRead()) {
                customerInput = new Scanner(customersFile);
                errorReport += loadCustomers(customersFile, tempConcert, customerInput);
            }
            else {
                customersFile.createNewFile();
            }

            //Load in seat information from file
            File seatsFile = new File(
                concertDirectory + File.separator + "Booked_seats.txt"
            );
            if(seatsFile.canRead()) {
                seatInput = new Scanner(seatsFile);
                errorReport += loadSeats(seatsFile, tempConcert, seatInput);
            }
            else {
                seatsFile.createNewFile();
            }
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
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
        if(errorReport.length() > 0) {
            throw new ConcertIOException(tempConcert, errorReport, concertLineNum);
        }
        return tempConcert;
    }
    
    private static String loadCustomers(File customersFile, Concert tempConcert, Scanner customerInput)
    {                                                          
        int customerLineNum = 1;
        String customerErrors = "";
        while(customerInput.hasNextLine()) {
            try {
                Customer tempCustomer = Customer.load(customerInput, customersFile, customerLineNum++);
                if (tempCustomer != null) {
                    tempConcert.customers.add(tempCustomer);
                }
            }
            catch(CustomerIOException io) {
                customerErrors += io.getMessage();
            }
        }
        return customerErrors;
    }
    
    private static String loadSeats(File seatsFile, Concert tempConcert, Scanner seatInput)
    {                                                
        int seatLineNum = 1;
        String seatErrors = "";
        while(seatInput.hasNextLine()) {
            try {
                Seat tempSeat = Seat.load(seatInput, seatsFile, seatLineNum++);
                Seat actualSeat = tempConcert.findSeat(tempSeat);

                if (actualSeat != null) {
                    actualSeat.setBookee(tempSeat.getBookee());
                    tempConcert.nBookedSeats_++;
                    Customer tempCustomer = tempConcert.findCustomer(actualSeat.getBookee());
                    if (tempCustomer != null) {
                        tempCustomer.addSeat(actualSeat);
                    } else {
                        Customer newCustomer = new Customer(actualSeat.getBookee());
                        newCustomer.addSeat(actualSeat);
                        tempConcert.customers.add(newCustomer);
                    }
                }
            }
            catch(SeatIOException io) {
                seatErrors += io.getMessage();
            }
        }
        return seatErrors;
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
        while(!seatRow.equals(SEAT_ROWS[i])) {
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
                  
    //Books the seat in the current concert that matches the supplied seat
    public void bookSeat(Seat seat, String name)
    {                               
        Customer customer = this.findCustomer(name);
        
        if(customer != null) {           
            Seat temp = this.findSeat(seat);
            temp.book(customer);                         
            this.nBookedSeats_++;
        }
        else {           
            Customer newCustomer = new Customer(name); 
            Seat temp = this.findSeat(seat);                                
            this.customers.add(newCustomer);  
            temp.book(newCustomer);  
            this.nBookedSeats_++;           
        }
        this.recentlyChanged = true;
    }
            
    //Unbooks the seat in the current concert that matches the supplied seat
    public void unBookSeat(Seat seat) throws CannotUnbookSeatException
    {        
        Customer customer = this.findCustomer(seat.getBookee());
        Seat temp = this.findSeat(seat);
        
        if(customer != null) {
            temp.unBook(customer);           
            this.nBookedSeats_--; 
            if(!customer.hasBookedASeat())
            {
                this.customers.remove(customer);               
            }
        }
        this.recentlyChanged = true;
    }

    public void setLinePosition(int lineNum)
    {
        this.linePosition = lineNum;
    }

    public int getLinePosition()
    {
        return this.linePosition;
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
    
    //This returns the seat section price of the supplied seat section
    public double getSectionPrice(String seatSection)
    {              
        int i = 0;       
        while(!seatSection.equals(SEAT_SECTIONS[i])) {
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
        fullReport.add("Customers: " + String.valueOf(this.customers.size()));
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
    
    //This method returns all the seats that the supplied customer has booked,
    //as well as any entitlements that have been given to them
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
    
    //This method changes a seat sections price, and assigns the new price
    //to the approriate seats belonging to that section
    public void setSectionPrice(String seatSection, double newPrice) 
    {               
        int i = 0;
        double finalPrice = Double.parseDouble(PRICE_FORMAT.format(newPrice)); 
        
        while(!seatSection.equals(SEAT_SECTIONS[i])) {
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
        this.recentlyChanged = true;
    }
    
    //Finds and returns the customer with the supplied name
    private Customer findCustomer(String name)
    {       
        if(this.customers.size() > 0) {
            if(Arrays.binarySearch(this.customers.toArray(), new Customer(name)) >= 0) {
                return this.customers.get(Arrays.binarySearch(this.customers.toArray(), new Customer(name)));
            }
        }
        return null;
    }
           
    //Finds and returns the seat in this concert, that matches the supplied seat
    private Seat findSeat(Seat seat)
    {        
        return this.seats[Arrays.binarySearch(this.seats, seat)];
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

    @Override
    public int compareTo(Object obj) 
    {
        if(this.hashCode() < ((Concert) obj).hashCode()) {
            return -1;
        }
        else if(this.hashCode() == ((Concert) obj).hashCode()) {
            return 0;
        }
        else {
            return 1;
        }          
    }

    public boolean isRecentlyChanged()
    {
        return this.recentlyChanged;
    }
}                                


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

public class Concert
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
    private final List<Customer> customers;
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
    public boolean save(String directory) throws FileNotFoundException
    {
        try {
            //Create directory for current concert
            File concertDirectory = new File(
                directory + File.separator + this
            );
            concertDirectory.mkdir();

            this.saveCustomers(concertDirectory);
            this.saveSeats(concertDirectory);
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
            return false;
        }
        this.recentlyChanged = false;
        return true;
    }

    private void saveSeats(File concertDirectory) throws FileNotFoundException
    {
        PrintWriter seatOutput = new PrintWriter(new File(
                concertDirectory + File.separator + "Booked_seats.txt")
        );

        for (Seat seat : this.seats) {
            if (seat.getStatus()) {
                if (seat.save(seatOutput)) {
                    System.out.println(
                            "Successfully saved seat " + "(" + seat + ")"
                                    + " for concert " + this
                    );
                } else {
                    System.out.println(
                            "Failed to save seat " + "(" + seat + ")"
                                    + " for concert " + this
                    );
                }
            }
        }
        seatOutput.close();
    }

    private void saveCustomers(File concertDirectory) throws FileNotFoundException
    {
        PrintWriter customerOutput = new PrintWriter(new File(
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
        customerOutput.close();
    }
    
    //Load in a concert from file, populating it with its customers and booked seats, 
    //and returns it for the ConcertController to manage
    public static Concert load(Scanner concertInput, String mainDirectory, int concertLineNum) throws ConcertIOException
    {
        Concert tempConcert = new Concert();
        tempConcert.linePosition = concertLineNum;
        List<RuntimeException> errors = new ArrayList<>();
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

            File concertDirectory = new File(mainDirectory + File.separator + tempConcert);
            concertDirectory.mkdir();

            loadCustomers(concertDirectory, tempConcert, errors);
            loadSeats(concertDirectory, tempConcert, errors);
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
        }
        finally {
            if(concertInput.hasNextLine()) {
                concertInput.nextLine();
            }
        }
        if(errors.size() > 0) {
            throw new ConcertIOException(tempConcert, errors);
        }
        return tempConcert;
    }
    
    private static void loadCustomers(File concertDirectory, Concert tempConcert, List<RuntimeException> errors) throws IOException
    {                                                          
        int customerLineNum = 1;
        File customersFile = new File(concertDirectory + File.separator + "Customers.txt");

        if(customersFile.canRead()) {
            Scanner customerInput = new Scanner(customersFile);
            while(customerInput.hasNextLine()) {
                try {
                    Customer tempCustomer = Customer.load(customerInput, customersFile, customerLineNum++);
                    tempConcert.customers.add(tempCustomer);
                }
                catch(CustomerIOException io) {
                    errors.add(io);
                }
            }
            customerInput.close();
        }
        else {
            customersFile.createNewFile();
        }
    }
    
    private static void loadSeats(File concertDirectory, Concert tempConcert, List<RuntimeException> errors) throws IOException
    {                                                
        int seatLineNum = 1;
        File seatsFile = new File(concertDirectory + File.separator + "Booked_seats.txt");

        if(seatsFile.canRead()) {
            Scanner seatInput = new Scanner(seatsFile);
            while(seatInput.hasNextLine()) {
                try {
                    Seat tempSeat = Seat.load(seatInput, seatsFile, seatLineNum);
                    Seat actualSeat = tempConcert.findSeat(tempSeat);
                    actualSeat.setBookee(tempSeat.getBookee());

                    Customer tempCustomer = new Customer(actualSeat.getBookee());
                    Customer actualCustomer = tempConcert.findCustomer(tempCustomer);

                    if (actualCustomer != null) {
                        actualCustomer.addSeat(actualSeat);
                        tempConcert.nBookedSeats_++;
                    }
                    else {
                        throw new SeatIOException(seatsFile, seatLineNum);
                    }
                    seatLineNum++;
                }
                catch(SeatIOException io) {
                    errors.add(io);
                }
            }
            seatInput.close();
        }
        else {
            seatsFile.createNewFile();
        }
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
    
    public List<Customer> getCustomers()
    {
        return this.customers;
    }

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

    public void bookSeat(Seat seat, String name)
    {
        Customer customer = this.findCustomer(new Customer(name));
        
        if(customer != null) {
            Seat actualSeat = this.findSeat(seat);
            actualSeat.book(customer);
        }
        else {           
            Customer newCustomer = new Customer(name); 
            Seat actualSeat = this.findSeat(seat);
            actualSeat.book(newCustomer);
            this.customers.add(newCustomer);
            this.customers.sort(null);
        }
        this.nBookedSeats_++;
        this.recentlyChanged = true;
    }

    public void unBookSeat(Seat seat) throws CannotUnbookSeatException
    {
        Customer customer = this.findCustomer(new Customer(seat.getBookee()));
        Seat temp = this.findSeat(seat);
        
        if(customer != null) {
            temp.unBook(customer);
            this.nBookedSeats_--; 
            if(!customer.hasBookedASeat())
            {
                this.customers.remove(customer);
                this.customers.sort(null);
            }
        }
        this.recentlyChanged = true;
    }

    public int getLinePosition()
    {
        return this.linePosition;
    }

    //This method returns the entitlement of the supplied seats bookee
    public String getCustomerEntitlement(Seat seat)
    {
        Customer customer = this.findCustomer(new Customer(seat.getBookee()));
        
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
        
        fullReport.add("Available Seats: ");
        fullReport.add(String.valueOf(this.seats.length - this.nBookedSeats_));
        fullReport.add("Booked Seats: ");
        fullReport.add(String.valueOf(this.nBookedSeats_));
        fullReport.add("Customers: ");
        fullReport.add(String.valueOf(this.customers.size()));
        fullReport.add("GoldSeat Price: ");
        fullReport.add("£" + PRICE_FORMAT.format(this.goldSectionPrice_));
        fullReport.add("SilverSeat Price: ");
        fullReport.add("£" + PRICE_FORMAT.format(this.silverSectionPrice_));
        fullReport.add("BronzeSeat Price: ");
        fullReport.add("£" + PRICE_FORMAT.format(this.bronzeSectionPrice_));
        fullReport.add("Total Sales: ");
        fullReport.add("£" + PRICE_FORMAT.format(totalSales));
        return fullReport;
    }
    
    //This method returns information about a specific seat,
    //such as if it is booked, and if someone has booked it, 
    //and returns if the person should receive any entitlements
    public String queryBySeat(Seat seat)
    {                         
        if(seat.getStatus()) {
            Customer customer = this.findCustomer(new Customer(seat.getBookee()));
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
    public String queryByCustomer(String name)
    {              
        String returnQuery = "";
        Customer customer = this.findCustomer(new Customer(name));
        
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
    //to the appropriate seats belonging to that section
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

    private Customer findCustomer(Customer customer)
    {
        int i = 0;
        boolean foundCustomer = false;
        while (i < this.customers.size() && !foundCustomer) {
            if(this.customers.get(i).equals(customer)) {
                foundCustomer = true;
            } else {
                i++;
            }
        }
        if(!foundCustomer) {
            return null;
        }
        else {
            return this.customers.get(i);
        }
    }

    private Seat findSeat(Seat seat)
    {        
        return this.seats[Arrays.binarySearch(this.seats, seat)];
    }

    @Override
    public String toString()
    {
        return this.name_ + " " + this.date_;
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
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.name_);
        hash = 61 * hash + Objects.hashCode(this.date_);
        return hash;
    }         

    public boolean isRecentlyChanged()
    {
        return this.recentlyChanged;
    }
}                                


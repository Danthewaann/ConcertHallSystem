package concerthallsystem.controllers;

import concerthallsystem.Concert;
import concerthallsystem.DialogPopup;
import concerthallsystem.exceptions.ConcertIOException;
import concerthallsystem.exceptions.ConcertAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This is the ConcertController class that manages and holds each concert in the system.
 * This class is responsible for loading and saving concerts from/to file.
 * The class also manages and reports errors if their is a problem when trying to either
 * load or save a concert to file, so this class is the main area where error checking will be
 * carried out.
 * 
 * @author Daniel Black
 */

public class ConcertController
{
    private List<Concert> concerts;
    private Concert currentConcert;
    private List<ConcertIOException> concertExceptions;
    private List<Concert> duplicatedConcerts;
    private static final String MAIN_DIRECTORY = "Concerts";
    private static final String CONCERT_LIST = "Concert_list.txt";
    
    public ConcertController()
    {      
        this.concerts = new ArrayList<>();
        this.concertExceptions = new ArrayList<>();
        this.duplicatedConcerts = new ArrayList<>();
        try {            
            this.loadConcerts();
        }
        catch(FileNotFoundException f) {
            File directory = new File(MAIN_DIRECTORY);
            directory.mkdir();
        }
    }

    private void displayDuplicatedConcerts() throws ConcertAlreadyExistsException
    {
        //This is where duplicatedConcerts is checked to see if any concerts are
        //stored inside it, if there is then we throw an exception, passing the
        //concerts that have duplicates to a ConcertAlreadyExistsException object to be caught
        if(this.duplicatedConcerts.size() > 0) {
            String concertList = "";
            for(Concert concert : this.duplicatedConcerts) {
                concertList += "----- " + concert +  " ----- on line " + concert.getLinePosition() + "\n";
            }
            throw new ConcertAlreadyExistsException(concertList, MAIN_DIRECTORY + File.separator + CONCERT_LIST);
        }
    }

    private void checkForDuplicatedConcerts(Concert concert, int lineNum)
    {
        //See if the loaded concert already exists,
        //if not then just add it to the concerts list
        if(concert != null) {
            Concert actual = this.findConcert(concert);
            if(actual != null) {
                if(this.duplicatedConcerts.contains(actual)) {
                    this.duplicatedConcerts.add(concert);
                } else {
                    this.duplicatedConcerts.add(actual);
                    this.duplicatedConcerts.add(concert);
                }
            }
            else {
                this.concerts.add(concert);
            }
            concert.setLinePosition(lineNum);
        }
    }

    private void loadConcerts() throws FileNotFoundException
    {
        //Connect to concerts directory and load each concert into the system from file
        Scanner concertInput = new Scanner(new File(MAIN_DIRECTORY + File.separator + CONCERT_LIST));

        int concertLineNum = 1; //Keeps track of what line we are on in the Concert_list file  
        
        while(concertInput.hasNextLine()) {                  
            //This part checks if the currently loaded concert already exists in the system,
            //and if it does it adds it to dupConcerts that will hold it for error checking 
            //after we have finished loading in all the concerts
            try {
                Concert temp = Concert.load(concertInput, MAIN_DIRECTORY, concertLineNum);
                this.checkForDuplicatedConcerts(temp, concertLineNum);
            }                       
            catch(ConcertIOException e) {
                //stores the ConcertIOException when caught, so we can detail all
                //ConcertIOExceptions to the user all at once when we finish loading
                //in all the concerts from file
                this.concertExceptions.add(e);
                this.checkForDuplicatedConcerts(e.getConcert(), concertLineNum);
            }
            concertLineNum++;
        }
        concertInput.close();
        this.checkForErrors();
    }  
    
    //Saves all concerts, along with their customers and booked seats to file
    public void saveConcerts() throws FileNotFoundException
    {                             
        //Connect to concerts directory and save each concert to file
        PrintWriter concertOutput = new PrintWriter(MAIN_DIRECTORY + File.separator + CONCERT_LIST);
        
        for(int i = 0; i < this.concerts.size(); i++) {           
            if(this.concerts.get(i).save(concertOutput, MAIN_DIRECTORY)) {
                System.out.printf(
                    "Successfully saved concert %s%n", this.concerts.get(i)                   
                );
            }
            else {
                System.out.printf(
                    "Failed to save concert %s%n", this.concerts.get(i)
                );
            }
        }
        concertOutput.close();                
    }   
    
    public Concert findConcert(Concert concert)
    {
        if(this.concerts.size() > 0) {
            if(Arrays.binarySearch(this.concerts.toArray(), concert) >= 0) {
                return this.concerts.get(Arrays.binarySearch(this.concerts.toArray(), concert));
            }
        }
        return null;   
    }

    private void checkForErrors()
    {
        //This is where concertExceptions is checked to see if we have caught any
        //ConcertIOExceptions, and if we have, we detail them to the user so they know
        //which concert on which line in the Concert_list file has failed to load
        String errorReport = "";
        String dupReport = "";
        if(concertExceptions.size() > 0) {
            for (ConcertIOException exception : concertExceptions) {
                errorReport += exception.getMessage() + "\n";
            }
        }
        try {
            this.displayDuplicatedConcerts();
        }
        catch(ConcertAlreadyExistsException e) {
            dupReport += e.getMessage() + "\n";
        }
        if(errorReport.length() > 0) {
            DialogPopup.drawErrorDialog(
                    "One or more concerts failed to load in location " + MAIN_DIRECTORY + File.separator + CONCERT_LIST + "\n" + errorReport + dupReport
            );
            System.exit(1);
        }
        else if(dupReport.length() > 0){
            DialogPopup.drawErrorDialog(dupReport);
            System.exit(1);
        }
    }
            
    public List<Concert> getConcertList()
    {
        return this.concerts;
    }
    
    public void setCurrentConcert(Concert concert)
    {
        this.currentConcert = concert;
    }
    
    public Concert getCurrentConcert()
    {
        return this.currentConcert;
    }
}


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

    private void checkForDuplicatedConcerts() throws ConcertAlreadyExistsException
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

    private boolean concertAlreadyExists(Concert concert)
    {
        //See if the loaded concert already exists,
        //if not then just add it to the concert list
        Concert actual = this.findConcert(concert);
        if(actual != null) {
            if(this.duplicatedConcerts.contains(actual)) {
                this.duplicatedConcerts.add(concert);
            } else {
                this.duplicatedConcerts.add(actual);
                this.duplicatedConcerts.add(concert);
            }
            return true;
        }
        return false;
    }

    private void loadConcerts() throws FileNotFoundException
    {
        Scanner concertInput = new Scanner(new File(MAIN_DIRECTORY + File.separator + CONCERT_LIST));

        int concertLineNum = 1;
        Concert temp = null;
        
        while(concertInput.hasNextLine()) {
            try {
                temp = Concert.load(concertInput, MAIN_DIRECTORY, concertLineNum++);
            }                       
            catch(ConcertIOException e) {
                this.concertExceptions.add(e);
            }
            finally {
                if(temp != null) {
                    if(!this.concertAlreadyExists(temp)) {
                        this.concerts.add(temp);
                    }
                    temp = null;
                }
            }
        }
        concertInput.close();
        this.checkForErrors();
    }  

    public void saveConcerts() throws FileNotFoundException
    {
        PrintWriter concertOutput = new PrintWriter(MAIN_DIRECTORY + File.separator + CONCERT_LIST);

        for(Concert concert : this.concerts) {
            if(concert.isRecentlyChanged()) {
                if (concert.save(concertOutput, MAIN_DIRECTORY)) {
                    System.out.printf(
                            "Successfully saved concert %s%n", concert
                    );
                } else {
                    System.out.printf(
                            "Failed to save concert %s%n", concert
                    );
                }
            }
        }
        concertOutput.close();                
    }

    //TODO - Find a way to save just one concert to file, instead of all concerts
    public void saveConcert(Concert concert) throws FileNotFoundException
    {
        PrintWriter concertOutput = new PrintWriter(MAIN_DIRECTORY + File.separator + CONCERT_LIST);
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
            this.checkForDuplicatedConcerts();
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


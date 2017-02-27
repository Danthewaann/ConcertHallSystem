package concerthallsystem.controllers;

import concerthallsystem.Concert;
import concerthallsystem.DialogPopup;
import concerthallsystem.exceptions.ConcertIOException;
import concerthallsystem.exceptions.ConcertAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    private static final String MAIN_DIRECTORY = "Concerts";
    private static final String CONCERT_LIST = "Concert_list.txt";  
    
    public ConcertController()
    {      
        this.concerts = new ArrayList<>();
        try {            
            this.loadConcerts();
        }
        catch(FileNotFoundException f) {
            File directory = new File(MAIN_DIRECTORY);
            directory.mkdir();
        }
        catch(ConcertAlreadyExistsException e) {
            DialogPopup.drawResultDialog(e.getMessage());
        }
    }
    
    private void loadConcerts() throws FileNotFoundException, ConcertAlreadyExistsException
    {
        //Connect to concerts directory and load each concert into the system from file
        Scanner concertInput = new Scanner(new File(MAIN_DIRECTORY + File.separator + CONCERT_LIST));
        ArrayList<ConcertIOException> concertExceptions = new ArrayList<>(); //Stores concert load errors
        ArrayList<Concert> dupConcerts = new ArrayList<>(); //Stores duplicate concerts detected in file        
        int concertLineNum = 1; //Keeps track of what line we are on in the Concert_list file    
        while(concertInput.hasNextLine()) {                  
            //This part checks if the currently loaded concert already exists in the system,
            //and if it does it adds it to dupConcerts that will hold it for error checking 
            //after we have finished loading in all the concerts
            try {
                Concert concert = Concert.load(concertInput, MAIN_DIRECTORY, concertLineNum++);
                if(concert != null) {                                       
                    for(int i = 0; i < this.concerts.size(); i++) {                                               
                        if(concert.equals(this.concerts.get(i))) {                               
                            if(!dupConcerts.contains(concert)) {
                                dupConcerts.add(concert);                                    
                            }                                                              
                        }                        
                    }                    
                    this.concerts.add(concert);                    
                }               
            }                       
            catch(ConcertIOException e) {
                //stores the ConcertIOException when caught, so we can detail all
                //ConcertIOExceptions to the user all at once when we finish loading
                //in all the concerts from file
                concertExceptions.add(e);               
            }
        }
        concertInput.close();
                         
        //This is where dupConcerts is checked to see if any concerts are
        //stored inside it, if there is then we throw an exception, passing the
        //concerts that have duplicates to a ConcertAlreadyExistsException object to be caught
        if(dupConcerts.size() > 0) {
            String concertList = "";               
            for(Concert concert : dupConcerts) {
                concertList += "----- " + concert + " -----\n";                                   
            }            
            throw new ConcertAlreadyExistsException(concertList, MAIN_DIRECTORY + File.separator + CONCERT_LIST);
        }     
        
        //This is where concertExceptions is checked to see if we have caught any 
        //ConcertIOExceptions, and if we have, we detail them to the user so they know
        //which concert on which line in the Concert_list file has failed to load
        if(concertExceptions.size() > 0) {
            String errorReport = "";
            for(ConcertIOException exception : concertExceptions) {
                errorReport += exception.getMessage() + "\n";
            }
            DialogPopup.drawResultDialog(
                "One or more concerts failed to load:\n" + errorReport + "\n" 
                + "...in loaction " + MAIN_DIRECTORY + File.separator + CONCERT_LIST
            );                     
            System.exit(0);
        }       
    }  
    
    //Saves all concerts, along with their customers and seats to file
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


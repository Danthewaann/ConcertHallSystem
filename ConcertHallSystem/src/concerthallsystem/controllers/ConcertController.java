package concerthallsystem.controllers;

import concerthallsystem.Concert;
import concerthallsystem.Constant;
import concerthallsystem.exceptions.ConcertIOException;
import concerthallsystem.exceptions.ConcertAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Daniel
 */
class ConcertController 
{
    private ArrayList<Concert> concerts = new ArrayList<>();
    private Concert currentConcert;   
    
    public ConcertController()
    {
        try
        {            
            this.loadConcerts();
        }
        catch(FileNotFoundException f)
        {
            File directory = new File(Constant.DIRECTORY);
            directory.mkdir();
        }
    }
    
    private void loadConcerts() throws FileNotFoundException
    {
        //Connect to concerts directory and load each concert into the system from file
        Scanner concertInput = new Scanner(new File(Constant.DIRECTORY + File.separator + Constant.CONCERTS_FILE));
        
        ArrayList<ConcertIOException> concertExceptions = new ArrayList<>(); //Stores concert load errors
        ArrayList<Concert> dupConcerts = new ArrayList<>(); //Stores duplicate concerts detected in file        
        int concertLineNum = 1; //Keeps track of what line we are on in the Concert_list file    
        while(concertInput.hasNextLine())
        {                  
            //This part checks if the currently loaded concert already exists in the system,
            //and if it does it adds it to dupConcerts that will hold it for error checking 
            //after we have finished loading in all the concerts
            try
            {
                Concert concert = Concert.load(concertInput, concertLineNum);
                if(concert != null)
                {
                    if(this.concerts.size() > 0)
                    {
                        for(int i = 0; i < this.concerts.size(); i++)
                        {                                               
                            if(concert.getName().compareToIgnoreCase(this.concerts.get(i).getName()) == 0)
                            {
                                if(concert.getDateWithSlashes().compareTo(this.concerts.get(i).getDateWithSlashes()) == 0)
                                {
                                    if(!dupConcerts.contains(concert))                              
                                    {
                                        dupConcerts.add(concert);                                    
                                    }                               
                                }
                            }                        
                        }
                    }
                    this.concerts.add(concert);                    
                }
                concertLineNum++;
            }                       
            catch(ConcertIOException e)
            {
                //stores the ConcertIOException when caught, so we can detail all
                //ConcertIOExceptions to the user all at once when we finish loading
                //in all the concerts from file
                concertExceptions.add(e);
                concertLineNum++;
            }
        }
                         
        //This is where dupConcerts is checked to see if any concerts are
        //stored inside it, if there is then we throw an exception, passing the
        //concerts that have duplicates to a ConcertAlreadyExistsException object to be caught
        if(dupConcerts.size() > 0)
        {
            String concertList = "";               
            for(Concert concert : dupConcerts)
            {
                concertList += "----- " + concert.getName() + " " + concert.getDateWithSlashes() + " -----\n";                                   
            }            
            throw new ConcertAlreadyExistsException(concertList, Constant.DIRECTORY + File.separator + Constant.CONCERTS_FILE);
        }     
        
        //This is where concertExceptions is checked to see if we have caught any 
        //ConcertIOExceptions, and if we have, we detail them to the user so they know
        //which concert on which line in the Concert_list file has failed to load
        if(concertExceptions.size() > 0)
        {
            String errorReport = "";
            for(ConcertIOException exception : concertExceptions)
            {
                errorReport += exception.getMessage() + "\n";
            }
//            MessagePanel.displayMessage(
//                this, "One or more concerts failed to load:\n" + errorReport + "\n"
//                + "...in location " + Constant.DIRECTORY + File.separator + Constant.CONCERTS_FILE,
//                "Fatal Error: " + Constant.DIRECTORY + File.separator + Constant.CONCERTS_FILE
//            ); 
            System.exit(0);
        }       
    }  
    
    public ArrayList<Concert> getConcertList()
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


package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.scene.Node;

/**
 * This purpose of this class is to respond to a mouse click on a seat icon
 * in the seating plan of the GUI class. This class checks to see if the selected
 * seat is either booked or not, and acts accordingly depending on the seat type
 * that is passed through to the constructor when an object of this class is initialized 
 * 
 * @author Daniel Black
 */
public class SeatEventHandler implements EventHandler
{
    private final Concert selectedConcert_;   
    private final Seat selectedSeat_;     
    private final Node seatIcon;
              
    public SeatEventHandler(Concert concert, Seat seat, Node icon) 
    {
        this.selectedConcert_ = concert; 
        this.selectedSeat_ = seat;
        this.seatIcon = icon;
    }

    @Override
    public void handle(Event event) 
    {
        if(this.selectedSeat_.getStatus())
        {
            String seatType = this.selectedSeat_.getClass().getSimpleName();
            try
            {
                if(seatType.equals("GoldSeat") || seatType.equals("SilverSeat"))
                {
                    //Prompt the user to ask them if they want to unbook the selected seat
                    Object[] options = {"Yes", "No"};
                    int option = MessagePanel.optionDialog(
                        null, "Are you sure you want to unbook this seat?", 
                        "Unbook Seat " + "(" + this.selectedSeat_.getPosition() + ")", options
                    );   
                    
                    //If the option selected was yes, then unbook the selected seat
                    if(option == 0)
                    {                                                                       
                        if(seatType.equals("GoldSeat"))
                        {
                            this.seatIcon.setStyle("-fx-background-color: linear-gradient(#FFD700, #EDC800);");
                        }
                        else
                        {
                            this.seatIcon.setStyle("-fx-background-color: linear-gradient(#C0C0C0, #ABABAB);");
                        }                                               
                    }
                }
                this.selectedConcert_.unBookSeat(this.selectedSeat_);
            }
            catch(CannotUnbookSeatException e)
            {
                MessagePanel.displayMessage(
                    null, e.getMessage(), "Cannot Unbook Seat " 
                    + "(" + this.selectedSeat_.getPosition() + ")"
                );
            }          
        } 
        //If the seat is not booked, then allow the user to book it
        else
        {                                                 
            String customerName = MessagePanel.inputDialog(
                null, "Please input your fullname:", "Booking for " 
                +  "(" + this.selectedSeat_.getPosition() + ")"
            );
                        
            //Numerous checks on the supplied customerName for the booking
            if(customerName.length() > 30)
            {
                MessagePanel.displayMessage(
                    null, "Inputted value is too long", "Error: Invalid Input"
                );               
            }
            else if(customerName.length() == 0)
            {   
                MessagePanel.displayMessage(
                    null, "Please input a name to book this seat", "Error: Invalid Input"
                );                
            }
            else
            {
                boolean nameIsAString = false;
                try
                {                
                    //Checks input to see if it is a number,
                    //if this code doesn't throw an error, input is a number                    
                    Double.parseDouble(customerName);
                }
                catch(NumberFormatException i)
                {                   
                    //If we catch this error, the input is a string
                    nameIsAString = true;
                }                                                                  
                if(nameIsAString)
                {
                    this.selectedConcert_.bookSeat(this.selectedSeat_, customerName);                                      
                    String entitlement = this.selectedConcert_.getCustomerEntitlement(this.selectedSeat_);
                    if(entitlement == null)
                    {
                        MessagePanel.displayMessage(
                            null, Constant.capitalize(customerName) + " has booked seat " 
                            + "(" + this.selectedSeat_.getPosition() + ")", "Seat Bookings"
                        );                       
                    }
                    else
                    {
                        MessagePanel.displayMessage(
                            null, Constant.capitalize(customerName) + " has booked seat " 
                            + "(" + this.selectedSeat_.getPosition() + ")" + "\n" + entitlement, "Seat Bookings"
                        );                       
                    }                   
                    this.seatIcon.setStyle("-fx-background-color: linear-gradient(#FF0000, #D10000);");
                } 
                else
                {
                    MessagePanel.displayMessage(
                        null, "Inputted value is not a name", "Error: Invalid Input"
                    );                   
                }
            }
        }
    }    
}

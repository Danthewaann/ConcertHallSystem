package concerthallsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * This purpose of this class is to respond to a mouse click on a seat icon
 * in the seating plan of the GUI class. This class checks to see if the selected
 * seat is either booked or not, and acts accordingly depending on the seat type
 * that is passed through to the constructor when an object of this class is initialized 
 * 
 * @author Daniel Black
 */
public class SeatHandler implements ActionListener
{
    private final Concert selectedConcert_;   
    private final JButton seatIcon_;
    private final Seat selectedSeat_;
    private final JFrame frame_;
    private final String seatType_;
        
    public SeatHandler(JFrame frame, Concert concert, JButton seatIcon, Seat seat, String seatType)
    {
        this.frame_ = frame;
        this.selectedConcert_ = concert;       
        this.seatIcon_ = seatIcon;
        this.selectedSeat_ = seat;
        this.seatType_ = seatType;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {                                          
        //Checks if the seat is either a gold or silver seat and if it is booked
        if(this.selectedSeat_.getStatus())
        {
            try
            {
                if(this.seatType_.equals("GoldSeat") || this.seatType_.equals("SilverSeat"))
                {
                    //Prompt the user to ask them if they want to unbook the selected seat
                    Object[] options = {"Yes", "No"};
                    int option = MessagePanel.optionDialog(
                        this.frame_, "Are you sure you want to unbook this seat?", 
                        "Unbook Seat " + "(" + this.selectedSeat_.getSeatPosition() + ")", options
                    );   
                    
                    //If the option selected was yes, then unbook the selected seat
                    if(option == 0)
                    {                                                                       
                        if(this.seatType_.equals("GoldSeat"))
                        {
                            this.seatIcon_.setBackground(Constant.GOLD);
                        }
                        else
                        {
                            this.seatIcon_.setBackground(Constant.SILVER);
                        }                                               
                    }
                }
                this.selectedConcert_.unBookSeat(this.selectedSeat_);
            }
            catch(CannotUnbookSeatException e)
            {
                MessagePanel.displayMessage(
                    this.frame_, e.getMessage(), "Cannot Unbook Seat " 
                    + "(" + this.selectedSeat_.getSeatPosition() + ")"
                );
            }          
        }                        
        //If the seat is not booked, then allow the user to book it
        else
        {                                                 
            String customerName = MessagePanel.inputDialog(
                this.frame_, "Please input your fullname:", "Booking for " 
                +  "(" + this.selectedSeat_.getSeatPosition() + ")"
            );
                        
            //Numerous checks on the supplied customerName for the booking
            if(customerName.length() > 30)
            {
                MessagePanel.displayMessage(
                    this.frame_, "Inputted value is too long", "Error: Invalid Input"
                );               
            }
            else if(customerName.length() == 0)
            {   
                MessagePanel.displayMessage(
                    this.frame_, "Please input a name to book this seat", "Error: Invalid Input"
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
                            this.frame_, Constant.capitalize(customerName) + " has booked seat " 
                            + "(" + this.selectedSeat_.getSeatPosition() + ")", "Seat Bookings"
                        );                       
                    }
                    else
                    {
                        MessagePanel.displayMessage(
                            this.frame_, Constant.capitalize(customerName) + " has booked seat " 
                            + "(" + this.selectedSeat_.getSeatPosition() + ")" + "\n" + entitlement, "Seat Bookings"
                        );                       
                    }                   
                    this.seatIcon_.setBackground(Constant.RED);
                } 
                else
                {
                    MessagePanel.displayMessage(
                        this.frame_, "Inputted value is not a name", "Error: Invalid Input"
                    );                   
                }
            }            
        }
    }       
}

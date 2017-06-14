package concerthallsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This purpose of this class is to respond to a mouse click on a seat icon
 * in the seating plan of the GUI class. This class checks to see if the selected
 * seat is either booked or not, and acting accordingly depending on the seat type
 * that is passed through to the constructor when an object of this class is initialized 
 * 
 * @author Daniel Black, George Bingham, Rebecca Curtis, Minyu Lei
 */
public class SeatHandler implements ActionListener
{
    private Concert[] concerts;
    private int selectedConcertIndex_;
    private JButton seatIcon_;
    private String seatRow_;
    private int seatNum_;
    private JFrame frame_;
        
    public SeatHandler(JFrame frame, Concert[] concerts, int selectedConcertIndex, JButton seatIcon, String seatRow, int seatNum)
    {
        this.frame_ = frame;
        this.concerts = concerts;
        this.selectedConcertIndex_ = selectedConcertIndex;
        this.seatIcon_ = seatIcon;
        this.seatRow_ = seatRow;
        this.seatNum_ = seatNum;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        //Gets the seat index (0 to 89) and the seat position (A1 to I10) for use elsewhere
        int seatIndex = this.concerts[this.selectedConcertIndex_].getSeatAsInt(seatRow_, seatNum_);
        String seatPosition = this.concerts[this.selectedConcertIndex_].getSeatAsString(seatRow_, seatNum_);
        
        //Checks if the seat is either a gold or silver seat and is booked
        if(seatIcon_.getBackground() == Constant.RED && seatIndex < 60)
        {
            //Prompt the user to ask them if they want to unbook the selected seat
            Object[] options = {"Yes", "No"};
            int option = JOptionPane.showOptionDialog(
                frame_, "Are you sure you want to unbook this seat?", "Unbook Seat " + "(" + seatPosition + ")", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
            );
            
            //If the option selected was yes, then unbook the selected seat
            if(option == 0)
            {
                this.concerts[this.selectedConcertIndex_].unBookSeat(seatRow_, seatNum_);
                if(seatIndex < 30)
                {
                    seatIcon_.setBackground(Constant.GOLD);
                }
                else
                {
                    seatIcon_.setBackground(Constant.SILVER);
                }                                               
            }  
        }
        
        //Checks if the seat is a bronze seat and is booked, telling the user they can't unbook it
        else if(seatIcon_.getBackground() == Constant.RED && seatIndex >= 60)
        {    
            JOptionPane.showMessageDialog(
                frame_, "Cannot unbook seats in the Bronze section", "Cannot unbook seat " 
                + "(" + seatPosition + ")", JOptionPane.PLAIN_MESSAGE
            );
        }
        
        //If the seat is not booked, then allow the user to book it
        else
        {                                     
            String customerNameInput = JOptionPane.showInputDialog(
                frame_, "Please input your fullname:", "Booking for " +  "(" + seatPosition + ")", JOptionPane.PLAIN_MESSAGE
            );
            
            String customerName = customerNameInput.trim();
            
            //Numerous checks on the supplied customerName for the booking
            if(customerName.length() > 30)
            {
                JOptionPane.showMessageDialog(
                    frame_, "Inputted value is too long", "Error: Invalid Input", JOptionPane.PLAIN_MESSAGE
                );
            }
            else if(customerName.length() == 0)
            {   
                JOptionPane.showMessageDialog(
                    frame_, "Please input a name to book this seat", "Error: Invalid Input", JOptionPane.PLAIN_MESSAGE
                );
            }
            else
            {
                boolean nameIsAString = false;
                try
                {                
                    //Checks input to see if it is a number,
                    //if this code doesn't throw an error
                    //the input is then a number
                    double nameToDouble = Double.parseDouble(customerName);
                }
                catch(NumberFormatException i)
                {                   
                    //If we catch this error, the input is a string
                    nameIsAString = true;
                }                                                                  
                if(nameIsAString)
                {
                    this.concerts[this.selectedConcertIndex_].bookSeat(seatRow_, seatNum_, customerName);
                    
                    JOptionPane.showMessageDialog(frame_, customerName + " has booked seat " + "(" + seatPosition + ")" + 
                        "\n" + this.concerts[this.selectedConcertIndex_].getSeatEntitlement(seatRow_, seatNum_), "Seat Bookings", JOptionPane.PLAIN_MESSAGE                           
                    );
                    
                    seatIcon_.setBackground(Constant.RED);
                } 
                else
                {
                    JOptionPane.showMessageDialog(
                        frame_, "Inputted value is not a name", "Error: Invalid Input", JOptionPane.PLAIN_MESSAGE
                    );
                }
            }           
        }
    }       
}

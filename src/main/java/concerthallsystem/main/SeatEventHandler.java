package concerthallsystem.main;

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
        //If the seat is booked, then try to unbook it
        if(this.selectedSeat_.getStatus()) {
            DialogPopup unBookSeatDialog = new DialogPopup();
            unBookSeatDialog.drawUnBookSeatDialog(this.selectedConcert_, this.selectedSeat_, this.seatIcon);
        }
        //If the seat is not booked, then allow the user to book it
        else {
            DialogPopup bookSeatDialog = new DialogPopup();
            bookSeatDialog.drawBookSeatDialog(this.selectedConcert_, this.selectedSeat_, this.seatIcon);
        }
    }
}

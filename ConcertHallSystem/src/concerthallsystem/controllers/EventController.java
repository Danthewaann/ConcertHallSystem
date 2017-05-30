package concerthallsystem.controllers;

import concerthallsystem.Concert;
import concerthallsystem.SeatEventHandler;
import concerthallsystem.exceptions.ConcertAlreadyExistsException;
import java.io.FileNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * The EventController class responds to user action, such as a button press in 
 * any view. This class then decides how to respond to that event, and it calls
 * on the SceneController and ConcertController classes to provide the necessary information
 * to display to the user
 * 
 * @author Daniel Black
 */

public class EventController 
{    
    private final SceneController sceneController;
    private final ConcertController concertController;
    
    @FXML
    private GridPane seatIcons;
    @FXML
    private TextField newConcertName;
    @FXML
    private DatePicker newConcertDate;
    @FXML
    private ComboBox dropDownList;
    @FXML
    private Label selectedConcertTitle;
    
    public EventController(SceneController controller)
    {        
        this.sceneController = controller;
        this.concertController = new ConcertController();        
    }
                              
    @FXML
    private void goToCreateConcertScene(ActionEvent event)
    {
        this.sceneController.setScene("CreateConcert");               
    }
    
    @FXML
    private void goToSelectConcertScene(ActionEvent event)
    {           
        ObservableList<String> concertList = FXCollections.observableArrayList();
        for(Concert concert : this.concertController.getConcertList()) {
            concertList.add(concert.getName() + " | " + concert.getDate());
        }
        this.dropDownList.setItems(concertList);
        this.sceneController.setScene("SelectConcert");
    }
    
    @FXML
    private void goToMainMenuScene(ActionEvent event)
    {        
        if(this.sceneController.getCurrentScene().equals(this.sceneController.getAllScenes().get("SelectConcert"))) {           
            this.dropDownList.getItems().clear();
        }  
        else if(this.sceneController.getCurrentScene().equals(this.sceneController.getAllScenes().get("CreateConcert"))) {
            this.newConcertName.clear();
            this.newConcertDate.getEditor().clear();
        }          
        this.sceneController.setScene("MainMenu");
    }
    
    @FXML
    private void exitApplication(ActionEvent event) //TODO
    {
        System.exit(0);
    }
            
    @FXML
    private void createNewConcert(ActionEvent event)
    {
        String name = newConcertName.getText().trim();
        String date = newConcertDate.getValue().toString();
        Concert temp = new Concert(name, date);
        
        Concert actual = this.concertController.findConcert(temp);
        if(actual != null) {
            try {                   
                throw new ConcertAlreadyExistsException(actual);
            }
            catch(ConcertAlreadyExistsException e) {
                if(this.sceneController.displayConcertAlreadyExistsDialog(e.getMessage())) {
                    this.concertController.getConcertList().remove(actual);
                    this.concertController.getConcertList().add(temp);
                    this.concertController.setCurrentConcert(temp);
                    this.goToSeatingPlanScene(temp.getName() + " | " + temp.getDate());
                }
            }
        }                     
        else {           
            this.concertController.getConcertList().add(temp);
            this.concertController.setCurrentConcert(temp);
            this.dropDownList.getItems().add(temp.getName() + " | " + temp.getDate());
            this.goToSeatingPlanScene(temp.getName() + " | " + temp.getDate());
        }             
        this.newConcertName.clear();
        this.newConcertDate.getEditor().clear();
    }
    
    @FXML
    private void viewSelectedConcert(ActionEvent event) 
    {
        String selectedConcert = this.dropDownList.getSelectionModel().getSelectedItem().toString();
        int divider = selectedConcert.lastIndexOf("|");
        String name = selectedConcert.substring(0, divider-1);
        String date = selectedConcert.substring(divider+2);
        Concert temp = new Concert(name, date);
        
        Concert actual = this.concertController.findConcert(temp);
        if(actual != null) {
            this.concertController.setCurrentConcert(actual);
            this.goToSeatingPlanScene(selectedConcert);           
        }
        else {
            System.out.println("Error, couldn't find concert");
        }   
        this.dropDownList.getItems().clear();
    }
    
    private void goToSeatingPlanScene(String selectedConcert)
    {
        ObservableList<Node> seats = this.seatIcons.getChildren();
        for(int i = 0; i < seats.size(); i++) {               
            if(this.concertController.getCurrentConcert().getSeats()[i].getStatus()) {                    
                seats.get(i).setStyle("-fx-background-color: linear-gradient(#FF0000, #D10000);");
            }
            else {
                switch(this.concertController.getCurrentConcert().getSeats()[i].getClass().getSimpleName()) {
                    case "GoldSeat":
                        seats.get(i).setStyle("-fx-background-color: linear-gradient(#FFD700, #EDC800);");
                        break;
                    case "SilverSeat":
                        seats.get(i).setStyle("-fx-background-color: linear-gradient(#C0C0C0, #ABABAB);");
                        break;
                    default:
                        seats.get(i).setStyle("-fx-background-color: linear-gradient(#CD7F32, #B5702B);");
                        break;
                }
            }
            seats.get(i).setCursor(Cursor.HAND);
            seats.get(i).setOnMouseClicked(new SeatEventHandler(
                this.concertController.getCurrentConcert(), 
                this.concertController.getCurrentConcert().getSeats()[i], seats.get(i))
            );
        }           
        this.selectedConcertTitle.setText(selectedConcert);
        this.sceneController.setScene("SeatingPlan");
    }
    
    @FXML
    private void obtainReport(ActionEvent event)
    {    
        this.sceneController.displayReportDialog(            
            this.concertController.getCurrentConcert()
        );                              
    }
    
    @FXML
    private void setSectionPrice(ActionEvent event)
    {                     
        this.sceneController.displaySectionPriceDialog(             
            this.concertController.getCurrentConcert()
        ); 
    }
    
    @FXML
    private void querySeat(ActionEvent event)
    {
        this.sceneController.displayQuerySeatDialog(
            this.concertController.getCurrentConcert()
        );
    }
    
    @FXML
    private void queryCustomer(ActionEvent event)
    {
        this.sceneController.displayQueryCustomerDialog(           
            this.concertController.getCurrentConcert()
        );
    }
    
    @FXML
    private void saveConcert(ActionEvent event)
    {
        try {
            this.concertController.saveConcerts();
            this.sceneController.displaySaveDialog(
                this.concertController.getCurrentConcert()
            );
        }
        catch(FileNotFoundException e) { //TODO
            System.out.println("Error");
        }
    }       
}

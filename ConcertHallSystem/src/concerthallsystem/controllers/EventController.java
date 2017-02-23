package concerthallsystem.controllers;


import concerthallsystem.Concert;
import concerthallsystem.Constant;
import concerthallsystem.Customer;
import concerthallsystem.SeatEventHandler;
import concerthallsystem.exceptions.ConcertAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author Daniel
 */

public class EventController implements Initializable
{    
    private SceneController sceneController;
    private ConcertController concertController;
    
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
       
    @Override
    public void initialize(URL location, ResourceBundle resources){}
                        
    @FXML
    private void goToCreateConcertScene(ActionEvent event)
    {
        this.sceneController.setScene("CreateConcertScene.fxml");               
    }
    
    @FXML
    private void goToSelectConcertScene(ActionEvent event)
    {           
        ObservableList<String> concertList = FXCollections.observableArrayList();
        for(Concert concert : this.concertController.getConcertList())
        {
            concertList.add(concert.getName() + " | " + concert.getDateWithSlashes());
        }
        this.dropDownList.setItems(concertList);
        this.sceneController.setScene("SelectConcertScene.fxml");
    }
    
    @FXML
    private void goToMainMenu(ActionEvent event)
    {        
        if(this.sceneController.getCurrentScene().equals(this.sceneController.getAllScenes().get("SelectConcertScene.fxml")))
        {
            this.sceneController.setScene("MainMenuScene.fxml");
            this.dropDownList.getItems().clear();
        }  
        else
        {
            this.sceneController.setScene("MainMenuScene.fxml");
        }
    }
    
    @FXML
    private void exitApplication(ActionEvent event)
    {
        System.exit(0);
    }
    
    @FXML
    private void goToSeatingPlanScene(ActionEvent event)
    {
        this.sceneController.setScene("SeatingPlanScene.fxml");     
    }
        
    @FXML
    private void createNewConcert(ActionEvent event)
    {
        String name = newConcertName.getText();
        String date = newConcertDate.getValue().toString();
        int i = 0;
        boolean concertAlreadyExists = false;
        while(i < this.concertController.getConcertList().size() && !concertAlreadyExists)
        {
            if(name.compareToIgnoreCase(this.concertController.getConcertList().get(i).getName()) == 0)
            {
                if(date.compareTo(this.concertController.getConcertList().get(i).getDateWithSlashes()) == 0)
                {
                    try
                    {
                        concertAlreadyExists = true;
                        throw new ConcertAlreadyExistsException(this.concertController.getConcertList().get(i));
                    }
                    catch(ConcertAlreadyExistsException e)
                    {   
                        System.out.println("That concert already Exists!");
                    }
                }
                else
                {
                    i++;
                }
            }
            else
            {
                i++;
            }
        }
        if(!concertAlreadyExists)
        {
            Concert concert = new Concert(name, date);
            this.concertController.getConcertList().add(concert);
            this.concertController.setCurrentConcert(concert);
            this.dropDownList.getItems().add(concert.getName() + " | " + concert.getDateWithSlashes());
            this.goToSeatingPlan(concert.getName() + " | " + concert.getDateWithSlashes());
        }                
    }
    
    @FXML
    private void viewSelectedConcert(ActionEvent event)
    {
        String selectedConcert = this.dropDownList.getSelectionModel().getSelectedItem().toString();
        int divider = selectedConcert.lastIndexOf("|");
        String name = selectedConcert.substring(0, divider-1);
        String date = selectedConcert.substring(divider+2);
        
        int i = 0;
        boolean foundConcert = false;
        while(i < this.concertController.getConcertList().size() && !foundConcert)
        {
            if(name.compareToIgnoreCase(this.concertController.getConcertList().get(i).getName()) == 0)
            {
                if(date.compareTo(this.concertController.getConcertList().get(i).getDateWithSlashes()) == 0)
                {
                    foundConcert = true;
                    break;
                }
                else
                {
                    i++;
                }
            }
            else
            {
                i++;
            }
        }
        if(foundConcert)
        {           
            this.concertController.setCurrentConcert(this.concertController.getConcertList().get(i));
            this.goToSeatingPlan(selectedConcert);
        }
        else
        {
            System.out.println("Error, couldn't find concert");
        }
    }
    
    private void goToSeatingPlan(String selectedConcert)
    {
        ObservableList<Node> seats = this.seatIcons.getChildren();
        for(int j = 0; j < seats.size(); j++)
        {               
            if(this.concertController.getCurrentConcert().getSeats()[j].getStatus())
            {                    
                seats.get(j).setStyle("-fx-background-color: linear-gradient(#FF0000, #D10000);");
            }
            else
            {
                switch(this.concertController.getCurrentConcert().getSeats()[j].getClass().getSimpleName()) 
                {
                    case "GoldSeat":
                        seats.get(j).setStyle("-fx-background-color: linear-gradient(#FFD700, #EDC800);");
                        break;
                    case "SilverSeat":
                        seats.get(j).setStyle("-fx-background-color: linear-gradient(#C0C0C0, #ABABAB);");
                        break;
                    default:
                        seats.get(j).setStyle("-fx-background-color: linear-gradient(#CD7F32, #B5702B);");
                        break;
                }
            }
            seats.get(j).setCursor(Cursor.HAND);
            seats.get(j).setOnMouseClicked(new SeatEventHandler(
                this.concertController.getCurrentConcert(), 
                this.concertController.getCurrentConcert().getSeats()[j], seats.get(j))
            );
        }           

        this.selectedConcertTitle.setText(selectedConcert);
        this.sceneController.setScene("SeatingPlanScene.fxml");
    }
    
    @FXML
    private void obtainReport(ActionEvent event)
    {
        Dialog dialog = new Dialog();
        dialog.setHeaderText("Report for " + this.concertController.getCurrentConcert().getName() + " " + this.concertController.getCurrentConcert().getDateWithSlashes());
        ButtonType exit = new ButtonType("OK", ButtonData.OK_DONE);     
        dialog.getDialogPane().getButtonTypes().add(exit);
        Label label = new Label(this.concertController.getCurrentConcert().getReport());
        label.setStyle("-fx-font-size: 16px;");
        label.setPadding(new Insets(25,25,25,25));
        
        dialog.getDialogPane().setContent(label);
        dialog.show();
        Window window = dialog.getDialogPane().getScene().getWindow();       
        window.setOnCloseRequest(e -> window.hide());                                
    }
    
    @FXML
    private void setSectionPrice(ActionEvent event)
    {       
        ChoiceDialog<String> dialog = new ChoiceDialog();
        dialog.setTitle("Set Section Price");
        dialog.getItems().addAll(Concert.SEAT_SECTIONS);
        dialog.setSelectedItem("Gold");
        dialog.setHeaderText(null);        
        dialog.setContentText("Choose a section to change:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(new Consumer<String>() {
            @Override
            public void accept(String result)
            {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText(null);
                dialog.setContentText("Input a new price: £");
                Optional<String> price = dialog.showAndWait();
                price.ifPresent(e -> concertController.getCurrentConcert().setSectionPrice(result, Double.parseDouble(e)));
                System.out.println(concertController.getCurrentConcert().getSectionPrice(result));
            }
        });
    }
    
    @FXML
    private void querySeat(ActionEvent event)
    {
        Dialog dialog = new Dialog();
        dialog.setTitle("Query Seat");
        dialog.setHeaderText("Select a seat to query");
        
        ButtonType querySeat = new ButtonType("Query Seat", ButtonData.YES);     
        dialog.getDialogPane().getButtonTypes().addAll(querySeat, ButtonType.CANCEL);
        
        GridPane layout = new GridPane();
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setPadding(new Insets(25, 25, 25 ,25));
        
        ObservableList<String> seatRowsList = FXCollections.observableArrayList();
        for(String row: Concert.SEAT_ROWS)
        {
            seatRowsList.add(row);
        }       
        ComboBox seatRows = new ComboBox(seatRowsList);
        seatRows.setValue("A");
        
        ObservableList<String> seatNumsList = FXCollections.observableArrayList();
        for(int num: Concert.SEAT_NUMBERS)
        {
            seatNumsList.add(String.valueOf(num));
        }              
        ComboBox seatNums = new ComboBox(seatNumsList);
        seatNums.setValue("1");        
         
        layout.add(new Label("Select a row:"), 0, 0);
        layout.add(seatRows, 1, 0);  
        layout.add(new Label("Select a number:"), 2, 0);
        layout.add(seatNums, 3, 0);
        
        dialog.getDialogPane().setContent(layout);
        dialog.showAndWait();
    }
    
    @FXML
    private void queryCustomer(ActionEvent event)
    {
        ChoiceDialog<String> dialog = new ChoiceDialog();
        dialog.setTitle("Query Customer");       
        for(Customer customer: this.concertController.getCurrentConcert().getCustomers())
        {
            dialog.getItems().add(customer.getName());
        }
        dialog.setHeaderText(null);
        dialog.setContentText("Select a customer:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(new Consumer<String>() {
            @Override
            public void accept(String t) 
            {
                Dialog dialog = new Dialog();
                dialog.setHeaderText("Query Result for " + t);
                ButtonType exit = new ButtonType("OK", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(exit);              
                dialog.setTitle("Query Result");
                Label label= new Label(concertController.getCurrentConcert().queryByCustomer(t));
                label.setPadding(new Insets(25,25,25,25));
                label.setStyle("-fx-font-size: 16px;");
                dialog.getDialogPane().setContent(label);
                dialog.show();
                Window window = dialog.getDialogPane().getScene().getWindow();       
                window.setOnCloseRequest(e -> window.hide()); 
            }          
        }
        );
    }
    
    @FXML
    private void saveConcert(ActionEvent event)
    {
        
    }       
}

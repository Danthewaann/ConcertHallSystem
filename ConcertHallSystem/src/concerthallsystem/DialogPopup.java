package concerthallsystem;

import concerthallsystem.exceptions.CannotUnbookSeatException;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * This Class is responsible for drawing and displaying the dialog boxes
 * for every operation that a user can do in a concert seating plan.
 * The SceneController creates an instance of this class for specific events
 * that the EventController tells the SceneController to display
 * 
 * @author Daniel Black
 */

public class DialogPopup extends Dialog
{
    private final GridPane grid;   
    private final static ButtonType CANCEL = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    private final static ButtonType QUERY = new ButtonType("Query", ButtonData.OK_DONE);    
    private final static ButtonType OK = new ButtonType("OK", ButtonData.OK_DONE);
    private final static ButtonType BOOK = new ButtonType("Book", ButtonData.OK_DONE);       
    private final static ButtonType UNBOOK = new ButtonType("Unbook", ButtonData.YES);      
 
    public DialogPopup()
    {
        this.grid = new GridPane();        
        this.grid.setVgap(5);
        this.grid.setHgap(20);        
        this.grid.setPadding(new Insets(25, 25, 25, 25));
        this.grid.setAlignment(Pos.CENTER);      
        this.initOwner(Main.WINDOW);      
        this.getDialogPane().setContent(this.grid);       
    }
    
    public DialogPopup(boolean bool)
    {
        this.grid = new GridPane();
        this.grid.setVgap(5);
        this.grid.setHgap(20);        
        this.grid.setPadding(new Insets(25, 25, 25, 25));
        this.grid.setAlignment(Pos.CENTER);     
        this.getDialogPane().setContent(this.grid);    
    }
    
    private GridPane drawGridPane(ObservableList<Node> nodes, GridPane grid, int maxCols, int maxRows)
    {
        int rowIndex = 0;
        int colIndex = 0;
        for(Node node : nodes)
        {                                  
            if(colIndex < maxCols)
            {                            
                grid.add(node, colIndex++, rowIndex);  
            }   
            else
            {
                if(rowIndex < maxRows - 1)
                {
                    colIndex = 0;                       
                    grid.add(node, colIndex++, ++rowIndex);   
                }
                else
                {
                    break;
                }
            }          
        } 
        return grid;
    }
    
    public void drawReportDialog(List<String> elements, int maxCols, int maxRows)
    {           
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        
        for(String element : elements) {
            int i = element.lastIndexOf(":");
            
            Label label = new Label(element.substring(0, i+1));           
            label.setStyle("-fx-font-size: 16px");
            
            Label value = new Label(element.substring(i+2));
            value.setStyle("-fx-font-size: 16px");
            
            nodes.addAll(label, value);
        }      
        this.drawGridPane(nodes, this.grid, maxCols, maxRows);  
        this.getDialogPane().getButtonTypes().add(OK);
    }
    
    public void drawQuerySeatDialog(Concert concert, int maxCols, int maxRows)
    {          
        Label rowLabel = new Label("Select a Seat Row:");
        rowLabel.setStyle("-fx-font-size: 16px;");    
        
        Label numberLabel = new Label("Select a Seat Number:");
        numberLabel.setStyle("-fx-font-size: 16px;");
        
        ComboBox rows = new ComboBox();
        rows.prefWidthProperty().set(65);
        
        ComboBox numbers = new ComboBox();   
        numbers.prefWidthProperty().set(65);
        
        for(String row : Concert.SEAT_ROWS) {
            rows.getItems().add(row);
        }
        rows.getSelectionModel().selectFirst();
        
        for(int number : Concert.SEAT_NUMBERS) {
            numbers.getItems().add(String.valueOf(number));
        }
        numbers.getSelectionModel().selectFirst();
        
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        nodes.addAll(rowLabel, rows, numberLabel, numbers);          
        this.drawGridPane(nodes, this.grid, maxCols, maxRows);
        this.getDialogPane().getButtonTypes().addAll(QUERY, CANCEL); 
        
        this.setResultConverter(button -> {
            if(button == QUERY) {
                return new Pair<>(
                    rows.getSelectionModel().selectedItemProperty().get(), 
                    numbers.getSelectionModel().selectedItemProperty().get()               
                );   
            }
            return null;
        });
        
        Optional<Pair<String, String>> result = this.showAndWait();
        result.ifPresent(seatAndRow -> {                  
            Seat seat = concert.getSeat(seatAndRow.getKey(), Integer.parseInt(seatAndRow.getValue()));               
            drawResultDialog(concert.queryBySeat(seat));
        });    
    }
    
    public static void drawResultDialog(String result)
    {
        DialogPopup dialog = new DialogPopup();
        dialog.setHeaderText("Result Confirmation");
        
        Label label = new Label(result);
        label.setStyle("-fx-font-size: 16px");
        
        dialog.grid.add(label, 1, 1);
        dialog.getDialogPane().getButtonTypes().add(OK);
        dialog.show();
    }
    
    public static void drawErrorDialog(String result)
    {
        DialogPopup dialog = new DialogPopup(true);
        dialog.setHeaderText("Fatal Error Detected");
        
        Label label = new Label(result);
        label.setStyle("-fx-font-size: 16px");
        
        dialog.grid.add(label, 1, 1);
        dialog.getDialogPane().getButtonTypes().add(OK);
        dialog.showAndWait();
    }

    
    public void drawQueryCustomerDialog(Concert concert, int maxCols, int maxRows)
    {             
        Label label = new Label("Select Customer to Query:");
        label.setStyle("-fx-font-size: 16px"); 
        
        ComboBox options = new ComboBox();
        options.prefWidthProperty().set(180);
        
        for(Customer customer: concert.getCustomers()) {
            options.getItems().add(customer.getName());
        }
        options.getSelectionModel().selectFirst();
                                      
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        nodes.addAll(label, options);
        this.drawGridPane(nodes, this.grid, maxCols, maxRows);
        this.getDialogPane().getButtonTypes().addAll(QUERY, CANCEL);

        this.setResultConverter(button -> {
            if(button == QUERY) {
                return options.getSelectionModel().selectedItemProperty().get();                                              
            }
            return null;
        });
        
        Optional<String> result = this.showAndWait();
        result.ifPresent(customer -> {                                     
            drawResultDialog(concert.queryByCustomer(customer));
        });        
    }
    
    public void drawSectionPriceDialog(Concert concert, int maxCols, int maxRows)
    {       
        Label sectionLabel = new Label("Select a Seat Section:");
        sectionLabel.setStyle("-fx-font-size: 16px;");
        
        Label inputLabel = new Label("Input a New Price:");
        inputLabel.setStyle("-fx-font-size: 16px;");
        
        ComboBox sections = new ComboBox();
        sections.prefWidthProperty().set(175);
        
        TextField priceInput = new TextField();       
        priceInput.promptTextProperty().set("00.00");
        
        for(String section : Concert.SEAT_SECTIONS) {
            sections.getItems().add(section);
        }       
        sections.getSelectionModel().selectFirst();
        
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        nodes.addAll(sectionLabel, sections, inputLabel, priceInput);       
        this.drawGridPane(nodes, this.grid, maxCols, maxRows);
        this.getDialogPane().getButtonTypes().addAll(OK, CANCEL);
        
        this.setResultConverter(button -> {
            if(button == OK) {
                return new Pair<>(
                    sections.getSelectionModel().selectedItemProperty().get(), 
                    priceInput.getText()
                );
            }
            return null;
        });
        
        Optional<Pair<String, String>> result = this.showAndWait();
        result.ifPresent(input -> {
            concert.setSectionPrice(input.getKey(), Double.parseDouble(input.getValue()));
            drawResultDialog("Changed Price of " + input.getKey() + " Section to £" + input.getValue());
        });
    }
    
    public void drawSaveDialog(String message)
    {
        Label label = new Label(message);       
        label.setStyle("-fx-font-size: 16px");
        
        this.grid.add(label, 0, 0);        
        this.getDialogPane().getButtonTypes().add(OK);
    }
    
    public void drawBookSeatDialog(Concert concert, Seat seat, Node seatIcon)
    {       
        this.setHeaderText("Book Seat (" + seat + ")");
        Label label = new Label("Input name to book this seat:");
        label.setStyle("-fx-font-size: 16px;");
        
        TextField nameInput = new TextField();
        nameInput.promptTextProperty().set("Input fullname here...");
        
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        nodes.addAll(label, nameInput);
        this.drawGridPane(nodes, this.grid, 1, 2);
        this.getDialogPane().getButtonTypes().addAll(BOOK, CANCEL);
        
        this.setResultConverter(button -> {
            if(button == BOOK) {
                if(nameInput.getText().length() > 0 && nameInput.getText().length() < 30)                    
                    return nameInput.getText();                
            }
            return null;
        });
        
        Optional<String> result = this.showAndWait();       
        result.ifPresent(input -> {                      
            concert.bookSeat(seat, capitalize(input));
            seatIcon.setStyle("-fx-background-color: linear-gradient(#FF0000, #D10000);");
            if(concert.getCustomerEntitlement(seat) != null) {
                drawResultDialog(
                    capitalize(input) + " has booked seat " 
                    + "(" + seat + ")" + "\n" + concert.getCustomerEntitlement(seat)
                );     
            }
            else {
                drawResultDialog(
                    capitalize(input) + " has booked seat " 
                    + "(" + seat + ")" 
                );
            }                      
        });
    } 
    
    public void drawUnBookSeatDialog(Concert concert, Seat seat, Node seatIcon)
    {
        this.setHeaderText("Unbook Seat (" + seat + ")");
        Label label = new Label("Are you sure you want to unbook this seat?"); 
        label.setStyle("-fx-font-size: 16px;");
        
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        nodes.addAll(label);       
        this.drawGridPane(nodes, this.grid, 1, 2);    
        this.getDialogPane().getButtonTypes().addAll(UNBOOK, CANCEL);

        this.setResultConverter(button -> {
            if(button == UNBOOK) {
                return UNBOOK.getText();
            }
            return null;
        });
        
        Optional<String> result = this.showAndWait();
        result.ifPresent(input -> { 
            try {  
                concert.unBookSeat(seat);
                if(seat.getClass().getSimpleName().equals("GoldSeat")) {
                    seatIcon.setStyle("-fx-background-color: linear-gradient(#FFD700, #EDC800);");
                }
                else {
                    seatIcon.setStyle("-fx-background-color: linear-gradient(#C0C0C0, #ABABAB);");
                }
            }
            catch(CannotUnbookSeatException e) {
                drawResultDialog(e.getMessage());                                 
            }                                                 
        });
    } 
    
    public boolean drawConcertAlreadyExistsDialog(String message)
    {       
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px");
        
        this.grid.add(label, 1, 1);
        this.getDialogPane().getButtonTypes().addAll(OK, CANCEL);      
        
        this.setResultConverter(button -> {
            if(button == OK) {
                return OK.getText();
            }
            return null;
        });
        
        Optional<String> result = this.showAndWait();       
        return result.isPresent();
    }
    
    public static void drawErrorDialog()
    {
        
    }
    
    //Used to capitalize a string e.g. "daniel black" becomes "Daniel Black"
    private static String capitalize(String name)
    {
        String result = "";
        char[] nameChars = name.toLowerCase().toCharArray();                  
        for(int i = 0; i < nameChars.length; i++) {
            if(i == 0) {
                result += Character.toUpperCase(nameChars[i]);
            }
            else if(Character.isWhitespace(nameChars[i-1])) {
                result += Character.toUpperCase(nameChars[i]);
            }
            else {
                result += nameChars[i];
            }            
        }
        return result;
    }
}

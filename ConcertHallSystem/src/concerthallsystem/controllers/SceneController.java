package concerthallsystem.controllers;

import concerthallsystem.Concert;
import concerthallsystem.DialogPopup;
import concerthallsystem.Main;
import java.io.IOException;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * The SceneController class is responsible for displaying the appropriate
 * views and UI components for the user to see on screen. The EventController class
 * tells this class what scene to display depending on what button the user presses.
 * This class also calls on the DialogPopup class to display dialog boxes that are
 * used to display query information to the user, based on the concert they are viewing.
 * 
 * @author Daniel Black
 */

public class SceneController extends StackPane      
{
    private final HashMap<String, Node> scenes = new HashMap<>();
    private Node currentScene;
    private final EventController eventController;  
    private final String[] fileNames = {
        "MainMenu", "CreateConcert",
        "SelectConcert", "SeatingPlan"
    };           
    private final String[] sourceFiles = {
        "/concerthallsystem/views/MainMenuScene.fxml", 
        "/concerthallsystem/views/CreateConcertScene.fxml", 
        "/concerthallsystem/views/SelectConcertScene.fxml", 
        "/concerthallsystem/views/SeatingPlanScene.fxml"
    };
       
    public SceneController()
    {
        super();                         
        this.prefWidthProperty().bind(Main.WINDOW.widthProperty());
        this.prefHeightProperty().bind(Main.WINDOW.heightProperty());  
        this.eventController = new EventController(this);
        this.loadScenes();
    }
    
    private void loadScenes()
    {    
        int i = 0;
        for(String sourceFile : this.sourceFiles) {           
            try {              
                FXMLLoader loader = new FXMLLoader(getClass().getResource(sourceFile));                                              
                loader.setController(this.eventController);  
                Parent scene = loader.load();
                this.scenes.put(fileNames[i++], scene);              
            }
            catch(IOException e) {
                System.out.println("Failed to load " + sourceFile);
            }                                   
        }        
        this.setScene("MainMenu");
    }
        
    public void setScene(String name)
    {
        if(this.scenes.get(name) != null)
        {     
            DoubleProperty opacity = this.opacityProperty();
            this.currentScene = this.scenes.get(name);
            if(!getChildren().isEmpty()) {
                Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(250),(ActionEvent event) -> {
                        getChildren().remove(0);
                        getChildren().add(0, scenes.get(name));
                        Timeline fadeIn = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                            new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0))
                        );
                        fadeIn.play();
                    }, new KeyValue(opacity, 0.0)));
                fadeOut.play();                           
            }                                                       
            else {
                setOpacity(0.0);
                getChildren().add(this.scenes.get(name));
                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                    new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0))
                );
                fadeIn.play();                            
            }
        }
    } 
    
    public Node getCurrentScene()
    {
        return this.currentScene;
    }
    
    public HashMap<String, Node> getAllScenes()
    {
        return this.scenes;
    }                   
    
    public void displayReportDialog(Concert concert)
    {
        DialogPopup reportDialog = new DialogPopup();
        reportDialog.setHeaderText("Full Report For Concert | " + concert);
        reportDialog.drawReportDialog(concert.getReport(), 2, 7);       
        reportDialog.show();      
    }
    
    public void displaySaveDialog(Concert concert)
    {
        DialogPopup saveDialog = new DialogPopup();
        saveDialog.setHeaderText("Saved Concert | " + concert);       
        saveDialog.drawSaveDialog("Successfully Saved Concert:\n" + concert);        
        saveDialog.show();
    }
    
    public void displayQueryCustomerDialog(Concert concert)
    {
        DialogPopup queryDialog = new DialogPopup();
        queryDialog.setHeaderText("Customer List For | " + concert);
        queryDialog.drawQueryCustomerDialog(concert, 1, 2);       
    }
    
    public void displayQuerySeatDialog(Concert concert)
    {
        DialogPopup queryDialog = new DialogPopup();
        queryDialog.setHeaderText("Query Seat in | " + concert);
        queryDialog.drawQuerySeatDialog(concert, 2, 2);               
    }
    
    public void displaySectionPriceDialog(Concert concert)
    {
        DialogPopup sectionDialog = new DialogPopup();
        sectionDialog.setHeaderText("Seat Sections For | " + concert);
        sectionDialog.drawSectionPriceDialog(concert, 1, 4);  
    }        
    
    public boolean displayConcertAlreadyExistsDialog(String message)
    {
        DialogPopup alreadyExistsDialog = new DialogPopup();
        alreadyExistsDialog.setHeaderText("That concert already exists!");
        return alreadyExistsDialog.drawConcertAlreadyExistsDialog(message);
    }
}

package concerthallsystem;

import concerthallsystem.controllers.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

/**
 * This is the main starting point for the application.
 * The start() method is called when you open the application, and
 * main() method is a backup incase start() doesn't work
 * 
 * @author Daniel Black
 */

public class Main extends Application
{          
    private SceneController sceneController;
    public static Stage WINDOW;
    
    @Override
    public void start(Stage primaryStage)
    {       
        WINDOW = primaryStage;
        this.sceneController = new SceneController();
        
        Group root = new Group();   
        root.getChildren().addAll(this.sceneController);        
        WINDOW.setTitle("Concert Hall Booking System");
        WINDOW.setScene(new Scene(root,900, 700));
        WINDOW.show();               
    }
      
    public static void main(String[] args) 
    {
        launch(args);
    }         
}

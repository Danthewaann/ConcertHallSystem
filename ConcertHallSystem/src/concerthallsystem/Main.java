package concerthallsystem;

import concerthallsystem.controllers.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

/**
 *
 * @author Daniel
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

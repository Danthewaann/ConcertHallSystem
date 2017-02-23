package concerthallsystem.controllers;

import concerthallsystem.Main;
import java.io.IOException;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author Daniel
 */

public class SceneController extends StackPane      
{
    private final HashMap<String, Node> scenes = new HashMap<>();
    private Node currentScene;
    private EventController eventController;    
    String[] sceneFiles = {
        "MainMenuScene.fxml", "CreateConcertScene.fxml", 
        "SelectConcertScene.fxml", "SeatingPlanScene.fxml"
    };
       
    public SceneController()
    {
        super();                         
        this.prefWidthProperty().bind(Main.WINDOW.widthProperty());
        this.prefHeightProperty().bind(Main.WINDOW.heightProperty());  
        this.loadScenes();
    }
    
    private void loadScenes()
    {    
        this.eventController = new EventController(this);
        for(String fileName : sceneFiles)
        {           
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));                                              
                loader.setController(this.eventController);  
                Parent scene = loader.load();
                this.scenes.put(fileName, scene);              
            }
            catch(IOException e)
            {
                System.out.println("Failed to load " + fileName);
            }                                   
        }        
        this.setScene("MainMenuScene.fxml");
    }
        
    public void setScene(String name)
    {
        if(this.scenes.get(name) != null)
        {     
            DoubleProperty opacity = opacityProperty();
            this.currentScene = this.scenes.get(name);
            if(!getChildren().isEmpty())
            {
                Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(250), new EventHandler<ActionEvent>() 
                    {
                        @Override
                        public void handle(ActionEvent event) 
                        {
                            getChildren().remove(0);
                            getChildren().add(0, scenes.get(name));                            
                            Timeline fadeIn = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }                  
                    }, new KeyValue(opacity, 0.0)));
                fadeOut.play();                           
            }                                                       
            else
            {
                setOpacity(0.0);
                getChildren().add(this.scenes.get(name));
                Timeline fadeIn = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                            new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
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
    
}

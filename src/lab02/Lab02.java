package lab02;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Lab02 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        //Creates a text area
        TextArea textArea = new TextArea();
        //Sets the prompt text
        textArea.setPromptText("Type Here:");
        //Takes the focus off of the text area so that the prompt text is visible
        textArea.setFocusTraversable(false);
        
        StackPane root = new StackPane();
        //Adds the TextArea to the StackPane
        root.getChildren().add(textArea);
         
        Scene scene = new Scene(root, 500, 350);
        
        //Sets the title of the application
        primaryStage.setTitle("My Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

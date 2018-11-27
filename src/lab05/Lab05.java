package lab05;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Lab05 extends Application {
    public Settings settings;
    private final String filename = "text.json";
    String startText = "";
    double width = 500;
    double height = 350;
    @Override
    public void start(Stage primaryStage) {
        //Opens the file
        onOpen();
        //Creates a text area
        TextArea textArea = new TextArea();
        //Sets the prompt text
        textArea.setPromptText("Type Here:");
        //Takes the focus off of the text area so that the prompt text is visible
        textArea.setFocusTraversable(false);
        textArea.setText(settings.getInput());
        
        StackPane root = new StackPane();
        //Adds the TextArea to the StackPane
        root.getChildren().add(textArea);
        double Width = settings.getWidth();
        double Height = settings.getHeight();
        Scene scene = new Scene(root, Width, Height);
        
        //Sets the title of the application
        primaryStage.setTitle("My Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent ev) {
                onClose(scene.getWidth(), scene.getHeight(), textArea.getText());
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void onOpen() {
        try (FileReader fr = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(fr);
            settings = new Gson().fromJson(br.readLine(), Settings.class);
        } catch (IOException ex) {
            System.out.println("IOException");
            settings = new Settings(width, height, startText);
        }
    }
    public void onClose(double width, double height, String text) {
        settings = new Settings(width, height, text);
        String input = new Gson().toJson(settings);
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(input);
            fw.close();
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }
}

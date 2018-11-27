package lab07;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Lab07 extends Application {
    public Settings settings;
    private final String filename = "text.json";
    String startText = "";
    double width = 500;
    double height = 350;
    public BorderPane root = new BorderPane();
    public Stage primaryStage;
    public Scene scene;
    public double Width;
    public double Height;
    public TextArea textArea;
    @Override
    public void start(Stage primaryStage) {
        //Opens the file
        onOpen();
        //creates the TextArea 
        createTextArea(root);
        //Creates Menus
        createMenus(root);
        Width = settings.getWidth();
        Height = settings.getHeight();
        
        scene = new Scene(root, Width, Height);
        
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
    //called when the application opens
    public void onOpen() {
        try (FileReader fr = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(fr);
            settings = new Gson().fromJson(br.readLine(), Settings.class);
        } catch (IOException ex) {
            System.out.println("IOException");
            settings = new Settings(width, height, startText);
        }
    }
    //called when the application is closed
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
    //creates the TextArea for the application
    public void createTextArea(BorderPane root){
        //Creates a text area
        textArea = new TextArea();
        //Sets the prompt text
        textArea.setPromptText("Type Here:");
        //Takes the focus off of the text area so that the prompt text is visible
        textArea.setFocusTraversable(false);
        textArea.setText(settings.getInput());        
        
        //Adds the TextArea to the center of the BorderPane
        root.setCenter(textArea);
    }
    //creates a menu bar with all menus and sub-menus
    public void createMenus(BorderPane root){
        MenuBar menubar = new MenuBar();
        Menu menuFile = createFileMenu();
        Menu menuEdit = createEditMenu();
        Menu menuFormat = createFormatMenu();
        Menu menuView = createViewMenu();
        Menu menuHelp = createHelpMenu();
        menubar.getMenus().addAll(menuFile, menuEdit, menuFormat,
                menuView, menuHelp);
        //Adds menu bar to the top of the BorderPane
        root.setTop(menubar);
    }
    //creates File menu and File menu items and is returned to createMenus()
    public Menu createFileMenu(){
        Menu menuFile = new Menu("_File");
        MenuItem New = new MenuItem("_New");
        New.setAccelerator(KeyCombination.keyCombination("SHORTCUT+N"));
        MenuItem Open = createOpen();
        MenuItem Save = createSave();
        MenuItem SaveAs = createSaveAs();
        MenuItem PageSetup = new MenuItem("Page Set_up...");
        MenuItem Print = new MenuItem("_Print...");
        Print.setAccelerator(KeyCombination.keyCombination("SHORTCUT+P"));
        MenuItem Exit = createExit();
        //adds all menu items to the File menu
        menuFile.getItems().addAll(New, Open, Save, SaveAs, new SeparatorMenuItem(), PageSetup,
                Print,new SeparatorMenuItem(), Exit);
        return menuFile;
    }
    //creates Edit menu and Edit menu items and is returned to createMenus()
    public Menu createEditMenu(){
        Menu menuEdit = new Menu("_Edit");
        MenuItem Undo = new MenuItem("_Undo");
        Undo.setAccelerator(KeyCombination.keyCombination("SHORTCUT+Z"));
        MenuItem Cut = new MenuItem("Cu_t");
        Cut.setAccelerator(KeyCombination.keyCombination("SHORTCUT+X"));
        MenuItem Copy = new MenuItem("_Copy");
        Copy.setAccelerator(KeyCombination.keyCombination("SHORTCUT+C"));
        MenuItem Paste = new MenuItem("_Paste");
        Paste.setAccelerator(KeyCombination.keyCombination("SHORTCUT+V"));
        MenuItem Delete = new MenuItem("De_lete");
        Delete.setAccelerator(KeyCombination.keyCombination("Del"));
        MenuItem Find = new MenuItem("_Find...");
        Find.setAccelerator(KeyCombination.keyCombination("SHORTCUT+F"));
        MenuItem FindNext = new MenuItem("Find _Next");
        FindNext.setAccelerator(KeyCombination.keyCombination("F3"));
        MenuItem Replace = new MenuItem("_Replace...");
        Replace.setAccelerator(KeyCombination.keyCombination("SHORTCUT+H"));
        MenuItem GoTo = new MenuItem("_Go To...");
        GoTo.setAccelerator(KeyCombination.keyCombination("SHORTCUT+G"));
        MenuItem SelectAll = new MenuItem("Select _All");
        SelectAll.setAccelerator(KeyCombination.keyCombination("SHORTCUT+A"));
        MenuItem TimeDate = new MenuItem("Time/_Date");
        TimeDate.setAccelerator(KeyCombination.keyCombination("F5"));
        //adds all menu items to the Edit menu
        menuEdit.getItems().addAll(Undo,new SeparatorMenuItem(), Cut, Copy, 
                Paste, Delete, new SeparatorMenuItem(), Find, FindNext, Replace,
                GoTo, new SeparatorMenuItem(), SelectAll, TimeDate);
        return menuEdit;
    }
    //creates Format menu and Format menu items and is returned to createMenus()
    public Menu createFormatMenu(){
        Menu menuFormat = new Menu("F_ormat");
        MenuItem WordWrap = new MenuItem("_Word Wrap");
        MenuItem Font = new MenuItem("_Font...");
        //adds menuu items to the Format menu
        menuFormat.getItems().addAll(WordWrap, Font);
        return menuFormat;
    }
    //creates View menu and View menu items and is returned to createMenus()
    public Menu createViewMenu(){
        Menu menuView = new Menu("_View");
        MenuItem StatusBar = new MenuItem("_Status Bar");
        //adds menu items to the View menu
        menuView.getItems().add(StatusBar);
        return menuView;
    }
    //creates Help menu and Help menu items and is returned to createMenus()
    public Menu createHelpMenu(){
        Menu menuHelp = new Menu("_Help");
        MenuItem ViewHelp = new MenuItem("View _Help");
        MenuItem AboutMyTextEditor = new MenuItem("_About My Text Editor");
        //adds menu items to the Help menu
        menuHelp.getItems().addAll(ViewHelp, new SeparatorMenuItem(),
                AboutMyTextEditor);
        return menuHelp;
    }
    //creates the menu item Open and is returned to createFileMenu()
    public MenuItem createOpen(){
        MenuItem Open = new MenuItem("_Open...");
        //sets the accelerator(shortcut) to SHORTCUT+O
        Open.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
        Open.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                FileChooser fc = new FileChooser();                
                fc.setTitle("Open File");
                //opens the file explorer
                File file = fc.showOpenDialog(primaryStage);
                if(file != null){
                    try {
                        openFile(file);
                    } catch (Exception ex) {
                        Logger.getLogger(Lab07.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        return Open;
    }
    //creates the menu item Save and is returned to createFileMenu()
    public MenuItem createSave(){
        MenuItem Save = new MenuItem("_Save");
        //sets the accelerator(shortcut) to SHORTCUT+S
        Save.setAccelerator(KeyCombination.keyCombination("SHORTCUT+S"));
        onSave(Save);
        return Save;
    }
    //creates the menu item SaveAs and is returned to createFileMenu()
    public MenuItem createSaveAs(){
        MenuItem SaveAs = new MenuItem("Save _As...");
        onSave(SaveAs);
        return SaveAs;
    }
    //creates the menu item Exit and is returned to createFileMenu()
    public MenuItem createExit(){
        MenuItem Exit = new MenuItem("E_xit");
        Exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });
        return Exit;
    }
    //used by createSave() and createSaveAs() to set the EventHandler for both
    public void onSave(MenuItem Save){
        Save.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                FileChooser fc = new FileChooser();
                //shows .txt extension for saving the files
                FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter(
                        "JSON Files (*.json)", "*.json");
                fc.getExtensionFilters().add(ef);
                fc.setTitle("Save File");
                //opens up the file explorer
                File file = fc.showSaveDialog(primaryStage);
                if(file != null){
                    saveFile(textArea.getText(),file);
                }
            }
        });
    }
    //used by onSave() to save a file via file explorer
    private void saveFile(String content, File file){
        settings = new Settings(width, height, content);
        String input = new Gson().toJson(settings);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(input);
            fw.close();
        } catch (IOException e) {
            System.out.println("IOException");
        }       
    }
    //used by createOpen() to open a file via file explorer
    private void openFile(File file){
        String fileName = file.getName();          
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1
                , file.getName().length());
        if (extension.equals("txt")){
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                textArea.clear();
                //writes the text from file to the current text area
                while((line = br.readLine()) != null){
                    textArea.appendText(line + '\n');
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else if(extension.equals("json")){
            try (FileReader fr = new FileReader(file)) {
                BufferedReader br = new BufferedReader(fr);
                settings = new Gson().fromJson(br.readLine(), Settings.class);
                textArea.setText(settings.getInput());
                primaryStage.setHeight(settings.getHeight());
                primaryStage.setWidth(settings.getWidth());
            } catch (IOException ex) {
                ex.printStackTrace();
                settings = new Settings(width, height, startText);
            }
        }
    }
}
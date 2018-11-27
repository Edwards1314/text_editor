package lab14;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Lab14 extends Application {
    public Settings settings;
    public String filename = "text.json";
    public File currentFile;
    String startText = "";
    double width = 500;
    double height = 350;
    public BorderPane root = new BorderPane();
    public VBox vbox = new VBox();
    public Stage primaryStage;
    public Scene scene;
    public double Width;
    public double Height;
    public TextArea textArea;
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    public Font currentFont;
    public FontAttributes attributes;
    ObservableList <String> FontStyles;
    ListView<String> FontNames;
    ListView<String> FontStylesLV;
    ListView<Integer> FontSizesLV;
    ObservableList <String> Fonts;
    ObservableList <Integer> FontSizes;
    TextField fontField; 
    Label dateTime;
    Label caretColumn;
    Label caretRow;
    Label fileLabel;
    int second;
    int minute;
    int hour;
    public Text SampleText = new Text("SampleText");
    int caretPosition;
    int columns;
    int rows = 1;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //Opens the file
        onOpen();
        //creates the TextArea 
        createTextArea();
        //Creates Menus
        createMenus();
        //Creates Toolbar
        createToolbar();
        //Creates StatusBar
        createStatusBar();
        root.setTop(vbox);
        Width = settings.getWidth();
        Height = settings.getHeight();
        
        scene = new Scene(root, Width, Height);
        
        //Sets the title of the application
        primaryStage.setTitle("My Text Editor");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("editor_icon.png"), 40,40,true,true));
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
    public void createTextArea(){
        //Creates a text area
        textArea = new TextArea();
        //Sets the prompt text
        textArea.setPromptText("Type Here:");
        //Takes the focus off of the text area so that the prompt text is visible
        textArea.setFocusTraversable(false);
        textArea.setText(settings.getInput());        
        
        //Gets the current Font from the text Area
        currentFont = textArea.getFont();
        System.out.println("Font on Startup: " + currentFont.getName());
        textArea.caretPositionProperty().addListener(((observable, oldValue, newValue) -> {
            getCaretPosition();            
        }));
        //Adds the TextArea to the center of the BorderPane
        root.setCenter(textArea);
    }
    //creates a menu bar with all menus and sub-menus
    public void createMenus(){
        MenuBar menubar = new MenuBar();
        Menu menuFile = createFileMenu();
        Menu menuEdit = createEditMenu();
        Menu menuFormat = createFormatMenu();
        Menu menuView = createViewMenu();
        Menu menuHelp = createHelpMenu();
        menubar.getMenus().addAll(menuFile, menuEdit, menuFormat,
                menuView, menuHelp);
        //Adds menu bar to the top of the BorderPane
        vbox.getChildren().add(menubar);
    }
    //creates a toolbar with corresponding buttons
    public void createToolbar(){
        
        Button cutButton = createCutButton();
        Button copyButton = createCopyButton();
        Button pasteButton = createPasteButton();
        Button openButton = createOpenButton();
        Button saveButton = createSaveButton();
        ToolBar toolbar = new ToolBar(
                saveButton,
                openButton,
                new Separator(),
                cutButton,
                copyButton,
                pasteButton
        );
        vbox.getChildren().add(toolbar);
    }
    //creates a StatusBar at the bottom of the editor
    public void createStatusBar(){
        HBox hb = new HBox();
        dateTime = new Label();
        caretColumn = new Label();
        caretRow = new Label();
        initClock();
        getCaretPosition();
        fileLabel = new Label(filename);
        hb.getChildren().addAll(dateTime, new Separator(), caretColumn,
                new Separator(), caretRow, new Separator(), fileLabel);
        root.setBottom(hb);
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
        MenuItem Cut = createCut();
        MenuItem Copy = createCopy();
        MenuItem Paste = createPaste();
        MenuItem Delete = createDelete();
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
        MenuItem Font = createFont();
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
                currentFile = file;
                filename = currentFile.getName();
                fileLabel.setText(filename);
                if(file != null){
                    try {
                        openFile(file);
                    } catch (Exception ex) {
                        Logger.getLogger(Lab14.class.getName()).log(Level.SEVERE, null, ex);
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
        Save.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){                
                onSave();
            }
        });
        return Save;
    }
    //creates the menu item SaveAs and is returned to createFileMenu()
    public MenuItem createSaveAs(){
        MenuItem SaveAs = new MenuItem("Save _As...");
        SaveAs.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){                
                onSave();
            }
        });
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
    //creates the menu item Cut and is returned to createFileMenu()
    public MenuItem createCut(){
        MenuItem Cut = new MenuItem("Cu_t");
        Cut.setAccelerator(KeyCombination.keyCombination("SHORTCUT+X"));
        Cut.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onCut();
            }
        });
        return Cut;
    }
    //creates the menu item Copy and is returned to createEditMenu()
    public MenuItem createCopy(){
        MenuItem Copy = new MenuItem("_Copy");
        Copy.setAccelerator(KeyCombination.keyCombination("SHORTCUT+C"));
        Copy.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onCopy();
            }
        });
        return Copy;
    }
    //creates the menu item Paste and is returned to createEditMenu()
    public MenuItem createPaste(){
        MenuItem Paste = new MenuItem("_Paste");
        Paste.setAccelerator(KeyCombination.keyCombination("SHORTCUT+V"));
        Paste.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onPaste();
            }
        });
        return Paste;
    }
    //creates the menu item Delete and is returned to createEditMenu()
    public MenuItem createDelete(){
        MenuItem Delete = new MenuItem("De_lete");
        Delete.setAccelerator(KeyCombination.keyCombination("Del"));
        Delete.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                textArea.deleteText(textArea.getSelection());
            }
        });
        return Delete;
    }
    //create the menu item Font... and is returned to createFormatMenu()
    public MenuItem createFont(){
        MenuItem Font = new MenuItem("_Font...");
        Font.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onFont();
            }
        });
        return Font;
    }
    //creates Button Cut and is returned to createToolbar()
    public Button createCutButton(){
        Image cutIcon = new Image(getClass().getResourceAsStream("cut_icon.png"), 25,25,true,true);
        Button cutButton = new Button();
        cutButton.setGraphic( new ImageView(cutIcon));
        cutButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onCut();
            }
        });
        shadowOn(cutButton);
        shadowOff(cutButton);
        return cutButton;
    }
    //creates Button Copy and is returned to createToolbar()
    public Button createCopyButton(){
        Image copyIcon = new Image(getClass().getResourceAsStream("copy_icon.png"), 25,25,true,true);
        Button copyButton = new Button();
        copyButton.setGraphic( new ImageView(copyIcon));
        copyButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onCopy();
            }
        });
        shadowOn(copyButton);
        shadowOff(copyButton);
        return copyButton;
    }
    //creates Button Paste and is returned to createToolbar()
    public Button createPasteButton(){
        Image pasteIcon = new Image(getClass().getResourceAsStream("paste_icon.png"), 25,25,true,true);
        Button pasteButton = new Button();
        pasteButton.setGraphic( new ImageView(pasteIcon));
        pasteButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onPaste();
            }
        });
        shadowOn(pasteButton);
        shadowOff(pasteButton);
        return pasteButton;
    }
    //creates Button Paste and is returned to createToolbar()
    public Button createSaveButton(){
        Image saveIcon = new Image(getClass().getResourceAsStream("save_icon.png"), 25,25,true,true);
        Button saveButton = new Button();
        saveButton.setGraphic(new ImageView(saveIcon));
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                onSave();
            }
        });
        shadowOn(saveButton);
        shadowOff(saveButton);
        return saveButton;
    }
    //creates Button Paste and is returned to createToolbar()
    public Button createOpenButton(){
        Image openIcon = new Image(getClass().getResourceAsStream("open_icon.png"), 25,25,true,true);
        Button openButton = new Button();
        openButton.setGraphic( new ImageView(openIcon));
        openButton.setOnAction(new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent event){
            FileChooser fc = new FileChooser();                
            fc.setTitle("Open File");
            //opens the file explorer
            File file = fc.showOpenDialog(primaryStage);
            filename = file.getName();
            currentFile = file;
            if(file != null){
                try {
                    openFile(file);
                } catch (Exception ex) {
                    Logger.getLogger(Lab14.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        });
        shadowOn(openButton);
        shadowOff(openButton);
        return openButton;
    }
    //used by createCut and createCutButton to duplicate Cut functionality
    public void onCut(){
        if(!textArea.getSelectedText().isEmpty()){
            content.putString(textArea.getSelectedText());
            clipboard.setContent(content);
            textArea.replaceSelection("");
        }
    }
    //used by createCopy and createCopyButton to duplicate Cut functionality
    public void onCopy(){
        if(!textArea.getSelectedText().isEmpty()){
            content.putString(textArea.getSelectedText());
            clipboard.setContent(content);            
        }
    }
    //used by createPaste and createPasteButton to duplicate Cut functionality
    public void onPaste(){
        if(clipboard.hasString()){
            textArea.replaceSelection(clipboard.getString());
        }
    }
    //used by createSave() and createSaveAs() to set the EventHandler for both
    public void onSave(){ 
        if (currentFile != null){
            saveFile(textArea.getText(), currentFile);
        }else{
            FileChooser fc = new FileChooser();
            //shows .txt extension for saving the files
            FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter(
                    "JSON Files (*.json)", "*.json");
            fc.getExtensionFilters().add(ef);
            fc.setTitle("Save File");
            //opens up the file explorer
            File file = fc.showSaveDialog(primaryStage);
            filename = file.getName();
            currentFile = file;
            if(file != null){
                saveFile(textArea.getText(),file);
            }
        }
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
        String extension = filename.substring(filename.lastIndexOf(".") + 1
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
    //used by Button methods to turn on shadow effect
    public void shadowOn(Button button){
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
            button.setEffect(shadow);
            }
        });
    }
    //used by Button methods to turn off shadow effect
    public void shadowOff(Button button){
        //Removing the shadow when the mouse cursor is off
        button.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                button.setEffect(null);
            }
        });
    }
    //used by createFont() to open the font dialog box
    public void onFont(){
        createFontDialogBox();
    }
    //creates the dialog box for choosing a new font and returns to onFont()
    public void createFontDialogBox(){
        attributes = new FontAttributes(currentFont.getFamily(), currentFont.getStyle(), (int) currentFont.getSize());
        Dialog<FontAttributes> dialog = new Dialog<>();
        dialog.setTitle("Choose Your Font Wisely...");
        DialogPane dialogPane = new DialogPane();
        GridPane grid = createFontGridPane();
        dialogPane.setContent(grid);
        dialog.setDialogPane(dialogPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                attributes = new FontAttributes(FontNames.getSelectionModel().getSelectedItem(),
                FontStylesLV.getSelectionModel().getSelectedItem(), FontSizesLV.getSelectionModel().getSelectedItem());
                changeFont();
            }
            return null;
        });
        dialog.showAndWait();
    }
    //creates a GridPane and is returned to createFontDialogBox()
    public GridPane createFontGridPane(){
        GridPane grid = new GridPane();        
        
        VBox fontVBox = createFontVBox();
        
        VBox fontStyleVBox = createFontStyleVBox();
        
        VBox fontSizeVBox = createFontSizeVBox();   
        
        GridPane.setConstraints(fontVBox, 0, 1);
        GridPane.setConstraints(fontStyleVBox, 1, 1);
        GridPane.setConstraints(fontSizeVBox, 2, 1);
        GridPane.setConstraints(FontNames, 0, 2);
        GridPane.setConstraints(FontStylesLV, 1, 2);
        GridPane.setConstraints(FontSizesLV, 2, 2);
        GridPane.setConstraints(SampleText, 2, 3);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getChildren().addAll(fontVBox, fontStyleVBox, fontSizeVBox, FontNames,
                FontStylesLV, FontSizesLV, SampleText);
        return grid;
    }
    //creates the Vbox with all of the items for Font and is returned to createFontGridPane() 
    public VBox createFontVBox(){
        //Used to separate items in the columns
        Separator sep = new Separator();
        //Creates an ObervableList of Font Family names
        Fonts = FXCollections.observableArrayList(
                javafx.scene.text.Font.getFamilies());
        //Creates a ListView of Font Family names
        FontNames = new ListView<>(Fonts);
        FontNames.getSelectionModel().select(Fonts.indexOf(currentFont.getFamily()));
        FontNames.getFocusModel().focus(Fonts.indexOf(currentFont.getFamily()));
        FontNames.scrollTo(Fonts.indexOf(currentFont.getFamily()));
        FontNames.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String oldFont, String newFont) {
                        FontStyles = FXCollections.observableArrayList(javafx.scene.text.Font.getFontNames(newFont));
                        FontStylesLV.setItems(FontStyles);
                        Font sampleFont =javafx.scene.text.Font.font(newFont);
                        changeSampleTextFont(sampleFont);
            }
        });
        fontField = new TextField();   
        fontField.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFonts(newValue.toLowerCase(), Fonts);            
        }));
        VBox fontVBox = new VBox(new Text ("Font:"), fontField, sep);
        return fontVBox;
    }
    //creates the Vbox with all of the items for Font Style and is returned to createFontGridPane() 
    public VBox createFontStyleVBox(){
        //Used to separate items in the columns
        Separator sep = new Separator();
        //Creates a ListView of Font Style names
        FontStylesLV = new ListView<>(FontStyles);
        TextField fontStyleField = new TextField(); 
        
        FontStyles = FXCollections.observableArrayList(javafx.scene.text.Font.getFontNames(currentFont.getFamily()));
        FontStylesLV = new ListView<>(FontStyles);
        String style;
        if(attributes.getFontStyle().equals("Regular") && !attributes.getFontFamily().equals("System")){
            style = attributes.getFontFamily();
        }else {
            style = attributes.getFontFamily()+" "+attributes.getFontStyle();
        }
        FontStylesLV.getSelectionModel().select(FontStyles.indexOf(style));
        FontStylesLV.getFocusModel().focus(FontStyles.indexOf(style));
        FontStylesLV.scrollTo(FontStyles.indexOf(style));
        fontStyleField.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFontStyle(newValue.toLowerCase());
            
        }));
               
        VBox fontStyleVBox = new VBox(new Text("Font Style:"), fontStyleField, sep);
        return fontStyleVBox;
    }
    //creates the Vbox with all of the items for Font size and is returned to createFontGridPane() 
    public VBox createFontSizeVBox(){
        //Used to separate items in the columns
        Separator sep = new Separator();
        //creates a list of sizes
        List<Integer> sizes = new ArrayList<Integer>();
        for(int i = 0; i < 150; i++){sizes.add(i);}
        //Creates an ObervableList of Font Stlye names
        FontSizes = FXCollections.observableArrayList(sizes);
        //Creates a ListView of Font Style names
        FontSizesLV = new ListView<>(FontSizes);
        TextField fontSizeField = new TextField();
        fontSizeField.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFontSize(newValue, FontSizes);
            
        }));
        VBox fontSizeVBox = new VBox(new Text("Size:"), fontSizeField, sep);  
        return fontSizeVBox;
    }
    //Changes the font of the sample text in the font dialog box
    public void changeSampleTextFont(Font font){
        SampleText.fontProperty().set(font);
    }
    //used by createFontDialogBox to change the font to whatever is selected
    public void changeFont(){
        if(attributes.getFontStyle() == null){
            attributes.setFontStyle(attributes.getFontFamily());
        }
        Font newFont = new Font(attributes.getFontStyle(), attributes.getFontSize());
        textArea.setFont(newFont);
        currentFont = newFont;
    }
    //used by createFontVBox to make the text field search through the ListView
    public void searchFonts(String newValue, ObservableList<String> font_choices){
        boolean stop = false;
            for(int i = 0; i < font_choices.size();i++){
                if(font_choices.get(i).toLowerCase().contains(newValue)){
                    for(int j = 0; j < newValue.length(); j++){
                        if(font_choices.get(i).toLowerCase().charAt(j) == newValue.charAt(j)){
                            FontNames.getSelectionModel().select(i);
                            FontNames.getFocusModel().focus(i);
                            FontNames.scrollTo(i);
                            stop = true;
                        }
                        break;
                    }
                    if(stop){break;}
                }
            }
    }
    //used by createFontStyleVBox to make the text field search through the ListView
    public void searchFontStyle(String newValue){
        boolean stop = false;
            for(int i = 0; i < FontStyles.size();i++){
                if(FontStyles.get(i).toLowerCase().contains(newValue)){
                    for(int j = 0; j < newValue.length(); j++){
                        if(FontStyles.get(i).toLowerCase().charAt(j) == newValue.toLowerCase().charAt(j)){
                            FontStylesLV.getSelectionModel().select(i);
                            FontStylesLV.getFocusModel().focus(i);
                            FontStylesLV.scrollTo(i);
                            stop = true;
                        }
                        break;
                    }
                    if(stop){break;}
                }
            }
    }   
    //used by createFontSizeVBox to make the text field search through the ListView
    public void searchFontSize(String newValue, ObservableList<Integer> font_size){
        try{
            int entry = Integer.parseInt(newValue);
            FontSizesLV.getSelectionModel().select(font_size.indexOf(entry));
            FontSizesLV.getFocusModel().focus(font_size.indexOf(entry));
            FontSizesLV.scrollTo(font_size.indexOf(entry));
        }catch(NumberFormatException e){
            
        }
    }
    //Starts the clock for the dateTime Label
    public void initClock() {
        Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(1000),
            ae -> setDateTime()));
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }
    //Sets the text for the dateTime Label
    public void setDateTime(){
          Format formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
          Date date = new Date();
          dateTime.setText(formatter.format(date));
    }
    //gets the caret position
    public void getCaretPosition(){
        caretPosition = textArea.getCaretPosition();
        columns = 1;
        rows = 1;
        for(int i = 0; i < caretPosition; i++){
            if (textArea.getText().charAt(i) == '\n'){
                columns = 1;
                rows++;
            }else{
                columns++;
            }
        }
        caretColumn.setText("Column: " + Integer.toString(columns));
        caretRow.setText("Row: " + Integer.toString(rows));
    }
    //gets the current file name
    
}
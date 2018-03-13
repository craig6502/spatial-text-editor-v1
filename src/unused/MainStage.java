/** This application creates a GUI as a legal doc environment
JavaFX implementation of GUI started 17.11.2017 by Craig Duncan
*/
 

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;
//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
//Scene - general appearance & layout of Stages, nodes
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
 //ArrayList etc
 import java.util.*;

 //package classes
 //import WordTool;


/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

*/
public class MainStage extends Application {
    //setup instance variables here.  Static if shared across class (i.e. static=same memory location used)
    //instance variables for Screens to hold them if changed.
    Stage textStage = new Stage(); //basic constructor for main text stage
    Scene MainScene; // scene for adding on textStage.
    Scene boxScene;   //scene for graphic window
    Group boxGroup_root; //root node for graphic window
    ScrollPane scroll_rootNode; //root Node for Text Area
    HBox hbox1; //an hbox to add text and things to!
    HBox hbox3;
    TextArea textArea1 = new TextArea();
    TextArea textArea2 = new TextArea();
    TextArea textArea3 = new TextArea();
    TextArea textArea4 = new TextArea();
    String myTextFile="";
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    /*Declare any objects for 2nd window here
    Stage visualWindow;
    //DefBox littleBox;
    //SpriteBox littleStack;
    Scene graphicscene; //the scene in the second stage (window)
    */
    //General Sprite (block) manager
    SpriteManager mySpriteManager;
    //inspector window
    Stage inspectorWindow;
    Scene inspectorScene;
    ScrollPane inspectorGroup_root;
    TextArea inspectorTextArea = new TextArea();
    //Definitions window
    Stage defWindow;
    Scene defScene;
    Group defGroup_root;
    TextArea defTextArea = new TextArea();
    //definitions extraction window
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Clause analysis window
    Stage ClauseStage;
    Group ClauseGroup_root;
    //updated clause extract/analysis window
    Group myExtracted_clauses;
    Scene ClauseExScene;
    //New clauses Work in Progress window
    Group myGroup_clauses;
    ClauseContainer clausesWIP;

/*The main method uses the launch method of the Application class.
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html
*/


public static void main(String[] args) {
        launch(args);
  }

//-- Using instances of WordTool objects ---

private String getTextfromFile(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getFileAsString(fname);
}

private String getMostCommon(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromFile(fname);
}

private void printStatsfromFile(String fname) {
    WordTool myTool = new WordTool();
    myTool.printCountFromFile(fname);
}

private DefContainer grabDefinitionsFile(String fname) {
    WordTool myTool = new WordTool();
    String data = myTool.getFileAsString(fname);
    DefContainer defbox = myTool.doDefTextSearch(data);
    return defbox;
} 

private DefContainer grabDefinitionsString(String mydata) {
    WordTool myTool = new WordTool();
    DefContainer defbox = myTool.doDefTextSearch(mydata);
    return defbox;
} 

//return a ClauseContainer object with clauses after using text document as input

private ClauseContainer getClauseContainer(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    ClauseContainer clauseCarton = myTool.ClauseCapHeadingExtract(mydata);
    //ClauseContainer clauseCarton = myTool.ClauseInlineHeadingExtract(mydata);
    return clauseCarton;
} 


//used by event handler
private String getMatched(String data) {
    WordTool myTool = new WordTool();
    DefContainer defbox = myTool.doDefTextSearch(data);
    return defbox.getDefAndText();
                
}
//used by event handler
private String getCommonWordsNow(String data) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromString(data);
}

public void pressMe() {
    System.out.println ("Button pressed - registered with main app");
}


/* Setup text area with blank text to start.  To put default text in at time of constructing,
insert text strings into TextArea arguments
make this public so that the inner class can find it  */

public void setupInputStage(Stage textStage, String myTitle) {

        //This is the stage to be used but is not the JavaFX application default
        textStage.setTitle(myTitle);
        
         //This Vbox only has 1 child, a text area, and no spacing setting.
        //VBox vbox = new VBox(textArea);//unused
        int widthcol1=66; //columns? Think of in % terms?
        int widthcol2=33;
        int totalwidth=900; //this is pixels?

        //TO DO:  CONCEPTUALISE WINDOWS/GROUPS TO WORK WITH EACH AS OBJECTS
        
        //config for window
        double leftColWidth = 650;
        double leftColHeight = 400;
        double rightColWidth = 150;
        double windowWidth = 800;
        double windowHeight = leftColHeight+100;
        /* Setup a horizontal box with two text areas, but put first in scrollpane to allow scrolling */
        this.textArea1.setWrapText(true);
        this.textArea2.setWrapText(true);
        this.textArea1.setPrefWidth(leftColWidth);
        this.textArea1.setPrefHeight(leftColHeight);
        this.textArea2.setPrefWidth(rightColWidth);
        
        ScrollPane textpane = new ScrollPane();
        textpane.setContent(textArea1);
        textpane.setPrefHeight(leftColHeight);  
        textpane.setPrefWidth(leftColWidth);
        hbox1 = new HBox(0,textpane,this.textArea2);
        //
        this.textArea3 = new TextArea();
        this.textArea4 = new TextArea();
        this.textArea3.setPrefWidth(leftColWidth);
        this.textArea4.setPrefWidth(leftColWidth);
        
        /* UNUSUED
        TextArea textArea5 = new TextArea();
        TextArea textArea6 = new TextArea();
        textArea5.setPrefWidth(leftColWidth);
        textArea6.setPrefWidth(leftColWidth);
        */

        //Set horizontal boxes with spacing and child nodes *i.e. a row 
        HBox hbox2 = new HBox(0,this.textArea3,this.textArea4);

        //Button for Word Counts with Action Event handler
        Button btn = new Button();
        btn.setText("Update Word Counts");
        btn.setOnAction(updateWordCounts);
        
        //Button for definitions with Action Event handler
        Button btnDefs = new Button();
        btnDefs.setTooltip(new Tooltip ("Press to extract definitions from top text area"));
        btnDefs.setText("Extract Definitions");
        btnDefs.setOnAction(extractDefinitions);

        //Button for definitions icons with Action Event handler
        Button btnDefIcons = new Button();
        btnDefIcons.setTooltip(new Tooltip ("Press to create definitions icons from top text area"));
        btnDefIcons.setText("Extract Def Icons");
        btnDefIcons.setOnAction(makeDefIcons);

        //Button for Clause blocks with Action Event handler
        Button btnClauses = new Button();
        btnClauses.setTooltip(new Tooltip ("Press to extract Clauses from top Text Area"));
        btnClauses.setText("Extract Clause Icons");
        btnClauses.setOnAction(makeClauseIcons);

        //Set horizontal box to hold buttons
        hbox3 = new HBox(0,btn,btnDefs,btnDefIcons, btnClauses);
        //put each of our rows into a vertical scroll box
        //VBox vbox2 = new VBox(0,hbox1,hbox2,hbox3);
        //vbox2.getChildren().addAll(hbox1,hbox2,hbox3);
        VBox vbox2 = new VBox(0,hbox1);
        //vbox2.getChildren().add(hbox2); //<---Now put this in its own window
        vbox2.getChildren().add(hbox3);
        
        vbox2.setPrefWidth(totalwidth); //this is in different units to textarea
        /* Lastly, put the Vbox inside a scroll pane 

        /* ---- THE SCROLLPANE IS THE ROOT NODE OF THE SCENE (MAINSCENE) ---
        Make sure it is an instance variable?
        Set the scroll pane width otherwise it will default to some width based on contents of scene, stage 
        
        */
        scroll_rootNode = new ScrollPane();
        scroll_rootNode.setContent(vbox2); 
        //add your parent node to scene.  e.g. you put your vbox2 inside a scroll pane, add the scroll pane.
        this.MainScene = new Scene(scroll_rootNode, windowWidth, windowHeight); //width x height in pixels?  contents have diff sizes
        /*Adding this to avoid consumption of event by child controls i.e. this works first */
        textStage.setScene(MainScene);
        //Size and positioning
        textStage.sizeToScene(); 
        textStage.setX(20); 
        textStage.setY(50); 
        
    }

/* Setup method to create a space to add or remove clauses, and then process them into some kind of output

At this stage, the root node is a Group, which sizes according to the children (i.e. no size if empty)

This can be contrasted with a pane which has some sort of minimum size?  Design decision.

This is the only such window: the group box for adding clauses is unique and declared as an instance variable.

*/

public Group setupClauseWIPstage(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        
        
        Group myGroup_root = new Group(); //for root
        clausesWIP = new ClauseContainer();
        myGroup_clauses = new Group(); //for child node
        //add group layout object as root node for Scene at time of creation
        defScene = new Scene (myGroup_root,650,600); //default width x height (px)
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Clause WIP Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("ClauseWIP");
             }
        });

               //
        myStage.setScene(defScene); //this selects the stage as current scene
        /*myStage.setX(100);*/
        Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        double mySetX = ScreenBounds.getWidth() / 1.8;
        //myStage.setX(500);
        myStage.setX(mySetX);
        myStage.setY(450);
        myStage.show();
        
        //Button for new clauses
        Button btnNewClause = new Button();
        btnNewClause.setText("Add New Clause");
        btnNewClause.setTooltip(new Tooltip ("Press to add a new clause"));
        btnNewClause.setOnAction(addNewClauseBox);
        
        //Button for removing clauses
        Button btnDeleteClause = new Button();
        btnDeleteClause.setTooltip(new Tooltip ("Press to remove selected clause"));
        btnDeleteClause.setText("Remove Clause");
        //btnDeleteClause.setOnAction(extractDefinitions);

        //Button for summary print list of clauses
        Button btnClausePrint = new Button();
        btnClausePrint.setTooltip(new Tooltip ("Press to list all clauses in inspector/console"));
        btnClausePrint.setText("Print List");
        btnClausePrint.setOnAction(printClauseList);

        //Button for export/document clauses TO DO: some config or separate panel.
        Button btnExportClause = new Button();
        btnExportClause.setTooltip(new Tooltip ("Press to output clauses as RTF"));
        btnExportClause.setText("RTF Export");
        //btnDeleteClause.setOnAction(extractDefinitions);

        //Set horizontal box to hold buttons
        HBox hboxButtons = new HBox(0,btnNewClause,btnDeleteClause,btnClausePrint);
        VBox vbox1 = new VBox(0,myGroup_clauses,hboxButtons);
        //
        myGroup_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=650;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return myGroup_clauses;
        
    }


 /* 
 ---- SETUP A NEW STAGE TO DISPLAY MOVEABLE BOX OBJECTS--- 
This is a simple generic scene creator for a Stage.  It sets size and Title and default position.  
The Group object (root node) is placed in the scene without any text box etc.  
Method will @return same Group layout object (root node) to enable addition of further leaf nodes

Child objects can be added to root node later:
myGroup_root.getChildren().add(defTextArea); //std Text Area as default (optional)
defTextArea.setText("Some future contents");

The Scene placed on the stage is a standard size window (wd x ht) in a fixed position (no need to pass arguments about size yet)

Adds a generic event handler for future use.
 */

 public Group setupBlocksWindow(Stage myStage, String myTitle) {
        
        Group myGroup_root = new Group();
        //add group layout object as root node for Scene at time of creation
        defScene = new Scene (myGroup_root,650,600); //default width x height (px)
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("blocks");
             }
        });

        myStage.setScene(defScene); //this selects the stage as current scene
        myStage.setTitle(myTitle);
        //OLD: Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        myStage.setX(0);
        myStage.setY(450);
        myStage.show();
        return myGroup_root;

}

/* SETUP STAGE TO DISPLAY BLOCKS WITH SOME SIMPLE BUTTONS */

public Group setupBlocksButtonsStage(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        
        
        Group myGroup_root = new Group(); //for root
        ClauseContainer clausesExtracted = new ClauseContainer(); //TO DO: Instance variable
        myExtracted_clauses = new Group(); //for child node
        //add group layout object as root node for Scene at time of creation
        ClauseExScene = new Scene (myGroup_root,650,600); //default width x height (px)
        //optional event handler
        ClauseExScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Extracted Clause Window: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("ClauseWIP");
             }
        });

               //
        myStage.setScene(ClauseExScene); //this selects the stage as current scene
        myStage.setX(100);
        myStage.setY(450);
        myStage.show();
        
        //Button for moving clauses
        Button btnMoveClause = new Button();
        btnMoveClause.setText("Move Clause to WIP");
        btnMoveClause.setTooltip(new Tooltip ("Press to move clause to Clause WIP Window"));
        btnMoveClause.setOnAction(MoveClausetoWIP);

        //Button for copying clauses
        Button btnCopyClause = new Button();
        btnCopyClause.setText("Copy Clause to WIP");
        btnCopyClause.setTooltip(new Tooltip ("[TBA] Press to copy clause to Clause WIP Window"));
        //btnCopyClause.setOnAction(CopyClausetoWIP);
        
        //Set horizontal box to hold buttons
        HBox hboxButtons = new HBox(0,btnMoveClause,btnCopyClause);
        VBox vbox1 = new VBox(0,myExtracted_clauses,hboxButtons);
        //
        myGroup_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=650;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return myExtracted_clauses;
        
    }

/* Setup Stage as a Toolbar Panel for Sprite Move, Copy functions etc */

public Group setupToolbarPanel(Stage myStage, String myTitle) {

        myStage.setTitle(myTitle);
        //TO DO: Instance variable
        Group toolbar_root = new Group(); //for root
        Scene toolbarScene = new Scene (toolbar_root,180,150); //default width x height (px)
        //optional event handler
        toolbarScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Extracted Clause Window: Mouse click detected! " + mouseEvent.getSource());
         mySpriteManager.setStageFocus("Toolbar");
             }
        });

               //
        myStage.setScene(toolbarScene); //this selects the stage as current scene
        //Layout
        Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        double mySetX = ScreenBounds.getWidth() / 1.1;
        //myStage.setX(500);
        myStage.setX(mySetX);
        myStage.setY(50);
        myStage.show();
        
        //Button for moving clauses
        Button btnMoveClause = new Button();
        btnMoveClause.setText("Move Clause to WIP");
        btnMoveClause.setTooltip(new Tooltip ("Press to move clause to Clause WIP Window"));
        btnMoveClause.setOnAction(MoveClausetoWIP);

        //Button for copying clauses
        Button btnCopyClause = new Button();
        btnCopyClause.setText("Copy Clause to WIP");
        btnCopyClause.setTooltip(new Tooltip ("[TBA] Press to copy clause to Clause WIP Window"));
        //btnCopyClause.setOnAction(CopyClausetoWIP);

        //TO DO:  Buttons for 'Copy to Library' {Definition Library}{Clause Library}
        //Button for "Load a clause library from disk"  etc
        
        //Set horizontal box to hold buttons
        //HBox hboxButtons = new HBox(0,btnMoveClause,btnCopyClause);
        VBox vbox1 = new VBox(0,btnMoveClause,btnCopyClause);
        //
        toolbar_root.getChildren().add(vbox1); //add the vbox to the root node to hold everything
        int totalwidth=190;
        vbox1.setPrefWidth(totalwidth); //this is in different units to textarea
       
        //return the child node, not the root in this case?
        return toolbar_root;
}


/** Setup independent text inspector window 
@parameter Requires a Stage object and a title as arguments
@Returns a Scrollpane representing the root node

@notes Scene size will determine initial width of Stage window 

**/

public ScrollPane setupScrollTextWindow(Stage myStage, String width, String myTitle) {
        
        //Layout
        Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        //
        ScrollPane scroll_root1 = new ScrollPane();
        scroll_root1.setFitToHeight(true);
        scroll_root1.setFitToWidth(true);
        //default layout settings (display panels etc) 
        int setWidth=500;
        int setHeight=500;
        double mySetX = ScreenBounds.getWidth() / 1.8;
        //inspector panel settings
        if (width.equals("inspector")) {
            setWidth=250;
            setHeight=250;
            mySetX = ScreenBounds.getWidth() / 1.5; 
        }
        Scene defScene = new Scene (scroll_root1,setWidth,setHeight); //width x height (px)
        
        //setup starting position near right side of screen
        myStage.setX(mySetX); 
        myStage.setScene(defScene);
        //optional event handler
        defScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click detected for text scroll window! " + mouseEvent.getSource());
             }
        });
        //Size and positioning
        myStage.setTitle(myTitle);
        myStage.show();
        return scroll_root1; 
        
        }

private void setArea1Text(String fname) {
        //get text from file and put in textarea 1
        String myText=this.getTextfromFile(fname);
        this.textArea1.setText(myText);

}

private void setArea2Text(String fname) {
        //get stats from file and put in textarea 2
        String myStats=this.getMostCommon(fname);
        this.textArea2.setText(myStats);
        //send some stats to console
        this.printStatsfromFile(fname);
}

/*
private String getArea1Text() {
        //get stats from file and put in textarea 2
        return this.textArea1.getText();
        //return myStats;
}
*/


/* Method to see if any label or text contains legal 'role' words, for display purposes 

Many of these are pair words: relationship dichotomies; 
a RELATIVE inequality or division of social, economic or legal power that defines a transaction or struture, and the role of the participants.

TO DO: put into groups for managing different areas of law, but iterate through all.
*/

public Boolean isLegalRoleWord (String myWord) {
    ArrayList<String> RoleWords = new ArrayList<String>(Arrays.asList("employer","employee","landlord","tenant","lessor","lessee","director","shareholder","trustee","beneficiary", "debtor","creditor", "payor", "payee","mortgagor","mortgagee","regulator","manager","partner","owner","guarantor","guarantee","seller","buyer","vendor","purchaser","grantor","grantee","distributor","bailor","bailee","master","servant","licensor","licensee","developer","carrier","lender","borrower"));
    Iterator<String> myIterator = RoleWords.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
        String checkWord = myIterator.next();
        if (myWord.equalsIgnoreCase(checkWord)) {
            return true;
        }
        /* pedantic version with case checking
        if (myWord.equals(checkWord)) {
            return true;
        }
        */
    }
    return false;
}

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("File Utilities");
        //primaryStage.show();
        primaryStage.close();

        //the object that manages sprite with focus etc
        mySpriteManager = new SpriteManager();
       
        //*Stage that I will use for main text input display and editing
        Stage myStage = new Stage();
        this.setupInputStage(myStage,"Text Analysis");
        //set some default text in main text window
        //this.myTextFile="popstarlease.txt";
        this.myTextFile="electricity.txt";
        this.setArea1Text(this.myTextFile);
        this.setArea2Text(this.myTextFile);
        myStage.show();

        //setup main toolbar
        Stage toolbarStage = new Stage();
        Group toolbarGroup = MainStage.this.setupToolbarPanel(toolbarStage, "Toolbar");

        //setup clauses sandbox
        Stage ClauseSB = new Stage();
        Group clausePlayBox = MainStage.this.setupClauseWIPstage(ClauseSB, "WIP Sandbox/Clause Staging Area");

        /* Setup default Stage with Scrollpane to display Text as Inspector
        */
        inspectorWindow = new Stage();
        inspectorGroup_root = MainStage.this.setupScrollTextWindow(inspectorWindow, "inspector", "Inspector Window");
        
        //Outer class method class to obtain text from analysis area
        String gotcha = MainStage.this.textArea1.getText();
        String newDefs = MainStage.this.getMatched(gotcha);
        //set the default scrollpane content to a designated text area and size scrollpane
        inspectorGroup_root.setContent(inspectorTextArea); 
        //TO DO: remove these text setup lines?
        double width = 600; 
        double height = 500; 
        inspectorGroup_root.setPrefHeight(height);  
        inspectorGroup_root.setPrefWidth(width);
        inspectorTextArea.setWrapText(true);
        //now set the content of text area inside scrollpane to our text
        inspectorTextArea.setText("Some future contents");

        //TO DO: Setup another 'Stage' for file input, creation of toolbars etc.
    }

    /* This is a method to create a new eventhandler for the SpriteBox objects which are themselves a Stackpane that incorporate a Rectangle and a Text Node as components

    //unfocus current Sprite - only works for the Sandbox.  
    Need to refine scope so that it works with current window

    */

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            // If you are only moving child objects not panes
            orgTranslateX = ((SpriteBox)(t.getSource())).getTranslateX();
            orgTranslateY = ((SpriteBox)(t.getSource())).getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);
            //change colour if double click

            switch(t.getClickCount()){
                case 1:
                    System.out.println("One click");
                    //change colour or something
                    break;
                case 2:
                    System.out.println("Two clicks");
                    //toggle
                    //unfocus current Sprite - only works for the Sandbox? or record in any window?  
                    SpriteBox hadFocus = mySpriteManager.getCurrentSprite();
                    if (hadFocus!=null) {
                        hadFocus.endAlert();
                    }
                    SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
                    currentSprite.doAlert();
                    /* OLD: if this sprite had focus, then toggle
                    Boolean isAlert = ((SpriteBox)(t.getSource())).isAlert();
                    if (isAlert==true) {
                        ((SpriteBox)(t.getSource())).endAlert();
                        //toDO: clear the inspector window contents
                    }
                    
                    else {
                        SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
                        currentSprite.doAlert();
                        mySpriteManager.setCurrentSprite(currentSprite);  //what if wrong window?
                        String myOutput = currentSprite.getContent();
                        inspectorTextArea.setText(myOutput);

                    }
                    */
                    
                    //change target in WIP stage
                    
                    if (mySpriteManager.getStageFocus().equals("ClauseWIP")) {
                        mySpriteManager.setTargetSprite(currentSprite);
                    }
                    
                    mySpriteManager.setCurrentSprite(currentSprite);  //what if wrong window?
                    String myOutput = currentSprite.getContent();
                    inspectorTextArea.setText(myOutput);

                    break;
                case 3:
                    System.out.println("Three clicks");
                    break;
            }
            t.consume(); //trying this to see if it frees up for second press but better to deal with cause
        }
    };
    
     /* This is eventhandler interface to create a new eventhandler class for the SpriteBox objects 
     This uses a lambda expression to create an override of the handle method
     */
     /* These currently have no limits on how far you can drag */

    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            currentSprite.setTranslateX(newTranslateX);
            currentSprite.setTranslateY(newTranslateY);
            System.out.println("The handler for drag box is acting");
            //change the target to the last moved Sprite in WIP stage?
            /*
            if(mySpriteManager.getStageFocus().equals("ClauseWIP")) {
                mySpriteManager.setTargetSprite(currentSprite);
            }
            */
            t.consume();//check

        }
    };

    //BUTTON EVENT HANDLERS

    // Method to move selected sprite to Clause WIP (will not duplicate)
    /*
            The following 'add' actually copies to the second stage.
            By moving the object or referring to it on the new Stage, it forces JavaFX to refresh.

            Java FX does its own cleanup.

            To achieve a 'copy' rather than a move, additional code needed.

     */

    EventHandler<ActionEvent> MoveClausetoWIP = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            //call method to add Sprite to ...
            //offset new sprite handling
            
            //add sprite to Stage for clause WIP.  This will clean up object elsewhere...
            myGroup_clauses.getChildren().add(currentSprite); 
            clausesWIP.addClause(currentSprite.getClause()); 
            //TO DO: update property of group to keep track of last position added
            //sprite management - alerts and focus
            currentSprite.doAlert();
            mySpriteManager.setAsTarget(currentSprite);
        }
    };


    /* TO DO: Turn this into a copy not a move */

    EventHandler<ActionEvent> CopyClausetoWIP = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            //This sets the initial reference 
            SpriteBox currentSprite = mySpriteManager.getCurrentSprite(); //not based on the button
            //lose focus
            currentSprite.endAlert();
            //call method to add Sprite to ...
            //offset new sprite handling
            myGroup_clauses.getChildren().add(currentSprite); //add sprite to Stage for clause WIP
            clausesWIP.addClause(currentSprite.getClause()); 
            //TO DO: update property of group to keep track of last position added
            //sprite management
            currentSprite.doAlert();
            mySpriteManager.setAsTarget(currentSprite); 
        }
    };

    /* Event handler for adding a new clause box to Sandbox Stage
    TO DO: Prevent user from attempting to add same object to same stage twice.
    i.e. if focus is on clause WIP stage, then either copy, or disallow.
    */

    EventHandler<ActionEvent> addNewClauseBox = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
            SpriteBox b;
            String label = "New Clause";
            String text = "Default text inside Clause";
            b = new SpriteBox(label, "orange"); //default is blue
            b.setContent("some text to put in spritebox");
            b.setClauseText(text); //overrides box - i.e sets inner object text and Sprite to the 'label'
            b.setClauseLabel(label);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            //offset new sprite handling
            myGroup_clauses.getChildren().add(b); //add sprite to Stage
            clausesWIP.addClause(b.getClause()); //add clause from sprite to clauses container
            //alerts and focus
            SpriteBox CS = mySpriteManager.getCurrentSprite();
            CS.endAlert();
            b.doAlert();
            mySpriteManager.setAsTarget(b); 
            }
        };

    //printClauseList
        EventHandler<ActionEvent> printClauseList = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
             inspectorTextArea.setText("This is where list of clauses will appear");
             clausesWIP.doPrintIteration();
             String output=clausesWIP.getClauseAndText();
             inspectorTextArea.setText(output);
             /* TO DO: Have a separate "Output/Preview" Window to show clause output.  
             //Maybe HTMLview?
             i.e. this will be an 'output console', but within the application.
             */
            }
        };


    /* 

    Notice that I've included event handlers for each definition block added, so that they can handle mouse events inside the Window they've been added to 
    
    */

    EventHandler<ActionEvent> makeDefIcons = 
    new EventHandler<ActionEvent>() {

        @Override 
        public void handle(ActionEvent event) {
        System.out.println("Get DefIcons Button was pressed!");
        Stage adHoc = new Stage();

        defGroup_root = MainStage.this.setupBlocksWindow(adHoc, "Definitions Block Window");
        
        adHoc.setY(600);

        //obtain data to display
        DefContainer myContainer = grabDefinitionsString(textArea1.getText());
        ArrayList<Definition> myDList = myContainer.getDefArray();
        Iterator<Definition> myiterator = myDList.iterator();
        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Definition mydefinition = myiterator.next();
            String myLabel = mydefinition.getLabel();
            String mydeftext = mydefinition.getDef();
            String FreqCnt = Integer.toString(mydefinition.getFreq());
            String myCont = myLabel+"("+FreqCnt+")";
            SpriteBox b;
            if (isLegalRoleWord(myLabel)==true) {
                b = new SpriteBox(myCont, "orange"); //default is blue
                b.setContent(mydeftext); //to do - transfer defs to sep objects in SpriteBox
            } else {
                b = new SpriteBox(myCont, "green");
                b.setContent(mydeftext);
            }
            b.setTranslateX(offX); //increments offset each time for display. 
            //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
            b.setTranslateY(offY);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            defGroup_root.getChildren().add(b);
            if (offX>440) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        adHoc.show();
        }    
    };
     
    /* Notice that I've included event handlers for each clause block added, so that they can handle mouse events inside the Window they've been added to 
    */


    EventHandler<ActionEvent> makeClauseIcons = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        System.out.println("Clause Boxes Button was pressed!");
        //make a new stage
        ClauseStage = new Stage();
        /* OLD:
        ClauseGroup_root = MainStage.this.setupBlocksWindow(ClauseStage, "Extracted Clauses");
        */
        ClauseGroup_root = MainStage.this.setupBlocksButtonsStage(ClauseStage, "Extracted Clauses");
        
        //TO DO: get source of data
        ClauseContainer myContainer = getClauseContainer(textArea1.getText());
        ArrayList<Clause> myClauseList = myContainer.getClauseArray();
        Iterator<Clause> myiterator = myClauseList.iterator();
        int offX=0;
        int offY=0;
        while (myiterator.hasNext()) {
            Clause myclause = myiterator.next();
            String myLabel = myclause.getLabel();
            String myclausetext = myclause.getClause();
            //String FreqCnt = Integer.toString(myclause.getFreq());
            String myCont = myLabel; //+"("+FreqCnt+")";
            SpriteBox b;
            if (offY<=100) {
                b = new SpriteBox(myCont); //default blue
                //b.setContent(myclausetext); //to do - transfer defs to sep objects in SpriteBox
            } else {
                b = new SpriteBox(myCont, "green");
                //b.setContent(myclausetext);
            }
            b.setContent(myclausetext);  //this will be overriden
            b.setClauseText(myclausetext); //overrides box - i.e sets inner object text and Sprite to the 'label'
            b.setClauseLabel(myLabel);
            b.setTranslateX(offX); //increments offset each time for display. 
            //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
            b.setTranslateY(offY);
            b.setOnMousePressed(PressBoxEventHandler); 
            b.setOnMouseDragged(DragBoxEventHandler);
            
            ClauseGroup_root.getChildren().add(b);
            if (offX>640) {
                offY=offY+65;
                offX=0;
            }
            else {
                offX = offX+160;
            }
        }
        ClauseStage.show();
            }

        };

        //update word counts
        EventHandler<ActionEvent> updateWordCounts = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
                System.out.println("Word Count Button was pressed!");
                //Outer class method class
                String gotcha = MainStage.this.textArea1.getText();
                String newTA = MainStage.this.getCommonWordsNow(gotcha);
                MainStage.this.textArea2.setText(newTA);
                //new stage
                Stage MainWords = new Stage();
                Group CountGroup_root = MainStage.this.setupBlocksWindow(MainWords, "Common Words Window");
                //new one
                //MainStage.this.getMatched(gotcha);

                    //---ADD YELLOW BOXES WITH COMMON WORDS TO GRAPHICS WINDOW---

                    //TO DO: The SpriteBoxes will be meta-objects include both defs, clause and data.
                    //They should incorporate the text or contents objects so that the GUI can feed this back and forward from text edit windows etc as required.
                    WordTool myHelper = new WordTool();
                    ArrayList<String> boxList = new ArrayList<String>();
                    try {
                    boxList = myHelper.commonBoxSet(gotcha);
                    }
                    catch (Exception e) {
                               e.printStackTrace();
                              } 
                    Iterator<String> i = boxList.iterator();
                    int offX = 0;
                    while (i.hasNext()) {
                        offX=offX+50;
                        /* OLD:
                        SpriteBox b;
                        if (offX<=100) {
                            b = new SpriteBox(i.next()); //default blue
                            b.setContent("This is a blue box");
                        } else {
                            b = new SpriteBox(i.next(), "yellow");
                            b.setContent("This is a yellow box");
                        }
                        */
                        SpriteBox b;
                        String newlabel = i.next();
                        if (isLegalRoleWord(newlabel)==true) {
                            b = new SpriteBox(newlabel, "orange"); //default is blue
                            b.setContent(newlabel); //to do - transfer defs to sep objects in SpriteBox
                        } else {
                            b = new SpriteBox(newlabel, "green");
                            b.setContent(newlabel);
                        }
                        b.setTranslateX(offX); //increments offset each time for display. 
                        //TO DO: set some default object refs (StackPane has current; these will be alternate indexes).
                        b.setTranslateY(offX);
                        b.setOnMousePressed(PressBoxEventHandler); 
                        b.setOnMouseDragged(DragBoxEventHandler);
                        
                        CountGroup_root.getChildren().add(b);
                    }
            }
        };
        //
        //update word counts
        EventHandler<ActionEvent> extractDefinitions = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //make a new stage with scrollpane
            defsTextStage = new Stage();
            defsTextStage_root = MainStage.this.setupScrollTextWindow(defsTextStage, "display", "Definitions List");
            defsTextStage.setY(350);
            //Outer class method class to obtain text from analysis area
            String gotcha = MainStage.this.textArea1.getText();
            String newDefs = MainStage.this.getMatched(gotcha);
            //set the default scrollpane content to a designated text area and size it
            defsTextStage_root.setContent(textArea3); 
            double width = 800; 
            double height = 500; 
            textArea3.setPrefHeight(height);  
            textArea3.setPrefWidth(width);
            textArea3.setWrapText(true);
            //now set the content of text area inside scrollpane to our extracted text
            textArea3.setText(newDefs);
            System.out.println("Get Defs Button was pressed!");
            }
        };



}
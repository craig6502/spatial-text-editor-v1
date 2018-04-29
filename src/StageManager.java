/* 
Class to manage each Stage
30.3.2018
Until now, Stage Manager class was used as a singleton.
However, by creating a 'StageManager' object for each stage, it can keep Stage-specific information
and enormously reduce the complexity of stage position, current sprite location etc.

This is required because the Stage object in JavaFX defined for the GUI.
This class is a conceptual object that will hold not only the javaFX Stage object, but associated data

Requires stageID to be set at start of app.
The Group that is part of the JavaFX node tree to which SpriteBoxes are to be added can be stored here.
(i.e. this saves having to navigate through the GUI node instances to find it each time)

26.4.18
Most of the functions are intended to be used with a stage that displays a 'node'.
In effect, this class helps make a GUI: to create a Stage that will display a node, its text and its child nodes, and allow editing
It also performs tracking of the stage (open node window) with current focus.
The Workspace is an instance of this class but uses far fewer helper functions.

The stages are iterative: in creating new child node boxes, each box can open a new node editing window
Therefore, the StageManager is like a visual tree navigator for the node data.
A node or Stage does not require opening up a separate 'edit' window because each node viewer's design is informative and functional.
(To do: Consider if "NodeViewer" is a better class name.  Nodes represent abstract 'frames'
A display option is to have background colour of node editor change for different types/levels)

The stage manager will provide its own GUI functions for updating the node's text.
28.4.18
This is also possible with images and video:
Each node can hold 1 image see https://www.tutorialspoint.com/javafx/javafx_images.htm
https://docs.oracle.com/javase/8/javafx/media-tutorial/overview.htm

*/

//import utilities needed for Arrays lists etc
import java.util.*;
//JavaFX
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
//Scene - general appearance & layout of Background Fills, Stages, nodes
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//Paint
import javafx.scene.paint.Color;
//Menus
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;



/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

public class StageManager {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();
ArrayList<Stage> myStageList = new ArrayList<Stage>();
int spriteX = 0;
int spriteY = 0;
String stageName = "";
String stageTitle = "";

ClauseContainer reference_ParentNode = new ClauseContainer();
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup;
Pane spritePane;
Scene localScene;
SpriteBox focusbox; //for holding active sprite in this scene.  Pass to app.
//SpriteTracker globalTracker;
SpriteBox parentBox;//to hold the calling box for this viewer.  
//Do not create new object here or circular constructors! Do in constructor

String filename = ""; //current filename for saving this stage's contents
//STAGE IDS
int location = 0;
String category="";
//Displayed ClauseContainer (i.e. Node).  Will be updated through GUI.
ClauseContainer displayNode = new ClauseContainer();
int doccount=0; //document counter for this stage

//NODE VIEWER DIMENSIONS
int nodeViewWidth = 600;
int nodeViewHeight = 600;

//NODE'S TEXT CONTENT
//For storing main text output area for this Stage (if any)
//As of 26.4.2018: make this the default area to hold the node's own text (for stages that display a frame that is also an open node).  Always editable.

//This TextArea is the GUI display object for the nodes' docnotes String.  Edit button will update the node's (ClauseContainer) actual data
TextArea shortnameTextArea = new TextArea();
TextArea headingTextArea = new TextArea();
TextArea inputTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
Text parentBoxText;
//Store the common event handlers here for use
EventHandler<MouseEvent> PressBox;
EventHandler<MouseEvent> DragBox;
//MenuBar
MenuBar localmenubar;


/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
StageManager should store GUI objects in one way, data in another?  separation of concerns
Some kind of content manager for each stage?
Consider if subclasses of StageManager could deal with flavours of StageManager (e.g. position?
*/
ArrayList<Object> BoxContentsArray = new ArrayList<Object>(); //generic store of contents of boxes


//Track current stage that is open.
static StageManager currentFocus; //any StageManager can set this to itself

//constructor
public StageManager() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//temporary constructor for old windows (toolbars, output etc)
public StageManager(StageManager parent, String myTitle) {
    Stage myStage = new Stage();
    setStage(myStage);
    setTitle(myTitle);
    setDragBox(DragBox);
    setJavaFXStageParent(parent);
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//standard open node viewer constructor
public StageManager(StageManager parent, EventHandler PressBox, EventHandler DragBox) {
    setJavaFXStageParent(parent);
    setPressBox(PressBox);
    setDragBox(DragBox);
}

//workspace constructor.  Filename details will be inherited from loaded node.
//Passes MenuBar from main application for now
public StageManager(String title, MenuBar myMenu, EventHandler PressBox, EventHandler DragBox) {
    setTitle(title);
    setMenuBar(myMenu);
    setPressBox(PressBox);
    setDragBox(DragBox);
    newWorkstageFromGroup();  
}

//any instance can return the global variable with focus stage
public StageManager getCurrentFocus() {
    return currentFocus; //notice not a 'this' as not an instance
}

//setter: should generally only set it to current instance
public void setCurrentFocus(StageManager mySM) {
    currentFocus = mySM; //notice not a 'this' as not an instance
}

//JAVAFX SCENE GRAPH GUI INFO (THIS IS NOT THE DATA NODE!)
public void setSceneRoot(Node myNode) {
    this.rootNode = myNode;
}

public Node getSceneRoot() {
    return this.rootNode;
}


//FILE I/O DATA
public String getFilename() {
    return this.filename;
}

public void setFilename(String myFile) {
    this.filename = myFile;
}

public int getDocCount() {
    return this.doccount;
}

public void resetDocCount() {
    this.doccount=0;
}

public int advanceDocCount() {
    return this.doccount++;
}

//JAVAFX SCROLLERS FOR TEXT OUTPUT - DEFAULT
//Method to operate on external object passed to function (does not return)
//to DO - separate JavaFX objects wrapper functions class?

//add scene to stage
public void putTextScrollerOnStage() {
    ScrollPane rootnode_scroll = new ScrollPane();
    configDefaultScroller(rootnode_scroll); //scroller with text
    Scene textOutputScene = makeSceneScrollerAsRoot(rootnode_scroll);
    Stage textOutputStage = new Stage();
    storeSceneAndStage(textOutputScene, textOutputStage);
}

//make new scene with Scroller
private Scene makeSceneScrollerAsRoot (ScrollPane myRootNode) {

int setWidth=500;
int setHeight=250;
Scene myScene = new Scene (myRootNode,setWidth,setHeight); //width x height (px)
//this operates as a lambda - i.e events still detected by Main?
myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, myMouseLambda);
return myScene;
}
 
public void setPressBox(EventHandler<MouseEvent> myEvent) {
    this.PressBox=myEvent;
}

public void setDragBox(EventHandler<MouseEvent> myEvent) {
    this.DragBox=myEvent;
}


EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
 System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

private void configDefaultScroller(ScrollPane myScroll) {
    myScroll.setFitToHeight(true);
    myScroll.setFitToWidth(true);
    //setup text scroll node
    double width = 600; 
    double height = 500; 
    myScroll.setPrefHeight(height);  
    myScroll.setPrefWidth(width);
    //set to this object's outputtext area
    myScroll.setContent(getOutputTextNode()); 
    setSceneRoot(myScroll);
}

//JAVA FX TEXT AREAS - GETTERS AND SETTERS
public void setTextAreaLayout() {
    headingTextArea.setPrefRowCount(1);
    shortnameTextArea.setPrefRowCount(1);
    //inputTextArea  = makeTextArea();
}

public void setOutputText(String myText) {
    outputTextArea.setText(myText);
}

public String getOutputText() {
    return outputTextArea.getText();
}

//Return the JavaFX object (Node) 
public TextArea getOutputTextNode() {
    return this.outputTextArea;
}

//Input text area e.g. importer
public void setInputText(String myText) {
    inputTextArea.setText(myText);
}

public String getInputText() {
    return inputTextArea.getText();
}

/* Text Area in JavaFX inherits selected text method from
javafx.scene.control.TextInputControl
*/

private String getSelectedInputText() {
    return inputTextArea.getSelectedText();
}

//set the identified JavaFX object (TextArea) for the Stage
public void setStageTextArea(TextArea myTA) {
    this.inputTextArea = myTA;
}

//Return the JavaFX object (Node) 
public TextArea getInputTextNode() {
    return this.inputTextArea;
}

//SIMPLE SCENE GETTERS AND SETTERS AS JAVA FX WRAPPER

public void storeSceneAndStage (Scene myScene, Stage myStage) {
    setStage(myStage);
    updateScene(myScene);
}

private Scene getSceneLocal() {
    return this.localScene;
}

private Scene getSceneGUI () {
     return getStage().getScene(); //JavaFX
}

private void updateScene (Scene myScene) {
     getStage().setScene(myScene); //JavaFX in the GUI
     this.localScene = myScene; //local copy/reference
}

//SIMPLE STAGE GETTERS AND SETTERS FOR CUSTOM GUI.  WRAPPER FOR JAVAFX SETTERS

public void setStageName(String myName) {
    this.stageName = myName;
    this.localStage.setTitle(myName);
}

public String getStageName() {
    return this.stageName;
}

//probably redundant - keep name or title
public void setTitle(String myTitle) {
    this.stageTitle = myTitle;
    this.localStage.setTitle(myTitle);
}

public String getTitle() {
    return this.stageTitle;
}

public void setCategory(String myCat) {
    this.category=myCat;
}

public String getCategory() {
    return this.category;
}

//for passing in a menubar from main (for now: 29.4.18)
public void setMenuBar(MenuBar myMenu) {
    this.localmenubar = myMenu;
}

public MenuBar getMenuBar() {
    return this.localmenubar;
}

/* --- BASIC GUI SETUP FOR OPEN NODE VIEWERS --- */
public void updateOpenNodeView() {
    makeSceneForNodeEdit();
    resetSpriteOrigin();
    //title bar
    setTitle(displayNode.getType());
    //path
    //Set text to identify Stage containing parent box
    SpriteBox parBX = getParentBox();
    String parentSTR="";
    if (parBX==null) {
        parentSTR="[WS]";
    }
    else {
        if (parBX.getStageLocation()!=null) {
            parentSTR=parBX.getStageLocation().getTitle();
        }
        /*
        if(parBX.getBoxDocName()!=null) {
            parentSTR=parBX.getBoxDocName();

        }
        */
    }
    String pathText = parentSTR+"-->"+displayNode.getDocName()+"(contents)"; 
    parentBoxText.setText(pathText);
    //main node contents (text)
    shortnameTextArea.setText(displayNode.getDocName());
    headingTextArea.setText(displayNode.getHeading());
    inputTextArea.setText(displayNode.getNotes());
    //output node contents
    outputTextArea.setText(displayNode.getOutputText());
    //child nodes
    displayChildNodeBoxes();
}

/* ----- DATA (DISPLAY) NODE FUNCTIONS ----- */

/* 
This method sets the node that is used for the display in this stage.
All other nodes added to this node are considered child nodes of this node:
they are added as child nodes to the data node; they are display in the section of the stage for this

*/

private void setDisplayNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    updateOpenNodeView();
}

public ClauseContainer getDisplayNode() {
    return this.displayNode;
}

public void addWSNode(ClauseContainer myNode) {

}

//Method to update workspace appearance based on current node setting (usually root of project)
public void setWSNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    Group newGroup = new Group(); //new GUI node to show only new content.
    swapSpriteGroup(newGroup); //store the new GUI node for later use
    displayChildNodeBoxes(); //update WS view with new child boxes only
}

public void openNodeInViewer(ClauseContainer myNode) {

    setDisplayNode(myNode);
}

public ClauseContainer Node() {
    return this.displayNode;
}

//set the parent node for Nodes enclosed in boxes (i.e. level above)
public void setRefParentNode(ClauseContainer myParentID) {
    this.reference_ParentNode = myParentID;
}

public ClauseContainer getRefParentNode() {
    return this.reference_ParentNode;
}

/* GUI FUNCTIONS FOR WORKING WITH BOXES, NODES */

public void setParentBox (SpriteBox myPB) {
    this.parentBox = myPB;
    ClauseContainer myNode = myPB.getBoxNode();
    setDisplayNode(myNode);
}

public SpriteBox getParentBox () {
    return this.parentBox;
}

/* Box up a container of Sprites and place on Stage */

 private void displayChildNodeBoxes() {
    
        ClauseContainer parentNode = displayNode;
        //SpriteBox lastBox = new SpriteBox();
        ArrayList<ClauseContainer> childNodes = parentNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = childNodes.iterator();

        //only operates if there are Child Nodes to add
        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            System.out.println("Current child node to be added: "+thisNode.toString());
            //TO DO: check for duplication
            /*
            SpriteBox b = makeBoxWithNode(thisNode); //relies on Main, event handlers x
            addSpriteToStage(b); //differs from Main 
            setFocusBox(b); 
            */
            addNodeToView(thisNode);
        }
        showStage();
        //return getFocusBox();
        }

//only invoked here as needed i.e. when displaying child nodes on stage
private SpriteBox makeBoxWithNode(ClauseContainer node) {
    
    SpriteBox b = new SpriteBox();
    b.setOnMousePressed(PressBox); 
    b.setOnMouseDragged(DragBox);
    b.setBoxNode(node);
    return b;
}

/* ----- GENERAL GUI FUNCTIONS ----- */

//setter for the Stage
public void setStage(Stage myStage) {
    this.localStage = myStage;
}

//getter for the Stage
public Stage getStage() {
    return this.localStage;
}

/*
setter for the Group sprite boxes will be added to
*/
public void setSpriteGroup(Group myGroup) {
    this.spriteGroup = myGroup;
}

public Group getSpriteGroup() {
    return this.spriteGroup;
}

public void setSpritePane(Pane myPane) {
    this.spritePane = myPane;
}

public Pane getSpritePane() {
    return this.spritePane;
}

public void swapSpriteGroup(Group myGroup) {
    Pane myPane = getSpritePane();
    myPane.getChildren().remove(getSpriteGroup());
    setSpriteGroup(myGroup);
    myPane.getChildren().addAll(myGroup);
}

private void setStagePosition(double x, double y) {
    this.localStage.setX(x);
    this.localStage.setY(y);
}

private void stageBack() {
    this.localStage.toBack();
}

private void stageFront() {
    this.localStage.toFront();
}

//getters and setters
public void setCurrentXY(double x, double y) {

	this.latestX=x;
    this.latestY=y;
}

/*Method to set parent stage.  Call this before showing stage 

This is for GUI relationships, not data tree relationships.

nb If the stage has been called from a SpriteBox, the tree parent is the box, but
that box lies within a stage that can be used as parent stage here
(or make all stages the child of Stage_WS)
*/
private void setJavaFXStageParent(StageManager ParentSM) {
    Stage myStage = getStage(); 
    Stage Parent = ParentSM.getStage();
    myStage.initOwner(Parent);
}

/* 

The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
The workspace (WS) is, in effect, a large window placed at back.
TO DO: Make the MenuBar etc attach to a group that is at back,
then add WIP spritexboxes to a 'Document Group' that replaces Workspace with 'Document' menu

*/

//TO DO: set position based on StageManager category.
public void setPosition() {

    switch(this.stageName){

            case "workspace":
                setStagePosition(0,0);
                stageBack();
                break;

            case "editor":
                //myStage.initOwner(Parent);  //this must be called before '.show()' on child
                setStagePosition(850,0);
                stageFront();
                break;

            case "project":
                setStagePosition(800,300);
                stageFront();
                break;

            case "project library":
                setStagePosition(800,300);
                stageFront();
                break;

            case "library":
                setStagePosition(1000,300);
                stageFront();
                break;

            case "collection":
                setStagePosition(800,100);
                stageFront();
                break;
                
            case "document":
                setStagePosition(400,200);
                stageFront();
                break;

            case "Toolbar":
                setStagePosition(1000,50);
                stageFront();
                break;

            case "Output":  
                setStagePosition(150,550);
                stageFront();
                break;

            case "Import":
                setStagePosition(800,200);
                stageFront();
                break;
            
            default:
                setStagePosition(200,200);
                stageFront();
                break;
    }
}

//STAGE MANAGEMENT FUNCTIONS


public void showStage() {
    this.localStage.show(); 
}

public void hideStage() {
    this.localStage.hide(); 
}

public void toggleStage() {
    Stage myStage = getStage();         
    if (myStage==null) {
        System.out.println("Problem with Stage setup +"+myStage.toString());
    }
    if (myStage.isShowing()==false) {
        showStage();
        return;
    }
    if (myStage.isShowing()==true) {
        hideStage();
        return;
    }
}

//public interface setter helper - currently not used

public void setInitStage(StageManager myParentSM, Stage myStage, Group myGroup, String myTitle) {
   setStageName(myTitle);
   setStage(myStage);
   setJavaFXStageParent(myParentSM);
   setPosition(); 
   setSpriteGroup(myGroup);
   setTitle(myTitle);
}

/* Method to make new Scene with known Group for Sprite display */
public ScrollPane makeScrollGroup () {
    Group myGroup = new Group();
    setSpriteGroup(myGroup); 
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(myGroup);
    return outerScroll;
}

/* Method to make new TextArea that has associated functions in this class */
public TextArea makeTextArea() {
    TextArea tempTextArea = new TextArea();
    setStageTextArea(tempTextArea); 
    return tempTextArea;
}

//The scene only contains a pane to display sprite boxes
private Scene makeSceneForBoxes(ScrollPane myPane) {
        
        Scene tempScene = new Scene (myPane,650,400); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on SM scene detected! " + mouseEvent.getSource());
         //setStageFocus("document");
             }
        });
        updateScene(tempScene);
        return tempScene;
}

//The scene contains a text area, a pane to display sprite boxes and an Edit/Update button

private void makeSceneForNodeEdit() {
        
        ScrollPane tempPane = makeScrollGroup();
        setTextAreaLayout();
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateNodeText);
        //Button for cancel
        Button btnEditCancel = new Button();
        btnEditCancel.setText("Cancel Edits");
        btnEditCancel.setTooltip(new Tooltip ("Press to Cancel current edits"));
        //TO DO: set on action
      
        HBox hboxButtons = new HBox(0,btnUpdate,btnEditCancel);
        //
        parentBoxText = new Text();
        VBox allContent = new VBox(0,parentBoxText,shortnameTextArea,headingTextArea,inputTextArea,hboxButtons,tempPane,outputTextArea);
        //vboxAll.setPrefWidth(200);
        //
        Pane largePane = new Pane();
        largePane.getChildren().add(allContent); 
        Scene tempScene = new Scene (largePane,nodeViewWidth,nodeViewHeight); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on a node (StageManager scene) detected! " + mouseEvent.getSource());
         //setStageFocus("document");
         currentFocus=StageManager.this;
         //error checking i.e. like jUnit assert
         if (getCurrentFocus()==StageManager.this) {
            System.out.println("Change of Viewer Focus OK in Viewer!");
             System.out.println("Viewer :"+StageManager.this);
         }
         else {
            System.out.println("Problem with change Viewer Focus");
            System.out.println("Present Viewer :"+StageManager.this);
            System.out.println("Current Focus :"+getCurrentFocus());
         }
         }
        });
        updateScene(tempScene);
}

//Create Eventhandler to use with stages that allow edit button

EventHandler<ActionEvent> UpdateNodeText = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //main node contents (text)
            String editedName=shortnameTextArea.getText();
            String editedHeading=headingTextArea.getText();
            String editedText=inputTextArea.getText();
            String editedOutput=outputTextArea.getText();
            //
            displayNode.setDocName(editedName);
            //parentBox - should we insist on one?
            SpriteBox pntBox = getParentBox();
            if (pntBox!=null) {
                pntBox.setLabel(editedName);
            }
            displayNode.setHeading(editedHeading);
            displayNode.setNotes(editedText);
            displayNode.setOutputText(editedOutput);
            //error checking - log
            if (displayNode.getNotes().equals(editedText)) {
                System.out.println("Node updated OK!");
            }
            else {
                 System.out.println("Problem with node update.");
            }
            }
        };


private void newWorkstageFromGroup() {
    Group myGroup = makeWorkspaceTree();
    Scene myScene = makeWorkspaceScene(myGroup);
    Stage myStage = new Stage();
    setStage(myStage);
    updateScene(myScene);
    setPosition();
    showStage();
}

/* 

Java FX View setup:
Create root node and branches that is ready for placing in a Scene.

Sets up workspace stage with 2 subgroups for vertical separation:
(a) menu bar
(b) sprite display area, which is inside a border pane and group for layout reasons.

This method does not update content of the Sprite-display GUI node.

*/

private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        BorderPane myBP = new BorderPane(); //holds the menubar, spritegroup
        Group menubarGroup = new Group(); //subgroup
        MenuBar myMenu = getMenuBar();
        menubarGroup.getChildren().addAll(myMenu);
        
        //the Pane holding the group allows movement of SpriteBoxes independently, without relative movement
        
        Pane workspacePane = new Pane(); //to hold a group, holding a spritegroup
        Group displayAreaGroup = new Group(); //subgroup of Pane; where Sprites located
        
        workspacePane.getChildren().addAll(displayAreaGroup);
        setSpritePane(workspacePane); //store for later use
        setSpriteGroup(displayAreaGroup); //store for later use

        myBP.setTop(menubarGroup);
        myBP.setMargin(workspacePane, new Insets(50,50,50,50));
        myBP.setCenter(workspacePane);
        //workspacePane.setPadding(new Insets(150,150,150,150));
        
        //add the Border Pane and branches to root Group 
        myGroup_root.getChildren().addAll(myBP);
        //store the root node for future use
        setSceneRoot(myGroup_root); //store 
        //for box placement within the Scene - attach them to the correct Node.
        return myGroup_root;  
    }

private Scene makeWorkspaceScene(Group myGroup) {
        
        //construct scene with its root node
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), Color.BEIGE);
        
        //nb do not change focus unless click on sprite group
        //Nodes etc inherit Event Target so you can check it in the event chain.
        
        //filter for capture, handler for sorting through the bubbling
        workspaceScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent mouseEvent) {
             System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
             System.out.println("Workspace is "+StageManager.this.toString());
             System.out.println("Here is the target: "+mouseEvent.getTarget());
             System.out.println("Target class: "+mouseEvent.getTarget().getClass());
             if (getSceneGUI()!=getSceneLocal()) {
                  System.out.println("Problem with storing Scene");
             }
            if (mouseEvent.getTarget()==getSceneGUI()) {
                System.out.println("Clicked on scene; updated focus");
                currentFocus=StageManager.this;
            }
            if (mouseEvent.getTarget() instanceof BorderPane) {
                 System.out.println("Clicked on Border Pane ; updated focus");
                 currentFocus=StageManager.this;
            }

             //if source = ... only then change focus 
            }
        });

        workspaceScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent keyEvent) {
             System.out.println("Key pressed on workspace stage " + keyEvent.getSource());
             //if source = ... only then change focus 
            }
        });
        
        return workspaceScene;
    }

//SPRITE BOX ASSIST FUNCTIONS

/* public function to add a box (as a child node) to this Viewer.
It requires adding the contents of the box as a child node.
The sprite object is added through the normal addspritetostage function
This tackles the addition of a new child node as a single GUI step, rather than adding a node and updating the whole view with child nodes.
*/
public void addNewSpriteToStage(SpriteBox mySprite) {
        addChildBoxToDisplayNode(mySprite); //data
        addSpriteToStage(mySprite); //view
    }

/*
Internal method to add sprite to the Group/Pane of this Node Viewer 
This is to add an existing GUI 'box/node' to the Child Node section of this Viewer.
i.e. this adds a specific object, rather than updating the view from whole underlying data set.
*/

private void addSpriteToStage(SpriteBox mySprite) {
    getSpriteGroup().getChildren().add(mySprite);  
    System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    positionSpriteOnStage(mySprite);
    setFocusBox(mySprite); //local information
    mySprite.setStageLocation(StageManager.this); //give Sprite the object for use later.
}

//Method to add child node based on the contents of an identified NodeBox in GUI.
private void addChildBoxToDisplayNode(SpriteBox mySprite) {
    getDisplayNode().addChildNode(mySprite.getBoxNode());
}

//public method to allow Main controller to initiate child node creation in viewer

public void selectedAsChildNode() {
    String sampleText = getSelectedInputText();
    //construct new node using available inputs (i.e. suitable constructor)
    NodeCategory NC_clause = new NodeCategory ("clause",0,"blue"); //mirror main
    ClauseContainer myNode = new ClauseContainer(NC_clause,sampleText);
    addChildNodeToDisplayNode(myNode);
    updateOpenNodeView(); //update the viewer (independently of other update calls)
}

public void newNodeAsChildNode(ClauseContainer myNode) {
    addChildNodeToDisplayNode(myNode); //data
    updateOpenNodeView(); //view
}

//method to box up node as shape and add to GUI in node viewer
private void addNodeToView (ClauseContainer myNode) {
    SpriteBox b = makeBoxWithNode(myNode); //relies on Main, event handlers x
    addSpriteToStage(b); //differs from Main 
    setFocusBox(b); 
}

public void newNodeForWorkspace(ClauseContainer myNode) {
    addChildNodeToDisplayNode(myNode); //data
    addNodeToView(myNode); //view
}

//Method to add child node to the open node in this view

private void addChildNodeToDisplayNode(ClauseContainer myChildNode) {
        getDisplayNode().addChildNode(myChildNode);
}

public void removeSpriteFromStage(SpriteBox thisSprite) {
    this.spriteGroup.getChildren().remove(thisSprite); //view/GUI
    thisSprite.resetLocation();
     //TO DO: remove Node (data)
}

public void setContentsArray(ArrayList<Object> inputObject) {
    this.BoxContentsArray = inputObject;
}

public ArrayList<Object> getContentsArray() {
    return this.BoxContentsArray;
}

public void positionSpriteOnStage(SpriteBox mySprite) {
        
        if (mySprite!=null) {  //might be no current sprite if not dbl clicked
                mySprite.endAlert();
        }
        advanceSpritePosition();
        mySprite.setTranslateX(spriteX);
        mySprite.setTranslateY(spriteY); 
        mySprite.setStageLocation(StageManager.this); //needed if stage is not o/w tracked
        if (mySprite.getStageLocation()!=StageManager.this) {
            System.out.println("Problem with adding sprite:"+mySprite.toString());
        }
        else {
            System.out.println("Positioned sprite at:"+mySprite.toString()+" ("+spriteX+","+spriteY+")");
        }
}

public void resetSpriteOrigin() {
    this.spriteY=0;
    this.spriteX=0;
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
private void advanceSpritePosition() {
        if (this.spriteX>440) {
                this.spriteY=spriteY+65;
                this.spriteX=0;
            }
            else {
                spriteX = spriteX+160;
            }
}

public void setFocusBox(SpriteBox myBox) {
    this.focusbox = myBox;
}

public SpriteBox getFocusBox() {
    return this.focusbox;
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}


public double getBigY() {
    return this.myBigY;
}

}
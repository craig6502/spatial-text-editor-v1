//ArrayList etc
import java.util.*;
//For serialization IO 
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class NodeConfig extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -647978698708496L;

//each object contains default list
NodeCategory NC_World = new NodeCategory("World",0,"darkgrey");
NodeCategory NC_notes = new NodeCategory("notes",0,"khaki");
NodeCategory NC_footnotes = new NodeCategory ("footnotes",0,"khaki");
NodeCategory NC_clause = new NodeCategory ("clause",0,"blue");
NodeCategory NC_def = new NodeCategory ("definition",0,"green");
NodeCategory NC_Memory = new NodeCategory ("memory",0,"lightblue");
NodeCategory NC_testimony = new NodeCategory ("testimony",0,"lightblue");
NodeCategory NC_witness = new NodeCategory ("witness",0,"lightblue");
NodeCategory NC_fact = new NodeCategory ("fact",0,"lightblue");
NodeCategory NC_event = new NodeCategory ("event",0,"lightblue");
//NodeCategory NC_library = new NodeCategory ("library",1,"lemon");
NodeCategory NC_document = new NodeCategory ("document",1,"darkblue");
NodeCategory NC_law = new NodeCategory ("law",0,"darkgold");
//NodeCategory NC_collection = new NodeCategory ("collection",2,"orange");
//NodeCategory NC_project = new NodeCategory ("project",3,"white");
NodeCategory NC_Alien = new NodeCategory("Alien",0,"khaki");

private void initialiseNodeCategories() {
        
    }

public ArrayList<NodeCategory> getDefaultNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_World));
}

/* The following code initialises the NodeCategories. 
These can be saved with world view (so doc count is maintained).
It may be possible to add these in a child node to Worldview at some point,
swapping the main node class (ClauseContainer) for this.
*/

public ArrayList<NodeCategory> getLawNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_notes,NC_footnotes,NC_clause,NC_def,NC_law,NC_fact,NC_Memory,NC_event,NC_witness,NC_testimony));

}

public ArrayList<NodeCategory> getMerchantNodes() {

 
return new ArrayList<NodeCategory>(Arrays.asList(NC_Alien,NC_footnotes,NC_clause,NC_fact,NC_event));

}

public NodeConfig() {}


}
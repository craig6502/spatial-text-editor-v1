
//import utilities needed for Arrays lists etc
import java.util.*;

/*This is a class for a single definition object, not a collection.
Implements serializable for save functions.
Order of encapsulation is BoxContainer-->SpriteBox-->Clause
*/

public class Clause implements implements java.io.Serializable {
//setup declare instance variables. shared in class if preceded by static.	
String label=""; //will hold the GUI box term (initially same as def'n)
String heading="";
String interpretation="";
String clausetext=""; //will hold the definition text
ArrayList<String> Definitions = new ArrayList<String>(); //Currently: list of most freq definition words, not Def objects
ArrayList<String> Keywords = new ArrayList<String>();
String Category = "";
int IndexValue=0; //index for positioning in documents
int frequency = 0;  //frequency of use in text
//TO DO: financial terms.  These may need to be extensions of Clause?
//TO DO: terms, time periods

//empty constructor no arguments
public Clause() {

}

//Clause with properties set at construction
public Clause (String myLabel, String myHeading, String myText, String myCategory) {
	this.label = myLabel;
	this.heading=myHeading;
	this.clausetext=myText;
	this.Category=myCategory;
}

//Category
public void setCategory(String mytext) {
	this.Category=mytext;
}

public String getCategory() {
	return this.Category;
}

//Main clause text for this clause object

public void setClausetext(String mytext) {
	this.clausetext=mytext;
}

public String getClause() {
	return this.clausetext;
}

//Add the (external use) label to this clause object

public void setClauselabel(String mytext) {
	this.label=mytext;
}

public String getLabel() {
	return this.label;
}

//heading for this clause object

public void setHeading(String mytext) {
	this.heading=mytext;
}

public String getHeading() {
	return this.heading;
}

public void setClauseinterp(String mytext) {
	this.interpretation=mytext;
}

//freq count

public void setFreq(int myFreq) {
	this.frequency = myFreq;
}

public int getFreq() {
	return this.frequency;
}

public void incFreq() {
	this.frequency++;
}

}
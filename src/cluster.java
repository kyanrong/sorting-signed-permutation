import java.util.*;
import java.lang.*;

class cluster{
	private  ArrayList<String> children = new ArrayList<String>();
	private  int numSpecies;
	private  int index;
	private CNode node;

	public cluster(int speciesIndex, ArrayList<String> list,CNode newNode){
		index = speciesIndex;
		children = list;
		numSpecies = children.size();
		node = newNode;
	}

	public ArrayList<String> getChildren(){

			return children;
	
	}

	public int getNumSpecies(){
		return children.size();
	}

	public int getIndex(){
		return index;
	}

	public CNode getNode(){
		return node;
	}

}
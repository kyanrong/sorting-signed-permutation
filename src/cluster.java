import java.util.*;
import java.lang.*;

class cluster{
	private static ArrayList<String> children = new ArrayList<String>();
	private static int numSpecies;
	private static int index;

	public cluster(int speciesIndex, ArrayList<String> list){
		index = speciesIndex;
		children = list;
		numSpecies = children.size();
	}

	public ArrayList<String> getChildren(){
		return children;
	}

	public int getNumSpecies(){
		return numSpecies;
	}

	public int getIndex(){
		return index;
	}

}
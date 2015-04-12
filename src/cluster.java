import java.util.*;
import java.lang.*;

class cluster{
	private static ArrayList<String> species = new ArrayList<String>();
	private static int numSpecies;
	private static int index;

	public cluster(String speciesIndex, ArrayList<String> list,){
		index = speciesName;
		species = list;
		numSpecies = species.size();
	}

	public ArrayList<String> getSpecies(){
		return species;
	}

	public int getNumSpecies(){
		return numSpecies;
	}

	public int getIndex(){
		return index;
	}

}
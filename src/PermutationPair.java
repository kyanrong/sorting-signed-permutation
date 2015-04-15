import java.util.ArrayList;

// storing the pi[] and sigma[] of a permutation 

public class PermutationPair {
	String name;
	ArrayList<Integer> pi;
	ArrayList<Boolean> sigma;
	
	public PermutationPair(ArrayList<Integer> unsigned, ArrayList<Boolean> signs, String n) {
		pi = unsigned;
		sigma = signs;
		name = n;
	}
	
	public ArrayList<Integer> getPiArr() {
		return pi;
	}
	
	public ArrayList<Boolean> getSigmaArr() {
		return sigma;
	} 
	
	public String getName() {
		return name;
	}
}

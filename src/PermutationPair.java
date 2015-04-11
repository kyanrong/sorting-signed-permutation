import java.util.ArrayList;

// storing the pi[] and sigma[] of a permutation 

public class PermutationPair {
	ArrayList<Integer> pi;
	ArrayList<Boolean> sigma;
	
	public PermutationPair(ArrayList<Integer> unsigned, ArrayList<Boolean> signs) {
		pi = unsigned;
		sigma = signs;
	}
	
	public ArrayList<Integer> getPiArr() {
		return pi;
	}
	
	public ArrayList<Boolean> getSigmaArr() {
		return sigma;
	} 
}

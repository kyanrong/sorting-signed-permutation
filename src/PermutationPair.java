// storing the pi[] and sigma[] of a permutation 

public class PermutationPair {
	int[] pi;
	String[] sigma;
	
	public PermutationPair(int[] unsigned, String[] signs) {
		pi = unsigned;
		sigma = signs;
	}
	
	public int[] getPiArr() {
		return pi;
	}
	
	public String[] getSigmaArr() {
		return sigma;
	} 
}

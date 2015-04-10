import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class P2 {
	int seq_len;
	ArrayList<PermutationPair> permutations;			// all the permutations in the file

	public P2() throws IOException {
		permutations = new ArrayList<PermutationPair>();
		Scanner sc = new Scanner(System.in);
		
		System.out.println("********************* P2 *********************");
		System.out.println("Please input file path: ");
		
		String path = "datasets/viral_genome.txt";
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		while((line=br.readLine())!= null) {
			if(!line.startsWith(">")) {
				String[] seq = line.split(", ");
				seq_len = seq.length+2;				// +2 to add 0 and n+1 to the seq
				PermutationPair pp = separate(seq);
				permutations.add(pp);
			}
		}
	}
	
	// separate into unsigned elements and their signs
	private PermutationPair separate(String[] seq) {
		int[] pi = new int[seq_len];
		String[] sigma = new String[seq_len];
		
		for(int i=0; i<seq.length; i++) {
			int num = Integer.parseInt(seq[i]);
			if(num > 0) {
				pi[i+1] = num;					
				sigma[i+1] = "+";
			}
			else {
				pi[i+1] = num * -1;
				sigma[i+1] = "-";
			}
		}
			
		pi[0] = 0; sigma[0] = "+";
		pi[pi.length-1] = pi.length-1; sigma[sigma.length-1] = "+";
			
		return new PermutationPair(pi, sigma);
	}
}

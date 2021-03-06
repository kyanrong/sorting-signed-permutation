import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class P2NoPrint {
	int seq_len;
	static ArrayList<PermutationPair> permutations;			// all the permutations in the file
	static int[][] dist;			
	Mapper mapper;
	ArrayList<String> speciesNames;
	
	public P2NoPrint() throws IOException {
		System.out.println("********************* P2 *********************");
		System.out.println("Please input file path: ");
		
		permutations = new ArrayList<PermutationPair>();
		getInput();
		dist = new int[permutations.size()][permutations.size()];
		
		for(int i=0; i<permutations.size(); i++) {
			dist[i][i] = 0;
		}
		
		for(int i=0; i<permutations.size(); i++) {
			for(int j=i+1; j<permutations.size(); j++) {
				mapper = new Mapper(permutations.get(i).getPiArr(), permutations.get(j).getPiArr(), permutations.get(i).getSigmaArr(), permutations.get(j).getSigmaArr());
				ArrayList<Integer> newUnsigned = mapper.getNewStartUnsigned();
				ArrayList<Boolean> newSign = mapper.getNewStartSign();
				PermutationPair newP = new PermutationPair(newUnsigned, newSign, permutations.get(i).getName());
				int score = findScore(newP);
				dist[i][j] = score;
				dist[j][i] = score;
			}
		}	
		
		
	}
	
	public static int[][] getMatrix(){
		return dist;
	}

	public static ArrayList<String> getNames(){
		ArrayList<String> name = new ArrayList<String>();
		for(int i=0;i<permutations.size();i++){
			name.add(permutations.get(i).getName());
		}
		return name;
	}

	private void getInput() throws IOException {
		String path = "viral_genome.txt";
		
		SeqParser sp = new SeqParser(path);
		
		SeqExtractor se = new SeqExtractor(sp.getSequence());
		
		speciesNames = sp.getSpecies();
		
		for(int i=0; i<speciesNames.size(); i++) {

			ArrayList<Integer> pi = se.getUnsigned().get(i);
			ArrayList<Boolean> sigma = se.getSign().get(i);
			seq_len = pi.size();
			permutations.add(new PermutationPair(pi, sigma, speciesNames.get(i)));
		}
	}
	
	// d(P) = n - c + t
	private int findScore(PermutationPair pp) {
		ComponentPair cp = findComponents(pp.getPiArr(), pp.getSigmaArr());
		Tree subtree = constructTree(cp, seq_len);
		int t = findCoverCost(subtree);
		Node[][] intervals = findIntervals(pp);
		int c = findCyclesCount(intervals);
		int score = seq_len-1 - c + t;
		//System.out.println(seq_len + "-" + c + "-" + t);
		return score;
	}
	
	// pi contains the unsigned elements
	// sigma contains the signs
	private ComponentPair findComponents(ArrayList<Integer> pi, ArrayList<Boolean> sigma) {
		// for each index i, at most one component can start at pos i and at most one component can end at pos i	
		ArrayList<Component> c_start = new ArrayList<Component>();
		ArrayList<Component> c_end = new ArrayList<Component>();
		
		for(int i=0; i<pi.size(); i++) {
			c_start.add(null);
			c_end.add(null);
		}
		
		Stack<Integer> M1 = new Stack<Integer>();
		Stack<Integer> M2 = new Stack<Integer>();
		Stack<Integer> S1 = new Stack<Integer>();
		Stack<Integer> S2 = new Stack<Integer>();
		int[] M = new int[pi.size()];
		int[] m = new int[pi.size()];
		
		// initialization
		M1.push(pi.size());
		M2.push(0); 
		S1.push(0);
		S2.push(0);
		M[0] = pi.size();
		m[0] = 0;
		
		// Compute M[i]: the nearest element of pi that precedes pi(i) and is greater than pi(i)
		for(int i=1; i<pi.size(); i++) {
			if(pi.get(i-1) > pi.get(i)) {
				M1.push(pi.get(i-1));
			}
			// Pop from M1 all entries that are smaller than pi[i]
			else {
				while(M1.peek() < pi.get(i)) {
					M1.pop();
				}
			}
			M[i] = M1.peek();
			
			// Find direct components
			int s = S1.peek();
			while(pi.get(s)>pi.get(i) || M[s]<pi.get(i)) {
				S1.pop();
				s = S1.peek();
			}
			if(sigma.get(i)==true && M[i]==M[s] && i-s==pi.get(i)-pi.get(s)) {
				boolean oriented = isOriented(s, i, pi, sigma);
				c_start.set(s, new Component(s, i, oriented));
				c_end.set(i, new Component(s, i, oriented));
			}
			
			// Compute m[i]: the nearest element of pi that precedes pi(i) and is smaller than pi(i)
			if(pi.get(i-1) < pi.get(i)) {
				M2.push(pi.get(i-1));
			}
			else {
				while(M2.peek() > pi.get(i)) {
					M2.pop();
				}
			}
			m[i] = M2.peek();
			
			// Find reversed components
			int t = S2.peek();
			while((pi.get(t)<pi.get(i)||m[t]>pi.get(i)) && t>0) {
				S2.pop();
				t = S2.peek();
			}
			if(sigma.get(i)==false && m[i]==m[t] && i-t==pi.get(t)-pi.get(i)) {
				boolean oriented = isOriented(t, i, pi, sigma);
				c_start.set(t, new Component(t, i, oriented));
				c_end.set(i, new Component(t, i, oriented));
			}
			
			// Update stacks
			if(sigma.get(i)==true) {
				S1.push(i);
			}
			else {
				S2.push(i);
			}
		}
		
		/*System.out.println(M1.size());
		for(int j=M1.size()-1; j>-1; j--) {
			System.out.println(M1.get(j));
		}*/
		
		
		/*System.out.println("c_start size = " + c_start.size());
		for(int i=0; i<c_start.size(); i++) {
			if(c_start.get(i) != null)
				System.out.println(i + ", component: " + c_start.get(i).getStart() + "," + c_start.get(i).getEnd() + ", oriented: " + c_start.get(i).getOrientation());
			else 
				System.out.println(i + ", null");
		}
		System.out.println();
		
		System.out.println("c_end size = " + c_end.size());
		for(int i=0; i<c_end.size(); i++) {
			if(c_end.get(i) != null)	
				System.out.println(i + ", component: " + c_end.get(i).getStart() + "," + c_end.get(i).getEnd()+ ", oriented: " + c_end.get(i).getOrientation());
			else 
				System.out.println(i + ", null");
		}
		System.out.println();*/
		
		ComponentPair pair = new ComponentPair(c_start, c_end);
		
		return pair;
	}
	
	private Tree constructTree(ComponentPair pair, int n) {
		ArrayList<Component> c_s = pair.getCStart();
		ArrayList<Component> c_e = pair.getCEnd();
		
		Node root = new Node("square");		// root
		Node p = new Node("round", c_s.get(0));
		Node q = root;
		
		root.addChild(p);
		p.setParent(root);
		
		for(int i=1; i<n-1; i++) {
			if(c_s.get(i) != null) {			// if there is a component starting at pos i
				if(c_e.get(i) == null) {		// if there is no component starting at pos i
					q = new Node("square");
					p.addChild(q);
					q.setParent(p);
				}
				p = new Node("round", c_s.get(i));
				q.addChild(p);
				p.setParent(q);
			}		
			else if(c_e.get(i) != null) {		// there is a component ending at pos i
				p = q.getParent();
				q = p.getParent();
			}		
		}
		
		Tree tree = new Tree(root);
		
		//System.out.println("Tree size = " + tree.getTreeSize());
		//tree.printTree(root);
		
		Tree subtree = generateSubtree(tree);
		return subtree;
	}
	
	
	// Generate T': the smallest subtree of T that contains all unoriented components of P
	// obtained by recursively removing from T all dangling oriented components and square nodes (post-order traversal)
	// All leaves of T' will be unoriented components, while internal round nodes may still represent oriented components
	private Tree generateSubtree(Tree t) {
		//System.out.println("Generating subtree ...");
		Node root = t.getRoot();
		remove(root);
		Tree subtree = new Tree(root);
		//cleanup(root);
		
		//System.out.println("Subtree size = " + subtree.getTreeSize());
		//subtree.printTree(root);
		
		return subtree;		
	}
	
	private boolean remove(Node n) {
		/*System.out.println(n.getType() + " .Its children are:");
		for(int i=0; i<n.getChildrenSize(); i++) {
			if(n.getChildren().get(i).getComponent() == null) {
				System.out.print("	square ");
			}
			else {
				System.out.println("	" + n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd());
			}
		}*/
		for(int i=n.getChildrenSize()-1; i>=0; i--) {
			/*if(n.getChildren().get(i).getComponent() == null) {
				System.out.println("Calling remove on square");
			}
			else {
				System.out.println("Calling remove on " + n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd());
			}*/
			boolean valid = remove(n.getChildren().get(i));
			if(!valid) {
				/*if(n.getChildren().get(i).getComponent() == null) {
					System.out.println("Removing square");
				}
				else {
					System.out.println("Removing " + n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd());
				}*/
				n.getChildren().remove(i);
			}
		}
		
		if(!n.hasChild()) {
			if(n.getType().equals("square") || n.getComponent().getOrientation()==true) {
				return false;
			}
			else {
				return true;
			}
		}
		return true;
	}
	
	
	// Find out if a component is oriented
	// unoriented component: has one or more breakpoints, and (p,q) have the same sign
	// oriented component: otherwise 
	private boolean isOriented(int start, int end, ArrayList<Integer> pi, ArrayList<Boolean> sigma) {
		if(hasBreakpoints(start, end, pi, sigma) && !hasDifferentSigns(start, end, sigma)) {
			return false;
		}
		return true;
	}
	
	private boolean hasBreakpoints(int start, int end, ArrayList<Integer> pi, ArrayList<Boolean> sigma) {
		for(int i=start; i<end-1; i++) {
			String ss = sigma.get(i) ? "+" : "-";
			String es = sigma.get(i+1) ? "+" : "-";
			int s = Integer.parseInt(ss+pi.get(i));
			int e = Integer.parseInt(es+pi.get(i+1));
			if(e < s) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasDifferentSigns(int start, int end, ArrayList<Boolean> sigma) {
		for(int i=start; i<end-1; i++) {
			if(sigma.get(i) != (sigma.get(i+1))) {
				return true;
			}
		}
		return false;
	}
	
	// Calculated using Theorem 3, page 395 of the paper
	private int findCoverCost(Tree tree) {
		int leavesCount = tree.getLeavesCount();

		if(leavesCount == 0) {
			return 0;
		}
		
		if(!tree.hasShortBranch()) {
			return leavesCount+1;
		}
		else {
			return leavesCount;
		}
	}
	
	// right point of k if k is +ve, otherwise its left point
	// left point of k+1 if k+1 is +ve, else its right point
	private Node[][] findIntervals(PermutationPair pp) {
		ArrayList<Integer> pi = pp.getPiArr();
		ArrayList<Boolean> sigma = pp.getSigmaArr();
		Node[][] intervals = new Node[2][pi.size()-1];
		Node n1, n2;
		
		// for checking
		/*int[][] arr = new int[pi.size()-1][pi.size()-1]; 
		for(int i=0; i<pi.size()-1; i++) {
			int idx1 = pi.indexOf(i);
			int idx2 = pi.indexOf(i+1);
				
			if(sigma.get(idx1)) {
				arr[i][idx1] = 1;
			}
			else {
				arr[i][idx1-1] = 1;
			}
				
			if(sigma.get(idx2)) {
				arr[i][idx2-1] = 1;
			}
			else {
				arr[i][idx2] = 1;
			}
		}
		
			for(int i=0; i<pi.size()-1; i++) {
				for(int j=0; j<pi.size()-1; j++) {
					System.out.print(arr[i][j] + ", ");
				}
				System.out.println();
			}
			System.out.println();*/
		// end checking
		
		for(int i=0; i<pi.size()-1; i++) {
			int idx1 = pi.indexOf(i);
			int idx2 = pi.indexOf(i+1);
			
			// idx1
			if(sigma.get(idx1)) {
				if(intervals[0][idx1] == null) {
					intervals[0][idx1] = new Node();
					n1 = intervals[0][idx1];
				}
				else {
					intervals[1][idx1] = new Node();
					intervals[1][idx1].setColSibling(intervals[0][idx1]);
					intervals[0][idx1].setColSibling(intervals[1][idx1]);
					n1 = intervals[1][idx1];
				}
			}
			else {
				if(intervals[0][idx1-1] == null) {
					intervals[0][idx1-1] = new Node();
					n1 = intervals[0][idx1-1];
				}
				else {
					intervals[1][idx1-1] = new Node();
					intervals[1][idx1-1].setColSibling(intervals[0][idx1-1]);
					intervals[0][idx1-1].setColSibling(intervals[1][idx1-1]);
					n1 = intervals[1][idx1-1];
				}
			}
			
			// idx2
			if(sigma.get(idx2)) {
				if(intervals[0][idx2-1] == null) {
					intervals[0][idx2-1] = new Node();
					n2 = intervals[0][idx2-1];
				}
				else {
					intervals[1][idx2-1] = new Node();
					intervals[1][idx2-1].setColSibling(intervals[0][idx2-1]);
					intervals[0][idx2-1].setColSibling(intervals[1][idx2-1]);
					n2 = intervals[1][idx2-1];
				}
			}
			else {
				if(intervals[0][idx2] == null) {
					intervals[0][idx2] = new Node();
					n2 = intervals[0][idx2];
				}
				else {
					intervals[1][idx2] = new Node();
					intervals[1][idx2].setColSibling(intervals[0][idx2]);
					intervals[0][idx2].setColSibling(intervals[1][idx2]);
					n2 = intervals[1][idx2];
				}
			}
			
			n1.setRowSibling(n2);
			n2.setRowSibling(n1);
			
		}
		
		return intervals;
	}

	
	private int findCyclesCount(Node[][] intervals) {
		int colLen = intervals[0].length;
		int count = 0;
		Node curr = null;
		
		for(int i=0; i<colLen; i++) {
			if(!intervals[0][i].getVisited()) {
				curr = intervals[0][i];
				curr.setVisited(true);
					
				// while not completed a cycle
				while(!curr.getRowSibling().getVisited() || !curr.getColSibling().getVisited()) {
					if(!curr.getRowSibling().getVisited()) {
						curr.setVisited(true);
						curr = curr.getRowSibling();
					}
					else {
						curr.setVisited(true);
						curr = curr.getColSibling();
					}
				}
				curr.setVisited(true);
				count++;	
			}
		}
		return count;
		
	}
}
	
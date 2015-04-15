import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class P2 {
	int seq_len;
	ArrayList<PermutationPair> permutations;			// all the permutations in the file
	ArrayList<ComponentPair> componentPairs;
	ArrayList<Tree> subtrees;
	int[][] dist;			

	public P2() throws IOException {
		System.out.println("********************* P2 *********************");
		System.out.println("Please input file path: ");
		
		permutations = new ArrayList<PermutationPair>();
		componentPairs = new ArrayList<ComponentPair>();
		subtrees = new ArrayList<Tree>();

		getInput();
		//dist = new int[permutations.size()][permutations.size()];
		getComponents();
		getSubtrees();
		getCoverCosts();
	}
	
	private void getInput() throws IOException {
		Scanner sc = new Scanner(System.in);
		//String path = sc.nextLine();
		String path = "datasets/eg2.txt";
		
		
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		String name = null;
		while((line=br.readLine())!= null) {
			if(line.startsWith(">")) {
				name = line.split("> ")[1];
			}
			else {
				String[] seq = line.split(", ");
				seq_len = seq.length+2;				// +2 to add 0 and n+1 to the seq
				PermutationPair pp = separate(seq, name);
				permutations.add(pp);
			}
		}
		
		br.close();
		sc.close();
	}
	
	private void getComponents() {
		for(int i=0; i<permutations.size(); i++) {
			ComponentPair cp = findComponents(permutations.get(i).getPiArr(), permutations.get(i).getSigmaArr());
			componentPairs.add(cp);
		}
	}
	
	private void getSubtrees() {
		for(int i=0; i<componentPairs.size(); i++) {
			Tree t = constructTree(componentPairs.get(i), seq_len);
			subtrees.add(t);
		}
	}
	
	private void getCoverCosts() {
		for(int i=0; i<permutations.size(); i++) {
			int cost = findCoverCost(subtrees.get(i));
			System.out.println(cost);
		}
	}
	
	// separate into unsigned elements and their signs
	private PermutationPair separate(String[] seq, String name) {
		ArrayList<Integer> pi = new ArrayList<Integer>();
		ArrayList<Boolean> sigma = new ArrayList<Boolean>();
		
		pi.add(0); sigma.add(true);
		
		for(int i=0; i<seq.length; i++) {
			int num = Integer.parseInt(seq[i]);
			if(num > 0) {
				pi.add(num);					
				sigma.add(true);
			}
			else {
				pi.add(num*-1);
				sigma.add(false);
			}
		}
				
		pi.add(seq_len-1); sigma.add(true);
		
		return new PermutationPair(pi, sigma, name);
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
		
		System.out.println("Tree size = " + tree.getTreeSize());
		tree.printTree(root);
		
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
		
		System.out.println("Subtree size = " + subtree.getTreeSize());
		subtree.printTree(root);
		
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
		
		if(!tree.hasShortBranch()) {
			return leavesCount+1;
		}
		else {
			return leavesCount;
		}
	}

}
	
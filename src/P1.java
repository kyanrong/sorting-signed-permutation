import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class P1 {
	int seq_len;
	ComponentPair p1, p2;
	ArrayList<PermutationPair> permutations;			// all the permutations in the file
	
	public P1() {
		permutations = new ArrayList<PermutationPair>(); 
		
		System.out.println("********************* P1 *********************");
		System.out.println("Please input 2 sequences: ");
		getInput();
		
		p1 = findComponents(permutations.get(0).getPiArr(), permutations.get(0).getSigmaArr());
		p2 = findComponents(permutations.get(1).getPiArr(), permutations.get(1).getSigmaArr());
		
		constructTree(p1, seq_len);
		//constructTree(p2, seq_len);
	}
	
	private void getInput() {
		Scanner sc = new Scanner(System.in);
		int i = 4;
		while(i != 0) {
			String line = sc.nextLine();
			if(!line.startsWith(">")) {
				String[] seq = line.split(", ");
				seq_len = seq.length+2;				// +2 to add 0 and n+1 to the seq
				PermutationPair pp = separate(seq);
				permutations.add(pp);
			}
			i--;
		}
		sc.close();
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
	
	// pi contains the unsigned elements
	// sigma contains the signs
	private ComponentPair findComponents(int[] pi, String[] sigma) {
		// for each index i, at most one component can start at pos i and at most one component can end at pos i	
		Component[] c_start = new Component[pi.length];
		Component[] c_end = new Component[pi.length];
		
		Stack<Integer> M1 = new Stack<Integer>();
		Stack<Integer> M2 = new Stack<Integer>();
		Stack<Integer> S1 = new Stack<Integer>();
		Stack<Integer> S2 = new Stack<Integer>();
		int[] M = new int[pi.length];
		int[] m = new int[pi.length];
		
		// initialization
		M1.push(pi.length);
		M2.push(0); 
		S1.push(0);
		S2.push(0);
		M[0] = pi.length;
		m[0] = 0;
		
		// Compute M[i]: the nearest element of pi that precedes pi(i) and is greater than pi(i)
		for(int i=1; i<pi.length; i++) {
			if(pi[i-1] > pi[i]) {
				M1.push(pi[i-1]);
			}
			// Pop from M1 all entries that are smaller than pi[i]
			else {
				while(M1.peek() < pi[i]) {
					M1.pop();
				}
			}
			M[i] = M1.peek();
			
			// Find direct components
			int s = S1.peek();
			while(pi[s]>pi[i] || M[s]<pi[i]) {
				S1.pop();
				s = S1.peek();
			}
			if(sigma[i].equals("+") && M[i]==M[s] && i-s==pi[i]-pi[s]) {
				boolean oriented = isOriented(s, i, pi, sigma);
				c_start[s] = new Component(s, i, oriented);
				c_end[i] = new Component(s, i, oriented);
			}
			
			// Compute m[i]: the nearest element of pi that precedes pi(i) and is smaller than pi(i)
			if(pi[i-1] < pi[i]) {
				M2.push(pi[i-1]);
			}
			else {
				while(M2.peek() > pi[i]) {
					M2.pop();
				}
			}
			m[i] = M2.peek();
			
			// Find reversed components
			int t = S2.peek();
			while((pi[t]<pi[i]||m[t]>pi[i]) && t>0) {
				S2.pop();
				t = S2.peek();
			}
			if(sigma[i].equals("-") && m[i]==m[t] && i-t==pi[t]-pi[i]) {
				boolean oriented = isOriented(t, i, pi, sigma);
				c_start[t] = new Component(t, i, oriented);
				c_end[i] = new Component(t, i, oriented);
			}
			
			// Update stacks
			if(sigma[i].equals("+")) {
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
		
		
		System.out.println("c_start size = " + c_start.length);
		for(int i=0; i<c_start.length; i++) {
			if(c_start[i] != null)
				System.out.println(i + ", component: " + c_start[i].getStart() + "," + c_start[i].getEnd() + ", oriented: " + c_start[i].getOrientation());
			else 
				System.out.println(i + ", null");
		}
		
		System.out.println("c_end size = " + c_end.length);
		for(int i=0; i<c_end.length; i++) {
			if(c_end[i] != null)	
				System.out.println(i + ", component: " + c_end[i].getStart() + "," + c_end[i].getEnd()+ ", oriented: " + c_end[i].getOrientation());
			else 
				System.out.println(i + ", null");
		}
		System.out.println();
		
		ComponentPair pair = new ComponentPair(c_start, c_end);
		
		return pair;
	}
	
	private void constructTree(ComponentPair pair, int n) {
		Component[] c_s = pair.getCStart();
		Component[] c_e = pair.getCEnd();
		
		Node root = new Node("square");		// root
		Node p = new Node("round", c_s[0]);
		Node q = root;
		
		root.addChild(p);
		p.setParent(root);
		
		for(int i=1; i<n-1; i++) {
			if(c_s[i] != null) {			// if there is a component starting at pos i
				if(c_e[i] == null) {		// if there is no component starting at pos i
					q = new Node("square");
					p.addChild(q);
					q.setParent(p);
				}
				p = new Node("round", c_s[i]);
				q.addChild(p);
				p.setParent(q);
			}		
			else if(c_e[i] != null) {		// there is a component ending at pos i
				p = q.getParent();
				q = p.getParent();
			}		
		}
		
		Tree tree = new Tree(root);
		
		System.out.println("Tree size = " + tree.getTreeSize());
		tree.printTree(root);
		
		generateSubtree(tree);
	}
	
	// Generate T': the smallest subtree of T that contains all unoriented components of P
	// obtained by recursively removing from T all dangling oriented components and square nodes (post-order traversal)
	// All leaves of T' will be unoriented components, while internal round nodes may still represent oriented components
	private void generateSubtree(Tree t) {
		Node root = t.getRoot();
		boolean result = remove(root);
		Tree subtree = new Tree(root);

		System.out.println("Subtree size = " + subtree.getTreeSize());
		subtree.printTree(root);
		
	}
	
	private boolean remove(Node n) {
		System.out.println("Its children are:");
		for(int i=n.getChildrenSize()-1; i>=0; i--) {
			if(n.getChildren().get(i).getComponent() == null) {
				System.out.print("square ");
			}
			else {
				System.out.print(n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd() + " ");
			}
		}
		System.out.println();
		for(int i=0; i<n.getChildrenSize(); i++) {
			if(n.getChildren().get(i).getComponent() == null) {
				System.out.println("Calling remove on square");
			}
			else {
				System.out.println("Calling remove on " + n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd());
			}
			boolean valid = remove(n.getChildren().get(i));
			if(!valid) {
				if(n.getChildren().get(i).getComponent() == null) {
					System.out.println("Removing square");
				}
				else {
					System.out.println("Removing " + n.getChildren().get(i).getComponent().getStart() + "," + n.getChildren().get(i).getComponent().getEnd());
				}
				n.getChildren().remove(i);
			}
		}
		
		if(!n.hasChild()) {
			if(n.getType().equals("square") || n.getComponent().getOrientation()==true) {
				return false;
			}
		}
		else {
			return true;
		}
		
		return true;
	}
	
	// Find out if a component is oriented
	// unoriented component: has one or more breakpoints, and (p,q) have the same sign
	// oriented component: otherwise 
	private boolean isOriented(int start, int end, int[] pi, String[] sigma) {
		if(hasBreakpoints(start, end, pi, sigma) && !hasDifferentSigns(start, end, sigma)) {
			return false;
		}
		return true;
	}
	
	private boolean hasBreakpoints(int start, int end, int[] pi, String[] sigma) {
		for(int i=start; i<end-1; i++) {
			int s = Integer.parseInt(sigma[i]+pi[i]);
			int e = Integer.parseInt(sigma[i+1]+pi[i+1]);
			if(e < s) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasDifferentSigns(int start, int end, String[] sigma) {
		for(int i=start; i<end-1; i++) {
			if(!sigma[i].equals(sigma[i+1])) {
				return true;
			}
		}
		return false;
	}
	

}

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class P1 {
	int[] s1, s2;
	int seq_len;
	String[] s1_signs, s2_signs; 
	ArrayPair p1, p2;
	
	public P1() {
		System.out.println("********************* P1 *********************");
		System.out.println("Please input 2 sequences: ");
		getInput();
		
		/*for(int i=0; i<s1.length; i++) {
			System.out.println(s1[i] + ", " + s1Signs[i]);
			
		}*/
		
		p1 = findComponents(s1, s1_signs);
		p2 = findComponents(s2, s2_signs);
		constructTree(p1, seq_len);
		constructTree(p2, seq_len);
	}
	
	private void getInput() {
		Scanner sc = new Scanner(System.in);
		for(int i=0; i<4; i++) {
			String input = sc.nextLine();
			if(i == 1) {
				String[] seq = input.split(", ");
				seq_len = seq.length+2;				// +2 to add 0 and n+1 to the seq
				s1 = new int[seq_len];			
				s1_signs = new String[seq_len];
				separate(seq, s1, s1_signs);
			}
			else if(i == 3) {
				String[] seq = input.split(", ");
				s2 = new int[seq.length+2];
				s2_signs = new String[seq_len];
				separate(seq, s2, s2_signs);
			}
		}
		sc.close();
	}
	
	// separate into unsigned elements and their signs
	private void separate(String[] input, int[] s, String[] sign) {
		for(int i=0; i<input.length; i++) {
			int num = Integer.parseInt(input[i]);
			if(num >= 0) {
				s[i+1] = num;
				sign[i+1] = "+";
			}
			else {
				s[i+1] = num * -1;
				sign[i+1] = "-";
			}
		}
		
		s[0] = 0; sign[0] = "+";
		s[s.length-1] = s.length-1; sign[s.length-1] = "+";
	}
	
	// pi contains the unsigned elements
	// sigma contains the signs
	private ArrayPair findComponents(int[] pi, String[] sigma) {
		ArrayList<Component> c = new ArrayList<Component>();
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
				for(int j=M1.size()-1; j>-1; j--) {
					if(M1.get(j) < pi[i]) {
						M1.remove(j);
					}
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
				c.add(new Component(s, i));
				c_start[s] = new Component(s, i);
				c_end[i] = new Component(s, i);
			}
			
			// Compute m[i]: the nearest element of pi that precedes pi(i) and is smaller than pi(i)
			if(pi[i-1] < pi[i]) {
				M2.push(pi[i-1]);
			}
			else {
				for(int j=M2.size()-1; j>-1; j--) {
					if(M2.get(j) > pi[i]) {
						M2.remove(j);
					}
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
				c.add(new Component(t, i));
				c_start[t] = new Component(t, i);
				c_end[i] = new Component(t, i);
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
		
		/*System.out.println("Component list size = " + c.size());
		for(int i=0; i<c.size(); i++) {
			System.out.println(c.get(i).getStart() + ", " + c.get(i).getEnd());
		}*/
		
		System.out.println("c_start size = " + c_start.length);
		for(int i=0; i<c_start.length; i++) {
			if(c_start[i] != null)
				System.out.println(i + ", component: " + c_start[i].getStart() + "," + c_start[i].getEnd());
			else 
				System.out.println(i + ", null");
		}
		
		System.out.println("c_end size = " + c_end.length);
		for(int i=0; i<c_end.length; i++) {
			if(c_end[i] != null)	
				System.out.println(i + ", component: " + c_end[i].getStart() + "," + c_end[i].getEnd());
			else 
				System.out.println(i + ", null");
		}
		System.out.println();
		
		ArrayPair pair = new ArrayPair(c_start, c_end);
		
		return pair;
	}
	
	private void constructTree(ArrayPair pair, int n) {
		Component[] c_s = pair.getCStart();
		Component[] c_e = pair.getCEnd();
		
		Node root = new Node("square");		// root
		Node p = new Node("round", c_s[0]);
		Node q = root;
		
		root.addChild(p);
		
		for(int i=1; i<n-1; i++) {
			if(c_s[i] != null) {			// if there is a component starting at pos i
				if(c_e[i] == null) {		// if there is no component starting at pos i
					q = new Node("square");
					p.addChild(q);
				}
				p = new Node("round", c_s[i]);
				q.addChild(p);	
			}
			else if(c_e[i] != null) {		// there is a component ending at pos i
				q.setParent(p);
				p.setParent(q);
			}
		}
		
		Tree tree = new Tree(root);
		
		System.out.println("Tree size = " + tree.getTreeSize());
		
		/*for(int i=0; i<root.getChildrenSize(); i++) {
			System.out.println(root.getChildren().get(i));
		}*/
	}
	
	// Generate T': the smallest subtree of T that contains all unoriented components of P
	// obtained by recursively removing from T all dangling oriented components and square nodes
	// All leaves of T' will be unoriented components, while internal round nodes may still represent oriented components
	private Tree generateTreeSubset() {
		
	}
	
	// Find out if a component is oriented
	// unoriented component: has one or more breakpoints, and (p,q) have the same sign
	// oriented component: otherwise (no breakpoints, or have different sign)
	private boolean isOriented() {
		
	}
}

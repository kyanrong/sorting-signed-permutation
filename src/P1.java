import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;


public class P1 {
	int seq_len;
	ComponentPair p1, p2;
	ArrayList<PermutationPair> permutations;			// all the permutations in the file

	public P1() {
		System.out.println("********************* P1 *********************");
		System.out.println("Please input 2 sequences: ");

		permutations = new ArrayList<PermutationPair>(); 

		getInput();
		Mapper mapper = new Mapper (permutations.get(0).pi, permutations.get(1).pi, permutations.get(0).sigma, permutations.get(1).sigma);
		
		ArrayList<Boolean> sign = new ArrayList <Boolean>();
		for (int j = 0; j <= permutations.get(0).getSigmaArr().size()-1; j++){
			sign.add(permutations.get(0).getSigmaArr().get(j));
		}
		String signA;
		for (int y = 1; y<= permutations.get(0).getSigmaArr().size()-2; y++){
			if (permutations.get(0).getSigmaArr().get(y) == true){
				 signA = "";
			}else{
				signA = "-";
			}
			String value = signA + permutations.get(0).getPiArr().get(y).toString();
			System.out.print(value);
			if (y!=permutations.get(0).getSigmaArr().size()-2){
				System.out.print(", ");
			}
		}
		System.out.println();
		
	
		
		
		p1 = findComponents(permutations.get(0).getPiArr(), permutations.get(0).getSigmaArr());
		//p2 = findComponents(permutations.get(1).getPiArr(), permutations.get(1).getSigmaArr());
		/*Bergerone(permutations.get(0).getPiArr(), permutations.get(0).getSigmaArr(), permutations.get(1).getPiArr(), permutations.get(1).getSigmaArr());
		*/

		Tree subtree = constructTree(p1, seq_len);
		Node subtreeRoot = subtree.getRoot();
		ArrayList<Path> cover = findCover(subtreeRoot);
		findCoverCost(subtree);
		orientComponents(cover, permutations.get(0),  mapper);
		
		Bergerone(permutations.get(0).getPiArr(), permutations.get(0).getSigmaArr(), mapper);
		
		//constructTree(p2, seq_len);
		
	}

	private void getInput() {
		Scanner sc = new Scanner(System.in);
		int i = 4;
		String name = null;
		while(i != 0) {
			String line = sc.nextLine();
			if(line.startsWith(">")) {
				name = line.split("> ")[1];
			}
			else {
				String[] seq = line.split(", ");
				seq_len = seq.length+2;				// +2 to add 0 and n+1 to the seq
				PermutationPair pp = separate(seq, name);
				permutations.add(pp);
			}
			i--;
		}
		sc.close();
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

		//System.out.println("Tree size = " + tree.getTreeSize());
		//tree.printTree(root);

		return generateSubtree(tree);
	}

	// Generate T': the smallest subtree of T that contains all unoriented components of P
	// obtained by recursively removing from T all dangling oriented components and square nodes (post-order traversal)
	// All leaves of T' will be unoriented components, while internal round nodes may still represent oriented components
	private Tree generateSubtree(Tree t) {
		//System.out.println("Generating subtree ...");
		Node root = t.getRoot();
		boolean result = remove(root);
		Tree subtree = new Tree(root);

		//System.out.println("Subtree size = " + subtree.getTreeSize());
		//subtree.printTree(root);

		//System.out.println("Subtree leaves count = " + subtree.getLeavesCount());

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
		for(int i=0; i<n.getChildrenSize(); i++) {
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
		}
		else {
			return true;
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


	// input the root node of T'(only has all unoriented components)
	private ArrayList<Path> findCover(Node n) {
		ArrayList<Path> cover = new ArrayList<Path>();
		ArrayList<Component> list = new ArrayList<Component>();

		list = visit(n, cover, list);
		if (list.size()!= 0){
		cover.add(new Path(list.get(0)));
		}
		/*System.out.println("Cover size = " + cover.size());*/
		for(int i=0; i<cover.size(); i++) {
			/*if(cover.get(i).getEndComponent() != null) {
				System.out.println("	" + cover.get(i).getStartComponent().getStart() + "," + cover.get(i).getStartComponent().getEnd() + " to " + cover.get(i).getEndComponent().getStart() + "," + cover.get(i).getEndComponent().getEnd() );
			}*/
			/*else {
				System.out.println("	" + cover.get(i).getStartComponent().getStart()+","+cover.get(i).getStartComponent().getEnd());
			}*/
		}	

		return cover;
	}

	private ArrayList<Component> visit(Node n, ArrayList<Path> cover, ArrayList<Component> list) {
		for(int i=0; i<n.getChildrenSize(); i++) {
			visit(n.getChildren().get(i), cover, list);
		}
		// leaves are confirmed to be unoriented
		if(!n.hasChild() && !n.getVisited() && list.isEmpty()) {
			n.setVisited(true);
			list.add(n.getComponent());
		}
		else if(!n.hasChild() && !n.getVisited() && !list.isEmpty()) {
			n.setVisited(true);
			cover.add(new Path(list.get(0), n.getComponent()));
			list.clear();
		}

		return list;
	}


	// apply merge/cut on the cover to orient all components
	private void orientComponents(ArrayList<Path> cover, PermutationPair pp, Mapper mapper) {
				
		for(int i=0; i<cover.size(); i++) {
			if(cover.get(i).getSize() == 1) {
				if (cover.get(i).getStartComponent() == null){
					return;
				}
				cut(cover.get(i).getStartComponent(), pp);
			}
			else {
				merge(cover.get(i).getStartComponent(), cover.get(i).getEndComponent(), pp);
			}
			
			
			/*ArrayList<Boolean> sign = new ArrayList <Boolean>();
			for (int j = 0; j <= pp.sigma.size()-1; j++){
				sign.add(pp.sigma.get(j));
			}
			String signA;
			for (int y = 1; y<= pp.sigma.size()-2; y++){
				if (pp.sigma.get(y) == true){
					 signA = "";
				}else{
					signA = "-";
				}
				String value = signA + pp.pi.get(y).toString();
				System.out.print(value);
				if (y!=pp.sigma.size()-2){
					System.out.print(", ");
				}
			}
			System.out.println();*/
			
			/*Mapper mapper = new Mapper (pp.pi, ppB.pi, pp.sigma, ppB.sigma);*/
			ArrayList<Integer> tempPermut = mapper.getCurrentReversal(pp.getPiArr(), pp.getSigmaArr());
			for (int z=1; z<=pp.getPiArr().size()-2; z++){
				System.out.print(tempPermut.get(z));
				if(z!=pp.getPiArr().size()-2){
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}

	private void cut(Component c, PermutationPair pp) {
		
		int startidx = c.getStart();
		int endidx = c.getEnd();
		
		
		ArrayList<Integer> pi = pp.getPiArr();
		ArrayList<Boolean> sigma = pp.getSigmaArr();

		Collections.reverse(pi.subList(startidx, endidx+1));
		for(int i=startidx; i<endidx+1; i++) {
			sigma.set(i, !sigma.get(i));
		}
		Collections.reverse(sigma.subList(startidx, endidx+1));
		
	}

	// c1 and c2 are in order, so c1's startidx will always be smallest
	private void merge(Component c1, Component c2, PermutationPair pp) {

		int startidx = c1.getStart();
		int endidx = c2.getEnd();
		ArrayList<Integer> pi = pp.getPiArr();
		ArrayList<Boolean> sigma = pp.getSigmaArr();

		Collections.reverse(pi.subList(startidx, endidx+1));
		for(int i=startidx; i<endidx+1; i++) {
			sigma.set(i,  !sigma.get(i));
		}
		Collections.reverse(sigma.subList(startidx, endidx+1));
		
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

	private static int score (PermutationPair pair){
		int count = 0;
		ArrayList<Integer>pi = pair.getPiArr();
		ArrayList<Boolean>sigma = pair.getSigmaArr();

		for (int j = 0; j <sigma.size()-1; j++){
			int a = pi.indexOf(j);
			int b = pi.indexOf(j+1);
			if (sigma.get(a) != sigma.get(b)){
				count++;
			}
		}
		return count;
	}


	private static PermutationPair Bergerone(ArrayList<Integer> pi, ArrayList<Boolean> sigma, Mapper mapper2){
		
		
		
		ArrayList<Boolean> sign = new ArrayList<Boolean>();
		String name = null;
		//The given sequence
		ArrayList<Integer> ppi = new ArrayList<Integer>();
		ArrayList<Boolean> sig = new ArrayList<Boolean>();
		for (int i = 0; i<pi.size(); i++){
			ppi.add(i, i);
			sig.add(i, true);
		}
		//The identity sequence
		PermutationPair idseq = new PermutationPair (ppi, sig, name);

		int index=-1;
		//l refers to the index of the interval that gives the max score  and s refers to max score
		int l = -1; int s = 0;
		//do while loop the pi is not the identity sequence

	

		for (int j = 0; j <= sigma.size()-1; j++){
			sign.add(sigma.get(j));
		}
/*		String signA;
		for (int y = 1; y<= sigma.size()-2; y++){
			if (sigma.get(y) == true){
				 signA = "";
			}else{
				signA = "-";
			}
			String value = signA + pi.get(y).toString();
			System.out.print(value);
			if (y!=sigma.size()-2){
				System.out.print(", ");
			}
		}
		System.out.println();*/
		
		/*Mapper mapper = new Mapper (pi, piB, sigma, sigmaB);*/

		while(compare(idseq, sign, pi) != true){
			l=0;
			s=-1;

			for (int k = 0; k <pi.size()-1; k++){
				index = pi.indexOf(k);
				int indexA = pi.indexOf(k+1);

				if(sign.get(index) != sign.get(indexA) && indexA-index !=1){
					int t = score(reversal(index, pi, sign));

					if(t>s){
						l = index;
						s = t;
					}
				}
			}

			PermutationPair permutation = reversal(l,pi,sign);
			ArrayList<Integer> tempPermut = mapper2.getCurrentReversal(permutation.getPiArr(), permutation.getSigmaArr());
			for (int z=1; z<=permutation.getPiArr().size()-2; z++){
				System.out.print(tempPermut.get(z));
				if(z!=permutation.getPiArr().size()-2){
					System.out.print(", ");
				}
			}
			System.out.println();
			sign.clear();
			pi.clear();
			sign = permutation.getSigmaArr();
			pi = permutation.getPiArr();

		}


		PermutationPair permutation = new PermutationPair(pi,sign,null);

		/*//call mapper
		for (int a = 1; a <= pi.size()-3; a++){
		System.out.print(permutation.getSigmaArr().get(a).toString() +  permutation.getPiArr().get(a).toString()+", ");
		}
		System.out.println(permutation.getSigmaArr().get(pi.size()-2).toString()+ permutation.getPiArr().get(pi.size()-2).toString());*/
		return permutation;

	}

	private static boolean compare(PermutationPair idseq, ArrayList<Boolean> sign, ArrayList<Integer> pi) {
		for (int a = 0; a<=idseq.getPiArr().size()-1; a++){
			if(idseq.getPiArr().get(a) != pi.get(a)){
				return false;
			}
		}
		for (int b = 0; b <= idseq.getSigmaArr().size()-1; b++){
			if(idseq.getSigmaArr().get(b) != sign.get(b)){
				return false;
			}
		}
		return true;
	}

	private static PermutationPair reversal(int index, ArrayList<Integer> pi, ArrayList<Boolean> sign) {

		int i = pi.get(index);
		int iPlus = i+1;

		int indexPlus = pi.indexOf(iPlus);

		boolean iSign = sign.get(index);
		boolean iSignPlus = sign.get(indexPlus);

		int startIndex =0;
		int endIndex = 0;

		if(index < indexPlus){

			if(iSign == true){
				startIndex = index+1;
			}else{
				startIndex = index;
			}

			if(iSignPlus==true){
				endIndex=indexPlus-1;
			}else{
				endIndex = indexPlus;
			}

		}else{

			if(iSignPlus == true){

				startIndex = indexPlus;
			}else{
				startIndex = indexPlus+1;
			}

			if(iSign==true){
				endIndex=index;
			}else{
				endIndex = index-1;
			}
		}

		int numElements = endIndex - startIndex + 1;
		int reversalLength=0;
		if(numElements%2 ==0){
			reversalLength = numElements/2;

		}else{
			reversalLength = (numElements/2) +1;
		}

		ArrayList<Integer> tempPi = new ArrayList<Integer>();
		tempPi.addAll(pi);
		ArrayList<Boolean> tempSign = new ArrayList<Boolean>();
		tempSign.addAll(sign);


		for(int y =0;y<reversalLength;y++){

			int a = tempPi.get(startIndex);
			int b = tempPi.get(endIndex);
			tempPi.set(startIndex,b);
			tempPi.set(endIndex,a);
			Boolean checkStart = tempSign.get(startIndex);
			Boolean checkEnd = tempSign.get(endIndex);
			if(checkStart==true){
				tempSign.set(endIndex,false);
			}else{
				tempSign.set(endIndex,true);
			}

			if(checkEnd==true){
				tempSign.set(startIndex,false);
			}else{
				tempSign.set(startIndex,true);
			}
			startIndex++;
			endIndex--;
		}

		PermutationPair X = new PermutationPair(tempPi,tempSign,"temp");

		return X;
	}
}

import java.util.ArrayList;


public class Tree {
	private Node root;
	private boolean breakRecursion = false;
	
	public Tree(Node r) {
		root = r;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public int getTreeSize() {
		int nodes = 0;
		if(root != null) {
			nodes = getTreeSubsetSize(root);
		}
		
		return nodes+1;			// including root
	}
	
	private int getTreeSubsetSize(Node n) {
		int nodes = n.getChildrenSize();
		for(Node child : n.getChildren()) {
			nodes += getTreeSubsetSize(child);
		}
		
		return nodes;
	}
	
	public void printTree(Node n) {
		if(n.getComponent()==null) {
			System.out.println(n.getType());
		}
		else {
				System.out.println(n.getComponent().getStart() + "," + n.getComponent().getEnd() + " oriented=" + n.getComponent().getOrientation());
		}
		for(Node child : n.getChildren()) {
			printTree(child);
		}
		System.out.println();
	}
	
	public int getLeavesCount() {
		return visitLeaves(root, 0);
	}
	
	private int visitLeaves(Node n, int count) {
		if(!n.getType().equals("square") && !n.hasChild()) {
			return count+1;
		}
		for(Node child : n.getChildren()) {
			count = visitLeaves(child, count);
		}
	
		return count;
	}

	public boolean hasShortBranch() {
		ArrayList<Node> list = new ArrayList<Node>();
		if(hasShortBranchAuxiliary(root, 0)==1) {
			breakRecursion = false;
			return true;
		}
		return false;
	}
	
	// count = the number of unoriented components along a branch
	private int hasShortBranchAuxiliary(Node n, int count) {
		if(n.getComponent()!=null && !n.getComponent().getOrientation()) {
			//System.out.println(count+1);
			return count+1;
		}
		if(n.getType().equals("square") && n.getDegree()>=3) {
				//System.out.println("here square");
				//System.out.println("here square count = " + count);
				if(count == 1) {
					breakRecursion = true;
				}
				else {
					return 0;
				}
			}
		for(Node child : n.getChildren()) {
			if(breakRecursion) {
				count = 1;
				return count;
			}
			count = hasShortBranchAuxiliary(child, count);
		}
		
		return count;
		
	}
}

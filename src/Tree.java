
public class Tree {
	private Node root;
	
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
			System.out.println("	" + n.getType());
		}
		else {
			System.out.println("	" + n.getComponent().getStart() + "," + n.getComponent().getEnd() + " oriented=" + n.getComponent().getOrientation());
		}
		for(Node child : n.getChildren()) {
			if(n.getComponent()==null) {
				System.out.println("Calling printTree on " + n.getType() + ". Its children are : ");
			}
			else {
				System.out.println("Calling printTree on " + n.getComponent().getStart() + "," + n.getComponent().getEnd() + " oriented=" + n.getComponent().getOrientation() + ". Its children are: ");
			}
			printTree(child);
		}
		System.out.println();
	}

}

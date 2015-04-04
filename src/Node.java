import java.util.ArrayList;
import java.util.LinkedList;


public class Node {
	private String type;		// whether this is a square or round node
	private Component c;	
	private Node parent;
	private ArrayList<Node> children;
	
	public Node(String t, Component c) {
		type = t;
		this.c = c;
		parent = null;
		children = new ArrayList<Node>();
	}
	
	public Node(String t) {
		type = t;
		c = null;
		parent = null;
		children = new ArrayList<Node>();
	}
	
	public void addChild(Node child) {
		children.add(child);
	}
	
	public void setParent(Node p) {
		parent = p;
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}
	
	public int getChildrenSize() {
		return children.size();
	}
	
	public boolean hasChild() {
		if(children.size() != 0) {
			return true;
		}
		return false;
	}
}

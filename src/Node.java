import java.util.ArrayList;
import java.util.LinkedList;


public class Node {
	private String type;		// whether this is a square or round node
	private Component c;	
	private Node parent;
	private ArrayList<Node> children;
	private boolean visited;
	
	// for finding cycles 
	private Node rowSibling;
	private Node colSibling;
	
	public Node(String t, Component c) {
		type = t;
		this.c = c;
		parent = null;
		children = new ArrayList<Node>();
		visited = false;
	}
	
	public Node(String t) {
		type = t;
		c = null;
		parent = null;
		children = new ArrayList<Node>();
		visited = false;
	}
	
	public Node() {
		rowSibling = null;
		colSibling = null;
		visited = false;
	}
	
	public String getType() {
		return type;
	}
	
	public Component getComponent() {
		return c;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	public void addChild(Node child) {
		children.add(child);
	}
	
	public void setParent(Node p) {
		parent = p;
	}
	
	public void setVisited(boolean v) {
		visited = v;
	}
	
	public Node getParent() {
		return parent;
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
	
	public int getDegree() {
		int degree = 0;
		if(getParent() != null) {
			degree += 1;
		}
		degree += getChildrenSize();
		
		return degree;
	}
	
	// for finding cycles
	public Node getRowSibling() {
		return rowSibling;
	}
	
	public Node getColSibling() {
		return colSibling;
	}
	
	public void setRowSibling(Node rs) {
		/*if(rs.equals(colSibling)) {
			rowSibling = null;
		}
		else {*/
			rowSibling = rs;
		//}
	}
	
	public void setColSibling(Node cs) {
		colSibling = cs;
	}
}

import java.util.*;
import java.lang.*;

class CNode{
	
	private CNode childLeft;
	private CNode childRight;
	private CNode parent;
	private String type;
	private String index;

	public CNode( CNode parent,String nodeType,String indexNode){
		childLeft = null;
		childRight = null;
		this.parent = parent;
		type = nodeType;
		index = indexNode;
	}
	public CNode(){

	}
	public void addChildL(CNode child){
		childLeft= child;
	}
	public void addChildR(CNode child){
		childRight= child;
	}
	public CNode getChildL(){
		return childLeft;
	}
	public CNode getChildR(){
		return childRight;
	}

	public CNode getParent(){
		return parent;
	}
	public String getType(){
		return type;
	}

	public String getIndex(){
		return index;
	}

	public void setParent(CNode newParent){
		parent = newParent;
	}


}

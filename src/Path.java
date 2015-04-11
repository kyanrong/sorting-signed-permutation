import java.util.ArrayList;

// can be a long or short path

public class Path {
	private int cost;
	private String type;
	private Component start_c, end_c;
	
	// constructor for long path
	public Path(Component s, Component e) {
		start_c = s;
		end_c = e;
		cost = 2;
		type = "long";
	}
	
	// constructor for short path
	public Path(Component s) {
		start_c = s;
		end_c = null;
		cost = 1;
		type = "short";
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getType() {
		return type;
	}
	
	public Component getStartComponent() {
		return start_c;
	}
	
	public Component getEndComponent() {
		return end_c;
	}
	
	public int getSize() {
		if(start_c!=null && end_c!=null) {
			return 2;
		}
		else {
			return 1;
		}
	}
	
}

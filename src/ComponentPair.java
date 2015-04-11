import java.util.ArrayList;

// storing the pair of Component[] (c_start and c_end)

public class ComponentPair {
	private ArrayList<Component> c_start;
	private ArrayList<Component> c_end;
	
	public ComponentPair(ArrayList<Component> s, ArrayList<Component> e) {
		c_start = s;
		c_end = e;
	}
	
	public ArrayList<Component> getCStart() {
		return c_start;
	}
	
	public ArrayList<Component> getCEnd() {
		return c_end;
	}
}

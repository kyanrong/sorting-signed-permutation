// storing the pair of Component[] (c_start and c_end)

public class ComponentPair {
	private Component[] c_start;
	private Component[] c_end;
	
	public ComponentPair(Component[] s, Component[] e) {
		c_start = s;
		c_end = e;
	}
	
	public Component[] getCStart() {
		return c_start;
	}
	
	public Component[] getCEnd() {
		return c_end;
	}
}

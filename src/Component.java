
public class Component {
	private int start;
	private int end;
	private boolean oriented;
	
	public Component(int s, int e, boolean bool) {
		start = s;
		end = e;
		oriented = bool;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public boolean getOrientation() {
		return oriented;
	}
}

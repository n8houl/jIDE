
public class TextLine {
	public String text;
	/* 0 = process, 1 = user */
	public int owner;
	private boolean touched;
	
	public static final int USER = 1;
	public static final int PROCESS = 0;
	
	public TextLine(String text, int owner) {
		this.text = text;
		this.owner = owner;
		
		touched = false;
	}
	
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
}

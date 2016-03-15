/**
 * Wrapper class for a "chunk" of lines in an .srt file.
 * Contains a line number, 2 strings, and the timestamps.
 * 
 * @author Andrew Smith
 *
 */
public class StringBlock {
	private String str1;
	private String str2;
	
	//store the timestamp start/end in milliseconds
	private int startTime;
	private int endTime;
	
	/*
	 * Constructor does not set the timestamps, as that would simply be too many arguments.
	 * User must use setTime() to use a StringBlock properly.
	 */
	public StringBlock(String s1, String s2, int start, int end) {
		str1 = s1;
		str2 = s2;
		startTime = start;
		endTime = end;
	}
	
	public String getString1() {
		return str1;
	}
	
	public String getString2() {
		return str2;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getEndTime() {
		return endTime;
	}
}

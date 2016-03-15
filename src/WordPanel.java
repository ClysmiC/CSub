import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Middle component in the program, that displays the actual subtitles.
 * Stores all of the subtitle data, and must receive a setTime(int ms) call
 * whenever the time is changed by the user.
 * 
 * @author Andrew Smith
 *
 */
@SuppressWarnings("serial")
public class WordPanel extends JPanel implements MouseMotionListener, MouseListener {
    private CSub csub;
    
    private int width;
    private int height;
    
    Scanner reader;
    
    private StringBlock [] stringList;
    
    private CaptionLabel label1, label2;
    private Font font; //the font that the caption is displayed in
    
    private int mouseX; //x and y location relative to this component when the mouse is dragging it
    private int mouseY;
    
    private static int currentLineNum = 0; //tracks current line when parsing through file

	public WordPanel (CSub c) {
		csub = c;
		
		width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
		height = csub.getHeight() - CSub.WINDOW_BUTTON_PANEL_HEIGHT - CSub.INTERFACE_PANEL_HEIGHT - 2 * CSub.RESIZE_BORDER_WIDTH;
		setPreferredSize(new Dimension(width, height));
		
		setBackground(CSub.DEFAULT_COLOR);
        
        //set layout gaps to 0 (all components resize to be snug)
        FlowLayout wpLayout = new FlowLayout(FlowLayout.LEFT);
        wpLayout.setHgap(0);
        wpLayout.setVgap(0);
        setLayout(wpLayout);
        
        //instantiate to 0
		mouseX = 0;
		mouseY = 0;
		
		addMouseMotionListener(this);
		addMouseListener(this);

		//instantiate and add the labels
		label1 = new CaptionLabel(csub, true);
		label2 = new CaptionLabel(csub, false);
		
		font = getFont().deriveFont(50f); //default font with size 50

		add(label1);
		add(label2);
		
		setCaption("", "");
		
		repaint();
	}
	
	public void setCaption(String str1, String str2) {
		if (str2.isEmpty()) { //size label 1 accordingly
			label1.setText(str1, true);
		} else {
			label1.setText(str1, false);
		}

		label2.setText(str2, false);
		
		chooseSmallerFont();
	}
	
	public void setCaption(StringBlock sb) {
		setCaption(sb.getString1(), sb.getString2());
	}
	
	public void clearCaption() {
		setCaption("", "");
	}
	
	public void setCaptionAtTime(int ms) {
		if (ms > csub.getEndTime())
		{
			clearCaption();
			return;
		}
		
		//binary search to find caption whose startTime is closest (but not over) parameter ms
		int lower = 0;
		int upper = stringList.length - 1;
		int mid = (lower + upper) / 2;
		
		StringBlock q = null;
		StringBlock result = null;
		
		//the way the binary search works, it won't ever set the first string
		//to result, so I start it off as the first string
		if (stringList[0].getStartTime() <= ms) {
			result = stringList[0];
		}
		
		while (lower <= upper)
		{
			q = stringList[mid];
			
			if (q.getStartTime() < ms) {
				result = q; //store this as being closest found caption whose startTime is < ms 
				lower = mid + 1;
			} else if (q.getStartTime() > ms) {
				upper = mid - 1;
			} else {
				result = q;
				break;
			}
			
			mid = (lower + upper)/2;
		}
		
		//make sure the time is still within the range of "result"
		if (result == null || result.getEndTime() <= ms) {
			if (!label1.isEmpty())
			clearCaption();
		}
		//only set the label if something needs to change (i.e., caption ends or new caption starts)
		else if (!(label1.getText().equals(result.getString1()) && label2.getText().equals(result.getString2()))) {
				setCaption(result);
		}
	}
	
	/**
	 * Takes in an .srt file, and instantiates and fills the stringList with the
	 * StringBlock data from the file, in order.
	 * 
	 * Makes 2 passes through the file.
	 * Pass 1: count the number of chunks in the file, instantiate the array to appropriate size
	 * Pass 2: parse each chunk individually into a StringBlock object, store in the array
	 * 
	 * @param file - the .srt file whose data is being stored in the stringList
	 */
	public void fillList (File file) throws FileNotFoundException {
		if (!file.getName().endsWith(".srt")) {
			throw new IllegalArgumentException("Only .srt files supported");
		}

		//create file reader tied to the .srt file, used JUST to count the # of lines
		try {
			reader = new Scanner(file);
		} catch(FileNotFoundException e) {
			throw e; //let it propogate up
		}

		int numOfBlocks = countNumberOfStringBlocks();
		
		//don't directly copy into stringList, as there could be errors reading file.
		//don't want to overwrite current stringList until entire file is read succesfully.
		StringBlock[] stringListBuffer = new StringBlock[numOfBlocks];		//instantiate the array of stringblocks to the size needed
		//end of file reached during countNumberOfStringBlocks();
		
		reader.close();
		
		currentLineNum = 0;
		//return reader back to top of the file, to begin parsing/storing chunks
		try {
			reader = new Scanner(file);
		} catch(FileNotFoundException e) {
			throw e;
		}
		
		StringBlock stringBlock;
		int index = 0;
		
		//read in stringblocks, and store them in order
		try{
			while ((stringBlock = parseNextStringBlock()) != null) {
				stringListBuffer[index] = stringBlock;
				index++;
			}
			//end of file reached successfully
			
			stringList = stringListBuffer;
		} catch (IllegalStateException e) {
			throw e; //let it propogate all the way up to the method where the file is selected
		}
		
		//close reader
		reader.close();
	}
	
	public int endTimeOfCurrentFile() {
		return stringList[stringList.length - 1].getEndTime();
	}
	
	/**
	 * Reads in the next "chunk" of text in the .srt file, and returns
	 * the data as a StringBlock object. Returns null if there are no
	 * more "chunks" to read in. READER MUST ALREADY BE INSTANTIATED
	 * AND TIED TO A FILE BEFORE THIS METHOD IS CALLED.
	 */
	private StringBlock parseNextStringBlock() {		
		if(!reader.hasNext())
			return null;
		
		String num = "-1";
		
		try {
			
			do
			{
				num = reader.nextLine(); //read and toss out first line of chunk (number)
				currentLineNum++;
			} while(num.isEmpty());
			
			currentLineNum++;

			//first line = number
			//since the stringBlocks are stored in order,
			//this line can be ignored
			
			String timeStr = reader.nextLine(); //read second line of chunk
			currentLineNum++;
			
			//second line = timestamps
			int startTime = CSubUtility.parseTimestamp(timeStr.substring(0, 12));
			int endTime = CSubUtility.parseTimestamp(timeStr.substring(17, 29));
			
			String str1 = reader.nextLine(); //read third line of chunk
			currentLineNum++;			
			
			//if there are more than 2 lines (for the love of God, whyyyyyy would a file do this? [some actually do...])
			//then 2nd, 3rd, 4th, etc. lines get crammed into 2nd line to keep program from crashing.
			String str2 = "";
			String currentLine = "";
			
			while(reader.hasNext() && !(currentLine = reader.nextLine()).isEmpty()) {
				currentLineNum++;
				str2 += currentLine;
			}
			
			currentLineNum++; //didn't get incremented on the read where the loop fails
			
			//strip out any html. we ain't that fancy
			str1 = str1.replaceAll("<\\w*>", "").replaceAll("</\\w*>", "");
			str2 = str2.replaceAll("<\\w*>", "").replaceAll("</\\w*>", "");			
			
			return new StringBlock(str1, str2, startTime, endTime);
			
		} catch (Exception e) {
			throw new IllegalStateException("Failed reading line number: " + currentLineNum + "\nError: " + e.getMessage());		
		}
	}

	
	/**
	 * Counts the number of stringblocks in the file inside reader.
	 * Reader will reach end of file when this method is called,
	 * if you want to return back to the top of the file after calling this, a
	 * new BufferedReader object should be created.
	 * 
	 * READER MUST ALREADY BE INSTANTIATED
	 * AND TIED TO A FILE BEFORE THIS METHOD IS CALLED.
	 */
	private int countNumberOfStringBlocks() {
		int counter = 0;
		String currentLine = "";
		
		while (reader.hasNext()) {
			if(currentLine.isEmpty()) {
				currentLine = reader.nextLine();
				
				if(!currentLine.isEmpty()) {
					counter++;
				}
			}
			else
				currentLine = reader.nextLine();
		}
		
		
		return counter;
	}
	
	public void updateSize() {
		width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;

		//1 or 0 multipliers
		int mpVis = (csub.isMorePanelVisible()) ? 1 : 0;
		int csubAwake = (csub.isAwake() ? 1 : 0);
			
		height = csub.getHeight() - 2 * CSub.RESIZE_BORDER_WIDTH - csubAwake * (CSub.WINDOW_BUTTON_PANEL_HEIGHT + CSub.INTERFACE_PANEL_HEIGHT + mpVis * CSub.MORE_PANEL_HEIGHT);
		
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		
		//when the size updates, it doesn't always fill into the vacated spot in the layoutmanager
		//due to other panels being set have height 0. This manually sets it in the right spot
		//within the layout manager. I'm a bit hesitant to use any method overrriding the layoutmanager's
		//behavior, but this seems to work like a charm.
		setBounds(0, csubAwake * CSub.WINDOW_BUTTON_PANEL_HEIGHT, width, height);
		
		resizeCaptions();
	}
	
	private void resizeCaptions() {
		setCaption(label1.getText(), label2.getText());
	}
	
	private void chooseSmallerFont() {
		if (label2.isEmpty()) {
			font = label1.getFont();
		} else {
			//take the smaller of the two fonts
			float fontSize = Math.min(label1.getFont().getSize(), label2.getFont().getSize());
			font = font.deriveFont(fontSize);
		}
		
		label1.setFont(font);
		label2.setFont(font);		
	}
    
    public void play() {
    	
    }
    
    public void pause() {
    	
    }
    
    public void setTextColor(Color c) {
    	label1.setForeground(c);
    	label2.setForeground(c);
    	label1.repaint();
    	label2.repaint();
    }
    
   public void setBackgroundColor(Color c) {
	   setBackground(c);
   }
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int xscreen = e.getXOnScreen();
		int yscreen = e.getYOnScreen();
		
		csub.setX(xscreen - mouseX);
		csub.setY(yscreen - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
	
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
		
    	csub.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
    }

    public void mousePressed(MouseEvent e) {
		grabFocus();
		
    	//set "anchor point" for x and y within the window when it is dragged
    	mouseX = e.getX() + CSub.RESIZE_BORDER_WIDTH;
    	mouseY = e.getY() + CSub.RESIZE_BORDER_WIDTH + CSub.WINDOW_BUTTON_PANEL_HEIGHT;
    }

    public void mouseReleased(MouseEvent e) {

    }

}

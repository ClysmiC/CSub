import java.awt.Color;
import javax.swing.text.MaskFormatter;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.event.KeyEvent;
import javax.swing.JFormattedTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The text field that displays the current time that the subtitle player is at.
 * When the player is paused, the text field is focusable, and the user can input
 * a time to seek to. When playing, this field behaves just like a fillerpanel in
 * the sense that it can be clicked and dragged to move the program.
 * 
 * There wouldn't really be a nice way to implement seeking for a time while the
 * subtitles are playing (unless I created a separate field specifically for seeking),
 * so that is why it must be paused to do so.
 * 
 * IMPORTANT: JFormattedTextField has some idiotic behavior that makes it so every time
 * it becomes unfocusable and then refocusable, it loses its formatter... I tried using
 * setFormatter() manually every time it regains focus, but it doesn't work. If I getFormatter()
 * it just returns me the default formatter, which makes ABSOLUTELY NO SENSE. Must be a java bug.
 * BUT, if I use formatter.install(this) it works (I still have to do it every time it gains focus
 * though). Why does that work and not the other? No clue. But it works, so hallelujah.
 * 
 * @author Andrew
 *
 */
@SuppressWarnings("serial")
public class TimerField extends JFormattedTextField implements MouseMotionListener, MouseListener, DocumentListener, FocusListener {
    private CSub csub;
    private int width;
    private int height;
    
    private int mouseX; //x and y location relative to this component when the mouse is dragging it
    private int mouseY;
    
    //format will always be h:mm:ss
    private final int COLON1POS = 1;
    private final int COLON2POS = 4;
    private final int TEXTLENGTH = 7;
    
    private MaskFormatter mf;
    
    private Border border;
    
    //TODO: BUG
    //when clicking in text field (getting focus), then minimizing program, then restoring program, you can now longer click in text field to get focus
    //can only be fixed by re-minimizing then restoring.
    //TODO: highlight text as you enter it-- SEE TEST FILE ON MY LAPTOP!
    public TimerField (CSub c) {
    	super("0:00:00");
		csub = c;
		
		width = InterfacePanel.TIMER_FIELD_WIDTH;
		height = CSub.INTERFACE_PANEL_HEIGHT;
		setPreferredSize(new Dimension(width, height));
		
		//setBackground(CSub.DEFAULT_COLOR); //not needed since component is not opaque (ie no background gets painted)
		setForeground(Color.WHITE);
		setOpaque(false);
		
		border = getBorder();
		
		setBorder(null);
		setFocusable(false);
		setHorizontalAlignment(JFormattedTextField.CENTER);
		
        //instantiate to 0
		mouseX = 0;
		mouseY = 0;
		
		addMouseMotionListener(this);
		addMouseListener(this);
		addFocusListener(this);
		
		getDocument().addDocumentListener(this);

		try
		{
			mf = new MaskFormatter("#:##:##");
		} catch (Exception e) {

		}

		mf.setAllowsInvalid(false);
		mf.setPlaceholderCharacter('0');
		mf.install(this);
		
		repaint();
	}
    
    public void setCurrentTime(int ms) {
    	//use setValue instead of setText to play nice with the formatter
		setValue(CSubUtility.msToString(ms));
		repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	if (csub.isAwake()) {
    		super.paintComponent(g);
    	}
    	else {
    		//do nothing (i.e., don't draw the string)
    	}
    }
    
    /**
     * Refreshes the text to the current color dictated by
     * csub.getTextColor();
     * 
     * If not focusable, the color will be refreshed when focus changes
     * (see overridden setFocusable method)
     */
    public void refreshTextColor() {
    	if(!isFocusable()) {
    		setForeground(csub.getTextColor());
    		repaint();
    	}
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isFocusable()) {
			int xscreen = e.getXOnScreen();
			int yscreen = e.getYOnScreen();
			
			csub.setX(xscreen - mouseX);
			csub.setY(yscreen - mouseY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
		
		//not working for some reason. Java probably strongarms the cursor into being
		//a typing cursor somehow. not a big deal though, just a tiny cosmetic thing
    	if(!isFocusable()) {
    		csub.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    	}
    }

    public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
    }

    public void mousePressed(MouseEvent e) {
		/**
		 * Only set csub to be focusable if trying to type a time into the timerfield.
		 *
		 * unfortunately, it's impossible for Java to detect keyboard input if the component isn't in focus.
		 * this will likely bring windows task bar up over the video (since the fullscreen video no longer has focus),
		 * however as soon as play is clicked the program will become unfocusable again (as desired).
		 * I considered not adding the feature to seek a time by typing it in
		 * since it is incompatible with my desire to keep the program unfocused at all times.
		 * However the user has the option to simply not use the feature (by not clicking on the timer
		 * field) and it will behave identically to as if the feature wasn't even there.
		 *
		 * So it sucks that I can't implement it perfectly, but I can at least give the user the option
		 * to use the imperfect implementation if he wants to.
		 */
    	if(isFocusable()) {
            csub.setFocusableWindowState(true);
            grabFocus();
            
            int caretPos = getCaretPosition();
            
            if(caretPos <= COLON1POS) {
    			setCaretPosition(COLON1POS);
    			moveCaretPosition(0);
    		}
    		else if (caretPos <= COLON2POS) {
    			setCaretPosition(COLON2POS);
    			moveCaretPosition(COLON1POS + 1);
    		}
    		else {
    			setCaretPosition(TEXTLENGTH);
    			moveCaretPosition(COLON2POS + 1);
    		}
    	}
    	else {
        	//set "anchor point" for x and y within the window when it is dragged
    		mouseX = e.getX() + InterfacePanel.BROWSE_BUTTON_WIDTH + InterfacePanel.REWIND_BUTTON_WIDTH + InterfacePanel.PLAY_BUTTON_WIDTH + InterfacePanel.FFW_BUTTON_WIDTH + CSub.RESIZE_BORDER_WIDTH;
    		mouseY = e.getY() + CSub.RESIZE_BORDER_WIDTH + CSub.WINDOW_BUTTON_PANEL_HEIGHT + csub.getWordPanelHeight();	
    	}
    }

    public void mouseReleased(MouseEvent e) {

    }
    
    @Override
	protected void processKeyEvent(KeyEvent e) {
		//often multiple characters will be selected (for example, 'mm') for user-friendliness
		//however, only the first character should be replaced when key is typed, so setting
		//the caret position before processing the event makes it so the whole selection doesn't
		//get replaced
		//setCaretPosition(getCaretPosition());

		//type the key like normal (also moves caret position accordingly)
		super.processKeyEvent(e);
	}
    
    public void changedUpdate(DocumentEvent e) {
    	//do nothing -- not sure when this method is even called!
    }
    
    public void removeUpdate(DocumentEvent e) {
    	//do nothing -- this text field will automatically insert either a placeholder
    	//or the user input whenever removal happens.
    }
    
    public void insertUpdate(DocumentEvent e) {
    	//when textfield is focus owner, it can only possibly update by the user
    	//(can't own focus when subtitles are playing)
    	if(isFocusOwner()) {
    		int time;
        	String str = getText().trim();
        	
        	try {
        		//parseTimestamp parses .srt format timestamps, so it expects hh:mm:ss,mmm
        		//since timerfield format will always be h:mm:ss
        		//I need to modify the string a bit for this method
        		time = CSubUtility.parseTimestamp("0" + str + ",000");
        	}
        	catch(IllegalArgumentException exception) {
        		//do nothing, when play is clicked string will be returned to correct
        		//value by InterfacePanel.play()
        		return;
        	}
        	
        	csub.setCurrentTime2(time);
        	
        	int caretPos = getCaretPosition();

    		switch(caretPos) {
    			case 0:
    				setCaretPosition(COLON1POS);
    				moveCaretPosition(0);
    				break;

    			case 1:
    			case 2:
    				setCaretPosition(COLON2POS);
    				moveCaretPosition(COLON1POS + 1);
    				break;

    			case 3:
    				setCaretPosition(COLON2POS);
    				moveCaretPosition(COLON1POS + 2);
    				break;

    			case 4:
    			case 5:
    				setCaretPosition(TEXTLENGTH);
    				moveCaretPosition(COLON2POS + 1);
    				break;

    			case 6:
    				setCaretPosition(TEXTLENGTH);
    				moveCaretPosition(COLON2POS + 2);
    		}
    	}
    }
    
    public void test() {
    	
    }
    
    /**
     * Not exactly sure why this is even needed. Doesn't make sense that
     * the formatter gets reset when focusability is lost, but if I just
     * reinstall it every time it works. Go figure.
     * 
     * NOTE: it MUST be installed using mf.install(this);
     * using this.setFormatter(mf) does NOT work (I DON'T KNOW WHY????)
     */
    @Override
    public void focusGained(FocusEvent e){
    	mf.install(this);
    }
    
    @Override
    /**
     * When done typing in the time, make it so the program
     * isn't focusable anymore.
     */
    public void focusLost(FocusEvent e){
    	csub.setFocusableWindowState(false);
    }
    
    /**
     * Standard setFocusable, but also changes the color of the
     * textField background and font accordingly
     */
    @Override
    public void setFocusable(boolean b) {
    	
    	if (b)
    	{
    		setOpaque(true);
    		setForeground(Color.BLACK);
    		setBorder(border);
    	}
    	else
    	{
    		setOpaque(false);
    		setForeground(csub.getTextColor());
    		setBorder(null);
    	}
    	
    	super.setFocusable(b);
    }
}

import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

//TODO: WHY DOES CLICKING INTO TIMERFIELD MESS UP THE MINIMIZATION BEHAVIOR

//TODO: fix tooltips... for some reason I can only get tooltips to show after opening/closing JColorChooser???

/**
 * The main window of the GUI, which contains 3 (4 including the optional one) JPanels, which make up the GUI.
 * These JPanels are:
 * 1- the WindowButtonPanel (top bar that contains the center button, a filler panel, the minimize button, and the close button)
 * 2- the WordPanel (middle section that displays the words)
 * 3- the InterfacePanel (bottom section with open button, rewind, play, ffw, timer-slider, and "more..." button)
 * 4- the MorePanel (contains opacity slider, H/M/S fields, and a "less..." button)
 * 
 * @author Andrew Smith
 *
 */
@SuppressWarnings("serial")
public class CSub extends JFrame implements WindowListener {
	
	//starting dimensions of program
    public final static int DEFAULT_WIDTH = 700;
    public final static int DEFAULT_HEIGHT = 100;
    public final static int MIN_WIDTH = 300;
    public final static int MIN_HEIGHT = 80;

    //starting opacity of program
    public final static float DEFAULT_OPACITY = .7f;

    //heights of the top and bottom panels. the middle's (wordPanel) height
    //is changeable-- it resizes when the window resizes
    public final static int INTERFACE_PANEL_HEIGHT = 20;
    public final static int WINDOW_BUTTON_PANEL_HEIGHT = 20;
    public final static int MORE_PANEL_HEIGHT = 20;
    
    //all of the widths of the panels resize when the window resizes
    
    //size of the border encompassing the program.
    //this border can be clicked/dragged to resize the window
    public final static int RESIZE_BORDER_WIDTH = 8;
    
    //default background color for all components (transparent black)
    public final static Color DEFAULT_COLOR = new Color(30, 30, 30);
    public final static Color DEFAULT_SECONDARY_COLOR = new Color(80, 80, 80);

    //current dimensions of program
    private int width;
    private int height;
    
    //current x, y of program
    private int x;
    private int y;
    
    private Color textColor = Color.WHITE;
    private Color backgroundColor = DEFAULT_COLOR;
    private Color secondaryColor = DEFAULT_SECONDARY_COLOR;
    
    //if the mouse is over any part of the program, it is awake, and the buttons will be displayed
    //otherwise, it is not awake and the buttons are not displayed
    private boolean awake;
    private Timer sleepTimer;
    
    //tracks whether the "more..." panel is visible
    private boolean morePanelVisible;
    
    //stored here (with a getter) as a convenience for other classes that need to get the screen size
    //primarily, Center Button
    private Dimension screenSize;

    //all of the components that get added
    private WordPanel wordPanel;
    private InterfacePanel interfacePanel;
    private SouthPanel southPanel; //contains the windowButtonPanel and morePanel
    private WindowButtonPanel windowButtonPanel;
    private MorePanel morePanel;
    
    //lineborder that can be clicked/dragged to resize the window
    private LineBorder resizeBorder; //border that is clicked/dragged to resize window
    
    private Timer playTimer;
    private int currentTime;
    private int endTime;
    
    private boolean isPaused;
    
    /**
     * Constructor for the CSub GUI.
     * Creates an undecorated JFrame with default size, centers it horizontally, and sets the color/opacity.
     * Then, adds the three initial components (fourth component is only displayed when "more..." is clicked)
     */
    public CSub() {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	this.setUndecorated(true);

        //default size
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        super.setSize(width, height);

        //save screenSize in a variable, so other centerButton can easily access it
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();

        //start in middle of screen, with 100 pixels from bottom edge of window to bottom of screen
        x = screenWidth / 2 - width / 2;
        y = screenHeight - height - 100;
        this.setLocation(x, y);
        
        backgroundColor = DEFAULT_COLOR;
        
        setBackground(backgroundColor);
        setOpacity(.7f);
        
        
        //starts off awake
        awake = true;
        
        //timer that controls repainting when non-awake
        sleepTimer = new Timer();
        
        playTimer = new Timer();
        currentTime = 0;
        endTime = -1;
        isPaused = true;

        //set Layout
        BorderLayout layout = new BorderLayout();
        layout.setHgap(0);
        layout.setVgap(0);
        setLayout(layout);
        
        //add the resizeBorder to the root pane
        resizeBorder = new LineBorder(DEFAULT_COLOR, RESIZE_BORDER_WIDTH);
        this.getRootPane().setBorder(resizeBorder);
        
        //listener is added to the JFRAME, not the actual border
        this.addMouseMotionListener(new ResizeBorderListener(this));
        this.addMouseListener(new ResizeBorderListener(this));

        //instantiate JPanels
        windowButtonPanel = new WindowButtonPanel(this);
        wordPanel = new WordPanel(this);
        southPanel = new SouthPanel(this);
        interfacePanel = new InterfacePanel(this);
        morePanel = new MorePanel(this);
        
        southPanel.add(interfacePanel);
        southPanel.add(morePanel);
        
        
        //add JPanels
        add(windowButtonPanel, BorderLayout.NORTH);
        add(wordPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        this.addWindowListener(this);
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
    
    public void setX(int newX) {
    	x = newX;
    	setLocation(x, y);
    }
    
    public void setY(int newY) {
    	y = newY;
    	setLocation(x, y);
    }
    
    public int getHeight() {
    	return height;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public Color getTextColor() {
    	return textColor;
    }
    
    public Color getBackgroundColor() {
    	return backgroundColor;
    }
    
    public Color getSecondaryColor() {
    	return secondaryColor;
    }
    
    /**
     * Returns whether the program is awake or not. Negative values for
     * the variable awake are undefined, but will return true.
     * 
     * @return true if the program is awake, false if not awake
     */
    public boolean isAwake() {
    	return awake;
    }
    
    /**
     * Called when the mouse exits/enters a component of the program.
     * If a is true, the program "wakes up" (buttons/sliders become visible) instantly.
     * If a is false, the program waits 3 seconds, and THEN goes to "sleep" (only the
     * text is visible). 
     * 
     * Program will be awake when user is setting up the subtitles and changing position/size,
     * but for the duration of the movie/tv show, program should be asleep (assuming the user's
     * mouse is outside the program)
     */
    public void setAwake(boolean a)
    {
    	//repaints all sub-components as well
    	if (a) {
    		if(!awake)
    		{	    
        		awake = true;
        		
	    		windowButtonPanel.updateSize();
				southPanel.updateSize();
				interfacePanel.updateSize();
				morePanel.updateSize();
	    		
	    		wordPanel.updateSize();
	    		repaint();
    		}

    		sleepTimer.cancel();
    	}
    	else {
    		sleepTimer.cancel();		//cancel any unexecuted SLEEP_TASKS in the queue
    		sleepTimer = new Timer();
    		TimerTask sleep_task = new TimerTask() {
    			@Override
    			public void run() {
    				awake = false;

    				windowButtonPanel.updateSize();
    				southPanel.updateSize();
    				interfacePanel.updateSize();
    				morePanel.updateSize();
    				
    	    		wordPanel.updateSize();
    				repaint();
    			}
    		};
    		sleepTimer.schedule(sleep_task, 2000); //repaint and set awake to false 3 seconds after this gets called
    	}
    }
    
    @Override
    public void setSize(int w, int h) {
    	if (w < MIN_WIDTH) {
    		w = MIN_WIDTH;
    	}
    	if (h < MIN_HEIGHT + (morePanelVisible ? MORE_PANEL_HEIGHT : 0)) {
    		h = MIN_HEIGHT + (morePanelVisible ? MORE_PANEL_HEIGHT : 0);
    	}
    	
    	width = w;
    	height = h;
    	super.setSize(width, height);
    	
    	windowButtonPanel.updateSize();
    	wordPanel.updateSize();
    	southPanel.updateSize();
    	interfacePanel.updateSize();
    	morePanel.updateSize();
    }
    
    @Override
    public void setLocation(int x, int y) {
    	this.x = x;
    	this.y = y;
    	
    	super.setLocation(x, y);
    }
    
    public Dimension getScreenSize() {
    	return screenSize;
    }
    
    public void showMorePanel() {
    	morePanelVisible = true;
    	
    	//resize entire program to fit morePanel
    	height += MORE_PANEL_HEIGHT;
    	super.setSize(new Dimension(width, height));
    	
    	//resize southPanel to take up the newly resized space in program
    	southPanel.updateSize();
    	
    	//resize morePanel to take up the newly resized space in south panel
    	morePanel.updateSize();
    	
    	interfacePanel.hideMoreButton();
    	
    	//update the size now that morePanel is shown
    	wordPanel.updateSize();
    }
    
    public void hideMorePanel() {
    	morePanelVisible = false;
    	
    	//shrink entire program 
    	height -= MORE_PANEL_HEIGHT;
    	setSize(new Dimension(width, height));
    	
    	//resize southPanel to fit into the newly shrunk spot
    	southPanel.updateSize();
    
    	//resize morePanel to take up 0 space
    	morePanel.updateSize();
    	
    	interfacePanel.showMoreButton();
    	
    	//update the size now that morePanel is hidden
    	wordPanel.updateSize();
    }
    
    public boolean isMorePanelVisible() {
    	return morePanelVisible;
    }
    
    public void readInFile(File f) throws FileNotFoundException {
	    try{
	    	wordPanel.fillList(f);
	    	morePanel.setFileName(f.getName());
	    	setEndTime(wordPanel.endTimeOfCurrentFile());
    	} catch (FileNotFoundException e) {
    		throw e; //let it propogate up to browseButton
    	} catch (IllegalArgumentException e) {
    		throw e;
    	} catch (IllegalStateException e) {
    		throw e;
    	}
    }
    
    //word panel is private, and since height resizes,
    //it might be useful for other classes to know the height
    public int getWordPanelHeight() {
    	return wordPanel.getHeight();
    }
    
    public void setEndTime(int ms) {
    	endTime = ms;
    	interfacePanel.setEndTime(ms);
    }
	
    /**
     * The brunt of the output of the program happens here.
     * Interface panel updates the timerlabel and slider position.
     * Word panel updates the words that are displayed.
     * 
     * @param ms time in milliseconds
     */
	public void setCurrentTime(int ms) {
		currentTime = Math.min(ms, endTime);
		currentTime = Math.max(currentTime, 0);
		
		interfacePanel.setCurrentTime(currentTime);
		wordPanel.setCaptionAtTime(currentTime);
	}
	
	/**
	 * Same as setCurrentTime, but with one difference:
	 * The timerlabel does NOT get updated.
	 * This method is only called when the time is changing from the user typing
	 * a time into the timerField. If the timerField is mutated by this change,
	 * it violates the event model used by the DocumentListener.
	 * 
	 * However, the update is unnecessary anyway, since the user had to type in a valid
	 * time anyway, so when the rest of the program gets set to that time, the timerField
	 * will already be correct with the time the user typed in.
	 * 
	 * @param ms time in milliseconds
	 */
	public void setCurrentTime2(int ms) {
		currentTime = Math.min(ms, endTime);
		currentTime = Math.max(currentTime, 0);
		
		interfacePanel.setCurrentTime2(currentTime);
		wordPanel.setCaptionAtTime(currentTime);
	}
	
	public int getEndTime() {
		return endTime;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public void play() {
		if (endTime == -1)
			return;

		playTimer.cancel();
		playTimer = new Timer();
		isPaused = false;
		
		interfacePanel.play();
		
		TimerTask playTask = new TimerTask() {
			public void run() {
				setCurrentTime(currentTime + 100);
				
				if (currentTime >= endTime) {
					pause();
				}
			}
		};
		
		playTimer.scheduleAtFixedRate(playTask, 0, 100);
	}
	
	public void pause() {
		if(endTime == -1)
			return;
		
		playTimer.cancel();
		playTimer = new Timer();
		isPaused = true;
		
		interfacePanel.pause();
	}
	
	/**
	 * Rewinds the time of the subtitles. x3 speed if playing,
	 * x5 if paused.
	 */
	public void rewind() {
		if (endTime == -1)
			return;

		playTimer.cancel();
		playTimer = new Timer();
		
		TimerTask rewindTask;
		
		//x5 if paused, x3 if playing
		if(isPaused) {
			rewindTask = new TimerTask() {
				public void run() {
					setCurrentTime(currentTime - 500);
				}
			};
		}
		else {
			rewindTask = new TimerTask() {
				public void run() {
					setCurrentTime(currentTime - 300);
				}
			};
		}
		
		playTimer.scheduleAtFixedRate(rewindTask, 0, 100);
	}
	
	/**
	 * Fastforwards the time of the subtitles. x3 speed if playing,
	 * x5 if paused.
	 */
	public void ffw() {
		if (endTime == -1)
			return;

		playTimer.cancel();
		playTimer = new Timer();
		
		TimerTask ffwTask;
		
		//x5 if paused, x3 if playing
		if(isPaused) {
			ffwTask = new TimerTask() {
				public void run() {
					setCurrentTime(currentTime + 500);
					
					if (currentTime >= endTime) {
						pause();
					}
				}
			};
		}
		else {
			ffwTask = new TimerTask() {
				public void run() {
					setCurrentTime(currentTime + 300);
					
					if (currentTime >= endTime) {
						pause();
					}
				}
			};
		}
		
		
		playTimer.scheduleAtFixedRate(ffwTask, 0, 100);
	}
	
	/**
	 * Calls all necessary children to update the color of the text in the window.
	 * Gets called by TextColorButton.
	 * 
	 * @param c - the new text color
	 */
	void setTextColor(Color c) {
		Color oldColor = textColor;
		textColor = c;
		
		wordPanel.setTextColor(c);
		interfacePanel.setTextColor(c);
		morePanel.setTextColor(c);
		
		windowButtonPanel.changeButtonColors(oldColor, c);
		interfacePanel.changeButtonColors(oldColor, c);
		morePanel.changeButtonColors(oldColor, c);
	}
	
	/**
	 * Updates the background color of the program. Calls all necessary methods in
	 * all components to set backgrounds of buttons/labels/etc. to the same bkg color
	 * Gets called by BackgroundColorButton.
	 * 
	 * @param c - the new text color
	 */
	void setBackgroundColor(Color c) {
		backgroundColor = c;
		
		//generate a secondary color to change button backgrounds to when mouse is over them
		int rOffset = (c.getRed() < 180) ? 50 : -50;
		int gOffset = (c.getGreen() < 180) ? 50 : -50;
		int bOffset = (c.getBlue() < 180) ? 50 : -50;
		secondaryColor = new Color(c.getRed() + rOffset, c.getGreen() + gOffset, c.getBlue() + bOffset);
		

        resizeBorder = new LineBorder(backgroundColor, RESIZE_BORDER_WIDTH);
        this.getRootPane().setBorder(resizeBorder);
		windowButtonPanel.setBackgroundColor(backgroundColor);
		wordPanel.setBackgroundColor(backgroundColor);
		interfacePanel.setBackgroundColor(backgroundColor);
		morePanel.setBackgroundColor(backgroundColor);
	}
	
	public void chooseAFile() {
		FileDialog fd = new FileDialog(this, "Select a file", FileDialog.LOAD);
		fd.setFocusableWindowState(false);
		fd.setMultipleMode(false);
		//filter to only allow .srt files (potentially others in the future)
		fd.setFile("*.srt");
		
		//have fd pop up over main program
		setAlwaysOnTop(false);
		
		//show the file dialog-- program freezes until dialog is closed
		fd.setVisible(true);
		
		//set main program back to always on top
		setAlwaysOnTop(true);
		
		//Java is stupid, so the only way to get the File returned as a File
		//object (instead of a filename string which is inconsistent), is to return
		//an array of all the files selected. Since multiple file selection is turned
		//off, this array will simply either be empty (no file was chosen), or
		//contain 1 element (the chosen file)
		File [] fileArray = fd.getFiles();
		File file = null;
		
		if (fileArray.length > 0) {
			//TODO: come up with more permanent solution for the "Recent" files error (seems to only happen on linux)
			//maybe don't even develop for linux-- especially since alwaysOnTop doesn't work like it does on Windows????
			
			file = fileArray[0].getAbsoluteFile(); //relative pathnames are dumb!!!!
			
			//handle potential error when trying to open file from "recently used" in the file explorer.
			//for some reason it doesn't always give the correct filepath (error only seen on linux so far)
			if(!file.exists()) {
				String errorString = "File does not exist.";
				JOptionPane.showMessageDialog(this, errorString, "Error", JOptionPane.ERROR_MESSAGE);
				
				return; //no harm done
			}
			
			try{
				readInFile(file); //also sets the end time
				setCurrentTime(0);
				pause();
	    	} catch (FileNotFoundException ex) {
	    		displayFileNotFoundError();
	    	} catch (IllegalArgumentException ex) {
	    		displayIllegalFileTypeError();
	    	} catch (IllegalStateException ex) {
	    		displayFailedReadingFileError(ex.getMessage());
	    	}
		}
	}
	
	//TODO: Make this less dumb. It is working, but I'm not sure I totally understand how the minimize behavior works.
	//It seems like it should be pretty straightforward but I had to use a strange workaround.
	/**
	 * For some reason setIconified(true) still makes a little [minimize, maximize, close] window in the bottom left of the screen.
	 * By making it invisible it will act like a normal window being minimized
	 * 
	 * Also, e.getNewState() is only returning 1 or 0 (0 for normal, 1 for minimized) for some reason...
	 * Why isn't it returning the values WindowEvent.WINDOW_ICONIFIED / WINDOW_DEICONIFIED (both values of 200-something...)
	 * For now, I'll just hard-code in the 1 and 0 I guess, even though it makes terrible self-documenting code.
	 */
	public void windowStateChanged(WindowEvent e) {
		if(e.getNewState() == 1) {
		}
		if(e.getNewState() == 0) {
		}
	}

    public static void main (String [] args) {
    	
    	//try to set to the system L&F instead of the lame java one
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	} catch (Exception e) {
    		//do nothing, default to the lame java L&F
    	}
    	
        new CSub();
    }
    
    private void displayFileNotFoundError() {
    	JOptionPane.showMessageDialog(this, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void displayIllegalFileTypeError() {
    	JOptionPane.showMessageDialog(this, "Only .srt file types supported.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void displayFailedReadingFileError(String errorMessage) {
    	JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		setExtendedState(JFrame.NORMAL);
		this.setVisible(true);		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
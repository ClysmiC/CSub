import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * JPanel that runs along the bottom border of the program. Contains 5 buttons,
 * and a slider that resizes to fit the width of the program.
 * Does not listen for mouse movement, as each component has its own listeners,
 * and the components fit snug into the InterfacePanel.
 * 
 * @author Andrew Smith
 *
 */
public class InterfacePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//buttons on the interface panel
    //buttons have fixed widths (and heights, determined by the height of their panel)
    //the JSlider resizes to take up the remaining width
    public final static int BROWSE_BUTTON_WIDTH = 30;
    public final static int REWIND_BUTTON_WIDTH = 30;
    public final static int PLAY_BUTTON_WIDTH = 30;
    public final static int FFW_BUTTON_WIDTH = 30;
    public final static int TIMER_FIELD_WIDTH = 70;
    public final static int MORE_BUTTON_WIDTH = 30;
    
	@SuppressWarnings("unused")
	private BrowseButton browseButton;
	@SuppressWarnings("unused")
	private RewindButton rewindButton;
	private PlayButton playButton;
	@SuppressWarnings("unused")
	private FFWButton ffwButton;
	private TimerField timerField;
	private TimeSlider timeSlider;
	private MoreButton moreButton;

	private CSub csub;
	
	private int width, height;
	
	public InterfacePanel(CSub c) {
		csub = c;
		width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
		height = CSub.INTERFACE_PANEL_HEIGHT;
    	
    	setPreferredSize(new Dimension(width, height));
		
		setBackground(CSub.DEFAULT_COLOR);

        //give more manual control of layout
        FlowLayout ipLayout = new FlowLayout(FlowLayout.LEFT);
        ipLayout.setHgap(0);
        ipLayout.setVgap(0);
        setLayout(ipLayout);
        
        add(browseButton = new BrowseButton(csub));
        add(rewindButton = new RewindButton(csub));
        add(playButton = new PlayButton(csub));
        add(ffwButton = new FFWButton(csub));
        add(timerField = new TimerField(csub));
        add(timeSlider = new TimeSlider(csub));
        add(moreButton = new MoreButton(csub));
	}
    
    public void setEndTime(int ms) {
    	timeSlider.setEndTime(ms);
    }
	
    /**
     * Sets all components in this panel to the appropriate time:
     * Slider set to specified time.
     * TimerField text updated to display specified time.
     * 
     * @param ms - time in milliseconds
     */
	public void setCurrentTime(int ms) {
		timeSlider.setCurrentTime(ms);
		timerField.setCurrentTime(ms);
	}
	
	/**
	 * Sets the slider to the specified time in ms.
	 * Does not update the timerField (see documentation in CSub.java)
	 * 
	 * @param ms - time in milliseconds
	 */
	public void setCurrentTime2(int ms) {
		timeSlider.setCurrentTime(ms);
	}
	
	public void pause() {
		playButton.setPlayImage();
		timerField.setFocusable(true);
	}
	
	public void play() {
		playButton.setPauseImage();
		timerField.setFocusable(false);
	}
	
	public void setTextColor(Color c) {
		timerField.refreshTextColor();
	}
	
	public void setBackgroundColor(Color c) {
		setBackground(c);
		browseButton.refreshBackgroundColor();
		rewindButton.refreshBackgroundColor();
		playButton.refreshBackgroundColor();
		ffwButton.refreshBackgroundColor();
		timeSlider.setBackground(c);
		timeSlider.setTrackColor(csub.getSecondaryColor());
		moreButton.refreshBackgroundColor();
	}
	
	public void changeButtonColors(Color oldColor, Color newColor) {
		browseButton.changeButtonColor(newColor);
		rewindButton.changeButtonColor(oldColor, newColor);
		playButton.changeButtonColor(oldColor, newColor);
		ffwButton.changeButtonColor(oldColor, newColor);
		timeSlider.changeKnobColor(oldColor, newColor);
		moreButton.changeButtonColor(oldColor, newColor);
	}
    
	/*
     * Adjusts the width of this component and its subcomponents when the main window
     * gets resized.
     */
    public void updateSize() {
    	width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
    	height = (csub.isAwake()) ? CSub.INTERFACE_PANEL_HEIGHT : 0;
    	setPreferredSize(new Dimension(width, height));
    	setSize(new Dimension(width, height));
    	
    	timeSlider.updateSize();
    	
    	//don't let timer field keep focus if it's not even visible anymore
    	if(height == 0 && timerField.isFocusOwner()) {
    		timerField.setFocusable(false);
    	}
    	else {
    		//if vid is paused and height got set to 0, then back to normal height
    		//timerField should return to being focusable
    		if(csub.isPaused() && csub.getEndTime() != -1)
    			timerField.setFocusable(true);
    	}
    }
    
    public void showMoreButton() {
    	moreButton.setVisible(true);
    }
    
    public void hideMoreButton() {
    	moreButton.setVisible(false);
    }
    
    //shoddily designed way to give access to the browse button to
    //the FileNameLabel
    public BrowseButton getBrowseButton() {
    	return browseButton;
    }
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * JPanel that runs along the top border of the program. Contains 3 buttons,
 * and a "filler" panel that resizes to fit the width of the program.
 * Does not listen for mouse movement, as each component has its own listeners,
 * and the components fit snug into the WindowButtonPanel.
 * 
 * @author Andrew Smith
 *
 */
@SuppressWarnings("serial")
public class WindowButtonPanel extends JPanel {
    //buttons on the window panel
    //buttons have fixed widths (and heights, determined by the height of their panel)
    //there is a filler JPanel to take up the remaining width
    public final static int CENTER_BUTTON_WIDTH = 50;
    public final static int MINIMIZE_BUTTON_WIDTH = 20;
    public final static int CLOSE_BUTTON_WIDTH = 20;
    
    @SuppressWarnings("unused")
	private CenterButton centerButton;            //centers the subtitle player [horizontally] on the screen
    private FillerPanel fillerPanel;  		//empty panel just to take up space
    @SuppressWarnings("unused")
    private MinimizeButton minimizeButton;          //minimizes the subtitle player
    @SuppressWarnings("unused")
    private CloseButton closeButton;             //closes the subtitle player
    
    private CSub csub;
    
    private int width, height;
    
    
    public WindowButtonPanel(CSub c) {
    	csub = c;
    	width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
    	height = CSub.WINDOW_BUTTON_PANEL_HEIGHT;
    	
    	setPreferredSize(new Dimension(width, height));
		
		setBackground(CSub.DEFAULT_COLOR);

        //give more manual control of layout
        FlowLayout wbLayout = new FlowLayout(FlowLayout.LEFT);
        wbLayout.setHgap(0);
        wbLayout.setVgap(0);
        setLayout(wbLayout);

        add(centerButton = new CenterButton(csub));
        add(fillerPanel = new FillerPanel(csub));
        add(minimizeButton = new MinimizeButton(csub));
        add(closeButton = new CloseButton(csub));
    }
    
    /*
     * Adjusts the width of this component and its subcomponents when the main window
     * gets resized.
     */
    public void updateSize() {
    	width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
    	height = (csub.isAwake()) ? CSub.WINDOW_BUTTON_PANEL_HEIGHT : 0;
    	
    	setPreferredSize(new Dimension(width, height));
    	setSize(new Dimension(width, height));
    	
    	fillerPanel.updateSize();
    }
    
    public void setBackgroundColor(Color c) {
    	setBackground(c);
    	centerButton.refreshBackgroundColor();
    	fillerPanel.setBackground(c);
    	minimizeButton.refreshBackgroundColor();
    	closeButton.refreshBackgroundColor();
    }
    
    public void changeButtonColors(Color oldColor, Color newColor) {
    	centerButton.changeButtonColor(oldColor, newColor);
    	minimizeButton.changeButtonColor(oldColor, newColor);
    	closeButton.changeButtonColor(oldColor, newColor);
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
}

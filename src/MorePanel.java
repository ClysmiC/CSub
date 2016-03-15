import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * JPanel that appears when "more" button is clicked. Runs along bottom border of program. 
 * Contains an opacity slider, a formatted text field, a "filler" panel that resizes to fit
 * the program width, and a "less" button.
 * Does not listen for mouse movement, as each component has its own listeners,
 * and the components fit snug into the WindowButtonPanel.
 * 
 * @author Andrew Smith
 *
 */
//TODO: add more cool features to more panel... once I think of some !!!
@SuppressWarnings("serial")
public class MorePanel extends JPanel {
    //components on the window panel
    //buttons have fixed widths (and heights, determined by the height of their panel)
    //there is a filler JPanel to take up the remaining width
    public final static int FILENAME_LABEL_WIDTH = 200;
    public final static int TEXT_COLOR_BUTTON_WIDTH = 30;
    public final static int BACKGROUND_COLOR_BUTTON_WIDTH = 30;
    public final static int LESS_BUTTON_WIDTH = 30;
    
    private OpacitySlider opacitySlider;
    @SuppressWarnings("unused")
    private TextColorButton textColorButton;
    @SuppressWarnings("unused")
    private BackgroundColorButton backgroundColorButton;
    private FileNameLabel fileNameLabel;
    @SuppressWarnings("unused")
    private LessButton lessButton;
    
    private CSub csub;
    
    private int width, height;
    
    public MorePanel(CSub c) {
    	csub = c;
    	width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
    	height = 0;
    	
    	setPreferredSize(new Dimension(width, height)); //starts off hidden (height = 0)
    	
    	setBackground(CSub.DEFAULT_COLOR);

        //give more manual control of layout
        FlowLayout mpLayout = new FlowLayout(FlowLayout.LEFT);
        mpLayout.setHgap(0);
        mpLayout.setVgap(0);
        setLayout(mpLayout);
        
        add(opacitySlider = new OpacitySlider(csub));
        add(textColorButton = new TextColorButton(csub));
        add(backgroundColorButton = new BackgroundColorButton(csub));
        add(fileNameLabel = new FileNameLabel(csub));
        add(lessButton = new LessButton(csub));
    }
    
    public void updateSize() {
    	width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
    	height = (csub.isAwake() && csub.isMorePanelVisible()) ? CSub.MORE_PANEL_HEIGHT : 0;
    	setPreferredSize(new Dimension(width, height));
    	setSize(width, height);
    	
    	opacitySlider.updateSize();
    }
    
    public void setTextColor(Color c) {
    	fileNameLabel.setForeground(c);
    	fileNameLabel.repaint();
    	textColorButton.repaint();
    	backgroundColorButton.repaint();
    }
    
    public void setBackgroundColor(Color c) {
    	setBackground(c);
    	opacitySlider.setBackground(c);
    	opacitySlider.setTrackColor(csub.getSecondaryColor());
    	textColorButton.refreshBackgroundColor();
    	backgroundColorButton.refreshBackgroundColor();
    	fileNameLabel.setBackground(c);
    	lessButton.refreshBackgroundColor();
    }
    
    public void changeButtonColors(Color oldColor, Color newColor) {
    	opacitySlider.changeKnobColor(oldColor, newColor);
    	
    	//textColorButton and bkgColorButton aren't loaded from images. they are drawn in their respective paintComponent methods
    	textColorButton.changeButtonColor(newColor);
    	backgroundColorButton.changeButtonColor(newColor);
    	
    	lessButton.changeButtonColor(oldColor, newColor);
    }
    
    public void setFileName(String str) {
    	//2 spaces added to give a little breathing room between the color buttons and the text
    	fileNameLabel.setText("  " + str);
    }
}

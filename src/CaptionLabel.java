import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

/**
 * The Labels that actually hold the text in the WordPanel.
 * @author andrew
 *
 */
@SuppressWarnings("serial")
public class CaptionLabel extends JLabel {
	private CSub csub;
	private int width, height;
	private Font font;
	private String str;
	private boolean initialized = false;
	private boolean isTop;
	
	public CaptionLabel(CSub c, boolean top) {
		super();
		
		csub = c;
		isTop = top;
		
		width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
		height = csub.getHeight() - CSub.WINDOW_BUTTON_PANEL_HEIGHT - CSub.INTERFACE_PANEL_HEIGHT - 2 * CSub.RESIZE_BORDER_WIDTH;
		height /= 2; //when initialized, both panels take 1/2 the word panel. will shortly get changed when captions get added.
		
		str = "";
		
		font = getFont(); //use default font
		setHorizontalAlignment(JLabel.CENTER);
		
		setForeground(Color.WHITE);
		
		setPreferredSize(new Dimension(width, height));
		
		initialized = true;
	}
	
	public boolean isEmpty() {
		return str.isEmpty();
	}
	
	/**
	 * USE THIS CUSTOM SET_TEXT WHEN SETTING THE TEXT OF THE LABEL.
	 * It takes an extra param that helps the label determine how it should be resized
	 * (see updateSize)
	 * 
	 * @param s - text that label will be set to
	 * @param b - whether or not the label should take up the whole word panel
	 */
	public void setText(String s, boolean wholePanel) {
		str = s;
		
		updateSize(wholePanel);
		fitFontSizeToDimension();
		
		super.setText(s);
	}
	
	/**
	 * Don't allow client to call setText(String s)
	 */
	@Override
	public void setText(String s) {
		if(initialized)
			throw new UnsupportedOperationException("setText(String s) unsupported for CaptionLabel. Please use setText(String s, boolean wholePanel)");
		else
			//when super() constructor is called, code reaches this point
			super.setText(s);
	}
	
	@Override
	public String getText() {
		return str;
	}

	/**
	 * Updates the size of the labels to fit snug into the wordPanel.
	 * If the label contains no text, it sets it's height to 0. If it contains text,
	 * it will either be set to half the height of the wordPanel, or the entire height
	 * of the word panel.
	 * 
	 * @param wholePanel - whether or not it should be resized to take up the whole wordPanel. Should only be true if
	 * 		it is the top panel, and the bottom panel is empty.
	 */
	public void updateSize(boolean wholePanel) {
		width = csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH;
		
		//decide whether more panel has any effect on height
		int mpVis = (csub.isMorePanelVisible()) ? 1 : 0;
		int csubAwake = (csub.isAwake() ? 1 : 0);
			
		height = csub.getHeight() - 2 * CSub.RESIZE_BORDER_WIDTH - csubAwake * (CSub.WINDOW_BUTTON_PANEL_HEIGHT + CSub.INTERFACE_PANEL_HEIGHT + mpVis * CSub.MORE_PANEL_HEIGHT);
				
		//important to never set top label's height to 0. otherwise, when it gets resized and text put on it, there
		//is a brief splitsecond where label 1 and label 2 are overlapping
		if (str.isEmpty() && !isTop) {
			height = 0;
		} else if (wholePanel) {
			//height is unchanged
		} else {
			height /= 2; //halve the height
		}
		
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		
		fitFontSizeToDimension();
	}
	
	/**
	 * Resizes the text to take up as much space in the label as possible.
	 * Only considers the max size for THIS label, not the other label.
	 * To set so both labels are the same size, the wordPanel has to manually
	 * set the size of both labels to the min(label1size, label2size)
	 * 
	 */
	private void fitFontSizeToDimension() {
		if (height != 0) {
			
			//width in pixels that the string takes up
			//second term in max makes it so that the font is never comically large (usually happens when one word is on one panel,
			//e.g. "No" might take up almost the whole window, which just looks way too big
			int stringWidth = Math.max(getFontMetrics(font).stringWidth(str),	getFontMetrics(font).stringWidth(" ") * csub.getWidth() / 20);
			
			//% that the string can grow by to fit (horizontally)
			double widthRatio = ((double)width)/stringWidth;
			
			//size will be largest value still bound by width and height
			//subtract 4 from height to leave a little room at bottom for g, j, y, q
			float newFontSize = Math.min((int)(widthRatio * font.getSize()), height - 6);
			
			font = font.deriveFont(newFontSize - 1); //subtract 1 just to be super-certain it will fit
			
			setFont(font);
		}
	}
	
	@Override
	public void setFont(Font f) {
		font = f;
		super.setFont(f);
	}
}

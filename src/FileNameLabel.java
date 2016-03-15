import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

public class FileNameLabel extends JLabel implements MouseListener {
	private CSub csub;
	private int width;
	private int height;
	private Font font;
	
	public FileNameLabel(CSub c) {
		super("  No file opened - (Click to choose a file)");
		
		csub = c;
		font = getFont();
		
		width = MorePanel.FILENAME_LABEL_WIDTH;
		height = CSub.MORE_PANEL_HEIGHT;
		
		setPreferredSize(new Dimension(width, height));
	    setBackground(CSub.DEFAULT_COLOR);
	    setForeground(Color.WHITE);
	    
	    setToolTipText("Current file (click to change)");
        
        addMouseListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		csub.chooseAFile();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
    	csub.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	/**
    	 * Not exactly sure how this works, but it underlines the text
    	 */
    	Map attributes = font.getAttributes();
    	attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    	setFont(font.deriveFont(attributes));
	}
	@Override
	public void mouseExited(MouseEvent e) {
		/**
    	 * De-underlines the text
    	 */
    	setFont(font);
	}
}

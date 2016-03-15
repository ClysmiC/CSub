import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class LessButton extends JPanel implements MouseListener {
	private CSub csub;
	private int width;
	private int height;
	private BufferedImage image;
	private Color backgroundColor;
	
	public LessButton(CSub c) {
	    csub = c;
	
	    width = MorePanel.LESS_BUTTON_WIDTH;
	    height = CSub.MORE_PANEL_HEIGHT;
	    setPreferredSize(new Dimension(width, height));
	    
	    backgroundColor = CSub.DEFAULT_COLOR;
        
        //get the image for the more button
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("less.png");
        	image = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
	    
	    setToolTipText("Less...");
	    
	    addMouseListener(this);
	}
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	g.setColor(backgroundColor);
    	g.fillRect(0, 0, width, height);
    	
    	if (csub.isAwake()) {
    		g.drawImage(image, 0, 0, width, height, null);
    	}
    }
    
    public void changeButtonColor(Color oldColor, Color newColor) {
    	CSubUtility.recolorImage(image, oldColor, newColor);
    	repaint();
    }
    
    /**
     * Updates the background color when the bkg color setting is changed.
     * Assumes the mouse is not over the button (ie, never updates it to csub.getSecondaryColor())
     * since it is pretty safe to assume that the mouse will not be over one of the buttons after
     * clicking "OK" in the JColorChooser
     */
    public void refreshBackgroundColor() {
    	backgroundColor = csub.getBackgroundColor();
    	repaint();
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
		
		backgroundColor = csub.getSecondaryColor();
		
    	csub.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	repaint();	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
		
		backgroundColor = csub.getBackgroundColor();
		
    	repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		csub.hideMorePanel();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {	
	}
}

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Button that gets clicked to show the MorePanel. When clicked, the button won't be drawn until
 * the less button gets clicked. When the more button is invisible, it essentially acts as
 * a filler panel (draggable to reposition jframe). When visible, it behaves like any other button.
 * 
 * 
 * @author Andrew
 *
 */
@SuppressWarnings("serial")
public class MoreButton extends JPanel implements MouseListener, MouseMotionListener {
	private CSub csub;
	private int width;
	private int height;
	private BufferedImage image;
	private Color backgroundColor;
	
	private int mouseX, mouseY;
	private boolean visible;
	
	public MoreButton(CSub c) {
	    csub = c;
	    
	    width = InterfacePanel.MORE_BUTTON_WIDTH;
	    height = CSub.INTERFACE_PANEL_HEIGHT;
	    setPreferredSize(new Dimension(width, height));
	    
	    backgroundColor = CSub.DEFAULT_COLOR;
	    
	    mouseX = 0;
	    mouseY = 0;
	    visible = true;
        
        //get the image for the more button
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("more.png");
        	image = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
	    
	    setToolTipText("More...");
	    
	    addMouseListener(this);
	    addMouseMotionListener(this);
	}
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	if (csub.isAwake()) {
    		
	    	g.setColor(backgroundColor);
	    	g.fillRect(0, 0, width, height);
	    	
	    	if (visible) {
	    		g.drawImage(image, 0, 0, width, height, null);
	    	}
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
    
    //simply toggle the variable-- manually handle
    //"invisible" behaviors in the event handlers.
    //this allows it to still be clicked and dragged even
    //when it's "invisible"
    @Override
    public void setVisible(boolean b) {
    	visible = b;
    }
    
    @Override
    public boolean isVisible() {
    	return visible;
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
		
		backgroundColor = csub.getSecondaryColor();
		
		if (visible)
			csub.setCursor(new Cursor(Cursor.HAND_CURSOR));
		else
			csub.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		
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
		grabFocus();
		
		if (visible) {
			csub.showMorePanel();
			//button gets hidden, change cursor to the move cursor
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
		
    	mouseX = e.getX() + csub.getWidth() - CSub.RESIZE_BORDER_WIDTH - InterfacePanel.MORE_BUTTON_WIDTH;
    	mouseY = e.getY() + CSub.RESIZE_BORDER_WIDTH + CSub.WINDOW_BUTTON_PANEL_HEIGHT + csub.getWordPanelHeight();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!visible) {
			int xscreen = e.getXOnScreen();
			int yscreen = e.getYOnScreen();
			
			csub.setX(xscreen - mouseX);
			csub.setY(yscreen - mouseY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}

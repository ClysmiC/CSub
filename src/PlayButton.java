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
public class PlayButton extends JPanel implements MouseListener {
	private CSub csub;
	private int width;
	private int height;
	private BufferedImage playImage;
	private BufferedImage pauseImage;
	private BufferedImage image;
	private Color backgroundColor;
	
	public PlayButton(CSub c) {
	    csub = c;
	
	    width = InterfacePanel.PLAY_BUTTON_WIDTH;
	    height = CSub.INTERFACE_PANEL_HEIGHT;
	    setPreferredSize(new Dimension(width, height));
	    
	    backgroundColor = CSub.DEFAULT_COLOR;
        
        //get the image for the play button
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("play.png");
        	playImage = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
      //get the image for the pause button
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("pause.png");
        	pauseImage = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
        
        image = playImage;
	    
	    setToolTipText("Play");

	    addMouseListener(this);
	}
	
	public void setPlayImage() {
		image = playImage;
		repaint();
	}
	
	public void setPauseImage() {
		image = pauseImage;
		repaint();
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
    	CSubUtility.recolorImage(pauseImage, oldColor, newColor);
    	CSubUtility.recolorImage(playImage, oldColor, newColor);
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
		if(csub.getEndTime() == -1)
			return;
		
		if (csub.isPaused() && csub.getCurrentTime() < csub.getEndTime()) {
			csub.play();
		} else if (!csub.isPaused()) {
			csub.pause();
		} else {
			//slider at end of file-- set back to 0 and play
			csub.setCurrentTime(0);
			csub.play();
		}
		
		repaint();
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
		grabFocus();	
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
}

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

@SuppressWarnings("serial")
public class TimeSlider extends JPanel implements MouseListener, MouseMotionListener {
	private CSub csub;
	private int width;
	private int height;
	private BufferedImage knobImage;
	private Color trackColor;
	private int sliderStartX, sliderEndX;
	private int sliderWidth;
	private int knobX;
	private final int KNOB_WIDTH = 10;
	private int endTime;
	private int currentTime;
	private boolean draggingKnob;
	
	
	public TimeSlider(CSub c) {
	    csub = c;
	
	    width = csub.getWidth() - InterfacePanel.BROWSE_BUTTON_WIDTH - InterfacePanel.REWIND_BUTTON_WIDTH - InterfacePanel.PLAY_BUTTON_WIDTH - InterfacePanel.FFW_BUTTON_WIDTH - InterfacePanel.TIMER_FIELD_WIDTH - InterfacePanel.MORE_BUTTON_WIDTH - 2 * CSub.RESIZE_BORDER_WIDTH;
	    height = CSub.INTERFACE_PANEL_HEIGHT;
	    
	    setPreferredSize(new Dimension(width, height));
	    
	    sliderStartX = 15;
	    sliderEndX = width - 15;
	    sliderWidth = sliderEndX - sliderStartX;
	    knobX = 0; //x is the CENTER OF THE KNOB-- and it is RELATIVE TO THE BEGINNING OF THE SLIDER TRACK
	    
	    setBackground(CSub.DEFAULT_COLOR);
	    trackColor = CSub.DEFAULT_SECONDARY_COLOR;
        
        //get the image for the more button
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("knob.png");
        	knobImage = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
        
        endTime = -1;	//no file loaded yet
        currentTime = 0;
        draggingKnob = false;
        
        addMouseListener(this);
        addMouseMotionListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (csub.isAwake()) {
			g.setColor(trackColor);
			g.drawLine(sliderStartX, height/2, sliderEndX, height/2);
			g.drawImage(knobImage, sliderStartX + knobX - KNOB_WIDTH / 2, 0, null);
		}
	}
	
	public void setTrackColor(Color c) {
		trackColor = c;
	}
	
	public void changeKnobColor(Color oldColor, Color newColor) {
		CSubUtility.recolorImage(knobImage, oldColor, newColor);
		repaint();
	}
	
	public void updateSize() {
		width = csub.getWidth() - InterfacePanel.BROWSE_BUTTON_WIDTH - InterfacePanel.REWIND_BUTTON_WIDTH - InterfacePanel.PLAY_BUTTON_WIDTH - InterfacePanel.FFW_BUTTON_WIDTH - InterfacePanel.TIMER_FIELD_WIDTH - InterfacePanel.MORE_BUTTON_WIDTH - 2 * CSub.RESIZE_BORDER_WIDTH;
		sliderEndX = width - 15;
		sliderWidth = sliderEndX - sliderStartX;
		
    	knobX = (int)(sliderWidth * (double)currentTime / endTime);
		
		setPreferredSize(new Dimension(width, height));
	}
	
	public void setEndTime(int ms) {
		endTime = ms;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (draggingKnob && endTime != -1) {
	    	int xOnSlider = e.getX() - sliderStartX;
	    	
	    	xOnSlider = Math.max(xOnSlider, 0);
	    	xOnSlider = Math.min(xOnSlider, sliderWidth);
    	
    		csub.setCurrentTime((int)(endTime * (double)xOnSlider / sliderWidth));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
    	int xOnSlider = e.getX() - sliderStartX;
    	
    	xOnSlider = Math.max(xOnSlider, 0);
    	xOnSlider = Math.min(xOnSlider, sliderWidth);
	
    	if (endTime != -1) {
    		setToolTipText(CSubUtility.msToString((int)(endTime * (double)xOnSlider / sliderWidth)));
    	}
	}
	
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
    	int xOnSlider = e.getX() - sliderStartX;
    	
    	xOnSlider = Math.max(xOnSlider, 0);
    	xOnSlider = Math.min(xOnSlider, sliderWidth);
    	
		csub.setCursor(new Cursor(Cursor.HAND_CURSOR));
	
    	if (endTime != -1) {
    		setToolTipText(CSubUtility.msToString((int)(endTime * (double)xOnSlider / sliderWidth)));
    	}
    }

    public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
    }

    public void mousePressed(MouseEvent e) {
		grabFocus();
		
    	//clicked outside of knob-- inside of knob is handled by mouseDragged
    	if (e.getX() < knobX || e.getX() > knobX + KNOB_WIDTH) {
    		if (endTime != -1) {
		    	int xOnSlider = e.getX() - sliderStartX;
		    	
		    	xOnSlider = Math.max(xOnSlider, 0);
		    	xOnSlider = Math.min(xOnSlider, sliderWidth);
	    	
	    		csub.setCurrentTime((int)(endTime * (double)xOnSlider / sliderWidth));
	    	}
    	}

    	draggingKnob = true;
    }

    public void mouseReleased(MouseEvent e) {
    	draggingKnob = false;
    }
    
    public void setCurrentTime(int ms) {
    	currentTime = ms;
    	
    	int newKnobX = (int)(sliderWidth * (double)currentTime / endTime);
    	
    	//only repaint if the knob moves a pixel
    	if (knobX != newKnobX) {
	    	knobX = newKnobX;
	    	repaint();
    	}
    }
}

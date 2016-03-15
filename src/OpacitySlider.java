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
public class OpacitySlider extends JPanel implements MouseListener, MouseMotionListener {
	private CSub csub;
	private int width;
	private int height;
	private BufferedImage knobImage;
	private BufferedImage opacityImage;
	private BufferedImage baseImage;
	private Color trackColor;
	private int sliderStartX, sliderEndX;
	private int sliderWidth;
	private int knobX;
	private final int KNOB_WIDTH = 10;
	private boolean draggingKnob;
	private float currentOpacity;
	private final float MIN_OPACITY = .25f;
	
	
	public OpacitySlider(CSub c) {
	    csub = c;
	
	    width = csub.getWidth() - MorePanel.LESS_BUTTON_WIDTH - MorePanel.TEXT_COLOR_BUTTON_WIDTH - MorePanel.BACKGROUND_COLOR_BUTTON_WIDTH - MorePanel.FILENAME_LABEL_WIDTH - CSub.RESIZE_BORDER_WIDTH * 2;
	    height = CSub.MORE_PANEL_HEIGHT;
	    
	    setPreferredSize(new Dimension(width, height));
	    
	    sliderStartX = 25;
	    sliderEndX = width - 5;
	    sliderWidth = sliderEndX - sliderStartX;
	    knobX = 0; //x is the CENTER OF THE KNOB-- and it is RELATIVE TO THE BEGINNING OF THE SLIDER TRACK
	    
	    setBackground(CSub.DEFAULT_COLOR);
	    trackColor = CSub.DEFAULT_SECONDARY_COLOR;
        
        //get the knobImage for the slider knob
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("knob.png");
        	knobImage = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
        
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        	InputStream input = classLoader.getResourceAsStream("opacity.png");
        	opacityImage = ImageIO.read(input);
        	input = classLoader.getResourceAsStream("opacity.png");
        	baseImage = ImageIO.read(input);
        } catch (IOException e) {
        	System.out.println("Failed to load image");
        }
        
        draggingKnob = false;
        currentOpacity = CSub.DEFAULT_OPACITY;
        setOpacity(currentOpacity); //gets the slider in the right spot
        
        addMouseListener(this);
        addMouseMotionListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (csub.isAwake()) {
			g.drawImage(opacityImage, 0, 0, null);
			
			g.setColor(trackColor);
			g.drawLine(sliderStartX, height/2, sliderEndX, height/2);
			g.drawImage(knobImage, sliderStartX + knobX - KNOB_WIDTH / 2, 0, null);
		}
	}
	
	public void setTrackColor(Color c) {
		trackColor = c;
	}
	
	public void changeKnobColor(Color oldColor, Color newColor) {
		opacityImage = CSubUtility.imageDeepCopy(baseImage);
		CSubUtility.recolorImage(opacityImage, Color.WHITE, newColor);
		CSubUtility.recolorImage(knobImage, oldColor, newColor);
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (draggingKnob) {
	    	int xOnSlider = e.getX() - sliderStartX;
	    	
	    	xOnSlider = Math.max(xOnSlider, 0);
	    	xOnSlider = Math.min(xOnSlider, sliderWidth);
    	
	    	setOpacity((float)MIN_OPACITY + xOnSlider / (sliderWidth / (1 - MIN_OPACITY)));
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
    	int xOnSlider = e.getX() - sliderStartX;
    	
    	xOnSlider = Math.max(xOnSlider, 0);
    	xOnSlider = Math.min(xOnSlider, sliderWidth);
	
    	setToolTipText(Math.round(100*(MIN_OPACITY + xOnSlider / (sliderWidth / (1 - MIN_OPACITY)))) + "% opacity");
	}
	
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
    	int xOnSlider = e.getX() - sliderStartX;
    	
    	xOnSlider = Math.max(xOnSlider, 0);
    	xOnSlider = Math.min(xOnSlider, sliderWidth);
    	
		csub.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
    	setToolTipText(100*(MIN_OPACITY + xOnSlider / (sliderWidth / (1 - MIN_OPACITY))) + "%");
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
		grabFocus();
		
    	//clicked outside of knob-- inside of knob is handled by mouseDragged
    	if (e.getX() < knobX || e.getX() > knobX + KNOB_WIDTH) {
	    	int xOnSlider = e.getX() - sliderStartX;
	    	
	    	xOnSlider = Math.max(xOnSlider, 0);
	    	xOnSlider = Math.min(xOnSlider, sliderWidth);
    	
	    	setOpacity((float)MIN_OPACITY + xOnSlider / (sliderWidth / (1 - MIN_OPACITY)));
    	}

    	draggingKnob = true;
    }

    public void mouseReleased(MouseEvent e) {
    	draggingKnob = false;
    }
    
    public void setOpacity(float f) {
    	currentOpacity = f;
    	csub.setOpacity(f);
    	
    	knobX = (int)(sliderWidth / (1 - MIN_OPACITY) * (currentOpacity - MIN_OPACITY));
    	repaint();
    }
	
	public void updateSize() {
		width = csub.getWidth() - MorePanel.LESS_BUTTON_WIDTH - MorePanel.TEXT_COLOR_BUTTON_WIDTH - MorePanel.BACKGROUND_COLOR_BUTTON_WIDTH - MorePanel.FILENAME_LABEL_WIDTH - CSub.RESIZE_BORDER_WIDTH * 2;
		
		sliderEndX = width - 5;
		sliderWidth = sliderEndX - sliderStartX;
		
		knobX = (int)(sliderWidth / (1 - MIN_OPACITY) * (currentOpacity - MIN_OPACITY));
		
		setPreferredSize(new Dimension(width, height));
	}
}

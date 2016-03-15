import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Font;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class TextColorButton extends JPanel implements MouseListener {
	private CSub csub;
	private int width;
	private int height;
	private Color backgroundColor;
	private Color buttonColor;
	
	public TextColorButton(CSub c) {
	    csub = c;
	
	    width = MorePanel.TEXT_COLOR_BUTTON_WIDTH;
	    height = CSub.MORE_PANEL_HEIGHT;
	    setPreferredSize(new Dimension(width, height));
	    
	    backgroundColor = CSub.DEFAULT_COLOR;
	    buttonColor = Color.WHITE;
	    
	    setToolTipText("Font color");
	    
	    addMouseListener(this);
	}
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	g.setColor(backgroundColor);
    	g.fillRect(0, 0, width, height);
    	
    	if (csub.isAwake()) {
    		g.setColor(buttonColor);
    		g.setFont(g.getFont().deriveFont(Font.BOLD, 15));
    		g.drawString("A", width/2 - 4, height/2 + 3);
    		g.drawLine(2, 16, width - 2, 16);
    		g.drawLine(2, 17, width - 2, 17);
    	}
    }
    
    public void changeButtonColor(Color c) {
    	buttonColor = c;
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
		Color tempColor = JColorChooser.showDialog(csub, "Font Color", Color.WHITE);
		
		//null means user pressed "cancel"
		if(tempColor != null) {
			csub.setTextColor(tempColor);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {	
	}
}

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;



@SuppressWarnings("serial")
public class FillerPanel extends JPanel implements MouseMotionListener, MouseListener {	
	private CSub csub;
    private int width;
    private int height;
    
    private int mouseX; //x and y location relative to this component when the mouse is dragging it
    private int mouseY;

	public FillerPanel (CSub c) {
		csub = c;
		
		width = csub.getWidth() - WindowButtonPanel.CENTER_BUTTON_WIDTH - WindowButtonPanel.MINIMIZE_BUTTON_WIDTH - WindowButtonPanel.CLOSE_BUTTON_WIDTH - 2 * CSub.RESIZE_BORDER_WIDTH;
		height = CSub.WINDOW_BUTTON_PANEL_HEIGHT;
		setPreferredSize(new Dimension(width, height));
		
		setBackground(CSub.DEFAULT_COLOR);
        
        //instantiate to 0
		mouseX = 0;
		mouseY = 0;
		
		addMouseMotionListener(this);
		addMouseListener(this);
		
		repaint();
	}
	
	public void updateSize() {
		width = csub.getWidth() - WindowButtonPanel.CENTER_BUTTON_WIDTH - WindowButtonPanel.MINIMIZE_BUTTON_WIDTH - WindowButtonPanel.CLOSE_BUTTON_WIDTH - 2 * CSub.RESIZE_BORDER_WIDTH;
		
		setPreferredSize(new Dimension(width, height));
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int xscreen = e.getXOnScreen();
		int yscreen = e.getYOnScreen();
		
		csub.setX(xscreen - mouseX);
		csub.setY(yscreen - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
		
    	csub.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
    }

    public void mousePressed(MouseEvent e) {
		grabFocus();
		
    	//set "anchor point" for x and y within the window when it is dragged
    	mouseX = e.getX() + WindowButtonPanel.CENTER_BUTTON_WIDTH + CSub.RESIZE_BORDER_WIDTH;
    	mouseY = e.getY() + CSub.RESIZE_BORDER_WIDTH;
    }

    public void mouseReleased(MouseEvent e) {

    }
}

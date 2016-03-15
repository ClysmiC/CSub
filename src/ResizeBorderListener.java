import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;


public class ResizeBorderListener implements MouseListener, MouseMotionListener {
	
	private final static int TOP_LEFT_CORNER = 1;
	private final static int TOP_EDGE = 2;
	private final static int TOP_RIGHT_CORNER = 3;
	private final static int RIGHT_EDGE = 4;
	private final static int BOTTOM_RIGHT_CORNER = 5;
	private final static int BOTTOM_EDGE = 6;
	private final static int BOTTOM_LEFT_CORNER = 7;
	private final static int LEFT_EDGE = 8;
	
	private CSub csub;
	
	//keeps track of how the resize arrow should look
	Cursor curs;
	
	//current edge being dragged
	int edge;
	
	public ResizeBorderListener(CSub c) {
		csub = c;
		curs = new Cursor(Cursor.DEFAULT_CURSOR);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		csub.requestFocus(); //takes focus away from text field
		
		int x = e.getX();
		int y = e.getY();
		
		int ySizeOffset = 0;
		int xSizeOffset = 0;
		int yPosOffset = 0;
		int xPosOffset = 0;
		
		switch (edge) {
			case TOP_EDGE:
				xSizeOffset = 0;
				xPosOffset = 0;
				ySizeOffset = -y;
				yPosOffset = y;
				break;
					
			case BOTTOM_EDGE:
				xSizeOffset = 0;
				xPosOffset = 0;
				ySizeOffset = y - csub.getHeight();
				yPosOffset = 0;
				break;
				
			case LEFT_EDGE:
				xSizeOffset = -x;
				xPosOffset = x;
				ySizeOffset = 0;
				yPosOffset = 0;
				break;
				
			case RIGHT_EDGE:
				xSizeOffset = x - csub.getWidth();
				xPosOffset = 0;
				ySizeOffset = 0;
				yPosOffset = 0;
				break;
				
			case TOP_RIGHT_CORNER:
				xSizeOffset = x - csub.getWidth();
				xPosOffset = 0;
				ySizeOffset = -y;
				yPosOffset = y;
				break;
				
			case BOTTOM_RIGHT_CORNER:
				xSizeOffset = x - csub.getWidth();
				xPosOffset = 0;
				ySizeOffset = y - csub.getHeight();
				yPosOffset = 0;
				break;
				
			case BOTTOM_LEFT_CORNER:
				xSizeOffset = -x;
				xPosOffset = x;
				ySizeOffset = y - csub.getHeight();
				yPosOffset = 0;
				break;
				
			case TOP_LEFT_CORNER:
				xSizeOffset = -x;
				xPosOffset = x;
				ySizeOffset = -y;
				yPosOffset = y;
				break;
				
			default:
				xSizeOffset = 0;
				xPosOffset = 0;
				ySizeOffset = 0;
				yPosOffset = 0;
		}
		
		if (csub.getWidth() + xSizeOffset < CSub.MIN_WIDTH) {
			xSizeOffset = 0;
			xPosOffset = 0;
		}
		
		if (csub.getHeight() + ySizeOffset < ((csub.isMorePanelVisible()) ? CSub.MIN_HEIGHT + CSub.MORE_PANEL_HEIGHT : CSub.MIN_HEIGHT)) {
			ySizeOffset = 0;
			yPosOffset = 0;
		}
		
		csub.setAwake(true);
		csub.setSize(csub.getWidth() + xSizeOffset, csub.getHeight() + ySizeOffset);
		csub.setLocation((int)csub.getLocation().getX() + xPosOffset, (int)csub.getLocation().getY() + yPosOffset);
	}

	
	/** 
	 * BUG (NEED TO FIX):
	 * WHEN DRAGGING/RESIZING, IF MOUSE LEAVES PROGRAM TOO FAST THE PROGRAM IS CONSIDERED NOT AWAKE.
	 * GIVE SOME SORT OF CHECK TO MAKE SURE MOUSE ISN'T RESIZING, AND APPLY TO ALL MOUSE ENTERED/EXITED
	 * EVENTS
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		//x and y relative to the window
		int x = e.getX();
		int y = e.getY();
		
		//if the movement was inside the border, ignore it.
		//only care about movements ON the border
		//****MIGHT BE REDUNDANT, BUT GOOD CHECK TO LEAVE IN ANYWAY****//
		if (x > CSub.RESIZE_BORDER_WIDTH && x < csub.getWidth() - CSub.RESIZE_BORDER_WIDTH &&
				y > CSub.RESIZE_BORDER_WIDTH && y < csub.getHeight() - CSub.RESIZE_BORDER_WIDTH) {
			
			csub.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			
			//cursor will be changed by the next component it passes into
			return;
		}
		
		//set appropriate resize cursor, based on the location of the mouse
		if (x < 8) {
			if (y < 8) {										//top-left corner
				edge = TOP_LEFT_CORNER;
				
				if (curs.getType() != Cursor.NW_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.NW_RESIZE_CURSOR);
				}
				
			} else if (y > csub.getHeight() - 8) {			//bottom-left corner
				edge = BOTTOM_LEFT_CORNER;
				
				if (curs.getType() != Cursor.SW_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.SW_RESIZE_CURSOR);
				}
				
			} else {											//left edge
				edge = LEFT_EDGE;
				
				if (curs.getType() != Cursor.W_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.W_RESIZE_CURSOR);
				}
			}
			
		} else if (x > csub.getWidth() - 8) {
			if (y < 8) {										//top-right corner
				edge = TOP_RIGHT_CORNER;
				
				if (curs.getType() != Cursor.NE_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.NE_RESIZE_CURSOR);
				}
				
			} else if (y > csub.getHeight() - 8) {			//bottom-right corner
				edge = BOTTOM_RIGHT_CORNER;
				
				if (curs.getType() != Cursor.SE_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.SE_RESIZE_CURSOR);
				}
				
			} else {											//right edge
				edge = RIGHT_EDGE;
				
				if (curs.getType() != Cursor.E_RESIZE_CURSOR) {
					curs = new Cursor(Cursor.E_RESIZE_CURSOR);
				}
			}
			
		} else if (y < 8) {									//top edge
			edge = TOP_EDGE;
			
			if (curs.getType() != Cursor.N_RESIZE_CURSOR) {
				curs = new Cursor(Cursor.N_RESIZE_CURSOR);
			}
		} else if (y > csub.getHeight() - 8) {				//bottom edge
			edge = BOTTOM_EDGE;
			
			if (curs.getType() != Cursor.S_RESIZE_CURSOR) {
				curs = new Cursor(Cursor.S_RESIZE_CURSOR);
			}
		}
				
		updateCursor();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		csub.setAwake(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!csub.contains(e.getXOnScreen() - csub.getX(), e.getYOnScreen() - csub.getY()))
		{
			csub.setAwake(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	private void updateCursor() {
		csub.setCursor(curs);
	}

}

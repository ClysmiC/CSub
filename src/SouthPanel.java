import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class SouthPanel extends JPanel {
	private CSub csub;
	
	public SouthPanel(CSub c) {
		super();
		csub = c;
		
		//give more manual control of layout
        FlowLayout sLayout = new FlowLayout(FlowLayout.LEFT);
        sLayout.setHgap(0);
        sLayout.setVgap(0);
        setLayout(sLayout);
        
        setPreferredSize(new Dimension(csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH, CSub.INTERFACE_PANEL_HEIGHT)); //morePanel starts off invisible
	}
	
	public void updateSize() {
		//1 or 0 multipliers
		int mpVis = (csub.isMorePanelVisible()) ? 1 : 0;
		int csubAwake = (csub.isAwake()) ? 1 : 0;
		
		int height = csubAwake * (CSub.INTERFACE_PANEL_HEIGHT + mpVis * CSub.MORE_PANEL_HEIGHT);
		
		setPreferredSize(new Dimension(csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH, height));
		setSize(new Dimension(csub.getWidth() - 2 * CSub.RESIZE_BORDER_WIDTH, height));
	}
}

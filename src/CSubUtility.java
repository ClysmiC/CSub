import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Class full of static utility methods for the CSub program
 * 
 * @author Andrew
 */
public class CSubUtility {
    
    /**
     * Returns string in format of a timestamp h:mm:ss.
     * 
     * @param ms - the number of milliseconds to generate a timestamp for
     * @return - a string timestamp in the form of "h:mm:ss"
     */
    public static String msToString(int ms) {
    	int h = ms / 3600000;	//ms won't be large enough to fail this cast
    	ms %= 3600000;
    	
    	int m = ms / 60000;
    	ms %= 60000;
    	
    	int s = ms / 1000;
    	
    	String hString = "" + h;
    	String mString = String.format("%02d", m);
    	String sString = String.format("%02d", s);
    	
    	return hString + ":" + mString + ":" + sString;
    }
    
	/**
	 * Parse string in the form of "HH:MM:SS,mmm", and return
	 * the total number of milliseconds.
	 * 
	 * @param str - input string
	 * @return timestamp converted to milliseconds
	 */
    public static int parseTimestamp(String str) {
    	int h, m, s, ms;
    	
    	try {
			str = str.trim();
			h = new Integer(str.substring(0, 2));
			m = new Integer(str.substring(3, 5));
			s = new Integer(str.substring(6, 8));
			ms = new Integer(str.substring(9, 12));
    	} catch (Exception e) {
    		throw new IllegalArgumentException("Timestamp string formatted incorrectly");
    	}
			
			//overflow would require > 596 hours in timestame-- not a concern
			
		return ms + s * 1000 + m * 60 * 1000 + h * 60 * 60 * 1000;
	}
    
    /**
     * Recolors a (mostly) single-colored image pixel-by-pixel.
     * Used to change the color of the buttons in the program, which by default
     * are (mostly) white.
     * 
     * @param img - the image being recolored
     * @param oldColor - the color being changed from (used to determine which pixels should be slightly different in color)
     * @param newColor - the color to change the image to
     */
    public static void recolorImage(BufferedImage img, Color oldColor, Color newColor) {
    	int width = img.getWidth();
    	int height = img.getHeight();
    	
    	for(int down = 0; down < height; down++) {
    		for(int across = 0; across < width; across++) {
    			//true color of current pixel
    			Color actualColor = new Color(img.getRGB(across, down), true);
    			
    			//maintain difference between "image color" (such as oldColor, newColor), and the
    			//true color of each pixel
    			int newR = newColor.getRed() - (oldColor.getRed() - actualColor.getRed());
    			newR = Math.min(255, Math.max(newR, 0));
    			
    			int newG = newColor.getGreen() - (oldColor.getGreen() - actualColor.getGreen());
    			newG = Math.min(255, Math.max(newG, 0));

    			int newB = newColor.getBlue() - (oldColor.getBlue() - actualColor.getBlue());
    			newB = Math.min(255, Math.max(newB, 0));
    			
    			int alpha = actualColor.getAlpha();
    			
    			int rgba = alpha << 24 | newR << 16 | newG << 8 | newB;
    			
    			img.setRGB(across, down, rgba);
    		}
    	}
    }
    
    public static BufferedImage imageDeepCopy(BufferedImage img) {
    	BufferedImage b = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
    	Graphics g = b.getGraphics();
    	g.drawImage(img, 0, 0, null);
    	g.dispose();
    	return b;
    }
}

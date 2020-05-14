import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * ShowImagePanel extending JPanel representing the panel for the frame when showImage button is executed
 * It's whole functionality is to display the original image 
 * @author Suyash Lohia 
 * @version 1.0
 */
public class ShowImagePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image OrgImage;
	
	/**
	 * Constructor function to initialise values 
	 * @param i, image which is stored in private variable 
	 */
	public ShowImagePanel(Image i) {
		this.OrgImage=i;
	}
	/**
	 * Method to draw the image whenever show image button is clicled
	 */
	public void paintComponent(Graphics g) {
		g.drawImage(OrgImage,0,0,this);
	}
}

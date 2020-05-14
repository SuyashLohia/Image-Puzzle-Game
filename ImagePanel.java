import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Image Panel class exxtending JPanel is a panel which contains the jumbled up image, this class implements mouselistener and the method 
 * for swapping the tiles. It also checks id the game has ended sending a final message
 * @author suyashlohia
 *
 */
public class ImagePanel extends JPanel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame jf;
	private ArrayList<JButton> ImgBtns = new ArrayList<JButton>();
	private ArrayList<Point2D> BtnCoords = new ArrayList<Point2D>();
	private JButton Button1;
	private JButton Button2;
	
	/**
	 * Constructor function initialising the values of the panel
	 * @param f, object of Jframe represeting the entire puzzle 
	 * @param b, arraylist of buttons
	 * @param s, arralyist of points
	 */
	public ImagePanel(JFrame f,ArrayList<JButton> b, ArrayList<Point2D> s) {
		this.jf=f;
		this.ImgBtns=b;
		this.BtnCoords=s;
		this.setLayout(new GridLayout (5,5));
		this.setPreferredSize(new Dimension(500,500));
		addMouseListener(this);
		
	}
	
	/**
	 * Checking if the game was ended and the puzzle has been completed. 
	 * Closing the application if it's true
	 */
	public void EndOfGame() {
		this.repaint();
		ArrayList<Point2D> TempBtnCoords = new ArrayList<>();
        for (JComponent b : ImgBtns) {
        	Point temp=(Point) b.getClientProperty("position");
            TempBtnCoords.add(temp);
        }

        if (BtnCoords.equals(TempBtnCoords)) {
        	JOptionPane.showMessageDialog(jf,"You Win!");
        	System.exit(1);
        }
	}
	
	/**
	 *MouseListener to get the index of the second button
	 */
	public void mouseEntered(MouseEvent e) {
		Button2 = (JButton) e.getSource();
	}
	/**
	 *MouseListener to get the index of the first button
	 */
	public void mousePressed(MouseEvent e) {
		Button1 = (JButton) e.getSource();
	}
	
	/**
	 *MouseListener to swap the two buttons once the mouse has been released
	 */
	public void mouseReleased(MouseEvent e) {
		int i2 = ImgBtns.indexOf(Button2);
		int i1 = ImgBtns.indexOf(Button1);
		Collections.swap(ImgBtns, i1, i2);
		
		this.removeAll();
		for(JButton j: ImgBtns) {
			this.add(j);
		}
		this.validate();
		EndOfGame();
	}
	/**
	 * An empty mouselistener added because of package import and implemention
	 */
	public void mouseClicked(MouseEvent e) {}
	/**
	 * An empty mouselistener added because of package import and implemention
	 */
	public void mouseExited(MouseEvent e) {}
	



}
	


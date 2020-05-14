import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.DataBuffer;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;




/**
 * This Class Represents the GUI Application which inputs a file from the user and breaks it down into 25 pieces forming a 
 * puzzle, requirinig the user to solve the puzzle by swapping the tiles. This application also allows the user to load a new image, 
 * show the orginial image, save the game and load any existing game at any point of time.
 * 
 * @author Suyash Lohia
 * @version 1.0
 */
public class ImagePuzzle {
	private JFrame jf;
	private JPanel jpbase = new JPanel();
	private JPanel jptop = new JPanel();
	private ArrayList<JButton> ImgBtns = new ArrayList<JButton>();
	private ArrayList<Point2D> BtnCoords = new ArrayList<Point2D>();


	private JButton LoadImageBtn,ShowImageBtn,SaveGameBtn,LoadGameBtn;
	private Image OrgImage,FOrgImage;	
	private ImageIcon OrgImageIcon;
	private ImagePanel MyImagePanel = new ImagePanel(jf,ImgBtns,BtnCoords);
	private boolean flag=true;
	
	private String fpath="/Users/suyashlohia/Desktop/afakeimage.jpg";
	/**
	 * Main function initiatlising the Class by creating an instance and then calling the start method
	 * @param args, array of strings (dummy)
	 */
	public static void main(String[] args) {
		ImagePuzzle ImagePuzzleApp = new ImagePuzzle();
		ImagePuzzleApp.start();
	}
	
	/**
	 * Method function which sets up the GUI application by adding the buttons, actionlisteners, panels and frames. 
	 */
	private void start() {
		
		jf = new JFrame ("Puzzle Game");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LoadImageBtn = new JButton("Load New Image");
		SaveGameBtn = new JButton("Save Game");
		LoadGameBtn = new JButton("Load Game");
		ShowImageBtn = new JButton("Show Original Image");
		
		LoadImageBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
	
					GetImageFile();
					
					if (flag) {
						ClearData();
						BuildPuzzle();
						ShuffleButtons(ImgBtns);
						MyImagePanel.validate();
					}			
			}
		});
				
		ShowImageBtn.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e) {
				JFrame jf2 = new JFrame();
				jf2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ShowImagePanel newPanel = new ShowImagePanel(OrgImage);
				jf2.getContentPane().add(newPanel);
				jf2.setSize(500,500);
				jf2.setVisible(true);
			}
		});
		
		SaveGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JFileChooser ChooseFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int check = ChooseFile.showSaveDialog(null);
				String filepath = returnFilePath(check,ChooseFile);
								
				try {
					FileOutputStream FOut = new FileOutputStream(new File(filepath));
					ObjectOutputStream OOut = new ObjectOutputStream(FOut);
						
					OrgImageIcon= new ImageIcon (OrgImage);
					OOut.writeObject(OrgImageIcon);

				    for (JComponent b : ImgBtns) {
				    	OOut.writeObject(b);
				    }
					for (Point2D p: BtnCoords) {
						OOut.writeObject(p);
					}
						
				    OOut.close();
					FOut.close();

				}  catch (Exception err) {
					System.out.println("Save Game Error!");
				}
			}
		});
		
		LoadGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {

				JFileChooser ChooseFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int check = ChooseFile.showOpenDialog(null);
				String filepath = returnFilePath(check,ChooseFile);
				
				if (false ==filepath.equals("")) {
					FileInputStream FInput = null;
					ObjectInputStream OInput = null;
					
					try {
						
						FInput = new FileInputStream(new File(filepath));
						OInput = new ObjectInputStream(FInput);
						
						OrgImageIcon = (ImageIcon) OInput.readObject();
						ClearData();
						OrgImage=OrgImageIcon.getImage();
						
						for (int i = 2 ; i <= 26; ++i ) {
							ImgBtns.add((JButton) OInput.readObject());
						}
						for (int j = 2; j <= 26; ++j) {
							BtnCoords.add((Point) OInput.readObject());
						}

					}  catch (Exception err) {
						JOptionPane.showMessageDialog(jf, "Incorrect File");
					} 
					
					try {
						OInput.close();
						FInput.close();
					} catch (IOException err) {
						System.out.println("Load Game Error!");
					}
				}

				for (JButton j: ImgBtns) {
					j.addMouseListener(MyImagePanel);
					MyImagePanel.add(j);
				} 
			}
		});
		
		jpbase.setLayout(new GridLayout(1,4));
		jpbase.add(LoadImageBtn);
		jpbase.add(ShowImageBtn);
		jpbase.add(SaveGameBtn);
		jpbase.add(LoadGameBtn);

		jf.getContentPane().add(BorderLayout.SOUTH, jpbase);		

		GetImageFile();
		BuildPuzzle();
		ShuffleButtons(ImgBtns);

		jptop.setLayout(new GridBagLayout());
		jptop.add(MyImagePanel);
		jf.add(jptop);

		jf.setSize(570, 570);
		jf.setVisible(true);
		jf.pack();
		
	}
	
	/**
	 * Methof function which returns a string representing the path of the file 
	 * @param check, an integer used to check the option selected 
	 * @param cf, an object of class JFileChooser used to get the selected file
	 * @return string, representing the path of the file
	 */
	public String returnFilePath(int check, JFileChooser cf) {
		if (JFileChooser.CANCEL_OPTION== check) {
			System.exit(1);
		}
		else if (JFileChooser.APPROVE_OPTION==check) {
			File ActualFile = cf.getSelectedFile();
			return  ActualFile.getAbsolutePath().toString();
		}
		return "";	
	}
	
	/**
	 * Method function which takes in an arraylist of buttons as parametre and completely shuffles the arraylist randomly 
	 * adding back the buttons to the image panel
	 * @param IBtns, an array list of buttons containing the cropped image and its position
	 */
	public void ShuffleButtons(ArrayList<JButton> IBtns) {
		Collections.shuffle(IBtns);
		for (JButton b: ImgBtns) {
			MyImagePanel.add(b);
		}
	}
	/**
	 * Method function to clear the existing data to load new data 
	 */
	public void ClearData(){
		MyImagePanel.removeAll();
		ImgBtns.clear();
		BtnCoords.clear();
	}
	
	/**
	 * Method function which essentialy Builds the puzzle by assigning the buttons with cropped images and then setting their position
	 * further adding mouselisteners
	 */
	public void BuildPuzzle() {

		for(int rows=0;rows<5;++rows) {
			for(int cols=0; cols<5; ++cols) {
				Image croppedImg = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(OrgImage.getSource(), new CropImageFilter(cols*100,rows*100,100,100)));
				JButton b = new JButton(new ImageIcon(croppedImg));
				ImgBtns.add(b);
				Point temp= new Point(rows,cols);
				b.putClientProperty("position", temp);
				BtnCoords.add(temp);
				b.addMouseListener(MyImagePanel);
			}
		}
	}
	/**
	 * Method function to get the image file from the user, the image is stored in one of the instances. 
	 */
	public void GetImageFile() {
		FileNameExtensionFilter AcceptableFormat = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png","tif");
		JFileChooser ChooseFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		ChooseFile.setFileSelectionMode(JFileChooser.FILES_ONLY);		
		ChooseFile.addChoosableFileFilter(AcceptableFormat);
		ChooseFile.setAcceptAllFileFilterUsed(false);
		int check = ChooseFile.showOpenDialog(null);		
		String filepath = returnFilePath(check,ChooseFile);
		
		String temp= isvalidImage(filepath);
        if(temp.equals("Pixels equal: false")) {
        	ImageIcon tempImageIcon = new ImageIcon(new ImageIcon(filepath).getImage().getScaledInstance(500,500, Image.SCALE_SMOOTH));
    		System.out.println(tempImageIcon);
    		OrgImage= tempImageIcon.getImage();
    		flag = true;
        }
        else {
        	JOptionPane.showMessageDialog(jf,"Invalid File");
        	flag= false;
        }
			
	}
	public String isvalidImage(String filepath) {
		
		Image image1 = Toolkit.getDefaultToolkit().getImage(fpath);
		Image image2 = Toolkit.getDefaultToolkit().getImage(filepath);

		try {

			PixelGrabber grabImage1Pixels = new PixelGrabber(image1, 0, 0, -1,
					-1, false);
			PixelGrabber grabImage2Pixels = new PixelGrabber(image2, 0, 0, -1,
					-1, false);

			int[] image1Data = null;

			if (grabImage1Pixels.grabPixels()) {
				int width = grabImage1Pixels.getWidth();
				int height = grabImage1Pixels.getHeight();
				image1Data = new int[width * height];
				image1Data = (int[]) grabImage1Pixels.getPixels();
			}

			int[] image2Data = null;

			if (grabImage2Pixels.grabPixels()) {
				int width = grabImage2Pixels.getWidth();
				int height = grabImage2Pixels.getHeight();
				image2Data = new int[width * height];
				image2Data = (int[]) grabImage2Pixels.getPixels();
			}

			String s=("Pixels equal: "
					+ java.util.Arrays.equals(image1Data, image2Data));
			return s;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return "" ;
	}
		
}
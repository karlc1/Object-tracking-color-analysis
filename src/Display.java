import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;


/**
 * This class is used to convert images between mat objects and images, and display them
 * Only used for debug and presentation
 * @author Karl
 *
 */

public class Display {
	
	private  JFrame imageFrame = new JFrame();
	private  JLabel imageContainer = new JLabel();
	
	public Display() {
		imageFrame.setLayout(new FlowLayout());
		imageFrame.setSize(400, 400);
		imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imageFrame.add(imageContainer);
		imageFrame.setResizable(false);
		imageFrame.setVisible(false);
	}


	/**
	 * Display image object as image
	 * @param img
	 */
	public void displayImage(Image img) {
		
		if (imageFrame == null){
			imageFrame = new JFrame();
		}
		
		if (!imageFrame.isVisible()){
			imageFrame.setVisible(true);
		}
		
		if (imageFrame.getSize().getHeight() != img.getHeight(null) + 50 || 
				imageFrame.getSize().getWidth() != img.getWidth(null) + 50){
			imageFrame.setSize(img.getWidth(null)+50, img.getHeight(null)+50); 
		}
		
		ImageIcon icon = new ImageIcon(img);		
		imageContainer.setIcon(icon);
	}
	
	/**
	 * Display Mat object as image 
	 * @param img
	 */
	public void displayImage(Mat img) {
		
		if (imageFrame == null){
			imageFrame = new JFrame();
		}
		
		if (!imageFrame.isVisible()){
			imageFrame.setVisible(true);
		}
		
		if (imageFrame.getSize().getHeight() != img.height() + 50 || 
				imageFrame.getSize().getWidth() != img.width() + 50){
			imageFrame.setSize(img.width()+50, img.height()+50); 
		}
		
		Image convImg = mat2BufferedImage(img);
		ImageIcon icon = new ImageIcon(convImg);		
		imageContainer.setIcon(icon);
	}

	/**
	 * Convert mat to buffered image for display purposes
	 * 
	 * @param m
	 *            mat for conversion
	 * @return buffered image
	 */
	public static BufferedImage mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
}

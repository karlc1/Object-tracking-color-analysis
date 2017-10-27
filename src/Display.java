import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

public class Display {
	
	static JFrame imageFrame = new JFrame();
	static JLabel lbl = new JLabel();
	
	static{
		imageFrame.setLayout(new FlowLayout());
		imageFrame.setSize(400, 400);
		imageFrame.setVisible(true);
		imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		imageFrame.add(lbl);
		imageFrame.setResizable(false);
	}


	public static void displayImage(Image img2) {
		
		if (imageFrame.getSize().getHeight() != img2.getHeight(null) + 50 || 
				imageFrame.getSize().getWidth() != img2.getWidth(null) + 50){
			imageFrame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50); 
		}
		
		ImageIcon icon = new ImageIcon(img2);		
		lbl.setIcon(icon);

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

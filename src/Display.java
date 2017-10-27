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
	
	
	
	public static void displayImage(Image img2) {
		// BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
		ImageIcon icon = new ImageIcon(img2);
		imageFrame.setLayout(new FlowLayout());
		imageFrame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
		imageFrame.setVisible(true);
		imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		imageFrame.add(lbl);

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

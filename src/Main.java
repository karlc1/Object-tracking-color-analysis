
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;


import org.opencv.*;

/**
 * Reads and analyzes images
 * 
 * @author Karl
 *
 */

public class Main {

	public static boolean WEBCAM_MODE = true;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Main() {
		Mat mat = getNextImage();
		System.out.println("mat = " + mat.dump());
	}

	public static void main(String[] args) {
	}

	/**
	 * For Portability
	 * 
	 * @return image from specified source
	 */
	public Mat getNextImage() {
		if (WEBCAM_MODE) {
			return CamVideo.getImage();
		}

		else {
			System.err.println("getNextImage not specified correctly");
			System.exit(0);
			return null;
		}
	}

	
	
	/**
	 * Convert mat to buffered image for display purposes 
	 * @param m mat for conversion
	 * @return buffered image
	 */
	public static BufferedImage Mat2BufferedImage(Mat m) {
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

/**
 * Used for debug purposes, images are retrieved through sockets in release
 * 
 * @author Karl
 *
 */

class CamVideo {

	private static VideoCapture videoCapture;

	public CamVideo() {
	}

	static {
		videoCapture = new VideoCapture(0);
		if (!videoCapture.isOpened()) {
			System.err.println("Did not connect to camera, try changing video capture paramter index");
			System.exit(0);
		}
	}

	public static Mat getImage() {
		Mat temp = new Mat();
		videoCapture.retrieve(temp);
		return temp;
	}
}
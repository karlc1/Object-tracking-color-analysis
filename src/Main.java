
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG2;


import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.*;

/**
 * Reads and analyzes images
 * 
 * @author Karl
 *
 */

public class Main {

	public static boolean WEBCAM_MODE = true;

	// Always load library first 
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Main() {
		
		System.out.println("Program started..");
		
		
		Mat image1 = getNextImage();		
		Mat grayImage1 = new Mat(image1.height(), image1.width(), CvType.CV_8UC3);
		
		Imgproc.cvtColor(image1, grayImage1, Imgproc.COLOR_RGB2GRAY);
		
		Display.displayImage(grayImage1);
		
//		while(true){
//			
//			Display.displayImage(Display.mat2BufferedImage(CamVideo.getImage()));
//		}
	}

	/**
	 * For Portability
	 * 
	 * @return image from specified source
	 */
	public static Mat getNextImage() {
		
		Mat nextImage = null;
		
		if (WEBCAM_MODE) {
			nextImage = CamVideo.getImage();			
		}

		if (nextImage != null){
			return nextImage;
		}
		
		System.err.println("getNextImage not specified correctly");
		System.exit(0);
		return null; // Should never reach
		
	}
	
	public static void main(String[] args) {
		new Main();
	}
}


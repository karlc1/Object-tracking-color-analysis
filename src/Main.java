
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG2;


import org.opencv.*;

/**
 * Reads and analyzes images
 * 
 * @author Karl
 *
 */

public class Main {

	
	public static boolean WEBCAM_MODE = true; 	// for local debugging
	private static Mat lastImage = null;		// for storing previous image 

	// Always load library first 
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Main() {
		
		while (true){
			getDiff();
		}
		
	}
	
	private static void getDiff(){
		Mat newImage = getNextImage();		
		Mat newImageGray = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);
		Mat diffImage = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);

		Imgproc.cvtColor(newImage, newImageGray, Imgproc.COLOR_RGB2GRAY);		
		
		if (lastImage != null){
			
			Core.absdiff(newImageGray, lastImage, diffImage);
			
		

		}
		
		lastImage = newImageGray;
		
		Display.displayImage(diffImage);
		
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
		System.out.println("Program started..");
		new Main();
	}
}


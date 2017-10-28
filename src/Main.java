
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG;
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
		
//		while (true){
//			getDiff();
//		}
		
		backgroundSubtractionTest();
		
	}
	
	private static void getDiff(){
		Mat newImage = getNextImage();		
		Mat newImageGray = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);
		Mat diffImage = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);
		Mat thresImage = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);

		int threshold = 40;
		int blurSize = 10;

		Imgproc.cvtColor(newImage, newImageGray, Imgproc.COLOR_RGB2GRAY);		
		
		if (lastImage != null){
			
			Core.absdiff(newImageGray, lastImage, diffImage);
			Imgproc.threshold(diffImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);
			
			Imgproc.blur(thresImage, thresImage, new Size(blurSize, blurSize));
			Imgproc.threshold(thresImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);

		

		}
		
		lastImage = newImageGray;
		
		Display.displayImage(thresImage);
		
	}
	
	
	static Mat mask = new Mat();
	private static void backgroundSubtractionTest(){
		
		// Test variables
		int history = 500;
		float varThreshold = 1024;
		boolean detectShadows = false;		
		BackgroundSubtractorMOG mog = new BackgroundSubtractorMOG();
		
		while (true){
			
			mog.apply(getNextImage(), mask);
			
			Display.displayImage(mask);
			
		}
		
		
		
		
		
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


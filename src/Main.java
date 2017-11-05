
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.opencv.*;
import org.opencv.imgproc.*;

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
		
//		backgroundSubtractionTest();
		
	//	medianBackgroundTest();
		
	}
	
	static Mat baseImage = new Mat();
	static boolean baseInitialized = false; 
	
	private static void getDiff(){
		
		if (!baseInitialized){
			Mat imgForBase = getNextImage();
			baseImage = new Mat(imgForBase.height(), imgForBase.width(), CvType.CV_8UC3);
			Imgproc.cvtColor(imgForBase, baseImage, Imgproc.COLOR_RGB2GRAY);		
			baseInitialized = true; 
		}
		
		
		Mat newImage = getNextImage();		
		Mat newImageGray = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);
		Mat diffImage = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);
		Mat thresImage = new Mat(newImage.height(), newImage.width(), CvType.CV_8UC3);

		int threshold = 50;
		int blurSize = 10;

		Mat filterImg = new Mat();
		Imgproc.bilateralFilter(newImage, filterImg, 7, 150, 150);
		Imgproc.cvtColor(filterImg, newImageGray, Imgproc.COLOR_RGB2GRAY);		
		
		if (lastImage != null){
			
			Core.absdiff(newImageGray, lastImage, diffImage);
			Imgproc.threshold(diffImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);
			
			Imgproc.blur(thresImage, thresImage, new Size(blurSize, blurSize));
			
			//TODO: check out Core.adaptiveThreshold for this later
			Imgproc.threshold(thresImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);
			
			
//			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
//			
////		    Imgproc.findContours(thresImage, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
//		    
//		    
//		    boolean objectDetected = false;
//		    
//		    if (contours.size() > 0){
//		    	objectDetected = true;
//		    }
		    
//		    if (objectDetected){
//		    	
//		    	for(int i=0; i< contours.size();i++){
//		    		
//		            if (Imgproc.contourArea(contours.get(i)) > 50 ){
//		                Rect rect = Imgproc.boundingRect(contours.get(i));
//		                Core.rectangle(newImage, new Point(rect.x,rect.height), new Point(rect.y,rect.width),new Scalar(0,0,255));
//		            }
//		    	}
//		    }
		}
		
		
		

		
		boolean noMotion = true; 
		
		for (int col = 0; col < thresImage.height(); col+=32){
			for (int row = 0; row < thresImage.height(); row+=32){
				double[] valArr = (thresImage.get(row, col));
				double var = valArr[0];
				if (var != 0){
					noMotion = false; 
				}
			}
		}
		
		
		lastImage= newImageGray;
		if (lastImage == null || noMotion){
			lastImage = newImageGray;
		}
		//lastImage = newImageGray;
		System.out.println("MOTION: " + !noMotion);
		
		Display.displayImage(diffImage);
		
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
	
	
	@SuppressWarnings("unchecked")
	private static void medianBackgroundTest(){
		
		LinkedList<Mat> fifo = new LinkedList<>();
		int populationSize = 50;
		
		for (int i = 0; i < populationSize; i ++){
			fifo.push(getNextImage());
		}
		
		Mat img = fifo.peek();
		int rows = img.rows();
		int cols = img.cols();
		
		int resizeRows = (int) (rows * 0.1);
		int resizeCols = (int) (cols * 0.1);
		Size smallSize = new Size(resizeRows, resizeRows);
		Mat smallImage = new Mat();
		
		// Resize and gray scale image
		Imgproc.resize(img, smallImage, smallSize);
		Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);		

		// create store for saving values to compute 
		LinkedList<Double>[][] valueStores = new LinkedList[resizeRows][resizeCols];
		
		for (int row = 0; row < resizeRows; row++){
			for (int col = 0; col < resizeCols; col++){
				double[] intensityData = img.get(row, col);
				double data = intensityData[0];
				valueStores[row][col].push(data);				
			}
		}
		
		
		
		// Later
		Mat medianBackground = new Mat(resizeCols, resizeRows, CvType.CV_8UC3);
		for (int i = 0; i < resizeRows; i++){
			for(int j = 0; j < resizeCols; j++){
				
				// sort pixel values for position
				Collections.sort(valueStores[i][j]);
				// add median value at position
				medianBackground.put(i, j, valueStores[i][j].remove(valueStores[i][j].size() / 2));
				
				
			}
			
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


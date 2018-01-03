
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.opencv.*;
import org.opencv.imgproc.*;

import java.util.List;

/**
 * Reads and analyzes images
 * 
 * @author Karl
 *
 */

public class Main {

	public static boolean WEBCAM_MODE = true; // for local debugging
	static Mat baseImage = null;
	private static Mat lastImage = null; // for storing previous image

	// Always load library first
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Main() {

		// biteWiseBackground();
		while (true) {
			// getDiff();
			// newTest();
			//colorDiff();
			colorDiff();
		}

		// backgroundSubtractionTest();

		// medianBackgroundTest();

	}

	static void colorSpace() {

		Mat nextImage = getNextImage();
		Mat color = new Mat();

		Imgproc.cvtColor(nextImage, color, Imgproc.COLOR_RGB2YCrCb);

		Display.displayImage(color);

	}

	static void HOGExperiment() {

		HOGDescriptor hog = new HOGDescriptor();
		hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

		Mat mat = getNextImage();

		// Mat image = new Mat();
		// Imgproc.resize(nextImage, image, new Size(nextImage.width() * 0.3,
		// nextImage.height() * 0.3));

		final Scalar rectColor = new Scalar(0, 255, 0);
		final Scalar fontColor = new Scalar(255, 255, 255);

		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point();
		int framesWithPeople = 0;

		MatOfRect foundLocations = new MatOfRect();
		MatOfDouble foundWeights = new MatOfDouble();
		double hitThreshold = 0.0;
		Size winStride = new Size(8, 8);
		Size padding = new Size(32, 32);
		double scale = 1.05;
		double finalThreshold = 2.0;
		boolean useMeanshiftGrouping = false;

		hog.detectMultiScale(mat, foundLocations, foundWeights, hitThreshold, winStride, padding, scale, finalThreshold,
				useMeanshiftGrouping);

		if (foundLocations.rows() > 0) {
			framesWithPeople++;
			List<Double> weightList = foundWeights.toList();
			List<Rect> rectList = foundLocations.toList();
			int index = 0;
			
			
			for (final Rect rect : rectList) {
				rectPoint1.x = rect.x;
				rectPoint1.y = rect.y;
				rectPoint2.x = rect.x + rect.width;
				rectPoint2.y = rect.y + rect.height;
				// Draw rectangle around fond object
				Core.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);

				fontPoint.x = rect.x;
				// illustration
				fontPoint.y = rect.y - 4;
				// Print weight
				// illustration
				Core.putText(mat, String.format("%1.2f", weightList.get(index)), fontPoint, Core.FONT_HERSHEY_PLAIN,
						1.5, fontColor, 2, Core.LINE_AA, false);
				index++;
				
			}
		}
		
		Display.displayImage(mat);

	}

	static void colorDiff() {

		if (baseImage == null) {
			baseImage = getNextImage();
			// Imgproc.cvtColor(baseImage, baseImage, Imgproc.COLOR_RGB2HSV);
		}

		Mat newImage = DataSet.getNextImage();
		// Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_RGB2HSV);

		if (baseImage != null) {
			Mat diffImage = new Mat();
			Core.absdiff(newImage, baseImage, diffImage);
			Mat mask = Mat.zeros(new Size(newImage.rows(), newImage.cols()), CvType.CV_8UC1);

			double[] whiteData = { 255 };

			double threshold = 50;

			for (int i = 0; i < diffImage.rows(); i += 2) {
				for (int j = 0; j < diffImage.cols(); j += 2) {

					double[] vals = diffImage.get(i, j);
					if (vals != null) {
						double distance = vals[0] * vals[0] + vals[1] * vals[1] + vals[2] * vals[2];
						distance = Math.sqrt(distance);

						if (distance > threshold) {

							mask.put(i, j, whiteData[0]);
						}
					}

				}
			}
			Display.displayImage(mask);
		}

		lastImage = newImage;

	}

	static boolean baseInitialized = false;

	private static void biteWiseBackground() {

		Mat firstImage = getNextImage();
		Mat FirstBwImage = new Mat(firstImage.height(), firstImage.width(), CvType.CV_8UC3);
		Imgproc.cvtColor(firstImage, FirstBwImage, Imgproc.COLOR_RGB2GRAY);

		while (true) {
			Mat nextImage = getNextImage();
			Mat nextBwImage = new Mat(firstImage.height(), firstImage.width(), CvType.CV_8UC3);
			Imgproc.cvtColor(nextImage, nextBwImage, Imgproc.COLOR_RGB2GRAY);

			Mat nextImage2 = getNextImage();
			Mat nextBwImage2 = new Mat(firstImage.height(), firstImage.width(), CvType.CV_8UC3);
			Imgproc.cvtColor(nextImage2, nextBwImage2, Imgproc.COLOR_RGB2GRAY);

			Core.bitwise_or(nextBwImage, FirstBwImage, FirstBwImage);

			Display.displayImage(FirstBwImage);
			System.out.println("hallaj");

		}
	}

	private static void newTest() {

		Mat newImage = getNextImage();
		Mat filteredImage = new Mat();
		Imgproc.bilateralFilter(newImage, filteredImage, 10, 200, 200);
		Mat grayImage = new Mat(filteredImage.height(), filteredImage.width(), CvType.CV_8UC3);
		Imgproc.cvtColor(filteredImage, grayImage, Imgproc.COLOR_RGB2GRAY);

		if (lastImage != null) {
			Mat diffImage = new Mat(grayImage.height(), grayImage.width(), CvType.CV_8UC3);
			Core.absdiff(grayImage, lastImage, diffImage);

			final Mat kernel = Mat.ones(new Size(5, 5), CvType.CV_8UC1);
			final Mat morphImage = new Mat(diffImage.height(), diffImage.width(), CvType.CV_8UC3);

			Imgproc.morphologyEx(diffImage, morphImage, Imgproc.MORPH_CLOSE, kernel);

			final Mat thresImage = new Mat(diffImage.height(), diffImage.width(), CvType.CV_8UC3);

			Imgproc.threshold(morphImage, thresImage, 50, 255, Imgproc.THRESH_BINARY);

			Display.displayImage(thresImage);
		}

		lastImage = grayImage;

	}

	private static void getDiff() {

		if (!baseInitialized) {
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

		if (lastImage != null) {

			Core.absdiff(newImageGray, lastImage, diffImage);
			Imgproc.threshold(diffImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);

			Imgproc.blur(thresImage, thresImage, new Size(blurSize, blurSize));

			// TODO: check out Core.adaptiveThreshold for this later
			Imgproc.threshold(thresImage, thresImage, threshold, 255, Imgproc.THRESH_BINARY);

			// ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			//
			//// Imgproc.findContours(thresImage, contours, new Mat(),
			// Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
			//
			//
			// boolean objectDetected = false;
			//
			// if (contours.size() > 0){
			// objectDetected = true;
			// }

			// if (objectDetected){
			//
			// for(int i=0; i< contours.size();i++){
			//
			// if (Imgproc.contourArea(contours.get(i)) > 50 ){
			// Rect rect = Imgproc.boundingRect(contours.get(i));
			// Core.rectangle(newImage, new Point(rect.x,rect.height), new
			// Point(rect.y,rect.width),new Scalar(0,0,255));
			// }
			// }
			// }
		}

		boolean noMotion = true;

		for (int col = 0; col < thresImage.height(); col += 32) {
			for (int row = 0; row < thresImage.height(); row += 32) {
				double[] valArr = (thresImage.get(row, col));
				double var = valArr[0];
				if (var != 0) {
					noMotion = false;
				}
			}
		}

		lastImage = newImageGray;
		if (lastImage == null || noMotion) {
			lastImage = newImageGray;
		}
		// lastImage = newImageGray;
		System.out.println("MOTION: " + !noMotion);

		Display.displayImage(diffImage);

	}

	static Mat mask = new Mat();

	private static void backgroundSubtractionTest() {

		// Test variables
		int history = 500;
		float varThreshold = 1024;
		boolean detectShadows = false;
		BackgroundSubtractorMOG mog = new BackgroundSubtractorMOG();

		while (true) {

			mog.apply(getNextImage(), mask);

			Display.displayImage(mask);

		}
	}

	@SuppressWarnings("unchecked")
	private static void medianBackgroundTest() {

		LinkedList<Mat> fifo = new LinkedList<>();
		int populationSize = 50;

		for (int i = 0; i < populationSize; i++) {
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

		for (int row = 0; row < resizeRows; row++) {
			for (int col = 0; col < resizeCols; col++) {
				double[] intensityData = img.get(row, col);
				double data = intensityData[0];
				valueStores[row][col].push(data);
			}
		}

		// Later
		Mat medianBackground = new Mat(resizeCols, resizeRows, CvType.CV_8UC3);
		for (int i = 0; i < resizeRows; i++) {
			for (int j = 0; j < resizeCols; j++) {

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

		if (nextImage != null) {
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;


/** This class is based on the following repo:
 * https://github.com/badlogic/opencv-fun/blob/master/src/pool/tests/Cluster.java
 * 
 * Edited to fit this project and added some extra features, removed the display features from the original
 * Can be heavily optimized to suite this implementation
 */

public class Cluster {
	
	// Global for convenience, should be refactored
	static Map<Integer, Integer> counts;
	static Mat centers;
	
	// For debug/presentation
	static Display testDisplay1 = new Display();
	
	
	// Load open cv core lib
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public static int[] getColor (Mat original, Mat mask, double scale, boolean SHOW_MASKED_ORIGINAL) {	
		
		// Rescale original and save a clone
		Mat originalScaled = original.clone();
		Imgproc.resize(originalScaled, originalScaled, new Size(original.width() * scale, original.height() * scale));
		
		// Rescale mask to be compatible with rescaled original
		Mat maskScaled = mask.clone();
		Imgproc.resize(maskScaled, maskScaled, new Size(mask.width() * scale, mask.height() * scale));

		// Create en empty mat to fill with the foreground pixels of the original (as indicated in mask)
		Mat img = Mat.zeros(originalScaled.rows(), originalScaled.cols(), originalScaled.type());
		
		// Fill the mat
		for (int i = 0; i < maskScaled.height(); i++) {
			for (int j = 0; j < maskScaled.width(); j++) {

				if (maskScaled.get(i, j)[0] != 0) {
					

					int r = (int) originalScaled.get(i, j)[0];
					int g = (int) originalScaled.get(i, j)[1];
					int b = (int) originalScaled.get(i, j)[2];
					
					img.put(i, j, r, g ,b);
				}
			}
		}
		
		if (SHOW_MASKED_ORIGINAL) {
			
			Mat imgEnlarged = img.clone();
			Imgproc.resize(img, imgEnlarged, new Size(img.width() / scale, img.height() / scale));
			testDisplay1.displayImage(imgEnlarged);

		}

		// Empty the list of counts, having the label as key and cluster size as value
		counts = new HashMap<Integer, Integer>();
		// Perform the kNN clustering
		List<Mat> cluster = cluster(img, 5);
		
		// Find largest cluster and its label
		int maxCount = 0;
		int maxIndex = 0;
		for (int i = 0; i < 5; i++) {
			if (counts.get(i) > maxCount &&
					(centers.get(i, 2)[0] != 0 &&
					centers.get(i, 1)[0] != 0 &&
					centers.get(i, 0)[0] != 0)) {
				maxCount = counts.get(i);
				maxIndex = i;
			}
		}
		
		// Get centroid color value from largest cluster
		int r = (int)centers.get(maxIndex, 2)[0];
		int g = (int)centers.get(maxIndex, 1)[0];
		int b = (int)centers.get(maxIndex, 0)[0];
		
		// Return RGB of centroid		
		int[] prominentColor = {r,g,b};
		return prominentColor;
	}
	
	/**
	 * Perform clustering
	 * @param cutout
	 * @param k
	 * @return
	 */
	
	public static List<Mat> cluster(Mat cutout, int k) {
		Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
		Mat samples32f = new Mat();
		samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
		
		Mat labels = new Mat();
		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
		centers = new Mat();
		
		Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);	
		
		return showClusters(cutout, labels, centers);
	}
	
	/**
	 * This method is written by author of repo linked in top of file, used there for visualizing color cluster
	 * Here it is edited to be used only for counting the entries in the clusters, could be heavily optimized in future
	 * @param cutout
	 * @param labels
	 * @param centers
	 * @return
	 */

	private static List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
		centers.convertTo(centers, CvType.CV_8UC1, 255.0);
		centers.reshape(3);
		
		List<Mat> clusters = new ArrayList<Mat>();
		for(int i = 0; i < centers.rows(); i++) {
			clusters.add(Mat.zeros(cutout.size(), cutout.type()));
		}
		
		for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);
		
		int rows = 0;
		for(int y = 0; y < cutout.rows(); y++) {
			for(int x = 0; x < cutout.cols(); x++) {
				
				int label = (int)labels.get(rows, 0)[0];
				
				int r = (int)centers.get(label, 2)[0];
				int g = (int)centers.get(label, 1)[0];
				int b = (int)centers.get(label, 0)[0];
				
				counts.put(label, counts.get(label) + 1);
				clusters.get(label).put(y, x, b, g, r);
				rows++;
			}
		}
		return clusters;
	}
}
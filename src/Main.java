
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.*;

/**
 * Reads and analysis images
 * @author Karl
 *
 */

public class Main {
	
	public static boolean WEBCAM_MODE = true;
	
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public Main(){
		Mat mat = getNextImage();
		System.out.println("mat = " + mat.dump());		
	}
	
	
	
	public static void main(String[] args) {
	}  
	
	
	/**
	 * For Portability
	 * @return image from specified source
	 */
	public Mat getNextImage(){
		if (WEBCAM_MODE){
			return CamVideo.getImage();
		}
		
		else{
			System.err.println("getNextImage not specified correctly");
			System.exit(0);
			return null;
		}
	}
}

/**
 * Used for debug purposes, images are retrieved through sockets in release
 * @author Karl
 *
 */

class CamVideo {

	private static VideoCapture videoCapture;	

	public CamVideo() {
	}
		
	static{		
		videoCapture = new VideoCapture(0);
		if (!videoCapture.isOpened()) {
			System.err.println("Did not connect to camera, try changing video capture paramter index");
			System.exit(0);
		} 	
	}
	
	public static Mat getImage(){
		Mat temp = new Mat();
		videoCapture.retrieve(temp);
		return temp;
	}
}
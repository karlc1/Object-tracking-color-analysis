import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;


/**
 * Used for debug purposes, images are retrieved through sockets in release
 * 
 * @author Karl
 *
 */

public class CamVideo {

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
		videoCapture.read(temp);
		
		return temp;
	}
}
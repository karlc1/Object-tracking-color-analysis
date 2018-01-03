import java.io.File;
import java.sql.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

public class BackgroundSubtractor {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public BackgroundSubtractor() {
		// Test variables
		int history = 500;
		float varThreshold = 1024;
		boolean detectShadows = false;
		Mat mask = new Mat();
		BackgroundSubtractorMOG mog = new BackgroundSubtractorMOG(50, 6, 3);

		BackgroundSubtractorMOG2 mog2 = new BackgroundSubtractorMOG2(200, 200);

		while (true) {

			Mat img = DataSet.getNextImage();

			Mat original = img.clone();

			Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
			Imgproc.GaussianBlur(img, img, new Size(1, 1), 0);

			mog2.apply(img, mask);

			int dilation_size = 1;

			Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
					new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
			Imgproc.dilate(mask, mask, element1);
			Display.displayImage(mask);
			
			
			
			
			for (int i = 0; i < mask.height(); i++){
				for (int j = 0; j < mask.width(); j++){
					
					if (mask.get(i, j)[0] == 0){

						ColorData data = new ColorData(original.get(i, j)[0], original.get(i, j)[0], original.get(i, j)[0]);

						// Do something with data
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Program started BG..");
		new BackgroundSubtractor();
	}

	private static void backgroundSubtractionTest() {

	}
	
	
	

}

class ColorData{
	
	double R;
	double B;
	double G;
	Date time;
	
	public ColorData(double R, double B, double G){
		this.R = R;
		this.B = B;
		this.G = G;
	}
	
}

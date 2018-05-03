import java.sql.Timestamp;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class ImageProcessing {

	// Conditional Objects
	private ImageReader imageClient;
	private Thread imageRetrieveThread = null;
	private Database DB;

	// Non-conditional objects
	private final BackgroundSubtractorMOG2 mog2;
	private final Mat dilation_mat;
	private final Mat backgroundMask;

	// Visualization displays
	private final Display displayOriginal = new Display();
	private final Display displayMotion = new Display();
	private final Display displayColor = new Display();

	///////// SETTINGS ////////////

	// Tweakable Settings for Performance
	private final static int HISTORY = 300;
	private final static int THRESHOLD = 200;
	private final static int GAUSSIAN_BLUR = 7;
	private final static int DILATION_SIZE = 3;
	
	private final static double CLUSTERING_SCALING = 0.5;	//only affects speed (lower = faster)

	// Networking settings
	private static final String ipAddress = "192.168.20.249";
	private final static boolean USE_DATABASE_STORAGE = false;
	private final static boolean USE_IMAGE_STREAM = false;

	// Display settings
	private final static boolean USE_DEBUG_PRINTS = true;
	private final static boolean SHOW_ORIGINAL_IMAGE = true;
	private final static boolean SHOW_BACKGROUND_MASK = true;
	private final static boolean SHOW_COLOR_AVERAGE = true;
	private final static boolean SHOW_MASKED_ORIGINAL = true;

	// IMPORTANT: TEST BEFORE PRESENTATION
	private static final boolean INVERT_COLOR_DISPLAY = true;
	private static final boolean USE_CLUSTERING = true;

	// Load the library at startup
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Constructor, sets up the necessary variable objects for the program to
	 * function, kicks of the main loop with run()-function
	 */
	public ImageProcessing() {
		// Initialize Program Variables
		backgroundMask = new Mat();
		mog2 = Video.createBackgroundSubtractorMOG2(HISTORY, THRESHOLD, false);
		dilation_mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * DILATION_SIZE + 1, 2 * DILATION_SIZE + 1));

		if (USE_DATABASE_STORAGE) {
			DB = new Database();
		}

		if (USE_IMAGE_STREAM) {
			imageClient = new ImageReader(ipAddress);
			imageRetrieveThread = new Thread(imageClient);
			imageRetrieveThread.start();
		}

		// Start program loop
		run();
	}

	/**
	 * The main loop of the program
	 */
	private void run() {
		while (true) {
			// Get next Image for processing
			Mat img = this.getNextImage();
			// Skip if not ready
			if (img == null) {
				continue;
			}
			// Make a copy of the original
			Mat original = img.clone();

			// Update background mask with current image
			updateBackgroundMask(img);

			// Use mask to get colors from current image
			calculateColorAverage(original);
		}
	}

	/**
	 * Updates background mask with values from current image
	 * 
	 * @param nextImage
	 */
	private void updateBackgroundMask(Mat nextImage) {
		Imgproc.cvtColor(nextImage, nextImage, Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(nextImage, nextImage, new Size(GAUSSIAN_BLUR, GAUSSIAN_BLUR), 0);
		mog2.apply(nextImage, backgroundMask);
	}

	/**
	 * Calculates the average color of the relevant pixel positions of the original
	 * image as derived from the current state of the mask
	 * 
	 * @param original
	 */
	private void calculateColorAverage(Mat original) {

		Mat dilated_mask = new Mat();

		Imgproc.dilate(backgroundMask, dilated_mask, dilation_mat);
		// Experiment, maybe remove
		// Imgproc.cvtColor(original, original, Imgproc.COLOR_RGB2Lab);

		int averageRed;
		int averageGreen;
		int averageBlue;

		if (USE_CLUSTERING) {
			int[] colors = Cluster.getColor(original, dilated_mask, CLUSTERING_SCALING, SHOW_MASKED_ORIGINAL);
			
			averageRed = colors[0];
			averageGreen = colors[1];
			averageBlue = colors[2];
			
		} else {  // Below is the naive color extraction code, only use if clustering breaks. It is worse in every single way

			double totalRed = 0;
			double totalGreen = 0;
			double totalBlue = 0;
			int counter = 0;

			
			for (int i = 0; i < dilated_mask.height(); i++) {
				for (int j = 0; j < dilated_mask.width(); j++) {

					if (dilated_mask.get(i, j)[0] != 0) {

						totalRed += original.get(i, j)[0] * original.get(i, j)[0];
						totalGreen += original.get(i, j)[1] * original.get(i, j)[1];
						totalBlue += original.get(i, j)[2] * original.get(i, j)[2];

						counter++;
					}
				}
			}
			
			averageRed = (int) Math.sqrt((totalRed / counter));
			averageGreen = (int) Math.sqrt((totalGreen / counter));
			averageBlue = (int) Math.sqrt((totalBlue / counter));
		}



		Timestamp timeStap = new Timestamp(System.currentTimeMillis());

		if (USE_DATABASE_STORAGE) {
			DB.insertData(timeStap, averageRed, averageGreen, averageBlue);
		}

		if (USE_DEBUG_PRINTS) {
			System.out.println(
					"Color average retrieved: \t R: " + averageRed + "\t G: " + averageGreen + "\t B: " + averageBlue);
		}

		if (SHOW_ORIGINAL_IMAGE) {
			displayOriginal.displayImage(original);
		}

		if (SHOW_BACKGROUND_MASK) {
			displayMotion.displayImage(backgroundMask);
		}

		if (SHOW_COLOR_AVERAGE) {
			Mat uniColor = original.clone();

			// If clustering is used, this needs to be inverted for now
			if (INVERT_COLOR_DISPLAY) {
				uniColor.setTo(new Scalar(averageBlue, averageGreen, averageRed));
			} else {
				uniColor.setTo(new Scalar(averageRed, averageGreen, averageBlue));
			}
			displayColor.displayImage(uniColor);
		}
	}

	/**
	 * Returns the next image, either streamed from the axis camera or directly from
	 * the webcam of the running PC, depending on the boolean value USE_IMAGE_STREAM
	 * 
	 * @return
	 */
	private Mat getNextImage() {
		if (USE_IMAGE_STREAM) {
			byte[] nextImageArr = imageClient.getLastImage();
			if (nextImageArr == null) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			Mat mat = Imgcodecs.imdecode(new MatOfByte(nextImageArr), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
			return mat;
		} else {
			return CamVideo.getImage();
		}
	}

	public static void main(String[] args) {
		System.out.println("Program started BG..");
		new ImageProcessing();
	}

}


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

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

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public Main() {
		
		System.out.println("Program started..");
		
		while(true){
			
			Display.displayImage(Display.mat2BufferedImage(CamVideo.getImage()));
		}
	}

	public static void main(String[] args) {
		new Main();
	}

	/**
	 * For Portability
	 * 
	 * @return image from specified source
	 */
	public Mat getNextImage() {
		if (WEBCAM_MODE) {
			return CamVideo.getImage();
			
		}

		else {
			System.err.println("getNextImage not specified correctly");
			System.exit(0);
			return null;
		}
	}
}


import java.io.File;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class DataSet {
	
	private static String path = "C:\\Users\\Karl\\Desktop\\JPEGS";
	private static File dir = new File(path);
	private static File[] directoryListing = dir.listFiles();
	private static int currIndex = 0;
	
	
	public static Mat getNextImage(){
		File nextFile = directoryListing[currIndex++];
		return Highgui.imread(nextFile.getAbsolutePath());
	}
	
	
	
	

}

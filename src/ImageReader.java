
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Thread used as camera client, used to get images from axis camera
 *
 */

public class ImageReader implements Runnable {
	
	private byte[] lastImage = null;

	static Socket socket = null; // socket
	static DataOutputStream dos = null; // output stream
	int resWidth = 640;
	int resHeight = 480;
	Thread streamThread = null;
	boolean isFinished = false;

	public ImageReader(String ipAddress) {
		System.out.println("Enter : main");
		
		// CREATE SOCKET
		createSocket(ipAddress);
		// Send DATA TO SERVER SOCKET
		try {
			sendDatatoSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Exit : main");
	}


	public void run() {
		System.out.println("Enter Thread");
		try {
			this.receiveImage();
		} catch (IOException e) {
			e.printStackTrace();
			// CLOSE SOCKET
			try {
				socket.close();
				System.out.println("Executed : socket.close()");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// CLOSE SOCKET
		try {
			socket.close();
			System.out.println("Executed : socket.close()");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createSocket(String ipAddress) {
		System.out.println("Enter : createSocket");
		// Create socket connection
		try {
			socket = new Socket(ipAddress, 5555);
		} catch (UnknownHostException e) {
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O : Exit program");
			System.exit(1);
		}
		System.out.println("Exit : createSocket");
	}

	public void sendDatatoSocket() throws IOException {
		System.out.println("Enter : sendDatatoSocket");
		int frequency = 30;

		dos = new DataOutputStream(socket.getOutputStream());
		dos.writeInt(frequency);
		dos.writeInt(resWidth);
		dos.writeInt(resHeight);
		dos.flush();
		System.out.println("frequency=" + frequency + "resWidth=" + resWidth
				+ "resHeight=" + resHeight);
		System.out.println("Exit : sendDatatoSocket");
	}

	public void receiveImage() throws IOException {
		System.out.println("Enter: receiveImage");
		char done = '1';
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		while (isFinished == false) {
			//System.out.println("I am here : 1");
			// Read first 4 bytes, int representing the length of the following
			// image
			int imageLength = 0;
			try {
				imageLength = dis.readInt();
				// Send confirmation
				dos.write(done);
				dos.flush();

				//System.out.println("length of image = " + imageLength);

				if (imageLength == -55) {
					isFinished = true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isFinished = true;
			}

			if (imageLength > 0) {
				//System.out.println("I am here : 2");
				// Read the image itself
				byte[] byteArray = new byte[imageLength];
				int bytesRead = 0, totalBytesRead = 0;
				while (totalBytesRead < imageLength) {
					bytesRead = dis.read(byteArray, totalBytesRead, imageLength
							- totalBytesRead);
					totalBytesRead += bytesRead;
					//System.out.println("bytesRead  = " + bytesRead);
				}
				//System.out.println("totalBytesRead  = " + totalBytesRead);
				// Save last image for BackgorundSubtractor to use 
				lastImage = byteArray;

				// Inform server socket that jpeg is read.
				// Send confirmation
				try {
					dos.write(done);
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}// end of if
			else {
				// Error case : imageLength <= 0
				isFinished = true;
			}
		}// end of while
		dis.close();
		dos.close();
		System.out.println("Exit: receiveImage");
	}// end of function

	public byte[] getLastImage() {
		return lastImage;
	}
	
}
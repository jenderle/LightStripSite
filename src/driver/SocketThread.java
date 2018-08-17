package driver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Awful hack Ryan made me do it
 * "Multithread the TCP"...
 */
public class SocketThread implements Runnable {
	
	private DataOutputStream outputStream;
	private byte[] dataToSend;
	
	public SocketThread(DataOutputStream outputStream, byte[] dataToSend) {
		this.outputStream = outputStream;
		dataToSend = Arrays.copyOf(dataToSend, dataToSend.length);
	}

	@Override
	public void run() {
		try {
			outputStream.write(dataToSend); // Send the internal buffered frame to the RXsocket
			outputStream.flush();
		} catch (IOException e) {
			System.out.println("Socket exception.");
			System.out.println(e.getMessage());
		}
	}

}

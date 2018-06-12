package driver;

import java.io.*;
import java.net.*;

import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LedStrip {

	byte[] externalbuffer;
	byte[] internalbuffer;
	boolean transmitting;
	boolean isValid;
	int size;
	Socket skt;

	BufferedReader in;
	DataOutputStream dater;

	public LedStrip(int num_pixels, Socket myskt) throws Exception { // constructor
		externalbuffer = new byte[num_pixels * 3];
		internalbuffer = new byte[num_pixels * 3];
		size = num_pixels;
		transmitting = false;
		skt = myskt;
		isValid = true;

		in = new BufferedReader(new InputStreamReader(skt.getInputStream())); // create input 'file'
		dater = new DataOutputStream(skt.getOutputStream());

	}

	public void sendframe(byte[] newarray) throws Exception {
		externalbuffer = newarray;
	}

	public boolean buffermatch() throws Exception {
		return ((Arrays.equals(externalbuffer, internalbuffer) && !transmitting)); // Say the buffers still don't match
																					// even when the do if we are still
																					// transmitting
	}

	public int length() throws Exception {
		return (size);
	}

	public void run() throws Exception { // TODO: Make this threadable
		transmitting = true;
		internalbuffer = Arrays.copyOf(externalbuffer, externalbuffer.length); // copy working buffer into internal
																				// buffer
		if (isValid == true) {
			dater.write(internalbuffer); // Send the internal buffered frame to the RXsocket
			dater.flush();

			Date timeoutdate = new Date(); // Start counting ms for timeout
			long start_timeout_time = timeoutdate.getTime();
			boolean gotvalidping = false;
			while (!gotvalidping) {
				label1: while (!in.ready()) {// block until client has acked, or we have timed out

					timeoutdate = new Date();
					if (timeoutdate.getTime() - start_timeout_time > 1000) { // If we have timed-out
						System.out.println("Timeout! ");

						while (timeoutdate.getTime() - start_timeout_time < 20000) {
							if (in.ready()) {
								String myping = in.readLine();
								System.out.println("Received ping in timeout case: " + myping);
								// Startofblock
								String pattern = "(\\d)+";
								Pattern r = Pattern.compile(pattern);
								Matcher m = r.matcher(myping);

								if (m.find()) {
									System.out.println("Got Ping after Timeout! Value: " + m.group(0));
									transmitting = false;
									gotvalidping = true; // Set to true to break the loop.
									break label1;
								}
								// Endofblock
							}

							timeoutdate = new Date(); // update the time
						}
						//TODO: Here's where to fix the memory leak!
							System.out.println("Invalid Ping! or Timed out waiting for ping!");
							// Close the socket and move on with life
							isValid = false;
							skt.shutdownInput();
							skt.shutdownOutput();
							// We might need to add a small delay here to prevent an IO Exception
							skt.close();

							transmitting = false;
							gotvalidping = true; // Set to true to break the loop.
							break label1;
						
					}
				}
				if (in.ready()) { // if we got a ping, read it in
					String ping = in.readLine(); // get next

					String ack_code = "<A>";

					if (ping.contains(ack_code)) { // If we found a valid ack
						transmitting = false;
						gotvalidping = true;
					}
				}
			}
		} else { // Let the code keep on without writing to the socket so we don't throw an
					// exception.... and slowly memory leak. #TODO: Don't make this a memory leak!
			transmitting = false;
		}
	}

}
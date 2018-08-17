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
	Date lastRx;
	
	long PingWaitTime;
	long AckWaitTime;

	public LedStrip(int num_pixels, Socket myskt) throws Exception { // constructor
		externalbuffer = new byte[num_pixels * 3];
		internalbuffer = new byte[num_pixels * 3];
		size = num_pixels;
		transmitting = false;
		skt = myskt;
		isValid = true;
		PingWaitTime = 10000; //time in ms to wait for ping
		AckWaitTime = 1500; //allowable time to wait for a packet ACK

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
	
	public long lasttxms() throws Exception {
		return (lastRx.getTime());
	}
	
	public boolean isvalid() throws Exception {
		return (isValid);
	}

	public int length() throws Exception {
		return (size);
	}

	public void run() throws Exception { // TODO: Make this threadable
		transmitting = true;
		internalbuffer = Arrays.copyOf(externalbuffer, externalbuffer.length); // copy working buffer into internal
																				// buffer
		if (isValid == true) {
			
			try {
				Runnable multiThreadTheTCP = new SocketThread(dater, internalbuffer);
				multiThreadTheTCP.run();
				dater.flush(); // We hope that this will crash if the socket's dead.
			} catch(IOException e) {
				//kill socket, flag as invalid
				System.out.println("Threw IOException! (Probably writing to a dead socket)");
				System.out.println("Closing socket and flagging this strip as invalid...");
				
				killsocket(); // Close the socket and move on with life
			}
			
			checkfortimeout(); //Call our internal checkfortimeout function to wait for ack!

			
		} else { // Let the code keep on without writing to the socket so we don't throw an
					// exception.... and slowly memory leak. #TODO: Don't make this a memory leak!
			transmitting = false;
		}
	}
	
	void checkfortimeout() throws Exception{
		Date timeoutdate = new Date(); // Start counting ms for timeout
		long start_timeout_time = timeoutdate.getTime();
		boolean gotvalidping = false;
		while (!gotvalidping && isValid) {
			while (!in.ready() && transmitting == true && isValid == true) {// block until client has acked, or we have timed out

				timeoutdate = new Date();
				if (timeoutdate.getTime() - start_timeout_time > AckWaitTime) { // If we have timed-out
					System.out.println("Timeout! ");

					gotvalidping = waitforping(start_timeout_time);
					//TODO: Can delete this object when isValid = false
					if(!gotvalidping) {
						System.out.println("Invalid Ping! or Timed out waiting for ping!");
						if(isValid) {
							killsocket(); // Close the socket and move on with life
						}
						transmitting = false;
						gotvalidping = true; // Set to true to break the loop.
					}
					
				}
			}
			if (in.ready()) { // if we got a ping, read it in
				String ping = in.readLine(); // get next

				String ack_code = "<A>";

				if (ping.contains(ack_code)) { // If we found a valid ack
					transmitting = false;
					gotvalidping = true;
					lastRx = new Date(); //set last received time to now!
				}
			}
		}
	}
	
	void killsocket() throws Exception{
		if(isValid) {
			// Close the socket and move on with life
			isValid = false;
			//skt.shutdownInput();
			//skt.shutdownOutput();
			// We might need to add a small delay here to prevent an IO Exception
			skt.close();
		}
	}
	
	boolean waitforping(long start_timeout_time) throws Exception{
		Date waitingdate = new Date(); // Start counting ms for timeout
		while (waitingdate.getTime() - start_timeout_time < PingWaitTime) {
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
					lastRx = new Date(); //set last received time to now!
					return(true); // Set to true to break the loop.
				}
				// Endofblock
			}

			waitingdate = new Date(); // update the time
		}
		return(false);
	}

}
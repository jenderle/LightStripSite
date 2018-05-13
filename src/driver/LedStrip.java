package driver;

import java.lang.*;
import java.io.*;
import java.net.*;

import java.util.Arrays;
import java.util.Date;

public class LedStrip {

	byte[] externalbuffer;
	byte[] internalbuffer;
	boolean transmitting;
	int size;
	Socket skt;

	BufferedReader in;
   	DataOutputStream dater;
	

	public LedStrip(int num_pixels, Socket myskt) throws Exception { //constructor
    	externalbuffer = new byte [num_pixels*3];
    	internalbuffer = new byte [num_pixels*3];
    	size = num_pixels;
    	transmitting = false;
    	skt = myskt;

    	in = new BufferedReader(new InputStreamReader(skt.getInputStream())); // create input 'file'
    	dater = new DataOutputStream(skt.getOutputStream());

	}

	public void sendframe(byte[] newarray) throws Exception{
		externalbuffer = newarray;
	}

	public boolean buffermatch() throws Exception{
		return(Arrays.equals(externalbuffer, internalbuffer));
	} 

	public int length() throws Exception{
		return(size);
	} 

	public void run() throws Exception{
		transmitting = true;
		internalbuffer = Arrays.copyOf(externalbuffer, externalbuffer.length); //copy working buffer into internal buffer
		dater.write(internalbuffer); //Send the internal buffered frame to the RXsocket
   		dater.flush();
      	Date timeoutdate = new Date(); //Start counting ms for timeout
      	long start_timeout_time = timeoutdate.getTime();
      	while (!in.ready()) {// block until client has acked, or we have timed out
         	timeoutdate = new Date();
         	if(timeoutdate.getTime() - start_timeout_time> 5000){ //If we have timed-out
            	System.out.println("Timeout! ");
         		break;
      		}
		}
		if(in.ready()){ //if we got a ping, read it in
      		String ping = in.readLine(); //get next
      	}
	}

}
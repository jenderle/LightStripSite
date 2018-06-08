package driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StripConnector implements Runnable {

	private Socket tempskt; //The socket for a client
	private BufferedReader in;
	
	private ArrayList<LedStrip> mainstriplist;
	private ServerSocket myserver;
	
	public StripConnector(ArrayList<LedStrip> mainstriplist, ServerSocket myserver) {
		this.mainstriplist = mainstriplist;
		this.myserver = myserver;
	}
	
	
	
	/**
	 * Constantly acquires new clients and adds them to the list
	 * 
	 * @param mainstriplist list of strips to add to
	 * @param myserver server to peel off LED strips from
	 */
	public void run(){
		try {
		
			while(true) {
				tempskt = myserver.accept(); // acquire client.
				tempskt.setSendBufferSize(100); //sent recieve buffer size to 25bytes
				tempskt.setReceiveBufferSize(100); //sent recieve buffer size to 25bytes
				System.out.println("Client connected Buff = 100.");
		
				in = new BufferedReader(new InputStreamReader(tempskt.getInputStream())); // create input 'file'
		
				while (!in.ready()) {
		         // we got some data from the client
		         
				}
				String myping = in.readLine();
				System.out.println("Received ping: " + myping);
				
				String pattern = "(\\d)+";
		        Pattern r = Pattern.compile(pattern);
		        Matcher m = r.matcher(myping);
		
		        if (m.find( )) {
		            System.out.println("Number of LEDs: " + m.group(0) );
		            //Create new LEDStrip object with number of leds
		            int numberofleds = Integer.parseInt(m.group(0));
		            LedStrip templedstrip = new LedStrip(numberofleds,tempskt); // Create an led strip object
		            mainstriplist.add(templedstrip); //add this to the list
		            
		         }else {
		            System.out.println("NO Int in String!");
		            //Close the socket and move on with life
		            tempskt.shutdownInput();
		            tempskt.shutdownOutput();
		            //We might need to add a small delay here to prevent an IO Exception
		            tempskt.close();
		         }
			}
		} catch (Exception e) {
			// TODO: Handle exceptions better so that we don't crash.
			System.out.println("StripConnector broke. Exception message: ");
			e.printStackTrace(System.out);
		}
	}
}

package driver;

import java.io.*;
import java.net.*;

import transfer.AnimationItem;
import transfer.AnimationSequence;


public class SampleLightServer implements Runnable {

   // TODO: edit this to be the port you wanna communicate on
   private final int PORT = 1090;

   private BufferedReader consoleinput;
   private BufferedReader in;
   private DataOutputStream dater;

   private ServerSocket srvr; //The server
   private Socket skt; //The socket for a client

   private LedStrip myled;
   private FrameGenerator mygen;
   
   private AnimationSequence frames;

   public static void main(String args[]) {
      try {
    	  SampleLightServer testServer = new SampleLightServer(null); // TODO: Ryan you'll wanna put an actual animation sequence in there
          testServer.commsTest();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   public SampleLightServer(AnimationSequence frames) {
	   this.frames = frames;
   }

   public void commsTest() throws Exception {
      consoleinput = new BufferedReader(new InputStreamReader(System.in));

      srvr = new ServerSocket(PORT); // create server
      System.out.println("Server started.");
      skt = srvr.accept(); // acquire client.
      skt.setSendBufferSize(100); //sent recieve buffer size to 25bytes
      skt.setReceiveBufferSize(100); //sent recieve buffer size to 25bytes
      System.out.println("Client connected Buff = 100.");

      in = new BufferedReader(new InputStreamReader(skt.getInputStream())); // create input 'file'
      dater = new DataOutputStream(skt.getOutputStream());

      while (!in.ready()) {
         // we got some data from the client
         
      }
      String myping = in.readLine();
      System.out.println("Received ping: " + myping);
      //TODO: EXTRACT NUMBER OF LEDS FROM PING!

//TODO: Pass number of LEDs from ping instead of hard coded 300
      myled = new LedStrip(300, skt); //num pixels, socket 
      mygen = new FrameGenerator();


   int run = 1;
   while(run == 1){ //keep going while connection is active

      
      

      Thread.sleep(2000); // Just pause for a second
      
      int numberFrames = frames.getItems().size();

      for(int i=0; i<numberFrames-1; i++) {
    	  AnimationItem currentFrame = frames.getItems().get(i);
    	  AnimationItem nextFrame = frames.getItems().get(i+1);
    	  
	      if(mygen.sweep(currentFrame.getRed() , currentFrame.getGreen(), currentFrame.getBlue(), nextFrame.getRed() , nextFrame.getGreen(), nextFrame.getBlue(), 147, myled.length(), true)){ //set up a sweep with 300 stops
	         while(!mygen.isdone()){ //keep sending frames until done
	            myled.sendframe(mygen.run());//output the next frame
	            if(!myled.buffermatch()){ //If a new frame is in the input buffer
	               myled.run(); //Ship it.
	            }
	         }
	
	      }
      }
     
      System.out.print("Sending black FRAME '" + "'\n"); // put your data here. Can be stuff other than strings.
      myled.sendframe(mygen.buildsolid((byte)0, (byte)0, (byte)0, 300)); //build a solid black frame, and send it to the strip
      Thread.sleep(1000); // Just pause for a second
      
      if (consoleinput.ready()) {
         String userinput = consoleinput.readLine();
         System.out.println("Received console input: " + userinput);
         if (userinput.equals("q")){
           System.out.println("Quitting...."); //do quitting stuff
           run = 0;
         }else if(userinput.equals("s")){
            while(!consoleinput.ready()){
               //Block until we recieve another command.
               if (in.ready()) {
                  // we got some data from the client
                  String ping = in.readLine();
                  System.out.println("Received ping: " + ping);
               }
            }
         }
      }

      
   }
   System.out.println("Exiting.");
   in.close();
   dater.close();
   skt.close();
   srvr.close();
   }

	@Override
	public void run() {
	      try {
	          this.commsTest();
	      }
	      catch(Exception e) {
	         e.printStackTrace();
	      }
	}  

}
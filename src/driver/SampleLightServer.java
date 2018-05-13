package driver;

import java.lang.*;
import java.io.*;
import java.net.*;

import java.util.Date;

class SampleLightServer {

   // TODO: edit this to be the port you wanna communicate on
   private static final int PORT = 1090;

   private static BufferedReader consoleinput;
   private static BufferedReader in;
   private static DataOutputStream dater;

   private static ServerSocket srvr; //The server
   private static Socket skt; //The socket for a client

   private static LedStrip myled;
   private static FrameGenerator mygen;

   public static void main(String args[]) {
      /*try {
         UItest();
      }
      catch(Exception e) {
         System.out.print("Whoops! It didn't work!\n");
      }*/

      try {
         commsTest();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }

   public static void UItest() throws Exception {
      consoleinput = new BufferedReader(new InputStreamReader(System.in)); // create input 'file'

      if (consoleinput.ready()) {
         String userinput = consoleinput.readLine();
         System.out.println("Received console input: " + userinput);
         if (userinput.equals("q")){
           System.out.println("Quitting...."); //do quitting stuff
         }
      }
      
   }

   public static void commsTest() throws Exception {
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
      /*System.out.print("Sending TEAL FRAMES '" + "'\n"); // put your data here. Can be stuff other than strings.
      sendframe((byte)0, (byte)255, (byte)255, 300);

      Thread.sleep(1000); // Just pause for a second
      System.out.print("Sending yellow FRAME '" + "'\n"); // put your data here. Can be stuff other than strings.
      sendframe((byte)255, (byte)255, (byte)0, 300);

      Thread.sleep(1000); // Just pause for a second
      System.out.print("Sending pink FRAME '" + "'\n"); // put your data here. Can be stuff other than strings.
      sendframe((byte)255, (byte)0, (byte)255, 300);*/
      if(mygen.sweep(255 , 96, 00, 00, 255, 255, 300, myled.length(), true)){ //set up a sweep with 300 stops
         
         while(!mygen.isdone()){ //keep sending frames until done
            myled.sendframe(mygen.run());//output the next frame
            if(!myled.buffermatch()){ //If a new frame is in the input buffer
               myled.run(); //Ship it.
            }
         }

      }

      if(mygen.sweep(00, 255, 255, 255 , 96, 00, 150, myled.length(), false)){ //set up a sweep with 150 stops
         
         while(!mygen.isdone()){ //keep sending frames until done
            myled.sendframe(mygen.run());//output the next frame
            if(!myled.buffermatch()){ //If a new frame is in the input buffer
               myled.run(); //Ship it.
            }
         }

      }

      if(mygen.sweep(255, 96, 00, 255, 0, 0, 56, myled.length(), true)){ //set up a sweep with 56 stops
         
         while(!mygen.isdone()){ //keep sending frames until done
            myled.sendframe(mygen.run());//output the next frame
            if(!myled.buffermatch()){ //If a new frame is in the input buffer
               myled.run(); //Ship it.
            }
         }

      }

      if(mygen.sweep(255,0,0, 255,255,0, 425, myled.length(), true)){ //set up a sweep with 425 stops
         
         while(!mygen.isdone()){ //keep sending frames until done
            myled.sendframe(mygen.run());//output the next frame
            if(!myled.buffermatch()){ //If a new frame is in the input buffer
               myled.run(); //Ship it.
            }
         }

      }


/*Thread.sleep(1000); // Just pause for a second
Date date = new Date();
//Returns current time in millis
long timeMilli2 = date.getTime();

   System.out.print("Doing Red Fade '" + "'\n"); // put your data here. Can be stuff other than strings.
   heartbeat(255, 0, 0, 300);
   date = new Date();
long elapsed_time = date.getTime() - timeMilli2;
System.out.println("460.8kBytes transmitted in: " + elapsed_time + " ms.");
System.out.println("DR: " + 460800000/elapsed_time + " B/s.");

   System.out.print("Doing orange Fade '" + "'\n"); // put your data here. Can be stuff other than strings.

   heartbeat(255, 96, 0, 300);
*/

      
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

   /*private static void sweep(int red1, int green1, int blue1, int red2, int green2, int blue2, int num_steps, int num_pixels, boolean dir) throws Exception{
      float step = (((float)num_pixels)/num_steps);
      byte[] temparray = new byte [num_pixels*3];


      for (int count = 0; count<num_steps; count++){

         for(int pixelcount = 0; pixelcount < num_pixels; pixelcount++){ //build a frame
            if(count*step > pixelcount){ //If we are showing a frame where this pixel should be color two
               temparray[pixelcount*3]=(byte)red2;
               temparray[(pixelcount*3)+1]=(byte)green2;
               temparray[(pixelcount*3)+2]=(byte)blue2;
            }else{
               temparray[pixelcount*3]=(byte)red1;
               temparray[(pixelcount*3)+1]=(byte)green1;
               temparray[(pixelcount*3)+2]=(byte)blue1;
            }
         }
         if(dir){
            sendframe(flipframe(temparray));
         }else{
            sendframe(temparray);
         }
         
      }
   }*/

   /*private static byte[] flipframe(byte[] frame) throws Exception{

      byte[] outframe = new byte [frame.length];
      int length = frame.length;

      for(int i = 0; i < length/3; i++){
         outframe[i*3] = frame[length-3-(i*3)];
         outframe[(i*3)+1] = frame[length-2-(i*3)];
         outframe[(i*3)+2] = frame[length-1-(i*3)];
      }

      return(outframe);
   }*/

   /*private static boolean fade(int oldred, int oldgreen, int oldblue, int newred, int newgreen, int newblue, int num_steps, int num_pixels) throws Exception{
      float thisred = oldred;
      float thisgreen = oldgreen;
      float thisblue = oldblue;

      float redstep = (((float)newred-thisred)/num_steps);
      float greenstep = (((float)newgreen-thisred)/num_steps);
      float bluestep = (((float)newblue-thisred)/num_steps);

      boolean wegud = true;
      for(int count = 0; count<(num_steps+1); count++){
         thisred = thisred+redstep;
         thisgreen = thisgreen+greenstep;
         thisblue = thisblue+bluestep;
         System.out.println("fade up: "+(int)thisred+":"+(int)thisgreen+":"+(int)thisblue);
         wegud = sendframe(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, num_pixels));
         if(!wegud){
            break;
         }
      }
      if(!wegud){
         System.out.println("We not gud.");
      }
      return(wegud);
   }*/


   /*private static boolean heartbeat(int red, int green, int blue, int num_pixels) throws Exception{
      float thisred = 0;
      float thisgreen = 0;
      float thisblue = 0;
      boolean wegud = true;
         for(int count = 0; count<256; count++){
            thisred = ((float)red*count)/255;
            thisgreen = ((float)green*count)/255;
            thisblue = ((float)blue*count)/255;
            System.out.println("fade up: "+(int)thisred+":"+(int)thisgreen+":"+(int)thisblue);
            wegud = sendframe(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, num_pixels));
            if(!wegud){
               break;
            }
         }
         for(int count = 255; count>-1; count--){
            thisred = ((float)red*count)/255;
            thisgreen = ((float)green*count)/255;
            thisblue = ((float)blue*count)/255;
            System.out.println("fade down: "+(int)thisred+":"+(int)thisgreen+":"+(int)thisblue);
            wegud = sendframe(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, num_pixels));
            if(!wegud){
               break;
            }
         }
      if(!wegud){
         System.out.println("We not gud.");
      }
      return(wegud);
   }*/

   /*private static byte[] buildsolid(int red, int green, int blue, int num_pixels) throws Exception{
      byte[] tempframe = new byte[num_pixels*3]; //initalize blank array, three bytes per pixel
      for(int i = 0; i<num_pixels; i++){ //Fill array with RGB values recieved
         tempframe[i*3]=(byte)red;
         tempframe[(i*3)+1]=(byte)green;
         tempframe[(i*3)+2]=(byte)blue;
      }

      return(tempframe); //return byte array for display
   }*/


   /*private static boolean sendframe(byte[] newarray) throws Exception{
      dater.write(newarray); //Send the frame to the RXsocket
      dater.flush();
      Date timeoutdate = new Date(); //Start counting ms for timeout
      long start_timeout_time = timeoutdate.getTime();
      while (!in.ready()) {
         // block until client has acked, or we have timed out
         timeoutdate = new Date();
         if(timeoutdate.getTime() - start_timeout_time> 5000){ //If we have timed-out
            System.out.println("Timeout! ");
            return(false);
         }
      }
      String ping = in.readLine();
      if(ping.equals("ACK")){
         return(true);
      }
      System.out.println("Doh! "+ ping);
      return(false);
   }*/
}
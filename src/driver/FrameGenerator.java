package driver;

import java.net.Socket;

public class FrameGenerator {

	byte[] buffer;
	boolean done = true;
	
	int lastTxR;
	int lastTxG;
	int lastTxB;

	float thisred;
	float thisgreen;
	float thisblue;

	float nextred;
	float nextgreen;
	float nextblue;

	float redstep;
	float greenstep;
	float bluestep;

	float step;

	int numsteps;
	int numpixels;

	boolean flipdir;

	int count;

	int lastmode = 0; //0 = static; 1=fade; 2=sweep;
	
	public FrameGenerator() throws Exception { // constructor
		lastTxR= 0;
		lastTxG= 0;
		lastTxB= 0;
	}
	
	public int lastred() {
		return (lastTxR);
	}
	
	public int lastgreen() {
		return (lastTxG);
	}
	
	public int lastblue() {
		return (lastTxB);
	}
	
	public boolean isdone() throws Exception{
		return(done);
	}

	public byte[] buildsolid(int red, int green, int blue, int num_pixels) throws Exception{
      byte[] tempframe = new byte[num_pixels*3]; //initalize blank array, three bytes per pixel
      for(int i = 0; i<num_pixels; i++){ //Fill array with RGB values recieved
         tempframe[i*3]=(byte)red;
         tempframe[(i*3)+1]=(byte)green;
         tempframe[(i*3)+2]=(byte)blue;
      }
      
      return(tempframe); //return byte array for display
   }
	
	public boolean snap(int red1, int green1, int blue1, int num_pixels) throws Exception {
		if(done) {
			lastTxR=red1;
		    lastTxG=green1;
		    lastTxB=blue1;
		    
			thisred = red1;
		    thisgreen = green1;
		    thisblue = blue1;
		    numpixels = num_pixels;
		    lastmode = 0;
		    done = false;
			return(true);
		}else {
			System.out.println("Not Ready Yet. use MYVAR.run(). Trying to set up jump.");
		}
		return(false);
	}

   	public boolean fade(int red1, int green1, int blue1, int red2, int green2, int blue2, int num_steps, int num_pixels) throws Exception{

	   	if(done){ // if we were done
	   		lastTxR=red2;
		    lastTxG=green2;
		    lastTxB=blue2;
		    
	      thisred = red1;
	      thisgreen = green1;
	      thisblue = blue1;

	      redstep = (((float)red2-red1)/num_steps);
	      greenstep = (((float)green2-green1)/num_steps);
	      bluestep = (((float)blue2-blue1)/num_steps);
	      numsteps = num_steps;
	      numpixels = num_pixels;

	      done = false;
	      lastmode = 1;
	      count = 0;
	      return(true);
	  	}else{
	  		System.out.println("Not Ready Yet. use MYVAR.run(). Trying to set up fade.");
	   	}
	   	return(false);
   	}

   	public byte[] run() throws Exception {
   		switch (lastmode){
   			case 0:
   				done = true;
   				return(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, numpixels));
   			case 1: //Do a fade
				if(count < (numsteps+1)){
	  				thisred = thisred+redstep;
	         		thisgreen = thisgreen+greenstep;
	         		thisblue = thisblue+bluestep;
	  				count = count + 1;
	  			}
				
				if(count == numsteps) {
					done = true;
				}
	  			return(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, numpixels));
   			//break;
   			case 2: //Do a sweep
   				if(count < (numsteps+2)){ //Put animation frame generation in here
	  				byte[] temparray = new byte [numpixels*3];

	  				for(int pixelcount = 0; pixelcount < numpixels; pixelcount++){ //build a frame
			            if(count*step > pixelcount){ //If we are showing a frame where this pixel should be color two
			               temparray[pixelcount*3]=(byte)nextred;
			               temparray[(pixelcount*3)+1]=(byte)nextgreen;
			               temparray[(pixelcount*3)+2]=(byte)nextblue;
			            }else{
			               temparray[pixelcount*3]=(byte)thisred;
			               temparray[(pixelcount*3)+1]=(byte)thisgreen;
			               temparray[(pixelcount*3)+2]=(byte)thisblue;
			            }
			         }
			         count = count + 1;
			         if(count == (numsteps+1)) {
			        	 done = true;
			         }
			         if(flipdir){
			            return(flipframe(temparray));
			         }else{
			            return(temparray);
			         }
			         
			         
	  			}else{
	  				done = true;
	  			}
   			break;
   			default:
   			
   				System.out.println("Nothing to Do Here!");
   				return (new byte[1]);
   		}
	return (new byte[1]);
   	}

   	public boolean sweep(int red1, int green1, int blue1, int red2, int green2, int blue2, int num_steps, int num_pixels, boolean dir) throws Exception{

	   	if(done){ // if we were done
	   		lastTxR=red2;
		    lastTxG=green2;
		    lastTxB=blue2;
		    
	      thisred = red1;
	      thisgreen = green1;
	      thisblue = blue1;

	      nextred = red2;
	      nextgreen = green2;
	      nextblue = blue2;

	      step = (((float)num_pixels)/num_steps);
	      numsteps = num_steps;
	      numpixels = num_pixels;
		  flipdir = dir;
	      done = false;
	      lastmode = 2;
	      count = 0;
	      return(true);
	  	}else{
	  		System.out.println("Not Ready Yet. Trying to set up sweep.");
	   	}
	   	return(false);
   	}

   	public static byte[] flipframe(byte[] frame) throws Exception{

      byte[] outframe = new byte [frame.length];
      int length = frame.length;

      for(int i = 0; i < length/3; i++){
         outframe[i*3] = frame[length-3-(i*3)];
         outframe[(i*3)+1] = frame[length-2-(i*3)];
         outframe[(i*3)+2] = frame[length-1-(i*3)];
      }

      return(outframe);
   }

}
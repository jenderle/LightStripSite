package driver;

import java.lang.*;
import java.io.*;
import java.net.*;

import java.util.Arrays;

class FrameGenerator {

	byte[] buffer;
	boolean done = true;

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

   	public boolean fade(int red1, int green1, int blue1, int red2, int green2, int blue2, int num_steps, int num_pixels) throws Exception{

	   	if(done){ // if we were done
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
	  		if(lastmode != 1){ //if we are in the right mode
	  			System.out.println("Not Ready Yet. use MYVAR.run()");
	   		}
	   	}
	   	return(false);
   	}

   	public byte[] run() throws Exception {
   		switch (lastmode){
   			case 1: //Do a fade
				if(count < (numsteps+1)){
	  				thisred = thisred+redstep;
	         		thisgreen = thisgreen+greenstep;
	         		thisblue = thisblue+bluestep;
	  				count = count + 1;
	  			}else{
	  				done = true;
	  			}
	  			return(buildsolid((int)thisred, (int)thisgreen, (int)thisblue, numpixels));
   			//break;
   			case 2: //Do a sweep
   				if(count < (numsteps)){ //Put animation frame generation in here
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
	  		if(lastmode != 2){ //if we are in the right mode
	  			System.out.println("Not Ready Yet");
	   		}
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
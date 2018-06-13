package driver;

import java.io.*;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import transfer.AnimationStep;
import transfer.AnimationSequence;
import transfer.AnimationSource;
import transfer.TransitionType;

public class SampleLightServer implements Runnable {

	// The port we communicate on
	private final int PORT = 1090;

	private BufferedReader consoleinput;
	private BufferedReader in;
	private DataOutputStream dater;

	private ServerSocket srvr; // The server
	private Socket skt; // The socket for a client

	private LedStrip myled;
	private FrameGenerator mygen;
	
	private AnimationSource latestAnimationFromWeb;
	
	
	private int TARGETFRAMERATE = 60; //target 60 fps


	private int lastnumstrips = 0;

	private ArrayList<LedStrip> alldastrips = new ArrayList<LedStrip>();

	public SampleLightServer(AnimationSource animationSource) {
		latestAnimationFromWeb = animationSource;
	}

	public void commsTest() throws Exception {
		consoleinput = new BufferedReader(new InputStreamReader(System.in));

		srvr = new ServerSocket(PORT); // create server
		System.out.println("Server started.");

		// Spin up thread for the multi-client connector
		Runnable r = new StripConnector(alldastrips, srvr);
		new Thread(r).start();

		int run = 1;
		while (run == 1) { // keep going while connection is active
			
			// Fetch the latest sequence from the AnimationSource
			AnimationSequence animationSequence = latestAnimationFromWeb.getAnimationSequence();

			if (alldastrips.size() > 0) {
				
				int thisrunsize = alldastrips.size();

				ArrayList<FrameGenerator> doitup = new ArrayList<FrameGenerator>(); // Make an arraylist of frame generators
				for (int i = 0; i < thisrunsize; i++) { // Make a frame generator for every led strip.
					doitup.add(new FrameGenerator());
				}

				int numberSteps = animationSequence.getSteps().size();
				System.out.println("Number of Steps: " + numberSteps );
				

				for (int i = 0; i < numberSteps - 1; i++) {
					AnimationStep currentStep = animationSequence.getSteps().get(i);
					AnimationStep nextStep = animationSequence.getSteps().get(i + 1);

					// Set up all the animations
					for (int j = 0; j < thisrunsize; j++) { // set up all teh sweeps
						switch(nextStep.getTransitionType()) {
						case JUMP:
							while(!doitup.get(j).snap(nextStep.getRed(), nextStep.getGreen(), nextStep.getBlue(),alldastrips.get(j).length())) { //clear out any weird remaining frames
								doitup.get(j).run(); //Get a remaining frame
							}
							break;
						case FADE:
							while(!doitup.get(j).fade(currentStep.getRed(), currentStep.getGreen(), currentStep.getBlue(), nextStep.getRed(), nextStep.getGreen(), nextStep.getBlue(), (nextStep.getTransitionTime()/TARGETFRAMERATE), alldastrips.get(j).length())) {
								doitup.get(j).run(); //Get a remaining frame
							}
							break;
						case SWEEP:
							while(!doitup.get(j).sweep(currentStep.getRed(), currentStep.getGreen(), currentStep.getBlue(), nextStep.getRed(), nextStep.getGreen(), nextStep.getBlue(), (nextStep.getTransitionTime()/TARGETFRAMERATE), alldastrips.get(j).length(), true)) { // set up sweeps with 300 stops
								doitup.get(j).run(); //Get a remaining frame
							}
							break;
						}
						
					}

					while (!allAnimationsDone(doitup) && allBuffersMatch() ) { // Master framegen isn't done yet and all led strips have successfully transmitted
						Date frametime = new Date(); // Start counting ms for frame time
						long start_frametime = frametime.getTime();
						for (int k = 0; k < thisrunsize; k++) { // send all the daters
							alldastrips.get(k).sendframe(doitup.get(k).run());
							if (!alldastrips.get(k).buffermatch()) { // If a new frame is in the input buffer - this
																		// line probably adds delay between stripts
								alldastrips.get(k).run(); // Ship it.
							}
						}
						
						frametime = new Date(); // Update counter
						while((frametime.getTime() - start_frametime) < (1000/TARGETFRAMERATE)) { //Block until frame time elapses
							Thread.sleep(1); //wait a millisecond
							frametime = new Date(); // Update counter
						}
						
						/*if((frametime.getTime() - start_frametime) > (1050/TARGETFRAMERATE)) { // we were 5% over our frametime
							System.out.println("Frametime was " +(frametime.getTime() - start_frametime)+" ms. Expected: "+(1000/TARGETFRAMERATE) );
						}*/

					}
					
					//Do the hold time
					Date holdtime = new Date(); // Start counting ms for hold time
					long start_holdtime = holdtime.getTime();
					
					while((holdtime.getTime() - start_holdtime) < nextStep.getDisplayTime()) { //Block until hold time elapses
						Thread.sleep(1); //wait a millisecond
						holdtime = new Date(); // Update counter
					}
					
					

				}
			}
			Thread.sleep(100);
			System.out.println("AllDaStrips Size: " + alldastrips.size() );
			

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if all buffers match
	 * 
	 * @return boolean
	 */
	private boolean allBuffersMatch() {
		boolean buffersMatch = true;
		for(LedStrip strip : alldastrips) {
			try {
				buffersMatch = buffersMatch && strip.buffermatch();
			} catch (Exception e) {
				buffersMatch = false;
			}
		}
		return buffersMatch;
	}
	
	private boolean allAnimationsDone(ArrayList<FrameGenerator> copyofgenerators) {
		boolean AnimationsDone = true;
		for(FrameGenerator generator : copyofgenerators) {
			try {
				AnimationsDone = AnimationsDone && generator.isdone();
			} catch (Exception e) {
				AnimationsDone = false;
			}
		}
		return AnimationsDone;
	}
	
	public AnimationSource getAnimationSource() {
		return latestAnimationFromWeb;
	}

}
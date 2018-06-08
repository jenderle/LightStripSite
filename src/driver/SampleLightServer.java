package driver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import transfer.AnimationStep;
import transfer.AnimationSequence;
import transfer.AnimationSource;

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

				ArrayList<FrameGenerator> doitup = new ArrayList<FrameGenerator>(); // Make an arraylist of frame
																					// generators
				for (int i = 0; i < thisrunsize; i++) { // Make a frame generator for every led strip.
					doitup.add(new FrameGenerator());
				}

				int numberSteps = animationSequence.getSteps().size();

				for (int i = 0; i < numberSteps - 1; i++) {
					AnimationStep currentFrame = animationSequence.getSteps().get(i);
					AnimationStep nextFrame = animationSequence.getSteps().get(i + 1);

					// Set up all the sweeps
					for (int j = 0; j < thisrunsize; j++) { // set up all teh sweeps
						doitup.get(j).sweep(currentFrame.getRed(), currentFrame.getGreen(), currentFrame.getBlue(),
								nextFrame.getRed(), nextFrame.getGreen(), nextFrame.getBlue(), 300, alldastrips.get(j).length(),
								true); // set up sweeps with 300 stops
					}

					while (!doitup.get(0).isdone() && allBuffersMatch() ) { // Master framegen isn't done yet and all led strips have successfully transmitted
						for (int k = 0; k < thisrunsize; k++) { // send all the daters
							alldastrips.get(k).sendframe(doitup.get(k).run());
							if (!alldastrips.get(k).buffermatch()) { // If a new frame is in the input buffer - this
																		// line probably adds delay between stripts
								alldastrips.get(k).run(); // Ship it.
							}
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

}
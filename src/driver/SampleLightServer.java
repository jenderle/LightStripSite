package driver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import transfer.AnimationItem;
import transfer.AnimationSequence;
import java.util.*;

public class SampleLightServer implements Runnable {

	// TODO: edit this to be the port you wanna communicate on
	private final int PORT = 1090;

	private BufferedReader consoleinput;
	private BufferedReader in;
	private DataOutputStream dater;

	private ServerSocket srvr; // The server
	private Socket skt; // The socket for a client

	private LedStrip myled;
	private FrameGenerator mygen;

	private AnimationSequence frames;

	private int lastnumstrips = 0;

	private ArrayList<LedStrip> alldastrips = new ArrayList<LedStrip>(); // Do something with this arraylist

	public static void main(String args[]) {
		try {
			SampleLightServer testServer = new SampleLightServer(null); // TODO: Ryan you'll wanna put an actual
																		// animation sequence in there
			testServer.commsTest();
		} catch (Exception e) {
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

		// TODO: Spin up thread for clinets and whatnot <alldastrips>
		Runnable r = new StripConnector(alldastrips, srvr);
		new Thread(r).start();

		int run = 1;
		while (run == 1) { // keep going while connection is active

			if (alldastrips.size() > 0) {
				
				int thisrunsize = alldastrips.size();

				ArrayList<FrameGenerator> doitup = new ArrayList<FrameGenerator>(); // Make an arraylist of frame
																					// generators
				for (int i = 0; i < thisrunsize; i++) { // Make a frame generator for every led strip.
					doitup.add(new FrameGenerator());
				}

				int numberFrames = frames.getItems().size();

				for (int i = 0; i < numberFrames - 1; i++) {
					AnimationItem currentFrame = frames.getItems().get(i);
					AnimationItem nextFrame = frames.getItems().get(i + 1);

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

			/*
			 * System.out.print("Sending black FRAME '" + "'\n"); // put your data here. Can
			 * be stuff other than strings. myled.sendframe(mygen.buildsolid((byte)0,
			 * (byte)0, (byte)0, 300)); //build a solid black frame, and send it to the
			 * strip Thread.sleep(1000); // Just pause for a second
			 */

			if (consoleinput.ready()) {
				String userinput = consoleinput.readLine();
				System.out.println("Received console input: " + userinput);
				if (userinput.equals("q")) {
					System.out.println("Quitting...."); // do quitting stuff
					run = 0;
				} else if (userinput.equals("s")) {
					while (!consoleinput.ready()) {
						// Block until we recieve another command.
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
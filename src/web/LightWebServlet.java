package web;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transfer.AnimationSequence;
import transfer.AnimationSource;
import driver.SampleLightServer;

/**
 * Servlet implementation class WebServlet
 */
@WebServlet("/LightWebServlet")
public class LightWebServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private boolean started = false;
	
	private SampleLightServer ledServer;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LightWebServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Hello World!");
	}

	/**
	 * When a web client posts data, we spin up the LightServer if we haven't already.
	 * This manages getting LED strip clients and sending frames to them.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonResponse = request.getReader().lines().collect(Collectors.joining());
		System.out.println(jsonResponse);
		AnimationSequence animationSequence = null;
		try {
			animationSequence = new AnimationSequence(jsonResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!started) {
			try {
				AnimationSource webSource = new AnimationSource();
				webSource.setAnimationSequence(animationSequence);
				ledServer = new SampleLightServer(webSource);
				new Thread(ledServer).start();
				started = true;
				System.out.println("LED strip server started.");
			} catch(Exception e) {
				// TODO
			}
		} else {
			ledServer.getAnimationSource().setAnimationSequence(animationSequence);
			System.out.println("Animation source updated.");
		}
		
	}

}

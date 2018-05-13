package web;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transfer.AnimationSequence;
import driver.SampleLightServer;

/**
 * Servlet implementation class WebServlet
 */
@WebServlet("/LightWebServlet")
public class LightWebServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private boolean started = false;
       
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		String jsonResponse = request.getReader().lines().collect(Collectors.joining());
		System.out.println(jsonResponse);
		AnimationSequence testSequence = null;
		try {
			testSequence = new AnimationSequence(jsonResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!started) {
			try {
				Runnable r = new SampleLightServer(testSequence);
				new Thread(r).start();
				started = false;
			} catch(Exception e) {
				// TODO
			}
		}
		
	}

}

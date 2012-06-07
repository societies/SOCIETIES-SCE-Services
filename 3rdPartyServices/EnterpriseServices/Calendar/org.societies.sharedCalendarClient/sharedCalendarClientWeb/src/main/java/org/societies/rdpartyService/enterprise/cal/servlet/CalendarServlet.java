package org.societies.rdpartyService.enterprise.cal.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class CalendarServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750756987794398957L;
	private static Logger log = LoggerFactory.getLogger(CalendarServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("Ping!");
		resp.setContentType("application/javascript");
		PrintWriter out = resp.getWriter();
		out.println(new Gson().toJson(req.getRequestURI()));
		out.close();
	}

}

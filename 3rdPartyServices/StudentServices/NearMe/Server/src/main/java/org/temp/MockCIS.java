package org.temp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockplatform.MockCISManager;
import mockplatform.MockCommManager;
import mockplatform.MockCtxBroker;

import org.temp.CISIntegeration.ContextBinder;

/**
 * Servlet implementation class MockCIS
 */
@WebServlet("/MockCIS")
public class MockCIS extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public NearMeServer server=new NearMeServer();
	
    /**
     * Default constructor. 
     */
    public MockCIS() {
        // TODO Auto-generated constructor stub
		ContextBinder cb=new ContextBinder();
		cb.setCisMgm(new MockCISManager());
		cb.setComMgt(new MockCommManager());
		cb.setCtxBrk(new MockCtxBroker());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter writer=response.getWriter();
		if(request.getParameter("operation")==null){
			writer.write("hello world!");
			writer.close();
			return;
		}
		int opt=Integer.parseInt(request.getParameter("operation"));
		String uid=request.getParameter("uid");
		String json=request.getParameter("json");
		String ssid=request.getParameter("ssid");
		System.err.println(uid+":"+json+"@"+ssid);
		Listener.LastUpdate.getLastUpdate(uid, ssid);
		if(opt==1){
			writer.write(server.pushEvent(uid, json, ssid)?"1":"0");
		}else if(opt==2){
			String[] content=server.getEvents(uid, ssid);
			StringBuilder jsons=new StringBuilder();
			for(String cnt:content){
				jsons.append(cnt+"x1Z7w");
			}
			jsons.append("1");
			writer.write(jsons.toString());
		}else if(opt==3){
			writer.write("1");
		}else if(opt==4){
			String[] content=server.getAllEvents(uid, ssid);
			StringBuilder jsons=new StringBuilder();
			for(String cnt:content){
				jsons.append(cnt+"x1Z7w");
			}
			jsons.append("1");
			writer.write(jsons.toString());
		}else{
			String[] content=server.getEvents(uid, ssid);
			StringBuilder jsons=new StringBuilder();
			for(String cnt:content){
				jsons.append(cnt+"x1Z7w");
			}
			jsons.append("1");
			writer.write(jsons.toString());
		}
		writer.close();
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

}

package org.societies.thirdPartyServices.disaster.youRNotAloneServer;


import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/")
public class YouRNotAloneServer {

	// Allows to insert contextual objects into the class, 
		// e.g. ServletContext, Request, Response, UriInfo
		@Context
		UriInfo uriInfo;
		@Context
		Request request;


		// Return the list of todos to the user in the browser
		@GET
		@Produces(MediaType.TEXT_XML)
		public List<Volunteer> getVolunteerBrowser() {
			List<Volunteer> volunteers = new ArrayList<Volunteer>();
			volunteers.addAll(YouRNotAloneDAO.instance.getVO().getVolunteers().values());
			return volunteers; 
		}
		
		// Return the list of todos for applications
		@GET
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public List<Volunteer> getVolunteer() {
			List<Volunteer> volunteers = new ArrayList<Volunteer>();
			volunteers.addAll(YouRNotAloneDAO.instance.getVO().getVolunteers().values());
			
			return volunteers; 
		}
		
		
		// retuns the number of todos
		// Use http://localhost:8080/de.vogella.jersey.todo/rest/todos/count
		// to get the total number of records
		@GET
		@Path("count")
		@Produces(MediaType.TEXT_PLAIN)
		public String getCount() {
			int count = YouRNotAloneDAO.instance.getVO().getVolunteerCount();
			return String.valueOf(count);
		}
		
		@GET
		@Path("translator")
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public List<Volunteer> getTranslator() {
			System.out.println(YouRNotAloneDAO.instance.getVO().getTranslator().size());
			return YouRNotAloneDAO.instance.getVO().getTranslator();
		}
		
		@GET
		@Path("volunteers/{v}")
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public List<Volunteer> getTranslatorByLang(@PathParam("v") String pro) {
			System.out.println(YouRNotAloneDAO.instance.getVO().getTranslator().size());
			ArrayList<String> pros = new ArrayList<String>();
			Collections.addAll(pros, pro.toLowerCase().split("&"));
			return YouRNotAloneDAO.instance.getVO().getGroupByProperties(pros);
		}
		
//		@POST
//		@Produces(MediaType.TEXT_HTML)
//		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//		public void newVolunteer(@FormParam("ID") String ID,
//				@FormParam("firstname") String firstname,
//				@FormParam("lastname") String lastname,
//				@FormParam("institut") String institut,
//				@FormParam("country") String country,
//				@FormParam("email") String email,
////				@FormParam("spokenLanguagesString") HashMap<String,Integer> spokenLanguages,
////				@FormParam("expertiseSkillsString") HashMap<String,Integer> expertiseSkills,
//				@Context HttpServletResponse servletResponse) throws IOException {
////			String[] spokenLanguages = spokenLanguagesString.split(";");
////			String[] expertiseSkills = expertiseSkillsString.split(";");
//			Volunteer v = new Volunteer(ID, firstname, lastname, 
//					institut, country, email);
//			YouRNotAloneDAO.instance.getVO().addVolunteer(v);
//			servletResponse.sendRedirect("../create_volunteer.html");
//		}
		
		
		// Defines that the next path parameter after todos is
		// treated as a parameter and passed to the TodoResources
		// Allows to type http://localhost:8080/de.vogella.jersey.todo/rest/todos/1
		// 1 will be treaded as parameter todo and passed to TodoResource
		
		@Path("{v}")
		public VolunteerServer getVolunteer(@PathParam("v") String id) {
			return new VolunteerServer(uriInfo, request, id);
		}
		

		@PUT
		@Consumes(MediaType.APPLICATION_XML)
		public Response putContact(JAXBElement<Volunteer> jaxbVolunteer) {
			Volunteer c = jaxbVolunteer.getValue();
			return putAndGetResponse(c);
		}
		private Response putAndGetResponse(Volunteer v) {
			Response res;
			if(YouRNotAloneDAO.instance.getVO().getVolunteers().containsKey(v.getID())) {
				res = Response.ok().build();
			} else {
				res = Response.created(uriInfo.getAbsolutePath()).build();
			}
			YouRNotAloneDAO.instance.getVO().getVolunteers().put(v.getID(), v);
			return res;
		} 
		
		

}
	
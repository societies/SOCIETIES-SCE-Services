package uk.ac.hw.services.collabquiz;

import org.simpleframework.xml.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;

@Path("/quiz_server")
public class CollabQuizServer extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CollabQuizServer.class);

    public CollabQuizServer() {
        log.info("CollabQuizServer ctor()");
    }

    public void init() {
        log.info("init()");
    }

//    //    This method is called if TEXT_PLAIN is request
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String sayPlainTextHello() {
//        return "Hello Jersey";
//    }
//
//    // This method is called if XML is request
//    @GET
//    @Produces(MediaType.TEXT_XML)
//    public String sayXMLHello() {
//        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
//    }
//
//    // This method is called if HTML is request
//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    public String sayHtmlHello() {
//        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
//                + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
//    }

}

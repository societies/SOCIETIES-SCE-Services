package ac.hw.services.collabquiz;
//package uk.ac.hw.services.collabquiz;
//
//import org.restlet.Component;
//import org.restlet.data.Protocol;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class RestletInit {
//    private static final Logger log = LoggerFactory.getLogger(RestletInit.class);
//
//    public void init() throws Exception {
//        log.debug("init()");
//
//        // Create a new Component.
//        Component component = new Component();
//
//        // Add a new HTTP server listening on port 8182.
//        component.getServers().add(Protocol.HTTP, 8182);
//
//        // Attach the sample application.
//        component.getDefaultHost().attach("/quiz_server", new CollabQuizServerApplication());
//
//        // Start the component.
//        component.start();
//    }
//
//}

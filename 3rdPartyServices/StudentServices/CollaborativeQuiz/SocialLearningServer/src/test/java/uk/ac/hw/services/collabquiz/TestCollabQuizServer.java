package uk.ac.hw.services.collabquiz;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class TestCollabQuizServer {
    private static final Logger log = LoggerFactory.getLogger(TestCollabQuizServer.class);

    public static final String BASE_URL = "http://localhost:8080/collabquiz";
    public static final URI BASE_URI = UriBuilder.fromUri(BASE_URL).build();

    public static final String REST_PATH = "rest";
    public static final String SERVICE_PATH = "quiz_server";

    @Test
    public void serverStarts_returnsHelloWorld() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(BASE_URI);

        // Fluent interfaces
        log.debug(service.path(REST_PATH).path(SERVICE_PATH).accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());

        // Get plain text
        log.debug(service.path(REST_PATH).path(SERVICE_PATH).accept(MediaType.TEXT_PLAIN).get(String.class));
        // Get XML
        log.debug(service.path(REST_PATH).path(SERVICE_PATH).accept(MediaType.TEXT_XML).get(String.class));
        // The HTML
        log.debug(service.path(REST_PATH).path(SERVICE_PATH).accept(MediaType.TEXT_HTML).get(String.class));

    }

}

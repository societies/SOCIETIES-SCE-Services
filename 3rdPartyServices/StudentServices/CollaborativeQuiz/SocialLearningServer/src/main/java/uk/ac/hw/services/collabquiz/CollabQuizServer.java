package uk.ac.hw.services.collabquiz;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CollabQuizServer extends ServerResource {
    private static final Logger log = LoggerFactory.getLogger(CollabQuizServer.class);

    public CollabQuizServer() {
        log.info("CollabQuizServer ctor()");
    }

    @Get
    public String sayHello() {
        log.debug("sayHello() string");
        return "Hello, world";
    }

//    @Get
//    public Representation sayHello() {
//        log.debug("sayHello()");
//        StringRepresentation representation = new StringRepresentation(javax.ws.rs.core.MediaType.TEXT_PLAIN);
//        representation.setText("Hello, world!");
//        return representation;
//    }

//    @Get("xml")
//    public Representation sayXmlHello() {
//        log.debug("sayXmlHello()");
//        List<Item> items = new ArrayList<Item>();
//        items.add(new Item("name1", "desc1"));
//        items.add(new Item("name2", "desc2"));
//        items.add(new Item("name3", "desc3"));
//
//        try {
//            DomRepresentation representation = new DomRepresentation(org.restlet.data.MediaType.TEXT_XML);
//
//            // Generate a DOM document representing the list of items.
//            Document d = representation.getDocument();
//            Element r = d.createElement("items");
//            d.appendChild(r);
//            for (Item item : items) {
//                Element eltItem = d.createElement("item");
//
//                Element eltName = d.createElement("name");
//                eltName.appendChild(d.createTextNode(item.getName()));
//                eltItem.appendChild(eltName);
//
//                Element eltDescription = d.createElement("description");
//                eltDescription.appendChild(d.createTextNode(item.getDescription()));
//                eltItem.appendChild(eltDescription);
//
//                r.appendChild(eltItem);
//            }
//            d.normalizeDocument();
//
//            // Returns the XML representation of this document.
//            return representation;
//
//        } catch (IOException e) {
//            log.error("Error generating response", e);
//        }
//
//        return null;
//    }
//
//    private class Item {
//
//        private final String name;
//        private final String description;
//
//        public Item(String name, String description) {
//
//            this.name = name;
//            this.description = description;
//        }
//
//        private String getName() {
//            return name;
//        }
//
//        private String getDescription() {
//            return description;
//        }
//    }

}

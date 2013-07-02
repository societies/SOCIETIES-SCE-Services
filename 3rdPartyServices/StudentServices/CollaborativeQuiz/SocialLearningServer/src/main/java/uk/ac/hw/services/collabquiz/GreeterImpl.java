package uk.ac.hw.services.collabquiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@javax.jws.WebService(portName = "SoapPort", serviceName = "SOAPService",
        targetNamespace = "http://hw.ac.uk/collabquiz/greeter",
        endpointInterface = "uk.ac.hw.services.collabquiz.IGreeter")
public class GreeterImpl implements IGreeter {
    private static final Logger log = LoggerFactory.getLogger(GreeterImpl.class);

    @Override
    public String greetMe(String me) {
        log.debug("Executing operation greetMe(): Message received: " + me);
        return "Hello " + me;
    }

    @Override
    public String sayHi() {
        log.debug("Executing operation sayHi()");
        return "Bonjour";
    }
}

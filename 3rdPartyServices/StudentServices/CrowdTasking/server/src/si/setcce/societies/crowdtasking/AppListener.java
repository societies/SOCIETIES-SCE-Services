package si.setcce.societies.crowdtasking;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AppListener implements ServletContextListener {
    // This will be invoked as part of a warmup request, or the first user
    // request if no warmup request was invoked.
    public void contextInitialized(ServletContextEvent event) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            System.out.println("hostname:" + hostname);
            System.out.println("getByName:" + InetAddress.getByName(hostname));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        /*String domena = request.getServerName();
        if (request.getServerPort() != 80) {
			domena += request.getServerPort(); 
		}*/

    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}

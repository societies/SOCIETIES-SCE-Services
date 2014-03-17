package si.setcce.societies.crowdtasking.gcm;

/**
 * Created with IntelliJ IDEA.
 * User: juresas
 * Date: 22.10.2013
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;

/**
 * Exception thrown when GCM returned an error due to an invalid request.
 * <p/>
 * This is equivalent to GCM posts that return an HTTP error different of 200.
 */
public final class InvalidRequestException extends IOException {

    private final int status;
    private final String description;

    public InvalidRequestException(int status) {
        this(status, null);
    }

    public InvalidRequestException(int status, String description) {
        super(getMessage(status, description));
        this.status = status;
        this.description = description;
    }

    private static String getMessage(int status, String description) {
        StringBuilder base = new StringBuilder("HTTP Status Code: ").append(status);
        if (description != null) {
            base.append("(").append(description).append(")");
        }
        return base.toString();
    }

    /**
     * Gets the HTTP Status Code.
     */
    public int getHttpStatusCode() {
        return status;
    }

    /**
     * Gets the error description.
     */
    public String getDescription() {
        return description;
    }

}

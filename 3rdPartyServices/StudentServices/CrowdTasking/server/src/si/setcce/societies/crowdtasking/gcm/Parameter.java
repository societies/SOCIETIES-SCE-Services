package si.setcce.societies.crowdtasking.gcm;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Jureša
 * Date: 2.1.2014
 * Time: 10:07
 */
public class Parameter {
    String name, value;

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

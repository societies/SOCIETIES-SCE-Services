package si.stecce.societies.crowdtasking.gcm;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Jureša
 * Date: 2.1.2014
 * Time: 12:14
 */

import java.util.ArrayList;
import java.util.List;

public class Parameters {
    List<Parameter> params;

    public Parameters() {
        this.params = new ArrayList<Parameter>();
    }

    public Parameters(List<Parameter> params) {
        super();
        this.params = params;
    }

    public Parameters(String params) {
        parseString(params);
    }

    public void parseString(String paramsString) {
        this.params = fromString(paramsString);
    }

    public static List<Parameter> fromString(String paramsString) {
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        String[] result = paramsString.split(",");
        for (String r : result) {
            String[] result2 = r.split("=");
            params.add(new Parameter(result2[0], result2[1]));
        }
        return params;
    }

    public String toString() {
        if (params == null || params.size() == 0) {
            return "";
        }
        String out = "";
        int i;
        for (i = 0; i < params.size() - 1; i++) {
            out += params.get(i).getName() + "=" + params.get(i).getValue() + ",";
        }
        out += params.get(i).getName() + "=" + params.get(i).getValue();
        System.out.println(out);
        return out;
    }

    public List<Parameter> getParameters() {
        return params;
    }

    public void setParameters(List<Parameter> parameters) {
        this.params = parameters;
    }

    public void addParameter(Parameter parameter) {
        if (params == null) {
            params = new ArrayList<Parameter>();
        }
        params.add(parameter);
    }

    public void addParameter(String name, String value) {
        addParameter(new Parameter(name, value));
    }
}
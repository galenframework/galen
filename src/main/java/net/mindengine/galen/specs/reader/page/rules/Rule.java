package net.mindengine.galen.specs.reader.page.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ishubin on 2015/02/22.
 */
public class Rule {
    private Pattern pattern;
    private List<String> parameters = new LinkedList<String>();

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String name) {
        parameters.add(name);
    }
}

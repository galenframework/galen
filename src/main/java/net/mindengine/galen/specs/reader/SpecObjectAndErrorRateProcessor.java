package net.mindengine.galen.specs.reader;

import java.io.IOException;
import java.util.regex.Pattern;

import net.mindengine.galen.parser.ExpectWord;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Spec;

public class SpecObjectAndErrorRateProcessor implements SpecProcessor {
    private static final Pattern CENTERED_ERROR_RATE_PATTERN = Pattern.compile("[0-9]+px");
    private SpecObjectAndErrorRateInit init;

    public SpecObjectAndErrorRateProcessor(SpecObjectAndErrorRateInit init) {
        this.init = init;
    }

    @Override
    public Spec processSpec(String specName, String paramsText, String contextPath) throws IOException {
        
        StringCharReader reader = new StringCharReader(paramsText);
        String objectName = new ExpectWord().read(reader);
        
        if (objectName.isEmpty()) {
            throw new SyntaxException("Missing object name");
        }
        
        
        Integer errorRate = null;
        
        if (reader.hasMore()) {
            String theRest = reader.getTheRest();
            String errorRateText = theRest.replaceAll("\\s", "");
            if (CENTERED_ERROR_RATE_PATTERN.matcher(errorRateText).matches()) {
                errorRate = Integer.parseInt(errorRateText.replace("px", ""));
            }
            else throw new SyntaxException("Incorrect error rate syntax: \"" + theRest + "\"");
        }
        return init.init(specName, objectName, errorRate);
    }

}

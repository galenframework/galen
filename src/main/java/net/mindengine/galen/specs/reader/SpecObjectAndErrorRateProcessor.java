/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
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

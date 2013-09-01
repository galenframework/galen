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
package net.mindengine.galen.specs.reader.page;

import static java.lang.String.format;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.parser.ExpectWord;
import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.StringCharReader;

public class StateObjectDefinition extends State {

    private PageSpec pageSpec;

    public StateObjectDefinition(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    @Override
    public void process(String line) {
        StringCharReader reader = new StringCharReader(line);
        
        String objectName = expectWord(reader, "Object name is not defined correctly");
        
        try {
            String word = expectCorrectionsOrId(reader, objectName);
            String locatorType;
            Rect corrections = null;
            if (word.equals("corrections")) {
                corrections = Expectations.corrections().read(reader);
                
                locatorType = expectWord(reader, format("Missing locator for object \"%s\"", objectName));
            }
            else locatorType = word;
            
            
            String value = reader.getTheRest().trim();
            if (value.isEmpty()) {
                throw new SyntaxException(UNKNOWN_LINE, format("Locator for object \"%s\" is not defined correctly", objectName));
            }
            pageSpec.addObject(objectName, new Locator(locatorType, value).withCorrections(corrections));
        }
        catch (SyntaxException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SyntaxException(UNKNOWN_LINE, "Object \"" + objectName + "\" has incorrect locator", e);
        }
    }

    private String expectCorrectionsOrId(StringCharReader reader, String objectName) {
        String word = new ExpectWord().stopOnTheseSymbols('(').read(reader).trim();
        if (word.isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, format("Missing locator for object \"%s\"", objectName));
        }
        return word;
    }

    private String expectWord(StringCharReader reader, String errorMessage) {
        String word = new ExpectWord().read(reader).trim();
        if (word.isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, errorMessage);
        }
        return word;
    }

}

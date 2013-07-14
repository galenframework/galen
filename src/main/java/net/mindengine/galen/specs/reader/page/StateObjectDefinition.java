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

import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.ExpectWord;
import net.mindengine.galen.specs.reader.IncorrectSpecException;
import net.mindengine.galen.specs.reader.StringCharReader;

public class StateObjectDefinition extends State {

    private PageSpec pageSpec;

    public StateObjectDefinition(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    @Override
    public void process(String line) {
        StringCharReader reader = new StringCharReader(line);
        
        String objectName = expectWord(reader, "Object name");
        
        try {
            String locatorType = expectWord(reader, "Locator type");
            
            String value = reader.getTheRest().trim();
            if (value.isEmpty()) {
                throw new IncorrectSpecException(String.format("The locator for object '%s' is not defined correctly", objectName));
            }
            pageSpec.addObject(objectName, new Locator(locatorType, value));
        }
        catch (Exception e) {
            throw new IncorrectSpecException("Object \"" + objectName + "\" has incorrect locator", e);
        }
    }

    private String expectWord(StringCharReader reader, String what) {
        String word = new ExpectWord().read(reader).trim();
        if (word.isEmpty()) {
            throw new IncorrectSpecException(what + " is not defined correctly");
        }
        return word;
    }

}

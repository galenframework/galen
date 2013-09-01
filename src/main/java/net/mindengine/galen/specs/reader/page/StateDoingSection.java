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

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.SpecReader;

public class StateDoingSection extends State {

    private PageSection section;
    private ObjectSpecs currentObjectSpecs;
    
    private SpecReader specReader = new SpecReader();
    public StateDoingSection(PageSection section) {
        this.section = section;
    }

    @Override
    public void process(String line) {
        if (startsWithIndentation(line)) {
            if (currentObjectSpecs == null) {
                throw new SyntaxException(UNKNOWN_LINE,"There is no object defined in section");
            }
            else {
                try {
                    currentObjectSpecs.getSpecs().add(specReader.read(line.trim()));
                }
                catch (SyntaxException exception) {
                    throw new SyntaxException(UNKNOWN_LINE, "Incorrect spec for object \"" + currentObjectSpecs.getObjectName() + "\"", exception);
                }
            }
        }
        else {
            beginNewObject(line);
        }
    }

    private void beginNewObject(String line) {
        String name = line.trim().replace(":", "");
        currentObjectSpecs = new ObjectSpecs(name);
        section.getObjects().add(currentObjectSpecs);
    }

    private boolean startsWithIndentation(String line) {
        return line.startsWith("\t") || line.startsWith("  ");
    }

}

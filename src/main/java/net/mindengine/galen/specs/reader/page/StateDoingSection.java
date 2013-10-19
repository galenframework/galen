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
import net.mindengine.galen.parser.MathParser;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.SpecReader;

public class StateDoingSection extends State {

    private PageSection section;
    private ObjectSpecs currentObjectSpecs;
    private SpecReader specReader = new SpecReader();
    private String[] toParameterize;
    private Parameterization currentParameterization = null;
    private int currentIndentationLevel;
    
    
    private class Parameterization {
        private String[] parameters;
        private ObjectSpecs[] objectSpecs;

        public Parameterization(String[] parameters, ObjectSpecs[] objectSpecs) {
            this.parameters = parameters;
            this.objectSpecs = objectSpecs;
        }

        public void processObject(String line) {
            for (int i = 0; i < parameters.length; i++) {
                String specText = convertParameterizedLine(line, parameters[i]);
                objectSpecs[i].getSpecs().add(StateDoingSection.this.specReader.read(specText));
            }
        }
    }
    
    public StateDoingSection(PageSection section) {
        this.section = section;
    }

    @Override
    public void process(String line) {
        if (startsWithIndentation(line)) {
            if (currentParameterization != null) {
                currentParameterization.processObject(line);
            }
            else {
                processSpecForSimpleObject(line);
            }
        }
        else {
            if (toParameterize != null) {
                beginParameterizedObject(line, toParameterize);
                toParameterize = null;
            }
            else {
                beginNewObject(line);
                currentParameterization = null;
            }
        }
    }

    
    private void processSpecForSimpleObject(String line) {
        if (currentObjectSpecs == null) {
            throw new SyntaxException(UNKNOWN_LINE,"There is no object defined in section");
        }
        else {
            try {
                currentObjectSpecs.getSpecs().add(specReader.read(line.trim()));
            }
            catch (SyntaxException exception) {
                throw exception;
            }
            catch (Exception exception) {
                throw new SyntaxException(UNKNOWN_LINE, "Incorrect spec for object \"" + currentObjectSpecs.getObjectName() + "\"", exception);
            }
        }
    }

    private void beginParameterizedObject(String line, String[] parameters) {
        String objectNamePattern = readObjectNameFromLine(line);
        
        ObjectSpecs[] objectSpecs = new ObjectSpecs[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            String objectName = convertParameterizedLine(objectNamePattern, parameters[i]);
            objectSpecs[i] = new ObjectSpecs(objectName);
            section.getObjects().add(objectSpecs[i]);
        }
        currentParameterization = new Parameterization(parameters, objectSpecs);
    }

    private String convertParameterizedLine(String line, String parameter) {
    	MathParser parser = new MathParser();
    	return parser.parse(line, parameter);
    }

    private void beginNewObject(String line) {
        String name = readObjectNameFromLine(line);
        currentObjectSpecs = new ObjectSpecs(name);
        section.getObjects().add(currentObjectSpecs);
    }

    private String readObjectNameFromLine(String line) {
        String name = line.replace(":", "").trim();
        return name;
    }

    private boolean startsWithIndentation(String line) {
        if (line.startsWith("\t")) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. Should not use tabs. Use spaces");
        }
        
        int indentationLevel = getIndentationFrom(line).length();
        if (indentationLevel > 8 ) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. Use from 1 to 8 spaces for indentation");
        }
        else if (indentationLevel == 0) {
            currentIndentationLevel = 0;
            return false;
        }
        else {
            if (currentIndentationLevel > 0) {
                if (currentIndentationLevel != indentationLevel) {
                    throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. You should use same indentation within one spec");
                }
            }
            else {
                currentIndentationLevel = indentationLevel;
            }
            
            return true;
        }
    }

    private String getIndentationFrom(String line) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<line.length(); i++) {
            char symbol = line.charAt(i);
            if (symbol != ' ') {
                return buffer.toString();
            }
            else {
                buffer.append(symbol);
            }
        }
        
        return buffer.toString();
    }

    public void parameterizeNextObject(String[] parameters) {
        toParameterize = parameters;
    }

}

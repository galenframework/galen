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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.PageSection;


public class PageSpecLineProcessor {

    private static final String TAG = "@";
    private static final String COMMENT = "#";
    private static final String PARAMETERIZATION_SYMBOL = "[";
    PageSpec pageSpec = new PageSpec();
    
    private State state;
    
    public PageSpecLineProcessor() {
        startNewSection("");
    }

    public void processLine(String line) {
        if (!isCommentedOut(line) && !isEmpty(line)) {
            if (isObjectSeparator(line)) {
                switchObjectDefinitionState();
            }
            else if (line.startsWith(TAG)) {
                startNewSection(line.substring(1));
            }
            else if (isSectionSeparator(line)) {
                //Do nothing
            }
            else if (line.startsWith(PARAMETERIZATION_SYMBOL)) {
                startParameterization(line);
            }
            else state.process(line);
        }
    }
    
    
    private void startParameterization(String line) {
        line = line.replace(" ", "");
        line = line.replace("\t", "");
        if (line.matches("\\[[0-9]+\\-[0-9]+\\]")) {
            
            int dashIndex = line.indexOf('-');
            
            int rangeA = Integer.parseInt(line.substring(1, dashIndex));
            int rangeB = Integer.parseInt(line.substring(dashIndex + 1, line.length() - 1));
            
            int min = Math.min(rangeA, rangeB);
            int max = Math.max(rangeA, rangeB);
            startParameterization(createSequence(min, max));
        }
        else throw new SyntaxException(UNKNOWN_LINE, "Incorrect parameterization syntax"); 
        
    }

    private String[] createSequence(int min, int max) {
        int size = max - min + 1;
        String[] parameters = new String[size];
        for (int i = min; i <= max; i++) {
            parameters[i - min] = Integer.toString(i);
        }
        return parameters;
    }

    private void startParameterization(String[] parameters) {
        if (!(state instanceof StateDoingSection)) {
            startNewSection("");
        }
        
        StateDoingSection doingSection = (StateDoingSection)state;
        doingSection.parameterizeNextObject(parameters);
    }

    public PageSpec buildPageSpec() {
        Iterator<PageSection> it = pageSpec.getSections().iterator();
        while(it.hasNext()) {
            if (it.next().getObjects().size() == 0) {
                it.remove();
            }
         }
        return this.pageSpec;
    }

    private void switchObjectDefinitionState() {
        if (state.isObjectDefinition()) {
            startNewSection("");
        }
        else state = State.objectDefinition(pageSpec);
    }

    private boolean isSectionSeparator(String line) {
        line = line.trim();
        if (line.length() < 4) {
            return false;
        }
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '-') {
                return false;
            }
        }
        
        return true;
    }

    private boolean isObjectSeparator(String line) {
        return containsOnly(line.trim(), '=');
    }

    private void startNewSection(String tags) {
        PageSection section = new PageSection();
        section.setTags(readTags(tags));
        pageSpec.addSection(section);
        state = State.startedSection(section);
    }

    private List<String> readTags(String tagsText) {
        List<String> tags = new LinkedList<String>();
        for (String tag : tagsText.split(",")) {
            tag = tag.trim();
            if(!tag.isEmpty()) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private boolean isEmpty(String line) {
        return line.trim().isEmpty();
    }

    private boolean isCommentedOut(String line) {
        return line.trim().startsWith(COMMENT);
    }

    private boolean containsOnly(String line, char c) {
        if (line.length() > 1) {
            for (int i=0; i<line.length(); i++) {
                if (line.charAt(i) != c) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

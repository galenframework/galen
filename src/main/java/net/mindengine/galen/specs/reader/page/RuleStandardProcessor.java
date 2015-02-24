/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.Place;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class RuleStandardProcessor implements RuleProcessor {
    private final List<Pair<String, Place>> originalLines;

    public RuleStandardProcessor(List<Pair<String, Place>> lines) {
        this.originalLines = lines;
    }


    @Override
    public void processRule(ObjectSpecs objectSpecs, String ruleText, VarsContext varsContext, PageSection section, Properties properties, String contextPath, PageSpecReader pageSpecReader) throws IOException {

        List<Pair<String, Place>> lines = new LinkedList<Pair<String, Place>>();
        String indentation = "";
        if (objectSpecs != null) {
            indentation = "    ";
        }
        else {
            PageSection subSection = new PageSection();
            subSection.setName(ruleText);
            section.addSubSection(subSection);
            section = subSection;
        }

        for (Pair<String, Place> line : originalLines) {
            lines.add(new ImmutablePair<String, Place>(indentation + line.getLeft(), line.getRight()));
        }


        StateDoingSection stateDoingSection = new StateDoingSection(properties, section, contextPath, pageSpecReader);
        stateDoingSection.setCurrentObject(objectSpecs);

        for (Pair<String, Place> line : lines) {
            stateDoingSection.process(varsContext, line.getLeft(), line.getRight());
        }
    }
}

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

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.specs.reader.page.rules.Rule;
import net.mindengine.galen.specs.reader.page.rules.RuleParser;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class StateDoingRule extends State {
    private static final char SPACE = ' ';
    private final Rule rule;

    private List<Pair<String, Place>> lines = new LinkedList<Pair<String,Place>>();
    private int firstIndentation = -1;

    public StateDoingRule(String ruleText) {
        this.rule = new RuleParser().parse(ruleText);
    }

    @Override
    public void process(VarsContext varsContext, String line, Place place) throws IOException {
        if (!line.trim().isEmpty()) {
            if (firstIndentation < 0) {
                firstIndentation = amountOfSpaces(line);
            }
            else {
                if (amountOfSpaces(line) < firstIndentation) {
                    throw new SyntaxException("Incorrect indentation inside rule");
                }
            }

            lines.add(new ImmutablePair<String, Place>(line.substring(firstIndentation), place));
        }
    }

    private int amountOfSpaces(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != SPACE) {
                return i;
            }
        }
        return line.length();
    }

    public void build(PageSpec pageSpec) {
        pageSpec.addRuleProcessor(rule, new RuleStandardProcessor(lines));
    }
}

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
package net.mindengine.galen.speclang2.reader.pagespec;

import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.specs.reader.page.rules.Rule;
import net.mindengine.galen.specs.reader.page.rules.RuleParser;

import java.util.Collections;
import java.util.List;

public class RuleProcessor {
    private final PageSpecHandler pageSpecHandler;

    public RuleProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode statementNode) {
        String ruleText = reader.getTheRest().trim();
        if (ruleText.isEmpty()) {
            throw new SyntaxException(statementNode, "Missing rule text");
        }

        if (statementNode.getChildNodes() == null || statementNode.getChildNodes().size() == 0) {
            throw new SyntaxException(statementNode, "A rule is empty");
        }

        Rule rule = new RuleParser().parse(ruleText);

        pageSpecHandler.addRule(rule, new InPageRule(statementNode.getChildNodes()));

        return Collections.emptyList();
    }
}

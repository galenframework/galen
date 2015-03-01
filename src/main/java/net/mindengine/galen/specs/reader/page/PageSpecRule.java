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

import net.mindengine.galen.specs.reader.page.rules.Rule;

/**
 * Created by ishubin on 2015/02/24.
 */
public class PageSpecRule {
    private final Rule rule;
    private final RuleProcessor ruleProcessor;

    public PageSpecRule(Rule rule, RuleProcessor ruleProcessor) {
        this.rule = rule;
        this.ruleProcessor = ruleProcessor;
    }

    public Rule getRule() {
        return rule;
    }

    public RuleProcessor getRuleProcessor() {
        return ruleProcessor;
    }
}

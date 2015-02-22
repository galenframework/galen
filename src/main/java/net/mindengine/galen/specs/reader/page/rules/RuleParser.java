package net.mindengine.galen.specs.reader.page.rules;

import net.mindengine.galen.specs.reader.StringCharReader;

/**
 * Created by ishubin on 2015/02/22.
 */
public class RuleParser {
    public Rule parse(String ruleText) {
        StringCharReader reader = new StringCharReader(ruleText.trim());

        RuleBuilder ruleBuilder = new RuleBuilder();
        RuleParseState state = new RuleParserStateNormal();
        state.process(ruleBuilder, reader);

        return ruleBuilder.build();
    }

}

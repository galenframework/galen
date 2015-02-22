package net.mindengine.galen.specs.reader.page.rules;

import net.mindengine.galen.specs.reader.StringCharReader;

/**
 * Created by ishubin on 2015/02/22.
 */
public abstract class RuleParseState {


    public abstract void process(RuleBuilder ruleBuilder, StringCharReader reader);
}

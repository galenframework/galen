package net.mindengine.galen.specs.reader.page.rules;

import net.mindengine.galen.specs.reader.StringCharReader;

/**
 * Created by ishubin on 2015/02/22.
 */
public class RuleParserStateNormal extends RuleParseState {

    @Override
    public void process(RuleBuilder ruleBuilder, StringCharReader reader) {

        RuleBuilder.NormalTextChunk chunk = ruleBuilder.newNormalTextChunk();

        while(reader.hasMore()) {
            char symbol = reader.next();
            if (symbol == '%' && reader.currentSymbol() == '{') {
                reader.next();

                new RuleParseStateParameter().process(ruleBuilder, reader);
                chunk = ruleBuilder.newNormalTextChunk();
            } else {
                chunk.appendSymbol(symbol);
            }
        }
    }
}

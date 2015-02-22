package net.mindengine.galen.specs.reader.page.rules;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.StringCharReader;

/**
 * Created by ishubin on 2015/02/22.
 */
public class RuleParseStateParameter extends RuleParseState {

    private int amountOfOpenCurlyBraces = 1;

    @Override
    public void process(RuleBuilder ruleBuilder, StringCharReader reader) {

        RuleBuilder.ParameterChunk chunk = ruleBuilder.newParameterChunk();

        boolean notFinished = true;

        while(reader.hasMore() && notFinished) {
            char symbol = reader.next();

            if (symbol == '{') {
                amountOfOpenCurlyBraces += 1;
                chunk.appendSymbol(symbol);
            } else if (symbol == '}') {
                amountOfOpenCurlyBraces -= 1;

                if (amountOfOpenCurlyBraces == 0) {
                    notFinished = false;
                } else {
                    chunk.appendSymbol(symbol);
                }
            } else {
                chunk.appendSymbol(symbol);
            }
        }

        if (amountOfOpenCurlyBraces > 0) {
            throw new SyntaxException("Missing '}' to close parameter definition");
        }
    }
}

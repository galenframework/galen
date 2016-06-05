package com.galenframework.speclang2.pagespec.rules;

import com.galenframework.parser.StringCharReader;

public class RuleParserStateWhiteSpace extends RuleParseState {
    public static final char SPACE = ' ';
    public static final char TAB = '\t';

    @Override
    public void process(RuleBuilder ruleBuilder, StringCharReader reader) {
        ruleBuilder.newWhiteSpaceChunk();

        while(reader.hasMore()) {
            char symbol = reader.next();
            if (symbol != SPACE && symbol != TAB) {
                reader.back();
                return;
            }
        }
    }
}

package net.mindengine.galen.specs.reader.page.rules;


import net.mindengine.galen.parser.SyntaxException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ishubin on 2015/02/22.
 */
public class RuleBuilder {

    private List<Chunk> chunks = new LinkedList<Chunk>();

    public NormalTextChunk newNormalTextChunk() {
        return addChunk(new NormalTextChunk());
    }

    public ParameterChunk newParameterChunk() {
        return addChunk(new ParameterChunk());
    }

    public static abstract class Chunk {
        public abstract String build(Rule rule);
        public abstract void appendSymbol(char ch);
    }

    public static class NormalTextChunk extends Chunk {

        private StringBuilder stringBuilder = new StringBuilder();
        @Override
        public String build(Rule rule) {
            String text = stringBuilder.toString();
            if (text.isEmpty()) {
                return text;
            } else {
                return Pattern.quote(text);
            }
        }

        @Override
        public void appendSymbol(char ch) {
            stringBuilder.append(ch);
        }
    }

    public static class ParameterChunk extends Chunk {
        private static final int PARSE_NAME = 0;
        private static final int PARSE_PARAMETER = 1;

        private int state = 0;

        private StringBuilder nameBuilder = new StringBuilder();
        private StringBuilder regexBuilder = new StringBuilder();

        @Override
        public String build(Rule rule) {
            String parameterName = nameBuilder.toString().toString().trim();
            if (parameterName.isEmpty()) {
                throw new SyntaxException("Parameter name should not be empty");
            } else if (containsInvalidSymbolsForName(parameterName)) {
                throw new SyntaxException("Incorrect parameter name: " + parameterName);
            }

            rule.addParameter(parameterName);

            String customRegex = regexBuilder.toString().trim();
            if (customRegex.isEmpty()) {
                if (isStillParsingName()) {
                    customRegex = ".*";
                } else {
                    throw new SyntaxException("Missing custom regular expression after ':'");
                }
            }
            return "(" + customRegex + ")";
        }

        private final Pattern objectNamePattern = Pattern.compile("[a-zA-Z0-9_]+");

        private boolean containsInvalidSymbolsForName(String parameterName) {
            return !objectNamePattern.matcher(parameterName).matches();
        }

        @Override
        public void appendSymbol(char symbol) {
            if (symbol == ':' && isStillParsingName()) {
                startParsingCustomRegex();
            } else {
               if (isStillParsingName()) {
                   nameBuilder.append(symbol);
               } else {
                   regexBuilder.append(symbol);
               }
            }
        }

        private boolean isStillParsingName() {
            return state == PARSE_NAME;
        }

        private void startParsingCustomRegex() {
            this.state = PARSE_PARAMETER;
        }
    }

    public <T extends Chunk> T  addChunk(T chunk) {
        this.chunks.add(chunk);
        return chunk;
    }

    public Rule build() {
        Rule rule = new Rule();
        StringBuilder patternBuilder = new StringBuilder();

        for (Chunk chunk : chunks) {
            patternBuilder.append(chunk.build(rule));
        }
        rule.setPattern(Pattern.compile(patternBuilder.toString()));
        return rule;
    }
}

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
package net.mindengine.galen.parser;

import static net.mindengine.galen.parser.Expectations.isDelimeter;
import static net.mindengine.galen.parser.Expectations.isNumeric;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectRange implements Expectation<Range> {

    private String endingWord = "px";

    private enum RangeType {
        NOTHING, APPROXIMATE, LESS_THAN, GREATER_THAN
    }

    @Override
    public Range read(final StringCharReader reader) {

        RangeType rangeType = RangeType.NOTHING;

        final char firstNonWhiteSpaceSymbol = reader.firstNonWhiteSpaceSymbol();
        if (firstNonWhiteSpaceSymbol == '~') {
            rangeType = RangeType.APPROXIMATE;
        } else if (firstNonWhiteSpaceSymbol == '>') {
            rangeType = RangeType.GREATER_THAN;
        } else if (firstNonWhiteSpaceSymbol == '<') {
            rangeType = RangeType.LESS_THAN;
        }

        final Double firstValue = new ExpectNumber().read(reader);

        final String text = expectNonNumeric(reader);
        if (text.equals(endingWord)) {
            return createRange(firstValue, rangeType);
        }
        if (text.equals("%")) {
            return createRange(firstValue, rangeType).withPercentOf(readPercentageOf(reader));
        } else if (rangeType == RangeType.NOTHING) {
            Range range;

            if (text.equals("to")) {
                final Double secondValue = new ExpectNumber().read(reader);
                range = Range.between(firstValue, secondValue);
            } else {
                throw new SyntaxException(UNKNOWN_LINE, "Expecting \"px\", \"to\" or \"%\", got \"" + text + "\"");
            }

            final String end = expectNonNumeric(reader);
            if (end.equals(endingWord)) {
                return range;
            } else if (end.equals("%")) {
                return range.withPercentOf(readPercentageOf(reader));
            } else {
                throw new SyntaxException(UNKNOWN_LINE, "Missing ending: \"px\" or \"%\"");
            }
        } else {
            throw new SyntaxException(UNKNOWN_LINE, msgFor(text));
        }
    }

    private Range createRange(final Double firstValue, final RangeType rangeType) {
        if (rangeType == RangeType.APPROXIMATE) {
            Double delta = 0.0;
            final int approximationConfig = GalenConfig.getConfig().getRangeApproximation();

            if (Math.abs(firstValue) > 100) {
                delta = (approximationConfig) * Math.abs(firstValue) / 100.0;
            } else {
                delta = (double) approximationConfig;
            }
            return Range.between(firstValue - delta, firstValue + delta);
        } else if (rangeType == RangeType.GREATER_THAN) {
            return Range.greaterThan(firstValue);
        } else if (rangeType == RangeType.LESS_THAN) {
            return Range.lessThan(firstValue);
        } else {
            return Range.exact(firstValue);
        }
    }

    private String readPercentageOf(final StringCharReader reader) {
        final String firstWord = expectNonNumeric(reader);
        if (firstWord.equals("of")) {
            final String valuePath = expectAnyWord(reader).trim();
            if (valuePath.isEmpty()) {
                throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
            } else {
                return valuePath;
            }
        } else {
            throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
        }
    }

    private String expectNonNumeric(final StringCharReader reader) {
        boolean started = false;
        char symbol;
        final StringBuilder builder = new StringBuilder();
        while (reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            } else if (isNumeric(symbol)) {
                reader.back();
                break;
            } else if (!isDelimeter(symbol)) {
                builder.append(symbol);
                started = true;
            }
        }
        return builder.toString();
    }

    private String expectAnyWord(final StringCharReader reader) {
        boolean started = false;
        char symbol;
        final StringBuilder builder = new StringBuilder();
        while (reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            } else if (!isDelimeter(symbol)) {
                builder.append(symbol);
                started = true;
            }
        }
        return builder.toString();
    }

    private String msgFor(final String text) {
        return String.format("Cannot parse range: \"%s\"", text);
    }

    public String getEndingWord() {
        return endingWord;
    }

    public void setEndingWord(final String endingWord) {
        this.endingWord = endingWord;
    }

}

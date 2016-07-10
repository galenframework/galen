/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.parser;

import static com.galenframework.parser.Expectations.isDelimeter;
import static com.galenframework.parser.Expectations.isNumeric;

import com.galenframework.config.GalenConfig;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.Range;

public class ExpectRange implements Expectation<Range>{

    private String endingWord = "px";
    private boolean shouldUseEndingWord = true;

    public void setNoEndingWord() {
        shouldUseEndingWord = false;
    }

    private enum RangeType {
		NOTHING, APPROXIMATE, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN_OR_EQUALS
	}

    @Override
    public Range read(StringCharReader reader) {
        RangeType rangeType = identifyRangeType(reader);
        
        RangeValue firstValue = new ExpectRangeValue().read(reader);
        
        String text = expectNonNumeric(reader);

        if (shouldUseEndingWord && text.equals(endingWord)) {
            return createRange(firstValue, rangeType);
        }

        if (text.equals("%")) {
            return createRange(firstValue, rangeType).withPercentOf(readPercentageOf(reader));
        }
        else if (rangeType != RangeType.NOTHING) {
            return createRange(firstValue, rangeType);
        }
        else {
            Range range;

            if (text.equals("to")) {
                RangeValue secondValue =  new ExpectRangeValue().read(reader);
                range = Range.between(firstValue, secondValue);
            }
            else if (shouldUseEndingWord) {
                throw new SyntaxException("Expecting \"px\", \"to\" or \"%\", got \"" + text + "\"");
            } else {
                range = Range.exact(firstValue);
            }
            
            String end = expectNonNumeric(reader);
            if (shouldUseEndingWord && end.equals(endingWord)) {
                return range;
            } else if (end.equals("%")) {
                return range.withPercentOf(readPercentageOf(reader));
            } else if (shouldUseEndingWord) {
                throw new SyntaxException("Expecting \"" + endingWord + "\", got \"" + end + "\"");
            } else {
                return range;
            }
        }
    }

    private RangeType identifyRangeType(StringCharReader reader) {
        RangeType rangeType = RangeType.NOTHING;

        char firstNonWhiteSpaceSymbol = reader.firstNonWhiteSpaceSymbol();
        if (firstNonWhiteSpaceSymbol == '~') {
            rangeType = RangeType.APPROXIMATE;
        }
        else if (firstNonWhiteSpaceSymbol == '>') {
        	rangeType = RangeType.GREATER_THAN;
            reader.readSafeUntilSymbol('>');
            if (reader.firstNonWhiteSpaceSymbol() == '=') {
                reader.readSafeUntilSymbol('=');
                rangeType = RangeType.GREATER_THAN_OR_EQUALS;
            }
        }
        else if (firstNonWhiteSpaceSymbol == '<') {
        	rangeType = RangeType.LESS_THAN;
            reader.readSafeUntilSymbol('<');
            if (reader.firstNonWhiteSpaceSymbol() == '=') {
                reader.readSafeUntilSymbol('=');
                rangeType = RangeType.LESS_THAN_OR_EQUALS;
            }
        }
        return rangeType;
    }

    private Range createRange(RangeValue firstValue, RangeType rangeType) {
        if (rangeType == RangeType.APPROXIMATE) {
            Double delta = (double) GalenConfig.getConfig().getRangeApproximation();

            Double firstValueAsDouble = firstValue.asDouble();
            int precision = firstValue.getPrecision();

            return Range.between(new RangeValue(firstValueAsDouble - delta, precision),
                    new RangeValue(firstValueAsDouble + delta, precision));
        }
        else if (rangeType == RangeType.GREATER_THAN) {
        	return Range.greaterThan(firstValue);
        }
        else if (rangeType == RangeType.LESS_THAN) {
        	return Range.lessThan(firstValue);
        }
        else if (rangeType == RangeType.LESS_THAN_OR_EQUALS) {
            return Range.lessThanOrEquals(firstValue);
        }
        else if (rangeType == RangeType.GREATER_THAN_OR_EQUALS) {
            return Range.greaterThanOrEquals(firstValue);
        }
        else {
            return Range.exact(firstValue);
        }
    }

    private String readPercentageOf(StringCharReader reader) {
        String firstWord = expectNonNumeric(reader);
        if (firstWord.equals("of")) {
            String valuePath = expectAnyWord(reader).trim();
            if (valuePath.isEmpty()) {
                throw new SyntaxException("Missing value path for relative range");
            }
            else return valuePath;
        }
        else throw new SyntaxException("Missing value path for relative range");
    }

    private String expectNonNumeric(StringCharReader reader) {
        boolean started = false;
        char symbol;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            }
            else if (isNumeric(symbol)) {
                reader.back();
                break;
            }
            else if (!isDelimeter(symbol)) {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }
    
    private String expectAnyWord(StringCharReader reader) {
        boolean started = false;
        char symbol;
        StringBuffer buffer = new StringBuffer();
        while(reader.hasMore()) {
            symbol = reader.next();
            if (started && isDelimeter(symbol)) {
                break;
            }
            else if (!isDelimeter(symbol)) {
                buffer.append(symbol);
                started = true;
            }
        }
        return buffer.toString();
    }


    private String msgFor(String text) {
        return String.format("Cannot parse range: \"%s\"", text);
    }

    public String getEndingWord() {
        return endingWord;
    }

    public void setEndingWord(String endingWord) {
        this.endingWord = endingWord;
    }
         
}

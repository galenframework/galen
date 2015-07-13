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
import net.mindengine.galen.specs.RangeValue;
import net.mindengine.galen.specs.reader.StringCharReader;

public class ExpectRange implements Expectation<Range>{

    private String endingWord = "px";
   
	private enum RangeType {
		NOTHING, APPROXIMATE, LESS_THAN, GREATER_THAN
	}

    @Override
    public Range read(StringCharReader reader) {
        
        RangeType rangeType = RangeType.NOTHING;
        
        char firstNonWhiteSpaceSymbol = reader.firstNonWhiteSpaceSymbol();
        if (firstNonWhiteSpaceSymbol == '~') {
            rangeType = RangeType.APPROXIMATE;
        }
        else if (firstNonWhiteSpaceSymbol == '>') {
        	rangeType = RangeType.GREATER_THAN;
        }
        else if (firstNonWhiteSpaceSymbol == '<') {
        	rangeType = RangeType.LESS_THAN;
        }
        
        RangeValue firstValue = new ExpectRangeValue().read(reader);
        
        String text = expectNonNumeric(reader);
        if (text.equals(endingWord)) {
            return createRange(firstValue, rangeType);
        }
        if (text.equals("%")) {
            return createRange(firstValue, rangeType).withPercentOf(readPercentageOf(reader));
        }
        else if (rangeType == RangeType.NOTHING){
            Range range;

            if (text.equals("to")) {
                RangeValue secondValue =  new ExpectRangeValue().read(reader);
                range = Range.between(firstValue, secondValue);
            }
            else {
                throw new SyntaxException(UNKNOWN_LINE, "Expecting \"px\", \"to\" or \"%\", got \"" + text + "\"");
            }
            
            String end = expectNonNumeric(reader);
            if (end.equals(endingWord)) {
                return range;
            }
            else if (end.equals("%")) {
                return range.withPercentOf(readPercentageOf(reader));
            }
            else throw new SyntaxException(UNKNOWN_LINE, "Missing ending: \"px\" or \"%\"");
        }
        else throw new SyntaxException(UNKNOWN_LINE, msgFor(text));
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
        else {
            return Range.exact(firstValue);
        }
    }

    private String readPercentageOf(StringCharReader reader) {
        String firstWord = expectNonNumeric(reader);
        if (firstWord.equals("of")) {
            String valuePath = expectAnyWord(reader).trim();
            if (valuePath.isEmpty()) {
                throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
            }
            else return valuePath;
        }
        else throw new SyntaxException(UNKNOWN_LINE, "Missing value path for relative range");
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

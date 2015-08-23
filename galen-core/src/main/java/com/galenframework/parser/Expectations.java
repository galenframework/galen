/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.specs.Location;
import com.galenframework.specs.Side;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.specs.Location;
import com.galenframework.specs.Range;
import com.galenframework.specs.Side;
import com.galenframework.specs.colors.ColorRange;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.reader.StringCharReader;
import org.apache.commons.lang3.tuple.Pair;

public class Expectations {

    
    public static List<Expectation<?>> expectThese(Expectation<?>...expectations) {
        return Arrays.asList(expectations);
    }

    public static Expectation<List<Side>> sides() {
        return new ExpectSides();
    }

    public static Expectation<Range> range() {
        return new ExpectRange();
    }

    public static Expectation<String> objectName() {
        return new ExpectWord();
    }

    public static Expectation<String> filePath() {
        return new ExpectWord();
    }

    public static Expectation<Double>number() {
        return new ExpectNumber();
    }

    public static Expectation<String> word() {
        return new ExpectWord();
    }
    
    public static Expectation<String[]> commandLineArguments() {
        return new ExpectCommandLineArguments();
    }
    
    public static boolean isDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t';
    }
    
    public static boolean isWordDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t' || symbol == ',';
    }
    
    public static boolean isNumeric(char symbol) {
        return symbol == '-' || (symbol >= '0' && symbol <= '9');
    }

    public static Expectation<List<Location>> locations() {
        return new ExpectLocations();
    }

    public static Expectation<CorrectionsRect> corrections() {
        return new ExpectCorrection();
    }

    public static Expectation<List<ColorRange>> colorRanges() {
        return new ExpectColorRanges();
    }


    public static Expectation<List<Pair<String, String>>> commaSeparatedRepeatedKeyValues() {
        return new ExpectCommaSeparatedKeyValue();
    }

    public static List<String> readAllWords(String arguments) {
        List<String> words = new LinkedList<String>();
        StringCharReader reader = new StringCharReader(arguments);

        ExpectWord expectWord = new ExpectWord();

        while(reader.hasMoreNormalSymbols()) {
            String word = expectWord.read(reader);
            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    public static Expectation<String> doubleQuotedText() {
        return new Expectation<String>() {
            @Override
            public String read(StringCharReader charReader) {
                char firstNonWhiteSpaceSymbol = charReader.firstNonWhiteSpaceSymbol();
                if (firstNonWhiteSpaceSymbol == '"') {
                    charReader.readSafeUntilSymbol('"');
                    return new ExpectString().read(charReader);
                } else {
                    throw new SyntaxException("Expected \" symbol, got: " + firstNonWhiteSpaceSymbol);
                }
            }
        };
    }

    public static ExpectationErrorRate errorRate() {
        return new ExpectationErrorRate();
    }

    public static void expectNextWord(String expectedWord, StringCharReader reader) {
        String word = word().read(reader);
        if (!expectedWord.equals(word)) {
            throw new SyntaxException("Expected: " + expectedWord + ", got: " + word);
        }
    }
}

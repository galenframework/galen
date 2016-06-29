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

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


public class ExpectCommaSeparatedKeyValue implements Expectation<List<Pair<String, String>>> {

    private final Integer SQUARE_BRACKET = 1;
    private final Integer CURLY_BRACKET = 2;
    private final Integer ROUND_BRACKET = 3;

    @Override
    public List<Pair<String, String>> read(StringCharReader reader) {
        List<Pair<String, String>> data = new LinkedList<Pair<String, String>>();

        while(reader.hasMore()) {
            String word = new ExpectWord().read(reader);
            if (!word.isEmpty()) {
                MutablePair<String, String> currentParam = new MutablePair<>(word, readParamValue(reader).trim());
                data.add(currentParam);
            }
        }

        return data;
    }

    private String readParamValue(StringCharReader reader) {
        Stack<Integer> bracketStack = new Stack<>();
        StringBuilder text = new StringBuilder();

        while(reader.hasMore()) {
            char symbol = reader.next();

            if (bracketStack.isEmpty() && symbol == ',') {
                return text.toString();
            } else {
                if (symbol == '\"') {
                    reader.back();
                    text.append(Expectations.doubleQuotedText().read(reader));
                } else if (isOpeningBracket(symbol)) {
                    bracketStack.push(bracketType(symbol));
                    text.append(symbol);
                } else if (isClosingBracket(symbol)) {
                    if (Objects.equals(bracketStack.peek(), bracketType(symbol))) {
                        text.append(symbol);
                        bracketStack.pop();
                    } else {
                        throw new SyntaxException("Unexpected closing bracket: " + symbol);
                    }
                } else {
                    text.append(symbol);
                }
            }
        }
        return text.toString();
    }


    private Map<Character, Integer> bracketsToType = new HashMap<Character, Integer>() {{
        put('[', SQUARE_BRACKET);
        put(']', SQUARE_BRACKET);
        put('(', ROUND_BRACKET);
        put(')', ROUND_BRACKET);
        put('{', CURLY_BRACKET);
        put('}', CURLY_BRACKET);
    }};

    private Integer bracketType(char symbol) {
        if (bracketsToType.containsKey(symbol)) {
            return bracketsToType.get(symbol);
        } else {
            throw new SyntaxException("Not a bracket: " + symbol);
        }
    }

    private boolean isClosingBracket(char symbol) {
        return symbol == ']' || symbol == ')' || symbol == '}';
    }

    private boolean isOpeningBracket(char symbol) {
        return symbol == '[' || symbol == '(' || symbol == '{';
    }
}

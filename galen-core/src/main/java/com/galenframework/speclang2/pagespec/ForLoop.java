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
package com.galenframework.speclang2.pagespec;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.StringCharReader;
import com.galenframework.parser.StructNode;
import com.galenframework.specs.Place;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ForLoop {

    public static final String DEFAULT_VARIABLE_NAME = "index";
    private String indexMapping;
    private String previousMapping;
    private String nextMapping;
    private Object[] sequence;
    private String variableName;

    public ForLoop(Object[] sequence, String variableName, String previousMapping, String nextMapping, String indexMapping) {
        this.sequence = sequence;
        this.variableName = variableName;
        this.previousMapping = previousMapping;
        this.nextMapping = nextMapping;
        this.indexMapping = indexMapping;
    }



    public static ForLoop read(boolean isSimpleLoop, PageSpecHandler pageSpecHandler, StringCharReader reader, StructNode originNode) {
        try {
            String emptyness = reader.readUntilSymbol('[').trim();
            if (!emptyness.isEmpty()) {
                throw new SyntaxException(originNode, "Unexpected token: " + emptyness);
            }

            String sequenceStatement = reader.readUntilSymbol(']');
            Object[] sequence;
            if (isSimpleLoop) {
                sequence = readSequenceForSimpleLoop(sequenceStatement, originNode.getPlace());
            } else {
                sequence = readSequenceFromPageObjects(sequenceStatement, pageSpecHandler);
            }

            String variableName = DEFAULT_VARIABLE_NAME;

            String previousMapping = null;
            String nextMapping = null;
            String indexMapping = null;

            if (reader.hasMoreNormalSymbols()) {
                String nextWord = reader.readWord();
                if (!nextWord.equals("as")) {
                    throw new SyntaxException("Invalid token: " + nextWord);
                }

                variableName = reader.readWord();

                if (variableName.isEmpty()) {
                    throw new SyntaxException("Missing index");
                }

                if (reader.hasMoreNormalSymbols()) {
                    reader.readUntilSymbol(',');

                    Pair<String, String> extraMappings = parseExtraMapping(reader);
                    if ("prev".equals(extraMappings.getKey())) {
                        previousMapping = extraMappings.getValue();
                    } else if ("next".equals(extraMappings.getKey())) {
                        nextMapping = extraMappings.getValue();
                    } else if ("index".equals(extraMappings.getKey())) {
                        indexMapping = extraMappings.getValue();
                    } else {
                        throw new SyntaxException("Unknown loop mapping: " + extraMappings.getKey());
                    }
                }
            }
            return new ForLoop(sequence, variableName, previousMapping, nextMapping, indexMapping);
        } catch (SyntaxException ex) {
            ex.setPlace(originNode.getPlace());
            throw ex;
        }
    }

    private static Pair<String, String> parseExtraMapping(StringCharReader reader) {
        String type = reader.readWord();
        String as = reader.readWord();
        String varName = reader.readWord();

        if (type.isEmpty()) {
            throw new SyntaxException("Missing type. Expected 'prev' or 'next'");
        }
        if (!"as".equals(as)) {
            throw new SyntaxException("Incorrect statement. Use 'as'");
        }
        if (varName.isEmpty()) {
            throw new SyntaxException("Missing mapping name for '" + type + "'");
        }

        String theRest = reader.getTheRest().trim();
        if (!theRest.isEmpty()) {
            throw new SyntaxException("Cannot process: " + theRest);
        }

        return new ImmutablePair<>(type, varName);
    }

    private static String[] readSequenceFromPageObjects(String sequenceStatement, PageSpecHandler pageSpecHandler) {
        List<String> matchingObjects = pageSpecHandler.findAllObjectsMatchingStrictStatements(sequenceStatement);
        return matchingObjects.toArray(new String[matchingObjects.size()]);
    }

    private static Object[] readSequenceForSimpleLoop(String sequenceStatement, Place place) {
        sequenceStatement = sequenceStatement.replace(" ", "");
        sequenceStatement = sequenceStatement.replace("\t", "");
        Pattern sequencePattern = Pattern.compile(".*\\-.*");
        try {
            String[] values = sequenceStatement.split(",");

            ArrayList<Object> sequence = new ArrayList<>();

            for (String stringValue : values) {
                if (sequencePattern.matcher(stringValue).matches()) {
                    sequence.addAll(createSequence(stringValue));
                }
                else {
                    sequence.add(convertValueToIndex(stringValue));
                }
            }

            return sequence.toArray(new Object[sequence.size()]);
        }
        catch (Exception ex) {
            throw new SyntaxException(place, "Incorrect sequence syntax: " + sequenceStatement, ex);
        }
    }

    private static Object convertValueToIndex(String stringValue) {
        if (NumberUtils.isNumber(stringValue)) {
            return NumberUtils.toLong(stringValue);
        } else {
            return stringValue;
        }
    }

    private static List<Object> createSequence(String value) {
        int dashIndex = value.indexOf('-');

        int rangeA = Integer.parseInt(value.substring(0, dashIndex));
        int rangeB = Integer.parseInt(value.substring(dashIndex + 1));

        return createSequence(rangeA, rangeB);
    }

    private static List<Object> createSequence(int min, int max) {
        if (max >= min) {
            List<Object> parameters = new LinkedList<>();
            for (int i = min; i <= max; i++) {
                parameters.add(i);
            }
            return parameters;
        }
        else {
            return Collections.emptyList();
        }
    }

    public List<StructNode> apply(LoopVisitor loopVisitor) throws IOException {
        List<StructNode> resultingNodes = new LinkedList<>();

        int begin = 0;
        int end = sequence.length;

        if (previousMapping != null) {
            begin = 1;
        }
        if (nextMapping != null) {
            end = end - 1;
        }

        int index = 0;

        for (int i = begin; i < end; i++) {

            index += 1;

            Map<String, Object> vars = new HashMap<>();
            vars.put(variableName, sequence[i]);

            if (previousMapping != null) {
                vars.put(previousMapping, sequence[i-1]);
            }
            if (nextMapping != null) {
                vars.put(nextMapping, sequence[i+1]);
            }

            if (indexMapping != null) {
                vars.put(indexMapping, index);
            }

            resultingNodes.addAll(loopVisitor.visitLoop(vars));
        }


        return resultingNodes;
    }
}

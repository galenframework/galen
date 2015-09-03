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
package com.galenframework.speclang2.reader.pagespec;

import com.galenframework.parser.SyntaxException;
import com.galenframework.speclang2.AlphanumericComparator;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.parser.StructNode;
import com.galenframework.suite.reader.Line;
import com.galenframework.utils.GalenUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.galenframework.suite.reader.Line.UNKNOWN_LINE;

public class ForLoop {

    public static final String INDEX_DEFAULT_NAME = "index";
    private String previousMapping;
    private String nextMapping;
    private Object[] sequence;
    private String indexName;

    public ForLoop(Object[] sequence, String indexName, String previousMapping, String nextMapping) {
        this.sequence = sequence;
        this.indexName = indexName;
        this.previousMapping = previousMapping;
        this.nextMapping = nextMapping;
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
                sequence = readSequenceForSimpleLoop(sequenceStatement);
            } else {
                sequence = readSequenceFromPageObjects(sequenceStatement, pageSpecHandler);
            }

            String indexName = INDEX_DEFAULT_NAME;

            String previousMapping = null;
            String nextMapping = null;

            if (reader.hasMoreNormalSymbols()) {
                String as = reader.readWord();
                if (as.equals("as")) {

                } else {
                    throw new SyntaxException("Invalid token: " + as);
                }

                indexName = reader.readWord();

                if (indexName.isEmpty()) {
                    throw new SyntaxException("Missing index");
                }

                if (reader.hasMoreNormalSymbols()) {
                    reader.readUntilSymbol(',');

                    Pair<String, String> extraMappings = parseExtraMapping(reader);
                    if ("prev".equals(extraMappings.getKey())) {
                        previousMapping = extraMappings.getValue();
                    } else if ("next".equals(extraMappings.getKey())) {
                        nextMapping = extraMappings.getValue();
                    } else {
                        throw new SyntaxException("Unknown loop mapping: " + extraMappings.getKey());
                    }
                }
            }
            return new ForLoop(sequence, indexName, previousMapping, nextMapping);
        } catch (SyntaxException ex) {
            ex.setLine(new Line(originNode.getSource(), originNode.getFileLineNumber()));
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

        return new ImmutablePair<String, String>(type, varName);
    }

    private static String[] readSequenceFromPageObjects(String sequenceStatement, PageSpecHandler pageSpecHandler) {
        String[] objectPatterns = sequenceStatement.split(",");

        ArrayList<String> matchingObjects = new ArrayList<String>();
        List<String> allObjectNames = pageSpecHandler.getSortedObjectNames();

        for (String objectPattern : objectPatterns) {
            Pattern regex = GalenUtils.convertObjectNameRegex(objectPattern);
            for (String objectName : allObjectNames) {
                if (regex.matcher(objectName).matches()) {
                    matchingObjects.add(objectName);
                }
            }
        }


        Collections.sort(matchingObjects, new AlphanumericComparator());
        return matchingObjects.toArray(new String[]{});
    }

    private static Object[] readSequenceForSimpleLoop(String sequenceStatement) {
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

            return sequence.toArray(new Object[]{});
        }
        catch (Exception ex) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect sequence syntax: " + sequenceStatement, ex);
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
        List<StructNode> resultingNodes = new LinkedList<StructNode>();

        int begin = 0;
        int end = sequence.length;

        if (previousMapping != null) {
            begin = 1;
        }
        if (nextMapping != null) {
            end = end - 1;
        }

        for (int i = begin; i < end; i++) {

            Map<String, Object> vars = new HashMap<>();
            vars.put(indexName, sequence[i]);

            if (previousMapping != null) {
                vars.put(previousMapping, sequence[i-1]);
            }
            if (nextMapping != null) {
                vars.put(nextMapping, sequence[i+1]);
            }

            resultingNodes.addAll(loopVisitor.visitLoop(vars));
        }


        return resultingNodes;
    }
}

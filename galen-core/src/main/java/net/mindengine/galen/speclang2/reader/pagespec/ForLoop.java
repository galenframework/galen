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
package net.mindengine.galen.speclang2.reader.pagespec;

import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.suite.reader.Line;
import net.mindengine.galen.utils.GalenUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

public class ForLoop {

    public static final String INDEX_DEFAULT_NAME = "index";
    private String previousMapping;
    private String nextMapping;
    private String[] sequence;
    private String indexName;

    public ForLoop(String[] sequence, String indexName, String previousMapping, String nextMapping) {
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
            String[] sequence;
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

        return matchingObjects.toArray(new String[]{});
    }

    private static String[] readSequenceForSimpleLoop(String sequenceStatement) {
        sequenceStatement = sequenceStatement.replace(" ", "");
        sequenceStatement = sequenceStatement.replace("\t", "");
        Pattern sequencePattern = Pattern.compile(".*\\-.*");
        try {
            String[] values = sequenceStatement.split(",");

            ArrayList<String> sequence = new ArrayList<String>();

            for (String value : values) {
                if (sequencePattern.matcher(value).matches()) {
                    sequence.addAll(createSequence(value));
                }
                else {
                    sequence.add(value);
                }
            }

            return sequence.toArray(new String[]{});
        }
        catch (Exception ex) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect sequence syntax: " + sequenceStatement, ex);
        }
    }

    private static List<String> createSequence(String value) {
        int dashIndex = value.indexOf('-');

        int rangeA = Integer.parseInt(value.substring(0, dashIndex));
        int rangeB = Integer.parseInt(value.substring(dashIndex + 1));

        return createSequence(rangeA, rangeB);
    }

    private static List<String> createSequence(int min, int max) {
        if (max >= min) {
            List<String> parameters = new LinkedList<String>();
            for (int i = min; i <= max; i++) {
                parameters.add(Integer.toString(i));
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

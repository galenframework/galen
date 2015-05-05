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

import net.mindengine.galen.parser.ProcessedStructNode;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.suite.reader.Line;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

public class ForLoop {

    public static final String INDEX_DEFAULT_NAME = "index";
    private List<String> sequence;
    private String indexName;

    public ForLoop(List<String> sequence, String indexName) {
        this.sequence = sequence;
        this.indexName = indexName;
    }

    public static ForLoop read(StringCharReader reader, StructNode originNode) {
        try {
            String emptyness = reader.readUntilSymbol('[').trim();
            if (!emptyness.isEmpty()) {
                throw originNode.createSyntaxException("Unexpected token: " + emptyness);
            }

            String parameterizations = reader.readUntilSymbol(']');
            List<String> sequence = readSequence(parameterizations);
            String indexName = INDEX_DEFAULT_NAME;

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
                    throw new SyntaxException("Unknown statement: " + reader.getTheRest().trim());
                }
            }
            return new ForLoop(sequence, indexName);
        } catch (SyntaxException ex) {
            ex.setLine(new Line(originNode.getSource(), originNode.getFileLineNumber()));
            throw ex;
        }
    }

    private static List<String> readSequence(String sequenceText) {
        sequenceText = sequenceText.replace(" ", "");
        sequenceText = sequenceText.replace("\t", "");
        Pattern sequencePattern = Pattern.compile(".*\\-.*");
        try {
            String[] values = sequenceText.split(",");

            List<String> sequence = new LinkedList<String>();

            for (String value : values) {
                if (sequencePattern.matcher(value).matches()) {
                    sequence.addAll(createSequence(value));
                }
                else {
                    sequence.add(value);
                }
            }

            return sequence;
        }
        catch (Exception ex) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect sequence syntax: " + sequenceText, ex);
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

    public List<StructNode> apply(LoopVisitor loopVisitor) {
        List<StructNode> resultingNodes = new LinkedList<StructNode>();

        for (final String sequenceValue : sequence) {
            resultingNodes.addAll(loopVisitor.visitLoop(new HashMap<String, String>(){{
                put(indexName, sequenceValue);
            }}));
        }

        return resultingNodes;
    }
}

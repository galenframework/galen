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

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

public class LogicProcessor {
    public static final String FOR_LOOP_KEYWORD = "@for";
    public static final String FOR_EACH_LOOP_KEYWORD = "@forEach";
    public static final String SET_KEYWORD = "@set";
    public static final String OBJECTS_KEYWORD = "@objects";
    public static final String ON_KEYWORD = "@on";
    public static final String IMPORT_KEYWORD = "@import";


    private final PageSpecHandler pageSpecHandler;

    private List<String> logicOperators = asList(
            FOR_LOOP_KEYWORD,
            FOR_EACH_LOOP_KEYWORD,
            SET_KEYWORD,
            OBJECTS_KEYWORD,
            ON_KEYWORD,
            IMPORT_KEYWORD
    );

    public LogicProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(List<StructNode> nodes) throws IOException {
        List<StructNode> resultingNodes = new LinkedList<StructNode>();

        for (StructNode node : nodes) {
            StructNode processedNode = pageSpecHandler.processExpressionsIn(node);

            if (isLogicStatement(processedNode.getName())) {
                resultingNodes.addAll(processLogicStatement(processedNode));
            } else {
                resultingNodes.add(processNonLogicStatement(processedNode));
            }
        }

        return resultingNodes;
    }

    private StructNode processNonLogicStatement(StructNode processedNode) throws IOException {
        if (processedNode.getChildNodes() != null) {
            StructNode fullyProcessed = new StructNode(processedNode.getName());
            fullyProcessed.setFileLineNumber(processedNode.getFileLineNumber());
            fullyProcessed.setSource(processedNode.getSource());

            fullyProcessed.setChildNodes(process(processedNode.getChildNodes()));
            return fullyProcessed;
        } else {
            return processedNode;
        }
    }

    private List<StructNode> processLogicStatement(final StructNode statementNode) throws IOException {
        StringCharReader reader = new StringCharReader(statementNode.getName());
        String firstWord = reader.readWord();
        if (FOR_LOOP_KEYWORD.equals(firstWord)
                || FOR_EACH_LOOP_KEYWORD.equals(firstWord)) {
            ForLoop forLoop = ForLoop.read(FOR_LOOP_KEYWORD.equals(firstWord), pageSpecHandler, reader, statementNode);

            return forLoop.apply(new LoopVisitor() {
                @Override
                public List<StructNode> visitLoop(Map<String, String> variables) throws IOException {
                    pageSpecHandler.setGlobalVariables(variables, statementNode);
                    return process(statementNode.getChildNodes());
                }
            });
        } else if (SET_KEYWORD.equals(firstWord)) {
            new SetVariableProcessor(pageSpecHandler).process(reader, statementNode);
            return Collections.emptyList();
        } else if (OBJECTS_KEYWORD.equals(firstWord)) {
            new ObjectDefinitionProcessor(pageSpecHandler).process(reader, statementNode);
            return Collections.emptyList();
        } else if (ON_KEYWORD.equals(firstWord)) {
            return process(new OnFilterProcessor(pageSpecHandler).process(reader, statementNode));
        } else if (IMPORT_KEYWORD.equals(firstWord)) {
            return new ImportProcessor(pageSpecHandler).process(reader, statementNode);
        } else {
            throw new SyntaxException(statementNode, "Invalid statement: " + firstWord);
        }
    }

    private boolean isLogicStatement(String name) {
        String firstWord = new StringCharReader(name).readWord();
        return logicOperators.contains(firstWord);
    }
}

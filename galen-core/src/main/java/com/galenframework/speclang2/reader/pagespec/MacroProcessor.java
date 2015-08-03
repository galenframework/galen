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
import com.galenframework.parser.StructNode;
import com.galenframework.specs.reader.StringCharReader;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

public class MacroProcessor {
    public static final String FOR_LOOP_KEYWORD = "@for";
    public static final String FOR_EACH_LOOP_KEYWORD = "@forEach";
    public static final String SET_KEYWORD = "@set";
    public static final String OBJECTS_KEYWORD = "@objects";
    public static final String ON_KEYWORD = "@on";
    public static final String IMPORT_KEYWORD = "@import";
    public static final String SCRIPT_KEYWORD = "@script";
    public static final String RULE_KEYWORD = "@rule";
    public static final String IF_KEYWORD = "@if";
    public static final String ELSEIF_KEYWORD = "@elseif";
    public static final String ELSE_KEYWORD = "@else";


    private final PageSpecHandler pageSpecHandler;

    private List<String> macroOperators = asList(
            FOR_LOOP_KEYWORD,
            FOR_EACH_LOOP_KEYWORD,
            SET_KEYWORD,
            OBJECTS_KEYWORD,
            ON_KEYWORD,
            IMPORT_KEYWORD,
            SCRIPT_KEYWORD,
            RULE_KEYWORD
    );

    public MacroProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(List<StructNode> nodes) throws IOException {
        List<StructNode> resultingNodes = new LinkedList<StructNode>();

        ListIterator<StructNode> it = nodes.listIterator();

        while (it.hasNext()) {
            StructNode node = it.next();

            if (isConditionStatement(node.getName())) {
                try {
                    resultingNodes.addAll(processConditionStatements(node, it));
                } catch (Exception ex) {
                    throw new SyntaxException(node, "JavaScript error inside statement", ex);
                }
            } else {
                StructNode processedNode = pageSpecHandler.processExpressionsIn(node);
                if (isMacroStatement(processedNode.getName())) {
                    resultingNodes.addAll(processMacroStatement(processedNode));
                } else {
                    resultingNodes.add(processNonMacroStatement(processedNode));
                }
            }
        }

        return resultingNodes;
    }

    private List<StructNode> processConditionStatements(StructNode ifNode, ListIterator<StructNode> it) throws IOException {
        List<StructNode> elseIfNodes = new LinkedList<StructNode>();
        StructNode elseNode = null;
        boolean finishedConditions = false;

        while(it.hasNext() && !finishedConditions) {
            StructNode nextNode = it.next();

            String firstWord = new StringCharReader(nextNode.getName()).readWord();
            if (firstWord.equals(ELSEIF_KEYWORD)) {
                if (elseNode != null) {
                    throw new SyntaxException(nextNode, "Cannot use elseif statement after else block");
                }
                elseIfNodes.add(pageSpecHandler.processStrictExpressionsIn(nextNode));
            } else if (firstWord.equals(ELSE_KEYWORD)) {
                if (elseNode != null) {
                    throw new SyntaxException(nextNode, "Cannot use else statement after else block");
                }
                elseNode = pageSpecHandler.processStrictExpressionsIn(nextNode);
            } else {
                finishedConditions = true;
                it.previous();
            }
        }

        List<StructNode> nodesFromConditions = applyConditions(pageSpecHandler.processStrictExpressionsIn(ifNode), elseIfNodes, elseNode);
        return process(nodesFromConditions);
    }

    private List<StructNode> applyConditions(StructNode ifNode, List<StructNode> elseIfNodes, StructNode elseNode) {
        if (isSuccessfullCondition(ifNode)) {
            return ifNode.getChildNodes();
        } else if (elseIfNodes != null) {

            for (StructNode node : elseIfNodes) {
                if (isSuccessfullCondition(node)) {
                    return node.getChildNodes();
                }
            }
        }

        if (elseNode != null) {
            return elseNode.getChildNodes();
        }

        return Collections.emptyList();
    }

    private boolean isSuccessfullCondition(StructNode node) {
        StringCharReader reader = new StringCharReader(node.getName());

        reader.readWord();

        String booleanText = reader.readWord();
        if (booleanText.isEmpty()) {
            throw new SyntaxException(node, "Missing boolean statement in condition");
        }

        try {
            return Boolean.parseBoolean(booleanText);
        } catch (Exception ex) {
            throw new SyntaxException(node, "Couldn't parse boolean", ex);
        }
    }

    private boolean isConditionStatement(String name) {
        return IF_KEYWORD.equals(new StringCharReader(name).readWord());
    }

    private StructNode processNonMacroStatement(StructNode processedNode) throws IOException {
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

    private List<StructNode> processMacroStatement(final StructNode statementNode) throws IOException {
        StringCharReader reader = new StringCharReader(statementNode.getName());
        String firstWord = reader.readWord();
        if (FOR_LOOP_KEYWORD.equals(firstWord)
                || FOR_EACH_LOOP_KEYWORD.equals(firstWord)) {
            ForLoop forLoop = ForLoop.read(FOR_LOOP_KEYWORD.equals(firstWord), pageSpecHandler, reader, statementNode);

            return forLoop.apply(new LoopVisitor() {
                @Override
                public List<StructNode> visitLoop(Map<String, Object> variables) throws IOException {
                    pageSpecHandler.setGlobalVariables(variables, statementNode);
                    return process(statementNode.getChildNodes());
                }
            });
        } else if (SET_KEYWORD.equals(firstWord)) {
            return new SetVariableProcessor(pageSpecHandler).process(reader, statementNode);
        } else if (OBJECTS_KEYWORD.equals(firstWord)) {
            return new ObjectDefinitionProcessor(pageSpecHandler).process(reader, statementNode);
        } else if (ON_KEYWORD.equals(firstWord)) {
            return process(new OnFilterProcessor(pageSpecHandler).process(reader, statementNode));
        } else if (IMPORT_KEYWORD.equals(firstWord)) {
            return new ImportProcessor(pageSpecHandler).process(reader, statementNode);
        } else if (SCRIPT_KEYWORD.equals(firstWord)) {
            return new ScriptProcessor(pageSpecHandler).process(reader, statementNode);
        } else if (RULE_KEYWORD.equals(firstWord)) {
            return new RuleProcessor(pageSpecHandler).process(reader, statementNode);
        } else if (ELSEIF_KEYWORD.equals(firstWord)) {
            throw new SyntaxException(statementNode, "elseif statement without if block");
        } else if (ELSE_KEYWORD.equals(firstWord)) {
            throw new SyntaxException(statementNode, "else statement without if block");
        } else {
            throw new SyntaxException(statementNode, "Invalid statement: " + firstWord);
        }
    }

    private boolean isMacroStatement(String name) {
        String firstWord = new StringCharReader(name).readWord();
        return macroOperators.contains(firstWord);
    }
}

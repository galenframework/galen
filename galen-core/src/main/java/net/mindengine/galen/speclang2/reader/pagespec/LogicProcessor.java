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
import net.mindengine.galen.specs.reader.StringCharReader;

import java.util.*;

import static java.util.Arrays.asList;

public class LogicProcessor {
    private final PageSpecProcessor pageSpecProcessor;
    private List<String> logicOperators = asList(
            "@for",
            "@set"
    );

    public LogicProcessor(PageSpecProcessor pageSpecProcessor) {
        this.pageSpecProcessor = pageSpecProcessor;
    }

    public List<ProcessedStructNode> process(List<StructNode> nodes) {
        List<ProcessedStructNode> resultingNodes = new LinkedList<ProcessedStructNode>();

        for (StructNode node : nodes) {
            ProcessedStructNode processedNode = pageSpecProcessor.processExpressionsIn(node);

            if (isLogicStatement(processedNode.getName())) {
                resultingNodes.addAll(processLogicStatement(processedNode));
            } else {
                resultingNodes.add(processNonLogicStatement(processedNode));
            }
        }

        return resultingNodes;
    }

    private ProcessedStructNode processNonLogicStatement(ProcessedStructNode processedNode) {
        if (processedNode.getChildNodes() != null) {
            ProcessedStructNode fullyProcessed = new ProcessedStructNode(processedNode.getName(), processedNode);

            fullyProcessed.setChildNodes(convertList(process(processedNode.getChildNodes())));
            return fullyProcessed;
        } else {
            return processedNode;
        }
    }

    private List<StructNode> convertList(List<ProcessedStructNode> processed) {
        List<StructNode> list = new LinkedList<StructNode>();
        for (ProcessedStructNode item: processed) {
            list.add(item);
        }
        return list;
    }


    private List<ProcessedStructNode> processLogicStatement(final ProcessedStructNode logicStatementNode) {
        StringCharReader reader = new StringCharReader(logicStatementNode.getName());
        String firstWord = reader.readWord();
        if ("@for".equals(firstWord)) {
            ForLoop forLoop = ForLoop.read(reader, logicStatementNode);

            return forLoop.apply(new LoopVisitor() {
                @Override
                public List<ProcessedStructNode> visitLoop(Map<String, String> variables) {
                    pageSpecProcessor.setGlobalVariables(variables, logicStatementNode);
                    return process(logicStatementNode.getChildNodes());
                }
            });
        } else if ("@set".equals(firstWord)) {
            new SetVariableProcessor(pageSpecProcessor).process(reader, logicStatementNode);
            return Collections.emptyList();
        } else {
            throw logicStatementNode.createSyntaxException("Invalid statement: " + firstWord);
        }
    }

    private boolean isLogicStatement(String name) {
        String firstWord = new StringCharReader(name).readWord();
        return logicOperators.contains(firstWord);
    }
}

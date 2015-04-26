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


import net.mindengine.galen.suite.reader.Line;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Stack;

public class IndentationStructureParser {
    public static final String COMMENT_SYMBOL = "#";
    private static final char SPACE = ' ';
    private static final char TAB = '\t';
    private static final int TAB_SIZE = 4;

    public List<StructNode> parse(String contentWithTabs) throws IOException {
        return parse(new ByteArrayInputStream(contentWithTabs.getBytes()));
    }


    private static class IndentationNode {
        private final StructNode parent;
        private final Integer indentation;
        private final StructNode structNode;
        private int childIndentation = 0;
        public IndentationNode(Integer indentation, StructNode structNode, StructNode parent) {
            this.indentation = indentation;
            this.structNode = structNode;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return structNode.getName() + " #" + indentation;
        }
    }

    public List<StructNode> parse(InputStream stream) throws IOException {
        Stack<IndentationNode> nodeStack = new Stack<IndentationNode>();


        StructNode rootNode = new StructNode();
        nodeStack.push(new IndentationNode(-1, rootNode, null));

        List<String> lines = IOUtils.readLines(stream);

        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (isProcessable(line)) {
                processLine(nodeStack, line, lineNumber);
            }
        }

        return rootNode.getChildNodes();
    }

    private void processLine(Stack<IndentationNode> stack, String line, int lineNumber) {
        StructNode newStructNode = new StructNode(line.trim());

        int calculatedIndentation = calculateIndentation(line, lineNumber);

        while (calculatedIndentation <= stack.peek().indentation && stack.peek().parent != null) {
            stack.pop();
        }


        StructNode parent = stack.peek().structNode;

        if (parent.getChildNodes() != null && parent.getChildNodes().size() > 0
                && calculatedIndentation != stack.peek().childIndentation) {
            throw new SyntaxException(new Line(line, lineNumber), "Inconsistent indentation");
        }


        parent.addChildNode(newStructNode);
        stack.peek().childIndentation = calculatedIndentation;

        stack.push(new IndentationNode(calculatedIndentation, newStructNode, parent));
    }

    private int calculateIndentation(String line, int lineNumber) {
        int indentation = 0;
        char symbol;
        for (int i = 0; i < line.length(); i++) {
            symbol = line.charAt(i);
            if (symbol == SPACE) {
                indentation += 1;
            } else if (symbol == TAB) {
                indentation += TAB_SIZE;
            } else {
                return indentation;
            }
        }

        throw new SyntaxException(new Line(line, lineNumber), "This line does not have any text");
    }

    private boolean isProcessable(String line) {
        String trimmed = line.trim();
        return !(trimmed.isEmpty() || trimmed.startsWith(COMMENT_SYMBOL));
    }


}

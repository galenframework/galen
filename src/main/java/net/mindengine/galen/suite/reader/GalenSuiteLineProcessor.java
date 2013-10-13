/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.suite.reader;

import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.suite.GalenSuite;

public class GalenSuiteLineProcessor {

    private RootNode rootNode = new RootNode();
    private Node<?> currentNode = rootNode;
    
    private static final int INDENTATION = 4;

    public void processLine(String line, int number) {
        if (!isBlank(line) && !isCommented(line) && !isSeparator(line)) {
            if (line.startsWith("@@")) {
                currentNode = processSpecialInstruction(new Line(line.substring(2).trim(), number));
            }
            else {
                int level = indentationLevel(line);
                
                Node<?> processingNode = currentNode.findProcessingNodeByLevel(level);
                Node<?> newNode = processingNode.processNewNode(new Line(line, number));
                currentNode = newNode;
            }
        }
    }
    
    private Node<?> processSpecialInstruction(Line line) {
        String text = line.getText();
        currentNode = rootNode;
        int indexOfFirstSpace = text.indexOf(' ');
        
        String firstWord;
        String leftover;
        if (indexOfFirstSpace > 0) {
            firstWord = text.substring(0, indexOfFirstSpace).toLowerCase();
            leftover = text.substring(indexOfFirstSpace).trim();
        }
        else {
            firstWord = text.toLowerCase();
            leftover = "";
        }
        
        Line leftoverLine = new Line(leftover, line.getNumber());
        
        if (firstWord.equals("set")) {
            return processInstructionSet(leftoverLine);
        }
        else if (firstWord.equals("table")){
            return processTable(leftoverLine);
        }
        else if (firstWord.equals("parameterized")){
            return processParameterized(leftoverLine);
        }
        else throw new SuiteReaderException("Unknown instruction: " + firstWord);
    }

    private Node<?> processParameterized(Line line) {
        ParameterizedNode parameterizedNode = new ParameterizedNode(line);
        currentNode.add(parameterizedNode);
        return parameterizedNode;
    }

    private Node<?> processTable(Line line) {
        TableNode tableNode = new TableNode(line);
        currentNode.add(tableNode);
        return tableNode;
    }

    private Node<?> processInstructionSet(Line line) {
        SetNode newNode = new SetNode(line);
        currentNode.add(newNode);
        return newNode;
    }

    public List<GalenSuite> buildSuites() {
        return rootNode.build(new BashTemplateContext());
    }

    private int indentationLevel(String line) {
        int spacesCount = 0;
        for (int i=0; i<line.length(); i++) {
            if (line.charAt(i) == ' ') {
                spacesCount++;
            }
            else {
                return (int) Math.floor(spacesCount / INDENTATION);
            }
        }
        return 0;
    }

    private boolean isSeparator(String line) {
        line = line.trim();
        if (line.length() > 3) {
            char ch = line.charAt(0);
            for (int i = 1; i < line.length(); i++) {
                if (ch != line.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        else return false;
    }

    private boolean isBlank(String line) {
        return line.trim().isEmpty();
    }

    private boolean isCommented(String line) {
        return line.trim().startsWith("#");
    }
    
}

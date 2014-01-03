/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenSuite;

public class GalenSuiteLineProcessor {

    private RootNode rootNode = new RootNode();
    private Node<?> currentNode = rootNode;
    private boolean disableNextSuite = false;
    private String contextPath;

    public GalenSuiteLineProcessor(String contextPath) {
        this.contextPath = contextPath;
    }

    public void processLine(String line, int number) throws FileNotFoundException, IOException {
        if (!isBlank(line) && !isCommented(line) && !isSeparator(line)) {
            if (line.startsWith("@@")) {
                Node<?> node = processSpecialInstruction(new Line(line.substring(2).trim(), number));
                if (node != null) {
                    currentNode = node;
                }
            }
            else {
                int spaces = indentationSpaces(line);
                
                Node<?> processingNode = currentNode.findProcessingNodeByIndentation(spaces);
                Node<?> newNode = processingNode.processNewNode(new Line(line, number));
                
                if (newNode instanceof SuiteNode) {
                    if (disableNextSuite) {
                        disableNextSuite = false; 
                        ((SuiteNode)newNode).setDisabled(true);
                    }
                }
                
                currentNode = newNode;
            }
        }
    }
    
    private Node<?> processSpecialInstruction(Line line) throws FileNotFoundException, IOException {
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
        else if (firstWord.equals("disabled")) {
            markNextSuiteAsDisabled();
            return null;
        }
        else if (firstWord.equals("import")) {
            List<Node<?>> nodes = importSuite(leftover, line);
            rootNode.getChildNodes().addAll(nodes);
            return null;
        }
        else throw new SuiteReaderException("Unknown instruction: " + firstWord);
    }

    private List<Node<?>> importSuite(String path, Line line) throws FileNotFoundException, IOException {
        if (path.isEmpty()) {
            throw new SyntaxException(line, "No path specified for importing");
        }
        
        String fullChildPath = contextPath + File.separator + path;
        String childContextPath = new File(fullChildPath).getParent();
        GalenSuiteLineProcessor childProcessor = new GalenSuiteLineProcessor(childContextPath);
        
        File file = new File(fullChildPath);
        if (!file.exists()) {
            throw new SyntaxException(line, "File doesn't exist: " + file.getAbsolutePath());
        }
        childProcessor.readLines(new FileInputStream(file));
        return childProcessor.rootNode.getChildNodes();
    }

    private void markNextSuiteAsDisabled() {
        this.disableNextSuite = true;
    }

    private Node<?> processParameterized(Line line) {
        ParameterizedNode parameterizedNode = new ParameterizedNode(line);
        
        if (disableNextSuite) {
            parameterizedNode.setDisabled(true);
            disableNextSuite = false;
        }
        
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

    private int indentationSpaces(String line) {
        int spacesCount = 0;
        for (int i=0; i<line.length(); i++) {
            if (line.charAt(i) == ' ') {
                spacesCount++;
            }
            else {
                return spacesCount;
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

    public void readLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        String line = bufferedReader.readLine();
        int lineNumber = 0;
        while(line != null){
            lineNumber++;
            processLine(line, lineNumber);
            line = bufferedReader.readLine();
        }
    }
    
}

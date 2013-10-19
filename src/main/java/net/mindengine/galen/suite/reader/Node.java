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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;

public abstract class Node<T> {

    private int level = 0;
    private Node<?> parent;
    private Line line;
    private int spacesIndentation = 0;

    public Node(Line line) {
        this.setLine(line);
        this.setSpacesIndentation(calculateSpacesIndentation());
    }
    
    private int calculateSpacesIndentation() {
        int spaces = 0;
        
        if (line != null && line.getText() != null) {
            for (int i=0; i<line.getText().length(); i++) {
                if (line.getText().charAt(i) == ' ') {
                    spaces++;
                }
                else return spaces;
            }
        }
        return spaces;
    }

    private List<Node<?>> childNodes = new LinkedList<Node<?>>();
    
    protected void add(Node<?> childNode) {
        childNode.parent = this;
        childNode.level = this.level + 1;
        
        int spaceDiff = childNode.getSpacesIndentation() - this.getSpacesIndentation();
        if (spaceDiff > 8) {
            throw new SyntaxException(childNode.line, "Incorrect indentation. Should use from 1 to 8 spaces");
        }
        
        if (getChildNodes().size() > 0) {
            if (getChildNodes().get(0).getSpacesIndentation() != childNode.getSpacesIndentation()) {
                throw new SyntaxException(childNode.line, "Incorrect indentation. Amount of spaces in indentation should be the same within one level");
            }
        }
        
        getChildNodes().add(childNode);
    }
    
    public abstract T build(BashTemplateContext context);

    
    public Node<?> findProcessingNodeByIndentation(int spaces) {
        if (this.spacesIndentation < spaces) {
            return this;
        }
        else {
            return parent.findProcessingNodeByIndentation(spaces);
        }
    }


    public abstract Node<?> processNewNode(Line line);

    public List<Node<?>> getChildNodes() {
        return childNodes;
    }



    public Line getLine() {
        return line;
    }
    
    public String getArguments() {
        return line.getText();
    }



    public void setLine(Line line) {
        this.line = line;
    }

    public int getSpacesIndentation() {
        return spacesIndentation;
    }

    public void setSpacesIndentation(int spacesIndentation) {
        this.spacesIndentation = spacesIndentation;
    }

    
}

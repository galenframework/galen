/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.suite.reader;

import java.util.LinkedList;
import java.util.List;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.VarsContext;
import com.galenframework.specs.Place;

public abstract class Node<T> {

    private int level = 0;
    private Node<?> parent;
    private Place place;
    private String text;
    private int spacesIndentation = 0;

    public Node(String text, Place line) {
        this.setText(text);
        this.setPlace(line);
        this.setSpacesIndentation(calculateSpacesIndentation());
    }
    
    private int calculateSpacesIndentation() {
        int spaces = 0;

        if (text != null) {
            return GalenSuiteLineProcessor.calculateIndentationSpaces(text);
        }
        return spaces;
    }

    private List<Node<?>> childNodes = new LinkedList<>();

    protected void add(Node<?> childNode) {
        childNode.parent = this;
        childNode.level = this.level + 1;
        
        int spaceDiff = childNode.getSpacesIndentation() - this.getSpacesIndentation();
        if (spaceDiff > 8) {
            throw new SyntaxException(childNode.place, "Incorrect indentation. Should use from 1 to 8 spaces");
        }
        
        if (getChildNodes().size() > 0) {
            if (getChildNodes().get(0).getSpacesIndentation() != childNode.getSpacesIndentation()) {
                throw new SyntaxException(childNode.place, "Incorrect indentation. Amount of spaces in indentation should be the same within one level");
            }
        }
        
        getChildNodes().add(childNode);
    }
    
    public abstract T build(VarsContext context);

    
    public Node<?> findProcessingNodeByIndentation(int spaces) {
        if (this.spacesIndentation < spaces) {
            return this;
        }
        else {
            return parent.findProcessingNodeByIndentation(spaces);
        }
    }


    public abstract Node<?> processNewNode(String text, Place line);

    public List<Node<?>> getChildNodes() {
        return childNodes;
    }



    public Place getPlace() {
        return place;
    }
    
    public String getArguments() {
        return text.trim();
    }



    public void setPlace(Place place) {
        this.place = place;
    }

    public int getSpacesIndentation() {
        return spacesIndentation;
    }

    public void setSpacesIndentation(int spacesIndentation) {
        this.spacesIndentation = spacesIndentation;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

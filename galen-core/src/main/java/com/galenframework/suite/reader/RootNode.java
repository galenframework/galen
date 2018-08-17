/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.VarsContext;
import com.galenframework.specs.Place;
import com.galenframework.tests.GalenBasicTest;

public class RootNode extends Node<List<GalenBasicTest>> {

    public RootNode() {
        super(null, null);
    }
    
    
    @Override
    public Node<?> findProcessingNodeByIndentation(int spaces) {
        return this;
    }

    @Override
    public Node<?> processNewNode(String text, Place place) {
        if (text.startsWith(" ")) {
            throw new SyntaxException(place, "Should not start with space");
        }
        
        TestNode suiteNode = new TestNode(text, place);
        add(suiteNode);
        return suiteNode;
    }


    @Override
    public List<GalenBasicTest> build(VarsContext context) {
        rearrangeNodes();
        
        List<GalenBasicTest> suites = new LinkedList<>();
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TestNode) {
                TestNode suiteNode = (TestNode)childNode;
                if (suiteNode.isEnabled()) {
                    suites.add(suiteNode.build(context));
                }
            }
            else if (childNode instanceof ParameterizedNode) {
                ParameterizedNode parameterizedNode = (ParameterizedNode)childNode;
                if (parameterizedNode.isEnabled()) {
                    suites.addAll(parameterizedNode.build(context));
                }
            }
            else {
                childNode.build(context);
            }
        }
        return suites;
    }


    private void rearrangeNodes() {
        Iterator<Node<?>> it = getChildNodes().iterator();
        
        ParameterizedNode currentParameterizedNode = null;
        
        while(it.hasNext()) {
            Node<?> node = it.next();
            
            if (node instanceof ParameterizedNode) {
                if (currentParameterizedNode != null) {
                    currentParameterizedNode.setToParameterize(node);
                    currentParameterizedNode = (ParameterizedNode) node;
                    it.remove();
                }
                else {
                    currentParameterizedNode = (ParameterizedNode) node;
                }
            }
            else if (node instanceof TestNode) {
                if (currentParameterizedNode != null) {
                    currentParameterizedNode.setToParameterize(node);
                    it.remove();
                    currentParameterizedNode = null;
                }
            }
        }
    }

}

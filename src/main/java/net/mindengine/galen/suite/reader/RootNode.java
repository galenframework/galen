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
package net.mindengine.galen.suite.reader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.tests.GalenBasicTest;

public class RootNode extends Node<List<GalenBasicTest>> {

    public RootNode() {
        super(null);
    }
    
    
    @Override
    public Node<?> findProcessingNodeByIndentation(int spaces) {
        return this;
    }

    @Override
    public Node<?> processNewNode(Line line) {
        if (line.startsWith(" ")) {
            throw new SyntaxException(line, "Should not start with space");
        }
        
        TestNode suiteNode = new TestNode(line);
        add(suiteNode);
        return suiteNode;
    }


    @Override
    public List<GalenBasicTest> build(VarsContext context) {
        rearrangeNodes();
        
        List<GalenBasicTest> suites = new LinkedList<GalenBasicTest>();
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

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
import net.mindengine.galen.parser.GalenPageTestReader;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;

public class PageNode extends Node<GalenPageTest> {

    public PageNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        ActionNode actionNode = new ActionNode(line);
        add(actionNode);
        return actionNode;
    }

    @Override
    public GalenPageTest build(BashTemplateContext context) {
        GalenPageTest pageTest;
        try {
            pageTest = GalenPageTestReader.readFrom(context.process(getArguments()));
        }
        catch (SyntaxException e) {
            e.setLine(getLine());
            throw e;
        }
        
        List<GalenPageAction> actions = new LinkedList<GalenPageAction>();
        pageTest.setActions(actions);
        
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof ActionNode) {
                ActionNode actionNode = (ActionNode)childNode;
                actions.add(actionNode.build(context));
            }
        }
        
        return pageTest;
    }


}

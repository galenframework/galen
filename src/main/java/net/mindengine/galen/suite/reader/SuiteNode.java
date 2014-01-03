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

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;

public class SuiteNode extends Node<GalenSuite> {

    private boolean disabled = false;

    public SuiteNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        PageNode pageNode = new PageNode(line);
        add(pageNode);
        return pageNode;
    }

    @Override
    public GalenSuite build(BashTemplateContext context) {
        GalenSuite suite = new GalenSuite();
        List<GalenPageTest> pageTests = new LinkedList<GalenPageTest>();
       
        suite.setName(context.process(getArguments()));
        suite.setPageTests(pageTests);
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof PageNode) {
                PageNode pageNode = (PageNode) childNode;
                pageTests.add(pageNode.build(context));
            }
        }
        
        return suite;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isEnabled() {
        return !disabled;
    }
    

}

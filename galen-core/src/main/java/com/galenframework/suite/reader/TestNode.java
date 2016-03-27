/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import com.galenframework.suite.GalenPageTest;
import com.galenframework.parser.VarsContext;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.tests.GalenBasicTest;

public class TestNode extends Node<GalenBasicTest> {

    private boolean disabled = false;
    private List<String> groups;

    public TestNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        PageNode pageNode = new PageNode(line);
        add(pageNode);
        return pageNode;
    }

    @Override
    public GalenBasicTest build(VarsContext context) {
        GalenBasicTest test = new GalenBasicTest();
        List<GalenPageTest> pageTests = new LinkedList<>();
       
        test.setName(context.process(getArguments()));
        test.setPageTests(pageTests);

        test.setGroups(groups);
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof PageNode) {
                PageNode pageNode = (PageNode) childNode;
                pageTests.add(pageNode.build(context));
            }
        }
        
        return test;
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


    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getGroups() {
        return groups;
    }
}

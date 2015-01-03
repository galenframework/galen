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
package net.mindengine.galen.reports;

import net.mindengine.galen.reports.model.LayoutReport;

import java.util.List;


public class TestReport {

    private TestReportNode rootNode = new TestReportNode();
    private TestReportNode currentNode = rootNode;
    
    public TestReportNode info(String name) {
        TestReportNode node = TestReportNode.info(name);
        currentNode.addNode(node);
        return node;
    }

    public TestReportNode warn(String name) {
        TestReportNode node = TestReportNode.warn(name);
        currentNode.addNode(node);
        return node;
    }
    
    public TestReportNode error(String name) {
        TestReportNode node = TestReportNode.error(name);
        currentNode.addNode(node);
        return node;
    }

    public List<TestReportNode> getNodes() {
        return rootNode.getNodes();
    }

    public TestReportNode sectionStart(String name) {
        TestReportNode node = new TestReportNode();
        node.setName(name);
        
        this.currentNode.addNode(node);
        this.currentNode = node;
        return node;
    }
    public void gotoRoot() {
        this.currentNode = rootNode;
    }

    public void sectionEnd() {
        if (this.currentNode.getParent() != null) {
            this.currentNode = this.currentNode.getParent();
        }
    }

    public TestReportNode error(Throwable ex) {
        TestReportNode node = new ExceptionReportNode(ex);
        this.currentNode.addNode(node);
        return node;
    }

    public TestReportNode addNode(TestReportNode node) {
        this.currentNode.addNode(node);
        return node;
    }

    public LayoutReportNode layout(LayoutReport layoutReport, String title) {
        LayoutReportNode layoutReportNode = new LayoutReportNode(layoutReport, title);
        if (layoutReport.errors() > 0) {
            layoutReportNode.setStatus(TestReportNode.Status.ERROR);
        }
        else if (layoutReport.warnings() > 0) {
            layoutReportNode.setStatus(TestReportNode.Status.WARN);
        }
        this.currentNode.addNode(layoutReportNode);
        return layoutReportNode;
    }

    public TestStatistic fetchStatistic() {
        return rootNode.fetchStatistic(new TestStatistic());
    }
    
    

}

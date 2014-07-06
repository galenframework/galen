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
package net.mindengine.galen.reports;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestReportNode {
    
    private static Long _uniqueId = 0L;

    private Long id = generateUniqueId();
    private String name;
    private Status status = Status.INFO;
    private List<TestReportNode> nodes;
    private TestReportNode parent;
    private List<TestAttachment> attachments;
    private String details;
    private Date time = new Date();

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public static enum Status {
        INFO("info"),
        WARN("warn"),
        ERROR("error");
        
        Status(String name) {
            this.name = name;
        }
        
        private final String name;
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public TestReportNode withDetails(String details) {
        setDetails(details);
        return this;
    }
    public static TestReportNode info(String name) {
        TestReportNode node = new TestReportNode();
        node.setName(name);
        node.setStatus(Status.INFO);
        return node;
    }
    
    private synchronized Long generateUniqueId() {
        _uniqueId++;
        return _uniqueId;
    }

    public static TestReportNode warn(String name) {
        TestReportNode node = new TestReportNode();
        node.setName(name);
        node.setStatus(Status.WARN);
        return node;
    }
    
    public static TestReportNode error(String name) {
        TestReportNode node = new TestReportNode();
        node.setName(name);
        node.setStatus(Status.ERROR);
        return node;
    }
    
    public static ExceptionReportNode error(Throwable exception) {
        return new ExceptionReportNode(exception);
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



    public synchronized void addNode(TestReportNode node) {
        if (nodes == null) {
            nodes = new LinkedList<TestReportNode>();
        }
        
        nodes.add(node);
        node.setParent(this);
    }

    private void setParent(TestReportNode node) {
        this.parent = node;
    }
    
    public TestReportNode getParent() {
        return this.parent;
    }

    public List<TestReportNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TestReportNode> nodes) {
        this.nodes = nodes;
    }

    public TestReportNode withAttachment(String name, File file) {
        this.addAttachment(new TestAttachment(name, file));
        return this;
    }
    
    public TestReportNode withAttachment(String name, String filePath) {
        this.addAttachment(new TestAttachment(name, new File(filePath)));
        return this;
    }

    private synchronized void addAttachment(TestAttachment testAttachment) {
        if (attachments == null) {
            attachments = new LinkedList<TestAttachment>();
        }
        attachments.add(testAttachment);
    }

    public List<TestAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TestAttachment> attachments) {
        this.attachments = attachments;
    }

    public TestStatistic fetchStatistic(TestStatistic testStatistic) {
        
        if (nodes != null && nodes.size() > 0) {
            for (TestReportNode node : nodes) {
                node.fetchStatistic(testStatistic);
            }
        }
        else {
            testStatistic.setTotal(testStatistic.getTotal() + 1);
            if (status == TestReportNode.Status.INFO) {
                testStatistic.setPassed(testStatistic.getPassed() + 1);
            }
            else if (status == TestReportNode.Status.ERROR) {
                testStatistic.setErrors(testStatistic.getErrors() + 1);
            } 
            else {
                testStatistic.setWarnings(testStatistic.getWarnings() + 1);
            }
        }
        return testStatistic;
    }
    
    public Long getId() {
        return this.id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }



}

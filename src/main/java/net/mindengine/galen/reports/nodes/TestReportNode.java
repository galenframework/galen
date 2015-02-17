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
package net.mindengine.galen.reports.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import net.mindengine.galen.reports.ExceptionReportNode;
import net.mindengine.galen.reports.TestStatistic;
import net.mindengine.galen.reports.model.FileTempStorage;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestReportNode {

    private final FileTempStorage fileStorage;
    private String name;
    private Status status = Status.INFO;
    private List<TestReportNode> nodes;

    @JsonIgnore
    private TestReportNode parent;
    private List<String> attachments;
    private Date time = new Date();

    public TestReportNode(FileTempStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
    public TestReportNode(FileTempStorage fileStorage, String name, Status status) {
        this.fileStorage = fileStorage;
        this.name = name;
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public TestReportNode withAttachment(String name, File file) {
        if (attachments == null) {
            attachments = new LinkedList<String>();
        }

        String attachmentName = getFileStorage().registerFile(name, file);
        attachments.add(attachmentName);
        return this;
    }

    private FileTempStorage getFileStorage() {
        return fileStorage;
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
        @JsonValue
        public String toString() {
            return name;
        }
    }
    
    public TestReportNode withDetails(String details) {
        this.addNode(new TextReportNode(fileStorage, details));
        return this;
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


    public String getType() {
        return "node";
    }

}

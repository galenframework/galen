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
package net.mindengine.galen.tests.reports;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.util.List;

import net.mindengine.galen.reports.GalenReports;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestAttachment;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.TestReportNode;

import org.testng.annotations.Test;

public class GalenReportsTest {

    
    @Test
    public void shouldHave_singleton_reports() {
        GalenReports reports = GalenReports.get();
        assertThat("Reports should be", reports, is(not(nullValue())));
        assertThat("calling multiple times singleton should give same instance", reports, is(GalenReports.get()));
    }
    
    @Test
    public void shouldHave_independentReports() {
        GalenReports reportA = GalenReports.createNew();
        GalenReports reportB = GalenReports.createNew();
        
        assertThat("Reports should be", reportA, is(not(nullValue())));
        assertThat("Reports should be", reportB, is(not(nullValue())));
        assertThat("calling multiple times createNew should give different instances", reportA, is(not(reportB)));
    }
    
    @Test
    public void shouldCreate_basicReports_withTests() {
        GalenReports reports = GalenReports.createNew();
        
        GalenTestInfo test1 = reports.createTest("Test 1");
        assertThat("Test name should be", test1.getName(), is("Test 1"));
        
        GalenTestInfo test2 = reports.createTest("Test 2");
        assertThat("Test name should be", test2.getName(), is("Test 2"));
        
        assertThat("Amount of tests should be", reports.getTests().size(), is(2));
        assertThat("Test should be", reports.getTests().get(0), is(test1));
        assertThat("Test should be", reports.getTests().get(1), is(test2));
    }
    
    
    @Test
    public void galenTest_shouldHave_reports() {
        GalenTestInfo test = GalenReports.createNew().createTest("Test 1");
        
        TestReport report = test.getReport();
        assertThat("Report should not be null", report, is(not(nullValue())));
    }
    
    @Test
    public void testReport_shouldHave_generalReportingInterface() {
        TestReport report = new TestReport();
        
        report.info("Action A");
        report.warn("Action B");
        report.error("Action C");
        
        List<TestReportNode> nodes = report.getNodes();
        assertThat("Should have amount of nodes", nodes.size(), is(3));
        assertThat("Node name should be", nodes.get(0).getName(), is("Action A"));
        assertThat("Node status should be", nodes.get(0).getStatus(), is(TestReportNode.Status.INFO));
        
        assertThat("Node name should be", nodes.get(1).getName(), is("Action B"));
        assertThat("Node status should be", nodes.get(1).getStatus(), is(TestReportNode.Status.WARN));
        
        assertThat("Node name should be", nodes.get(2).getName(), is("Action C"));
        assertThat("Node status should be", nodes.get(2).getStatus(), is(TestReportNode.Status.ERROR));
    }
    
    
    @Test
    public void testReport_shouldHave_hierarchy() {
        TestReport report = new TestReport();
        
        report.sectionStart("Section 1");
        report.sectionStart("Section 2");
        report.info("Action");
        report.sectionEnd();
        report.sectionEnd();
        
        List<TestReportNode> nodes = report.getNodes();
        assertThat("Should have amount of nodes", nodes.size(), is(1));
        assertThat("Node name should be", nodes.get(0).getName(), is("Section 1"));
        
        assertThat("Should have amount of nodes", nodes.get(0).getNodes().size(), is(1));
        assertThat("Node name should be", nodes.get(0).getNodes().get(0).getName(), is("Section 2"));
        
        assertThat("Should have amount of nodes", nodes.get(0).getNodes().get(0).getNodes().size(), is(1));
        assertThat("Node name should be", nodes.get(0).getNodes().get(0).getNodes().get(0).getName(), is("Action"));
    }
    
    
    @Test
    public void testReport_shouldHave_attachments() {
        TestReport report = new TestReport();
        
        report.info("Check this screenshot")
            .withAttachment("Screen", new File("myfile1.txt"))
            .withAttachment("Screen2", new File("myfile2.txt"));
        
        List<TestReportNode> nodes = report.getNodes();
        assertThat("Should have amount of nodes", nodes.size(), is(1));
        assertThat("Node name should be", nodes.get(0).getName(), is("Check this screenshot"));
        
        List<TestAttachment> attachments = report.getNodes().get(0).getAttachments();
        assertThat("Should have amount of attachments", attachments.size(), is(2));
        
        assertThat("Attachment name should be", attachments.get(0).getName(), is("Screen"));
        assertThat("Attachment name should be", attachments.get(0).getFile().getPath(), is(new File("myfile1.txt").getPath()));
        
        assertThat("Attachment name should be", attachments.get(1).getName(), is("Screen2"));
        assertThat("Attachment name should be", attachments.get(1).getFile().getPath(), is(new File("myfile2.txt").getPath()));
    }
    
    
    
}

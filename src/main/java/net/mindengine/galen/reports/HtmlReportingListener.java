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
package net.mindengine.galen.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlReportingListener implements CompleteListener {
    
    private Configuration freemarkerConfiguration;
    private Template template;

    public HtmlReportingListener () throws IOException {
        freemarkerConfiguration = new Configuration();
        template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/html-report/report.ftl.html")), freemarkerConfiguration);
    }
    
    public HtmlReportingListener(String reportFolderPath) throws IOException {
        this();
        this.reportFolderPath = reportFolderPath;
    }
    
    private String reportFolderPath;
    
    //private XmlNode mainNode = createMainNode();
    
    private Map<GalenSuiteRunner, SuiteRun> suiteRuns = new HashMap<GalenSuiteRunner, HtmlReportingListener.SuiteRun>();
    private Map<GalenPageRunner, GalenSuiteRunner> pageRunnerLinks = new HashMap<GalenPageRunner, GalenSuiteRunner>();
    private List<SuiteRun> suiteRunList = new LinkedList<SuiteRun>();
    
    private int reportCounter = 0;
    
    public class SuiteRun {
        private HtmlSuiteReportingListener listener;
        private int passed = 0;
        private int failed = 0;
        private Set<String> objectNames = new HashSet<String>();
        
        private String name = "";
        private int total = 0;
        private String suiteReportFile = "";
        public int getObjectsCount() {
            return objectNames.size();
        }
        public HtmlSuiteReportingListener getListener() {
            return listener;
        }
        public void setListener(HtmlSuiteReportingListener listener) {
            this.listener = listener;
        }
        public int getPassed() {
            return passed;
        }
        public void setPassed(int passed) {
            this.passed = passed;
        }
        public int getFailed() {
            return failed;
        }
        public void setFailed(int failed) {
            this.failed = failed;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getTotal() {
            return total;
        }
        public void setTotal(int total) {
            this.total = total;
        }
        public String getSuiteReportFile() {
            return suiteReportFile;
        }
        public void setSuiteReportFile(String suiteReportFile) {
            this.suiteReportFile = suiteReportFile;
        }
    }
    
    
    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (hasListener(pageRunner)) {
            SuiteRun suiteRun = findSuiteRun(pageRunner);
            suiteRun.objectNames.add(objectName);
            suiteRun.listener.onObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (hasListener(pageRunner)) {
            findListener(pageRunner).onAfterObject(pageRunner, pageValidation, objectName);
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        if (hasListener(pageRunner)) {
            SuiteRun suiteRun = findSuiteRun(pageRunner);
            suiteRun.listener.onSpecError(pageRunner, pageValidation, objectName, spec, error);
            suiteRun.failed++;
            suiteRun.total++;
        }
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec spec) {
        if (hasListener(pageRunner)) {
            SuiteRun suiteRun = findSuiteRun(pageRunner);
            suiteRun.listener.onSpecSuccess(pageRunner, pageValidation, objectName, spec);
            suiteRun.passed++;
            suiteRun.total++;
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        if (hasListener(pageRunner)) {
            SuiteRun suiteRun = findSuiteRun(pageRunner);
            suiteRun.listener.onGlobalError(pageRunner, e);
            suiteRun.failed++;
        }
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, 
            GalenPageTest pageTest, Browser browser, List<ValidationError> errors) {
        if (hasListener(pageRunner)) {
            findListener(pageRunner).onAfterPage(galenSuiteRunner, pageRunner, pageTest, browser, errors);
            removeLinkFor(pageRunner);
        }
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        linkPageRunnerToSuite(pageRunner, galenSuiteRunner);
        if (hasListener(pageRunner)) {
            findListener(pageRunner).onBeforePage(galenSuiteRunner, pageRunner, pageTest, browser);
        }
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        if (hasListener(galenSuiteRunner)) {
            findListener(galenSuiteRunner).onSuiteFinished(galenSuiteRunner, suite);
            findListener(galenSuiteRunner).done();
            cleanSuiteListener(galenSuiteRunner);
        }
       
    }
    
    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        //Registering a suite run with listener
        SuiteRun suiteRun = new SuiteRun();
        suiteRun.suiteReportFile = String.format("report-%d-%s", getUniqueReportId(), convertToFileName(suite.getName()));
        try {
            suiteRun.listener = new HtmlSuiteReportingListener(reportFolderPath, suiteRun.suiteReportFile, freemarkerConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
            suiteRun.listener = null;
        }
        suiteRun.name = suite.getName();
        suiteRuns.put(galenSuiteRunner, suiteRun);
        suiteRunList.add(suiteRun);
        
        suiteRun.listener.onSuiteStarted(galenSuiteRunner, suite);
    }

    @Override
    public void done() {
        try {
            File file = new File(reportFolderPath + File.separator + "report.html");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Cannot create file: " + file.getAbsolutePath());
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("suiteRuns", suiteRunList);
            template.process(model, fileWriter);
            fileWriter.flush();
            fileWriter.close();
            
            copyHtmlResources();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
        
    private void copyHtmlResources() throws IOException {
        copyResourceToFolder("/html-report/galen-report.css", "galen-report.css");
        copyResourceToFolder("/html-report/galen-report.js", "galen-report.js");
        copyResourceToFolder("/html-report/jquery-1.10.2.min.js", "jquery-1.10.2.min.js");
    }

    private void copyResourceToFolder(String resourcePath, String destFileName) throws IOException {
        File destFile = new File(reportFolderPath + File.separator + destFileName);
        
        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                throw new RuntimeException("Cannot copy file to: " + destFile.getAbsolutePath());
            }
        }
        IOUtils.copy(getClass().getResourceAsStream(resourcePath), new FileOutputStream(destFile));
    }

    private CompleteListener findListener(GalenPageRunner pageRunner) {
        return suiteRuns.get(pageRunnerLinks.get(pageRunner)).listener;
    }
    
    private SuiteRun findSuiteRun(GalenPageRunner pageRunner) {
        return suiteRuns.get(pageRunnerLinks.get(pageRunner));
    }

    private boolean hasListener(GalenPageRunner pageRunner) {
        GalenSuiteRunner galenSuiteRunner = pageRunnerLinks.get(pageRunner);
        return galenSuiteRunner != null && suiteRuns.containsKey(galenSuiteRunner);
    }
    
    private boolean hasListener(GalenSuiteRunner suiteRunner) {
        return suiteRuns.containsKey(suiteRunner);
    }
    
    private CompleteListener findListener(GalenSuiteRunner suiteRunner) {
        return suiteRuns.get(suiteRunner).listener;
    }
    
    private void cleanSuiteListener(GalenSuiteRunner galenSuiteRunner) {
        if (suiteRuns.containsKey(galenSuiteRunner)) {
            suiteRuns.get(galenSuiteRunner).listener = null;
        }
    }
    
    /**
     * Removes the link between pageRunner and suiteRunner
     * @param pageRunner
     */
    private void removeLinkFor(GalenPageRunner pageRunner) {
        if (pageRunnerLinks.containsKey(pageRunner)) {
            pageRunnerLinks.remove(pageRunner);
        }
    }
    
    /**
     * Not the best solution. But as page runner does not know anything about suite we need to somehow link them together.
     * This is done by storing them in a map.
     * @param pageRunner
     * @param galenSuiteRunner
     */
    private void linkPageRunnerToSuite(GalenPageRunner pageRunner, GalenSuiteRunner galenSuiteRunner) {
        pageRunnerLinks.put(pageRunner, galenSuiteRunner);
    }
    
    private String convertToFileName(String name) {
        return name.toLowerCase().replaceAll("[^\\dA-Za-z\\.\\-]", " ").replaceAll("\\s+", "-");
    }

    private synchronized int getUniqueReportId() {
        reportCounter++;
        return reportCounter;
    }
}

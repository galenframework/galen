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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.reports.model.PageAction;
import net.mindengine.galen.reports.model.PageTest;
import net.mindengine.galen.reports.model.PageTestObject;
import net.mindengine.galen.reports.model.PageTestSpec;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlSuiteReportingListener implements CompleteListener {
    
    private String reportFolderPath;
    private String reportFileName;
    private Template template;
    private String suiteName = "";
    private List<PageTest> pageTests = new LinkedList<PageTest>();
    private PageTest currentPageTest;
    private PageTestObject currentObject;
    private Browser currentBrowser;
    
    private Map<Page, Screenshot> screenshots = new HashMap<Page, Screenshot>();
    
    //This is need in order to group objects by name within single page test
    private Map<String, PageTestObject> pageTestObjectsMap = new HashMap<String, PageTestObject>();
    
    private int screenshotId = 0;
    private PageAction currentPageAction;
    private class Screenshot {
        private String name;
        private String filePath;
        public Screenshot(String name, String filePath){
            this.name = name;
            this.filePath = filePath;
        }
    }
    


    public HtmlSuiteReportingListener(String reportFolderPath, String reportFileName, Configuration freemarkerConfiguration) throws IOException {
        this.reportFolderPath = reportFolderPath;
        this.reportFileName = reportFileName;
        this.template = new Template("suite-report", new InputStreamReader(getClass().getResourceAsStream("/html-report/report-suite.ftl.html")), freemarkerConfiguration);
    }

    
    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        
        if (pageTestObjectsMap.containsKey(objectName)) {
            currentObject = pageTestObjectsMap.get(objectName);
        }
        else {
            currentObject = new PageTestObject();
            currentObject.setName(objectName);
            currentPageAction.getObjects().add(currentObject);
            pageTestObjectsMap.put(objectName, currentObject);
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {   
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec, ValidationError error) {
        Screenshot screenshot = createScreenShot(pageValidation.getPage());
        
        PageTestSpec spec = new PageTestSpec();
        currentObject.getSpecs().add(spec);
        
        spec.setText(originalSpec.getOriginalText());
        spec.setFailed(true);
        spec.setScreenshot(screenshot.name);
        spec.setErrorMessages(error.getMessages());
        spec.setErrorAreas(error.getErrorAreas());
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec) {
        PageTestSpec spec = new PageTestSpec();
        currentObject.getSpecs().add(spec);
        spec.setText(originalSpec.getOriginalText());
        spec.setFailed(false);
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        currentPageTest.getGlobalErrors().add(e);
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser, List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        //Reseting objects map
        pageTestObjectsMap = new HashMap<String, PageTestObject>();
        
        currentBrowser = browser;
        
        currentPageTest = new PageTest();
        
        if (pageTest.getTitle() != null) {
            currentPageTest.setTitle(pageTest.getTitle());
        }
        else {
            String title = pageTest.getUrl();
            if (pageTest.getScreenSize() != null) {
                title = title + " " + pageTest.getScreenSize().width + "x" + pageTest.getScreenSize().height;
            }
            currentPageTest.setTitle(title);
        }
        pageTests.add(currentPageTest);
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        this.suiteName = suite.getName();
    }

    @Override
    public void done() {
        try {
            File file = new File(reportFolderPath + File.separator + reportFileName + ".html");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Cannot create file: " + file.getAbsolutePath());
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("suiteName", suiteName);
            model.put("pageTests", pageTests);
            template.process(model, fileWriter);
            fileWriter.flush();
            fileWriter.close();
            
            moveScreenshots();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void moveScreenshots() throws IOException {
        File folder = new File(reportFolderPath);
        for (Screenshot screenshot : screenshots.values()) {
            FileUtils.copyFile(new File(screenshot.filePath), new File(folder.getAbsolutePath() + File.separator + screenshot.name));
        }
    }


    private synchronized Screenshot createScreenShot(Page page) {
        if (screenshots.containsKey(page)) {
            return screenshots.get(page);
        }
        else {
            screenshotId++;
            String filePath = currentBrowser.createScreenshot();
            Screenshot screenshot = new Screenshot(reportFileName + "-screenshot-" + screenshotId + "." + extensionFrom(filePath), filePath);
            screenshots.put(page, screenshot);
            return screenshot;
        }
    }

    private String extensionFrom(String filePath) {
        StringBuilder extension = new StringBuilder();
        for (int i = filePath.length() - 1; i >= 0; i--) {
            char ch = filePath.charAt(i);
            if (ch == '.') {
                break;
            }
            else extension.append(ch);
        }
        return extension.reverse().toString();
    }


    @Override
    public void onPageAction(GalenPageRunner pageRunner, GalenSuite suite, GalenPageAction action) {
        currentPageAction = new PageAction();
        currentPageAction.setTitle(action.getOriginalCommand());
        currentPageTest.getPageActions().add(currentPageAction);
    }
}

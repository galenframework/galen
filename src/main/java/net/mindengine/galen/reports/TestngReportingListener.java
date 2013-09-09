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

import static net.mindengine.galen.xml.XmlBuilder.node;
import static net.mindengine.galen.xml.XmlBuilder.textNode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.xml.XmlBuilder;
import net.mindengine.galen.xml.XmlBuilder.XmlNode;

import org.apache.commons.io.FileUtils;

public class TestngReportingListener implements CompleteListener {

    private XmlNode rootNode = node("testng-results");
    private XmlBuilder xml = new XmlBuilder(XmlBuilder.XML_DECLARATION, rootNode);
    private XmlNode currentSuiteNode;
    private XmlNode currentPageNode;
    private XmlNode currentObjectNode;
    private String reportPath;
    
    public TestngReportingListener(String reportPath) {
        this.reportPath = reportPath;
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser,
            List<ValidationError> errors) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser) {
        currentPageNode = node("test").withAttribute("name", pageTest.getUrl() + " " + GalenUtils.formatScreenSize(pageTest.getScreenSize()));
        currentSuiteNode.add(currentPageNode);
    }


    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
    }
    

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        currentSuiteNode = node("suite").withAttribute("name", suite.getName());
        rootNode.add(currentSuiteNode);
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        currentObjectNode = node("class").withAttribute("name", objectName);
        currentPageNode.add(currentObjectNode);
    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        XmlNode errorNode = node("exception").withAttribute("class", "Error_" + spec.getClass().getSimpleName());
        XmlNode errorTrace = node("short-stacktrace");
        errorTrace.add(textNode(convertToText(error.getMessages())));
        errorNode.add(errorTrace);
        reportSpec(pageValidation, objectName, spec, "FAIL")
            .add(errorNode);
    }

    private String convertToText(List<String> messages) {
        StringBuffer buffer = new StringBuffer();
        
        boolean separate = false;
        for (String message : messages) {
            if (separate) {
                buffer.append("\n");
            }
            separate = true;
            buffer.append(message);
        }
        return buffer.toString();
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        reportSpec(pageValidation, objectName, spec, "PASS");
    }

    public String toXml() {
        return xml.build();
    }


    private XmlNode reportSpec(PageValidation pageValidation, String objectName, Spec spec, String status) {
        String now = formatDate(new Date());
        XmlNode specNode = node("test-method")
                .withAttribute("status", status)
                .withAttribute("signature", objectName + ": " + spec.toText())
                .withAttribute("name", spec.toText())
                .withAttribute("duration-ms", "0")
                .withAttribute("started-at", now)
                .withAttribute("finished-at", now)
                .withAttribute("description", "");
        
        currentObjectNode.add(specNode);
        return specNode;
    }
    
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private String formatDate(Date date) {
        return sdf.format(date);
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
    }

    @Override
    public void done() {
        try {
            File file = new File(reportPath);
            if (file.createNewFile()) {
                FileUtils.writeStringToFile(file, toXml());
            }
            else {
                throw new RuntimeException("Couldn't create file: " + reportPath);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

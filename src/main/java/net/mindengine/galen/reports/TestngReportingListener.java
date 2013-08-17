package net.mindengine.galen.reports;

import static net.mindengine.galen.xml.XmlBuilder.node;
import static net.mindengine.galen.xml.XmlBuilder.textNode;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;
import net.mindengine.galen.xml.XmlBuilder;
import net.mindengine.galen.xml.XmlBuilder.XmlNode;

public class TestngReportingListener implements ValidationListener, SuiteListener {

    private XmlNode rootNode = node("testng-results");
    private XmlBuilder xml = new XmlBuilder(XmlBuilder.XML_DECLARATION, rootNode);
    private XmlNode currentSuiteNode;
    private XmlNode currentPageNode;
    private XmlNode currentObjectNode;
    
    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser,
            List<ValidationError> errors) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser) {
        currentPageNode = node("test").withAttribute("name", browser.getUrl() + " " + formatScreenSize(browser.getScreenSize()));
        currentSuiteNode.add(currentPageNode);
    }


    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner) {
    }
    

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner) {
        currentSuiteNode = node("suite").withAttribute("name", galenSuiteRunner.getName());
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

    private String formatScreenSize(Dimension screenSize) {
        return String.format("%dx%d", screenSize.width, screenSize.height);
    }
}

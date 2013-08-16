package net.mindengine.galen.reports;

import static net.mindengine.galen.xml.XmlBuilder.node;

import java.awt.Dimension;
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

public class HtmlReportingListener implements ValidationListener, SuiteListener {

    private XmlNode bodyNode = node("body");
    private XmlNode currentSuiteNode;
    private XmlNode currentPageNode;
    private XmlNode currentObjectNode;
    private XmlNode currentSpecsListNode;
    
    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner) {
        currentSuiteNode = div("suite");
        currentSuiteNode.add(h(1, galenSuiteRunner.getName()));
        
        bodyNode.add(currentSuiteNode);
    }
    
    private XmlNode h(int size, String text) {
        return node("h" + size).withChildren(node(text).asTextNode());
    }

    private XmlNode div(String clazz) {
        return node("div").withAttribute("class", clazz);
    }
    
    private String formatScreenSize(Dimension screenSize) {
        return String.format("%dx%d", screenSize.width, screenSize.height);
        //TODO Move to utils
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser) {
        currentPageNode = div("test");
        currentPageNode.add(h(2, browser.getUrl() + " " + formatScreenSize(browser.getScreenSize())));
        currentSuiteNode.add(currentPageNode);
    }
    
    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageRunner pageRunner, Browser browser,
            List<ValidationError> errors) {
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        currentObjectNode = div("object");
        currentObjectNode.add(h(3, objectName));
        currentPageNode.add(currentObjectNode);
        
        currentSpecsListNode = node("ul").withAttribute("class", "test-specs");
        currentObjectNode.add(currentSpecsListNode);
    }

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        XmlNode errorList = node("ul").withAttribute("class", "error-message");
        
        for (String message : error.getMessages()) {
            errorList.add(node("li").withChildren(node(message).asTextNode()));
        }
        currentSpecsListNode.add(li("fail").withChildren(span(spec.toText()), errorList));
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        currentSpecsListNode.add(li("success").withChildren(span(spec.toText())));
        
    }

    private XmlNode li(String clazz) {
        return node("li").withAttribute("class", clazz);
    }
    private XmlNode span(String text) {
        return node("span").withChildren(node(text).asTextNode());
    }

    public String toHtml() {
        return new XmlBuilder(null, bodyNode).build();
    }

}

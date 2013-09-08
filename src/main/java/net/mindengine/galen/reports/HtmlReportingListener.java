package net.mindengine.galen.reports;

import static java.lang.String.format;
import static net.mindengine.galen.xml.XmlBuilder.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.xml.XmlBuilder;
import net.mindengine.galen.xml.XmlBuilder.XmlNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class HtmlReportingListener implements CompleteListener {

    private XmlNode bodyNode = node("body");
    
    private XmlNode headNode = createHeadNode();
    private XmlNode htmlNode = node("html").withChildren(headNode).withChildren(bodyNode);
    
    private XmlNode currentSuiteNode;
    private XmlNode currentPageNode;
    private XmlNode currentObjectNode;
    private XmlNode currentSpecsListNode;
    private Browser currentBrowser;
    
    
    private Map<Page, Screenshot> screenshots = new HashMap<Page, Screenshot>();
    
    private int screenshotId = 0;
    private String reportPath;
    
    private class Screenshot {
        private String name;
        private String filePath;
        public Screenshot(String name, String filePath){
            this.name = name;
            this.filePath = filePath;
        }
    }
    
    public HtmlReportingListener(String reportPath) {
        this.reportPath = reportPath;
    }

    private XmlNode createHeadNode() {
        XmlNode headNode = node("head");
        
        try {
            String css = IOUtils.toString(getClass().getResourceAsStream("/html-report/galen-report.css"));
            String jquery = IOUtils.toString(getClass().getResourceAsStream("/html-report/jquery-1.7.1.min.js"));
            String galenJs = IOUtils.toString(getClass().getResourceAsStream("/html-report/galen-report.js"));
            
            headNode.add(node("style").withUnescapedText(css));
            headNode.add(node("script").withUnescapedText(jquery));
            headNode.add(node("script").withUnescapedText(galenJs));
            //TODO make sure it doesn't escape
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return headNode;
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        currentSuiteNode = div("suite");
        currentSuiteNode.add(h(1, suite.getName()));
        
        bodyNode.add(currentSuiteNode);
    }
    
    private XmlNode h(int size, String text) {
        return node("h" + size).withText(text);
    }

    private XmlNode div(String clazz) {
        return node("div").withAttribute("class", clazz);
    }
    

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser) {
        currentBrowser = browser;
        currentPageNode = div("test");
        currentPageNode.add(h(2, pageTest.getUrl() + " " + GalenUtils.formatScreenSize(pageTest.getScreenSize())));
        currentSuiteNode.add(currentPageNode);
    }
    
    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser,
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
        Screenshot screenshot = createScreenShot(pageValidation.getPage());
        
        
        XmlNode errorList = node("ul").withAttribute("class", "error-message");
        
        for (String message : error.getMessages()) {
            errorList.add(node("li").withText(message));
        }
        currentSpecsListNode.add(li("fail").withAttribute("data-screenshot", screenshot.name).withChildren(span(spec.toText()), areas(error.getErrorAreas()), errorList));
    }

    private XmlNode areas(List<ErrorArea> errorAreas) {
        XmlNode ul = node("ul").withAttribute("class", "areas");
        
        for (ErrorArea area : errorAreas) {
            Rect rect = area.getRect();
            ul.add(node("li")
                    .withAttribute("data-area", format("%d,%d,%d,%d", rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight()))
                    .withText(area.getMessage()));
        }
        return ul;
    }

    private synchronized Screenshot createScreenShot(Page page) {
        if (screenshots.containsKey(page)) {
            return screenshots.get(page);
        }
        else {
            screenshotId++;
            String filePath = currentBrowser.createScreenshot();
            Screenshot screenshot = new Screenshot("screenshot-" + screenshotId + "." + extensionFrom(filePath), filePath);
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
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        currentSpecsListNode.add(li("success").withChildren(span(spec.toText())));
        
    }

    private XmlNode li(String clazz) {
        return node("li").withAttribute("class", clazz);
    }
    private XmlNode span(String text) {
        return node("span").withText(text);
    }

    public String toHtml() {
        return new XmlBuilder(null, htmlNode).build();
    }

    @Override
    public void onAfterObject(PageValidation pageValidation, String objectName) {
    }

    @Override
    public void done() {
        try {
            File file = new File(reportPath);
            if (file.createNewFile()) {
                FileUtils.writeStringToFile(file, toHtml());
                moveScreenshots(file.getParentFile());
            }
            else {
                throw new RuntimeException("Couldn't create file: " + reportPath);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void moveScreenshots(File folder) throws IOException {
        for (Screenshot screenshot : screenshots.values()) {
            FileUtils.copyFile(new File(screenshot.filePath), new File(folder.getAbsolutePath() + File.separator + screenshot.name));
        }
        
    }

}

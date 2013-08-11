package net.mindengine.galen.runner;

import java.awt.Dimension;
import java.util.List;

import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class GalenPageRunner {

    private String url;
    private Dimension screenSize;
    private PageSpec spec;
    private ValidationListener validationListener;
    private List<String> includedTags;
    private String javascript;
    private List<String> excludedTags;

    public GalenPageRunner withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GalenPageRunner withScreenSize(Dimension screenSize) {
        this.setScreenSize(screenSize);
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public GalenPageRunner withSpec(PageSpec spec) {
        this.setSpec(spec);
        return this;
    }

    public PageSpec getSpec() {
        return spec;
    }

    public void setSpec(PageSpec spec) {
        this.spec = spec;
    }

    public GalenPageRunner withValidationListener(ValidationListener validationListener) {
        this.setValidationListener(validationListener);
        return this;
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public List<ValidationError> run(WebDriver driver) {
        if (screenSize != null) {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(screenSize.width, screenSize.height));
        }
        
        driver.get(url);
        
        if (javascript != null) {
            ((JavascriptExecutor)driver).executeScript(javascript);
        }
        
        SeleniumPage page = new SeleniumPage(driver);
        
        List<PageSection> pageSections = spec.findSections(includedTags, excludedTags);
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, spec), validationListener);
        return sectionValidation.check();
    }

    public GalenPageRunner withIncludedTags(List<String> includedTags) {
        this.setIncludedTags(includedTags);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public GalenPageRunner withJavascript(String javascript) {
        this.setJavascript(javascript);
        return this;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public GalenPageRunner withExcludedTags(List<String> excludedTags) {
        this.setExcludedTags(excludedTags);
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

}

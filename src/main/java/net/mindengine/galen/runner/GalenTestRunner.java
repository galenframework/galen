package net.mindengine.galen.runner;

import java.awt.Dimension;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.ValidationError;

public class GalenTestRunner {

    private String url;
    private Dimension screenSize;
    private PageSpec spec;
    private TestValidationListener validationListener;
    private List<String> includedTags;

    public GalenTestRunner withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GalenTestRunner withScreenSize(Dimension screenSize) {
        this.setScreenSize(screenSize);
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public GalenTestRunner withSpec(PageSpec spec) {
        this.setSpec(spec);
        return this;
    }

    public PageSpec getSpec() {
        return spec;
    }

    public void setSpec(PageSpec spec) {
        this.spec = spec;
    }

    public GalenTestRunner withValidationListener(TestValidationListener validationListener) {
        this.setValidationListener(validationListener);
        return this;
    }

    public TestValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(TestValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public List<ValidationError> run() {
        WebDriver driver = new FirefoxDriver();
        List<ValidationError> errors = run(driver);
        
        driver.quit();
        return errors;
    }

    public List<ValidationError> run(WebDriver driver) {
        if (screenSize != null) {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(screenSize.width, screenSize.height));
        }
        
        driver.get(url);
        SeleniumPage page = new SeleniumPage(driver);
        
        List<PageSection> pageSections = spec.findSections(includedTags);
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, spec), validationListener);
        return sectionValidation.check();
    }

    public GalenTestRunner withIncludedTags(List<String> includedTags) {
        this.setIncludedTags(includedTags);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

}

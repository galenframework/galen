package net.mindengine.galen.tests.selenium;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.ValidationError;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GalenSeleniumTest {
    
    WebDriver driver;
    
    @BeforeMethod
    public void createDriver() {
        driver = new FirefoxDriver();
    }
    
    @AfterMethod
    public void quitDriver() {
        driver.quit();
    }
    
    @Test
    public void performsValidation_forAll() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().maximize();
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("all"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
        		"<SpecContains header>\n" +
        		"<SpecNear header>\n" +
        		"<SpecWidth header>\n" +
        		"<SpecHeight header>\n" +
        		"</o header>\n" +
        		"<o header-with-corrections>\n" +
        		"<SpecWidth header-with-corrections>\n" +
                "<SpecHeight header-with-corrections>\n" +
                "</o header-with-corrections>\n" +
        		"<o header-text-1>\n" +
        		"<SpecNear header-text-1>\n" +
        		"<SpecInside header-text-1>\n" +
        		"</o header-text-1>\n" +
        		"<o header-text-2>\n" +
        		"<SpecNear header-text-2>\n" +
        		"<SpecInside header-text-2>\n" +
        		"</o header-text-2>\n" +
        		"<o menu>\n" +
        		"<SpecNear menu>\n" +
        		"<SpecNear menu>\n" +
        		"</o menu>\n" +
        		"<o menu-item-home>\n" +
        		"<SpecHorizontally menu-item-home>\n" +
        		"<SpecNear menu-item-home>\n" +
        		"<SpecInside menu-item-home>\n" +
        		"</o menu-item-home>\n" +
        		"<o menu-item-categories>\n" +
        		"<SpecInside menu-item-categories>\n" +
        		"<SpecNear menu-item-categories>\n" + 
        		"</o menu-item-categories>\n"));
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void performsValidation_forMobile_withTwoSections() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(400, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("mobile"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(2));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "</o header>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "</o menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n" +
                "</o menu-item-rss>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void givesErrors_whenValidating_incorrectWebSite() throws Exception {
        openDriverForBadPage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(400, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("mobile"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(2));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 170px</msg></e>\n" +
                "</o header>\n" +
                "<o menu-item-home>\n" +
                "<SpecHorizontally menu-item-home>\n" +
                "</o menu-item-home>\n" +
                "<o menu-item-rss>\n" +
                "<SpecHorizontally menu-item-rss>\n" +
                "<SpecNear menu-item-rss>\n" +
                "</o menu-item-rss>\n"
                ));
        assertThat("Errors amount should be", errors.size(), is(1));
    }
    
    private void openDriverForBadPage() {
        driver.get("file://" + getClass().getResource("/html/page1.html").getPath());
    }

    private void openDriverForNicePage() {
        driver.get("file://" + getClass().getResource("/html/page-nice.html").getPath());
    }

}

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
package net.mindengine.galen.tests.selenium;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.mindengine.galen.components.TestGroups;
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


@Test(groups=TestGroups.SELENIUM)
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
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        openDriverForNicePage();
        
        driver.manage().window().maximize();
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("all"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
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
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
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
    public void shouldCheck_relativeToScreen() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(400, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("screen-object-check"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecWidth header>\n" +
                "</o header>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void shouldCheck_relativeToViewport() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(400, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("viewport-object-check"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o feedback>\n" +
                "<SpecInside feedback>\n" +
                "</o feedback>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void shouldCheck_multipleObjects() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(1024, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("multiple-objects-check"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-home>\n" +
                "<SpecHeight menu-item-home>\n" +
                "</o menu-item-home>\n"));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-categories>\n" +
                "<SpecHeight menu-item-categories>\n" +
                "</o menu-item-categories>\n"));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-blog>\n" +
                "<SpecHeight menu-item-blog>\n" +
                "</o menu-item-blog>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-rss>\n" +
                "<SpecHeight menu-item-rss>\n" +
                "</o menu-item-rss>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-about>\n" +
                "<SpecHeight menu-item-about>\n" +
                "</o menu-item-about>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-contacts>\n" +
                "<SpecHeight menu-item-contacts>\n" +
                "</o menu-item-contacts>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-item-help>\n" +
                "<SpecHeight menu-item-help>\n" +
                "</o menu-item-help>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o header-text-1>\n" +
                "<SpecWidth header-text-1>\n" +
                "</o header-text-1>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o header-text-2>\n" +
                "<SpecWidth header-text-2>\n" +
                "</o header-text-2>\n"));
        
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void shouldCheck_text() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(400, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("text-check"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is(
                "<o menu-item-home>\n" +
                "<SpecText menu-item-home>\n" +
                "</o menu-item-home>\n" +
                
                "<o menu-item-rss>\n" +
                "<SpecText menu-item-rss>\n" +
                "</o menu-item-rss>\n" +
                
                "<o menu-item-categories>\n" +
                "<SpecText menu-item-categories>\n" +
                "</o menu-item-categories>\n" +
                
                "<o menu-item-categories>\n" +
                "<SpecText menu-item-categories>\n" +
                "</o menu-item-categories>\n" +
                
                "<o menu-item-categories>\n" +
                "<SpecText menu-item-categories>\n" +
                "</o menu-item-categories>\n"
                ));
        assertThat("Errors should be empty", errors.size(), is(0));
    }
    
    @Test
    public void shouldCheck_multiObjects() throws Exception {
        openDriverForNicePage();
        
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/html/page.spec"));
        
        driver.manage().window().setSize(new Dimension(1000, 1000));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.findSections(asList("multi-check"));
        
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-1>\n" +
                "<SpecHeight menu-items-1>\n" +
                "</o menu-items-1>\n"));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-2>\n" +
                "<SpecHeight menu-items-2>\n" +
                "</o menu-items-2>\n"));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-3>\n" +
                "<SpecHeight menu-items-3>\n" +
                "</o menu-items-3>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-4>\n" +
                "<SpecHeight menu-items-4>\n" +
                "</o menu-items-4>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-5>\n" +
                "<SpecHeight menu-items-5>\n" +
                "</o menu-items-5>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-6>\n" +
                "<SpecHeight menu-items-6>\n" +
                "</o menu-items-6>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-7>\n" +
                "<SpecHeight menu-items-7>\n" +
                "</o menu-items-7>\n" ));
        
        assertThat("Invokations should contain", validationListener.getInvokations(), containsString(
                "<o menu-items-1>\n" +
                "<SpecNear menu-items-1>\n" +
                "</o menu-items-1>\n" ));
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
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is("<o header>\n" +
                "<SpecHeight header>\n" +
                "<e><msg>\"header\" height is 140px which is not in range of 150 to 185px</msg></e>\n" +
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
    
    
    
    @Test
    public void performsValidations_ofComponentSpecs() throws IOException {
        openDriverForPage("page-for-component-specs.html");
        PageSpec pageSpec = new PageSpecReader().read(new File(getClass().getResource("/specs/components/spec-for-component-test-main.spec").getFile()));
        
        driver.manage().window().setSize(new Dimension(1000, 800));
        
        SeleniumPage page = new SeleniumPage(driver);
        
        TestValidationListener validationListener = new TestValidationListener();
        List<PageSection> pageSections = pageSpec.getSections();
        assertThat("Filtered sections size should be", pageSections.size(), is(1));
        
        SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, pageSpec, validationListener), validationListener);
        List<ValidationError> errors = sectionValidation.check();
        
        assertThat("Invokations should", validationListener.getInvokations(), is(
                "<o user-profile-1>\n" +
                "<o user-pic>\n" +
                "<SpecWidth user-pic>\n" +
                "<SpecHeight user-pic>\n" +
                "<SpecInside user-pic>\n" +
                "</o user-pic>\n" +
                "<o user-name>\n" +
                "<SpecHeight user-name>\n" +
                "<SpecInside user-name>\n" +
                "<SpecNear user-name>\n" +
                "</o user-name>\n" +
                "<o user-age>\n" +
                "<SpecHeight user-age>\n" +
                "<SpecNear user-age>\n" +
                "<SpecBelow user-age>\n" +
                "</o user-age>\n" +
                "<SpecComponent user-profile-1>\n" +
                "</o user-profile-1>\n" +

                "<o user-profile-2>\n" +
                "<o user-pic>\n" +
                "<SpecWidth user-pic>\n" +
                "<SpecHeight user-pic>\n" +
                "<SpecInside user-pic>\n" +
                "</o user-pic>\n" +
                "<o user-name>\n" +
                "<SpecHeight user-name>\n" +
                "<SpecInside user-name>\n" +
                "<SpecNear user-name>\n" +
                "</o user-name>\n" +
                "<o user-age>\n" +
                "<SpecHeight user-age>\n" +
                "<SpecNear user-age>\n" +
                "<SpecBelow user-age>\n" +
                "</o user-age>\n" +
                "<SpecComponent user-profile-2>\n" +
                "</o user-profile-2>\n" + 
                
                "<o user-profile-3>\n" +
                "<o user-pic>\n" +
                "<SpecWidth user-pic>\n" +
                "<SpecHeight user-pic>\n" +
                "<SpecInside user-pic>\n" +
                "</o user-pic>\n" +
                "<o user-name>\n" +
                "<SpecHeight user-name>\n" +
                "<SpecInside user-name>\n" +
                "<SpecNear user-name>\n" +
                "</o user-name>\n" +
                "<o user-age>\n" +
                "<SpecHeight user-age>\n" +
                "<SpecNear user-age>\n" +
                "<e><msg>\"user-age\" is 204px right which is not in range of 8 to 12px</msg></e>\n" +
                "<SpecBelow user-age>\n" +
                "</o user-age>\n" +
                "<SpecComponent user-profile-3>\n" +
                "<e><msg>Child component spec contains 1 errors</msg></e>\n" +
                "</o user-profile-3>\n"
                ));
        assertThat("Errors amount should be", errors.size(), is(1));
    }
    
    
    
    private void openDriverForPage(String page) {
        driver.get("file://" + getClass().getResource("/html/" + page).getPath());
    }

    private void openDriverForBadPage() {
        openDriverForPage("page1.html");
    }

    private void openDriverForNicePage() {
        openDriverForPage("page-nice.html");
    }

}

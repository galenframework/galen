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
package net.mindengine.galen.components.report;

import static java.util.Arrays.asList;
import static net.mindengine.galen.specs.Range.between;
import static net.mindengine.galen.specs.Range.exact;
import static net.mindengine.galen.specs.Side.LEFT;

import java.awt.Dimension;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.components.MockedBrowser;
import net.mindengine.galen.components.validation.MockedPage;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class ReportingListenerTestUtils {

    public static void performSampleReporting(String suiteName, SuiteListener suiteListener, ValidationListener validationListener) {
        
        GalenSuiteRunner galenSuiteRunner = new GalenSuiteRunner();
        GalenSuite suite = new GalenSuite();
        suite.setName(suiteName);
        
        suiteListener.onSuiteStarted(galenSuiteRunner, suite);
        
        Browser browser = new MockedBrowser("http://example.com/page1", new Dimension(410, 610));
        GalenPageRunner pageRunner = new GalenPageRunner();
        PageValidation pageValidation = new PageValidation(new MockedPage(null), null, null, null);
        
        GalenPageTest pageTest = new GalenPageTest().withSize(400, 600).withUrl("http://example.com/page1");
        suiteListener.onBeforePage(galenSuiteRunner, pageRunner, pageTest, browser);
        
        GalenPageActionCheck action = new GalenPageActionCheck();
        action.setOriginalCommand("check homepage.spec --include all,mobile");
        validationListener.onBeforePageAction(pageRunner, action);
        {
            
            PageSection section1 = sectionWithName("some section 1");
            validationListener.onBeforeSection(pageRunner, pageValidation, section1);
            
            validationListener.onObject(pageRunner, pageValidation, "objectA1"); {
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectA1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left"),
                        new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectA1")), asList("objectA1 is not inside other-object")));
                
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA1");
            
            validationListener.onObject(pageRunner, pageValidation, "objectA2"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA2", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectA2", 
                        new SpecWidth(exact(10)).withOriginalText("width: 10px"),
                        new ValidationError(asList(new ErrorArea(new Rect(200, 300, 50, 30), "objectA2")), asList("objectA2 width is 20px instead of 10px")));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA2");
            
            validationListener.onAfterSection(pageRunner, pageValidation, section1);
            
            PageSection section2 = sectionWithName("some section 2");
            validationListener.onBeforeSection(pageRunner, pageValidation, section2);
            
            validationListener.onObject(pageRunner, pageValidation, "objectA1"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecHeight(between(10, 20)).withOriginalText("height: 10 to 20px"));
                
                //Doing sub-objects call
                {
                    validationListener.onObject(pageRunner, pageValidation, "sub-objectA1"); {
                        validationListener.onSpecSuccess(pageRunner, pageValidation, "sub-objectA1", new SpecHeight(between(10, 20)).withOriginalText("height: 10 to 20px"));
                        validationListener.onSpecError(pageRunner, pageValidation, 
                                "sub-objectA1", 
                                new SpecWidth(exact(10)).withOriginalText("width: 10px"),
                                new ValidationError(asList(new ErrorArea(new Rect(200, 300, 50, 30), "sub-objectA1")), asList("sub-objectA1 width is 20px instead of 10px")));
                    }
                    validationListener.onAfterObject(pageRunner, pageValidation, "sub-objectA1");
                }
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecHeight(between(10, 20)).withOriginalText("component: some-component.spec"));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA1");
            
            validationListener.onAfterSection(pageRunner, pageValidation, section2);
        
        }
        validationListener.onAfterPageAction(pageRunner, action);
        suiteListener.onAfterPage(galenSuiteRunner, pageRunner, pageTest, browser, asList(new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectA1")), asList("objectA1 is not inside other-object"))));
        
        
        pageValidation = new PageValidation(new MockedPage(null), null, null, null);
        Browser browser2 = new MockedBrowser("http://example.com/page2", new Dimension(610, 710));
        GalenPageTest pageTest2 = new GalenPageTest().withSize(600, 700).withUrl("http://example.com/page2");
        suiteListener.onBeforePage(galenSuiteRunner, pageRunner, pageTest2, browser2);
        
        validationListener.onBeforePageAction(pageRunner, action);
        {
            PageSection section1 = sectionWithName("some section 1");
            validationListener.onBeforeSection(pageRunner, pageValidation, section1);
            
            validationListener.onObject(pageRunner, pageValidation, "objectB1"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectB1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
                
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectB1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left"),
                        new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectB1")), asList("objectB1 is not inside other-object", "second error message with <xml> &tags")));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectB1"); 
            
            validationListener.onObject(pageRunner, pageValidation, "objectB2"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectB2", new SpecHeight(exact(100)).withOriginalText("height: 100px"));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectB2");
            
            validationListener.onGlobalError(pageRunner, new FakeException("Some exception here"));
            
            validationListener.onAfterSection(pageRunner, pageValidation, section1);
        }
        validationListener.onAfterPageAction(pageRunner, action);
        suiteListener.onAfterPage(galenSuiteRunner, pageRunner, pageTest2, browser2, asList(new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectB1")), asList("objectA1 is not inside other-object", "second error message"))));
        
        suiteListener.onSuiteFinished(galenSuiteRunner, suite);
    }

    private static PageSection sectionWithName(String name) {
        PageSection section = new PageSection();
        section.setName(name);
        return section;
    }

}

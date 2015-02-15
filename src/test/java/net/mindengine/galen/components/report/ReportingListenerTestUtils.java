/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.components.MockedPageValidation;
import net.mindengine.galen.components.validation.MockedPageElement;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.runner.TestListener;
import net.mindengine.galen.specs.*;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.tests.GalenBasicTest;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;
import net.mindengine.galen.validation.ValidationObject;
import net.mindengine.rainbow4j.Rainbow4J;

public class ReportingListenerTestUtils {

    private static String comparisonMapImagePath = ReportingListenerTestUtils.class.getResource("/imgs/page-sample-correct.png").getFile();

    public static void performSampleReporting(String suiteName, TestListener testListener, ValidationListener validationListener, SuiteListener suiteListener) throws IOException {
        
        GalenBasicTest suite = new GalenBasicTest();
        suite.setName(suiteName);
        
        if (testListener != null) testListener.onTestStarted(suite);
        
        GalenPageRunner pageRunner = new GalenPageRunner(new TestReport());
        
        Map<String, PageElement> pageElements = new HashMap<String, PageElement>();

        pageElements.put("objectA1", new MockedPageElement(10, 10, 100, 50));
        pageElements.put("objectA2", new MockedPageElement(200, 300, 50, 30));
        pageElements.put("objectB1", new MockedPageElement(10, 10, 100, 50));
        pageElements.put("objectB2", new MockedPageElement(200, 300, 50, 30));
        pageElements.put("sub-objectA1", new MockedPageElement(200, 300, 50, 30));
        
        MockedPageValidation pageValidation = new MockedPageValidation(pageElements);
        
        
        
        GalenPageActionCheck action = new GalenPageActionCheck();
        action.setOriginalCommand("check homepage.spec --include all,mobile");
        validationListener.onBeforePageAction(pageRunner, action);
        {
            
            PageSection section1 = sectionWithName("");
            validationListener.onBeforeSection(pageRunner, pageValidation, section1);
            
            validationListener.onObject(pageRunner, pageValidation, "objectA1"); {
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectA1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left")
                            .withPlace(new Place("specs.spec", 12)),
                        new ValidationError(asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectA1")), asList("objectA1 is not inside other-object")));
                
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px").withPlace(new Place("specs.spec", 12)));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA1");
            
            validationListener.onObject(pageRunner, pageValidation, "objectA2"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA2", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px").withPlace(new Place("specs.spec", 12)));
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectA2", 
                        new SpecWidth(exact(10)).withOriginalText("width: 10px")
                            .withPlace(new Place("specs.spec", 12)),
                        new ValidationError(asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectA2")), asList("objectA2 width is 20px instead of 10px")));

                validationListener.onSpecError(pageRunner, pageValidation,
                        "objectA2",
                        new SpecText(SpecText.Type.IS, "Login").withOriginalText("text is: Login")
                                .withPlace(new Place("specs.spec", 12))
                                .withOnlyWarn(true),
                        new ValidationError(asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectA2")), asList("objectA2 text is \"Logout\" instead of \"Login\"")));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA2");
            
            validationListener.onAfterSection(pageRunner, pageValidation, section1);
            
            PageSection section2 = sectionWithName("some section 2");
            validationListener.onBeforeSection(pageRunner, pageValidation, section2);
            
            validationListener.onObject(pageRunner, pageValidation, "objectA1"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecHeight(between(10, 20)).withOriginalText("height: 10 to 20px").withPlace(new Place("specs.spec", 12)));
                
                //Doing sub-objects call
                {
                    validationListener.onSubLayout(pageValidation, "objectA1");
                    PageSection subSection = sectionWithName("Sub section");
                    validationListener.onBeforeSection(pageRunner, pageValidation, subSection);

                    validationListener.onObject(pageRunner, pageValidation, "sub-objectA1"); {
                        validationListener.onSpecSuccess(pageRunner, pageValidation, "sub-objectA1", new SpecHeight(between(10, 20)).withOriginalText("height: 10 to 20px").withPlace(new Place("specs.spec", 12)));
                        validationListener.onSpecError(pageRunner, pageValidation, 
                                "sub-objectA1", 
                                new SpecWidth(exact(10)).withOriginalText("width: 10px")
                                    .withPlace(new Place("specs.spec", 12)),
                                new ValidationError(asList(new ValidationObject(new Rect(200, 300, 50, 30), "sub-objectA1")), asList("sub-objectA1 width is 20px instead of 10px")));
                    }
                    validationListener.onAfterObject(pageRunner, pageValidation, "sub-objectA1");

                    validationListener.onAfterSection(pageRunner, pageValidation, subSection);
                    validationListener.onAfterSubLayout(pageValidation, "objectA1");
                }
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectA1", new SpecHeight(between(10, 20)).withOriginalText("component: some-component.spec").withPlace(new Place("specs.spec", 12)));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectA1");
            
            validationListener.onAfterSection(pageRunner, pageValidation, section2);
        
        }
        validationListener.onAfterPageAction(pageRunner, action);
        
        
        
        validationListener.onBeforePageAction(pageRunner, action);
        {
            PageSection section1 = sectionWithName("some section 1");
            validationListener.onBeforeSection(pageRunner, pageValidation, section1);
            
            validationListener.onObject(pageRunner, pageValidation, "objectB1"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectB1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px").withPlace(new Place("specs.spec", 12)));
                
                validationListener.onSpecError(pageRunner, pageValidation, 
                        "objectB1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left")
                            .withPlace(new Place("specs.spec", 12)),
                        new ValidationError(asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectB1")), asList("objectB1 is not inside other-object", "second error message with <xml> &tags"))
                            .withImageComparisonSample(new Rect(20, 30, 100, 40), "imgs/button-sample-correct.png", Rainbow4J.loadImage(comparisonMapImagePath)));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectB1"); 
            
            validationListener.onObject(pageRunner, pageValidation, "objectB2"); {
                validationListener.onSpecSuccess(pageRunner, pageValidation, "objectB2", new SpecHeight(exact(100)).withOriginalText("height: 100px").withPlace(new Place("specs.spec", 12)));
            }
            validationListener.onAfterObject(pageRunner, pageValidation, "objectB2");
            
            validationListener.onGlobalError(pageRunner, new FakeException("Some exception here"));
            
            validationListener.onAfterSection(pageRunner, pageValidation, section1);
        }
        validationListener.onAfterPageAction(pageRunner, action);

        if (suiteListener != null) {
            tellAfterSuite(suiteListener);
        }
        
    }

    private static void tellAfterSuite(SuiteListener suiteListener) {
        GalenTestInfo test = new GalenTestInfo("page1.test", null);
        TestReport report = new TestReport();
        for (int i=0; i< 6; i++) {
            report.info("info" + i);
        }
        for (int i=0; i< 5; i++) {
            report.error("error" + i);
        }
        test.setReport(report);
        suiteListener.afterTestSuite(asList(test));
    }

    private static PageSection sectionWithName(String name) {
        PageSection section = new PageSection();
        section.setName(name);
        return section;
    }

}

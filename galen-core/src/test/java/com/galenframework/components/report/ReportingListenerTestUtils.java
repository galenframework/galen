/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.components.report;

import static java.util.Arrays.asList;
import static com.galenframework.specs.Range.between;
import static com.galenframework.specs.Range.exact;
import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.galenframework.components.MockedPageValidation;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.Rect;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.TestReport;
import com.galenframework.runner.SuiteListener;
import com.galenframework.runner.TestListener;
import com.galenframework.specs.*;
import com.galenframework.specs.page.PageSection;
import com.galenframework.specs.Place;
import com.galenframework.suite.actions.GalenPageActionCheck;
import com.galenframework.tests.GalenBasicTest;
import com.galenframework.validation.*;
import com.galenframework.page.PageElement;
import com.galenframework.rainbow4j.Rainbow4J;

public class ReportingListenerTestUtils {

    private static final com.galenframework.specs.Spec NO_SPEC = null;
    private static String comparisonMapImagePath = ReportingListenerTestUtils.class.getResource("/imgs/page-sample-correct.png").getFile();

    public static void performSampleReporting(String suiteName, TestListener testListener, ValidationListener validationListener, SuiteListener suiteListener) throws IOException {
        
        GalenBasicTest suite = new GalenBasicTest();
        suite.setName(suiteName);
        
        if (testListener != null) testListener.onTestStarted(suite);
        
        Map<String, PageElement> pageElements = new HashMap<>();

        pageElements.put("objectA1", new MockedPageElement(10, 10, 100, 50));
        pageElements.put("objectA2", new MockedPageElement(200, 300, 50, 30));
        pageElements.put("objectB1", new MockedPageElement(10, 10, 100, 50));
        pageElements.put("objectB2", new MockedPageElement(200, 300, 50, 30));
        pageElements.put("sub-objectA1", new MockedPageElement(200, 300, 50, 30));
        
        MockedPageValidation pageValidation = new MockedPageValidation(pageElements);
        
        
        
        GalenPageActionCheck action = new GalenPageActionCheck();
        action.setOriginalCommand("check homepage.spec --include all,mobile");
        validationListener.onBeforePageAction(action);
        {
            PageSection section1 = new PageSection("", new Place("specs.spec", 5));
            validationListener.onBeforeSection(pageValidation, section1);
            
            validationListener.onObject(pageValidation, "objectA1"); {
                validationListener.onSpecGroup(pageValidation, "some spec group");
                    onSpecError(validationListener, pageValidation, "objectA1",
                            new SpecInside("other-object", asList(new Location(exact(10), asList(Side.LEFT))))
                                    .withOriginalText("inside other-object 10px left")
                                    .withPlace(new Place("specs.spec", 12)),
                            new ValidationResult(NO_SPEC,
                                    asList(
                                            new ValidationObject(new Rect(10, 10, 100, 50), "objectA1"),
                                            new ValidationObject(new Rect(1, 1, 90, 100), "other-object")),
                                    new ValidationError(asList("objectA1 is not inside other-object")), emptyList()
                            ));
                validationListener.onAfterSpecGroup(pageValidation, "some spec group");

                onSpecSuccess(validationListener, pageValidation, "objectA1",
                    new SpecWidth(between(10, 20))
                        .withOriginalText("width 10 to 20px")
                        .withPlace(new Place("specs.spec", 12))
                        .withAlias("Some alias"),
                    new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectA1"))));

                onSpecSuccess(validationListener, pageValidation, "objectA1",
                    new SpecWidth(between(10, 20))
                        .withOriginalText("width 10 to 20px")
                        .withPlace(new Place("specs.spec", 12)),
                    new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectA1"))));
            }
            validationListener.onAfterObject(pageValidation, "objectA1");
            
            validationListener.onObject(pageValidation, "objectA2"); {
                onSpecSuccess(validationListener, pageValidation, "objectA2",
                        new SpecWidth(between(10, 20))
                                .withOriginalText("width 10 to 20px")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectA2"))));

                onSpecError(validationListener, pageValidation,
                        "objectA2",
                        new SpecWidth(exact(10)).withOriginalText("width 10px")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectA2")),
                                new ValidationError(asList("objectA2 width is 20px instead of 10px")), emptyList()));


                onSpecError(validationListener, pageValidation,
                        "objectA2",
                        new SpecText(SpecText.Type.IS, "Login").withOriginalText("text is \"Login\"")
                                .withPlace(new Place("specs.spec", 12))
                                .withOnlyWarn(true),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectA2")),
                                new ValidationError(asList("objectA2 text is \"Logout\" instead of \"Login\"")), emptyList()));
            }
            validationListener.onAfterObject(pageValidation, "objectA2");
            
            validationListener.onAfterSection(pageValidation, section1);
            
            PageSection section2 = new PageSection("some section 2", new Place("specs.spec", 14));
            validationListener.onBeforeSection(pageValidation, section2);
            
            validationListener.onObject(pageValidation, "objectA1"); {
                onSpecSuccess(validationListener, pageValidation, "objectA1",
                        new SpecHeight(between(10, 20))
                                .withOriginalText("height 10 to 20px")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectA1"))));

                /* Calling before spec event as after it will be a sub-layout */
                validationListener.onBeforeSpec(pageValidation, "objectA1",
                        new SpecHeight(between(10, 20))
                        .withOriginalText("component some-component.spec")
                        .withPlace(new Place("specs.spec", 12)));
                //Doing sub-layout call
                {
                    validationListener.onSubLayout(pageValidation, "objectA1");
                    PageSection subSection = new PageSection("Sub section", new Place("specs.spec", 15));
                    validationListener.onBeforeSection(pageValidation, subSection);

                    validationListener.onObject(pageValidation, "sub-objectA1"); {
                        onSpecSuccess(validationListener, pageValidation, "sub-objectA1",
                                new SpecHeight(between(10, 20))
                                        .withOriginalText("height 10 to 20px")
                                        .withPlace(new Place("specs.spec", 12)),
                                new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "sub-objectA1"))));


                        onSpecError(validationListener, pageValidation,
                                "sub-objectA1",
                                new SpecWidth(exact(10)).withOriginalText("width 10px")
                                        .withPlace(new Place("specs.spec", 12)),
                                new ValidationResult(NO_SPEC,
                                        asList(new ValidationObject(new Rect(200, 300, 50, 30), "sub-objectA1")),
                                        new ValidationError(asList("sub-objectA1 width is 20px instead of 10px")), emptyList()));
                    }
                    validationListener.onAfterObject(pageValidation, "sub-objectA1");

                    validationListener.onAfterSection(pageValidation, subSection);
                    validationListener.onAfterSubLayout(pageValidation, "objectA1");
                }

                validationListener.onSpecSuccess(pageValidation, "objectA1",
                        new SpecHeight(between(10, 20))
                                .withOriginalText("component some-component.spec")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectA1"))));
            }
            validationListener.onAfterObject(pageValidation, "objectA1");
            
            validationListener.onAfterSection(pageValidation, section2);
        
        }
        validationListener.onAfterPageAction(action);
        

        validationListener.onBeforePageAction(action);
        {
            PageSection section1 = new PageSection("some section 1", new Place("specs.spec", 16));
            validationListener.onBeforeSection(pageValidation, section1);
            
            validationListener.onObject(pageValidation, "objectB1"); {

                onSpecSuccess(validationListener, pageValidation, "objectB1",
                        new SpecWidth(between(10, 20))
                                .withOriginalText("width 10 to 20px")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectB1"))));
                
                onSpecError(validationListener, pageValidation,
                        "objectB1",
                        new SpecInside("other-object", asList(new Location(exact(10), asList(Side.LEFT)))).withOriginalText("inside other-object 10px left")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC,
                                asList(new ValidationObject(new Rect(10, 10, 100, 50), "objectB1")),
                                new ValidationError(asList("objectB1 is not inside other-object", "second error message with <xml> &tags"))
                                    .withImageComparison(createSampleImageComparison()), emptyList()));
            }
            validationListener.onAfterObject(pageValidation, "objectB1");
            
            validationListener.onObject(pageValidation, "objectB2"); {
                onSpecSuccess(validationListener, pageValidation, "objectB2",
                        new SpecHeight(exact(100))
                                .withOriginalText("height 100px")
                                .withPlace(new Place("specs.spec", 12)),
                        new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectB2"))));
            }
            validationListener.onAfterObject(pageValidation, "objectB2");

            validationListener.onObject(pageValidation, "objectB2"); {
            onSpecSuccess(validationListener, pageValidation, "objectB2",
                    new SpecWidth(exact(100))
                            .withOriginalText("width 100px")
                            .withPlace(new Place("specs.spec", 13)),
                    new ValidationResult(NO_SPEC, asList(new ValidationObject(new Rect(200, 300, 50, 30), "objectB2"))));
        }
            validationListener.onAfterObject(pageValidation, "objectB2");

            validationListener.onGlobalError(new FakeException("Some exception here"));
            
            validationListener.onAfterSection(pageValidation, section1);
        }
        validationListener.onAfterPageAction(action);

        if (suiteListener != null) {
            tellAfterSuite(suiteListener);
        }
        
    }

    private static ImageComparison createSampleImageComparison() throws IOException {
        File file = new File(comparisonMapImagePath);
        return new ImageComparison(file, file, file);
    }

    private static void onSpecError(ValidationListener validationListener, MockedPageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        validationListener.onBeforeSpec(pageValidation, objectName, spec);
        validationListener.onSpecError(pageValidation, objectName, spec, result);
    }

    private static void onSpecSuccess(ValidationListener validationListener, MockedPageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        validationListener.onBeforeSpec(pageValidation, objectName, spec);
        validationListener.onSpecSuccess(pageValidation, objectName, spec, result);
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
}

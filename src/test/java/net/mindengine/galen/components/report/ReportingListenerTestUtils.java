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
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class ReportingListenerTestUtils {

    public static void performSampleReporting(SuiteListener suiteListener, ValidationListener validationListener) {
        GalenPageRunner pageRunner = null;
        
        GalenSuiteRunner galenSuiteRunner = new GalenSuiteRunner(null);
        galenSuiteRunner.setName("page1.test");
        
        suiteListener.onSuiteStarted(galenSuiteRunner);
        
        Browser browser = new MockedBrowser("http://example.com/page1", new Dimension(400, 600));
        
        PageValidation pageValidation = new PageValidation(new MockedPage(null), null);
        suiteListener.onBeforePage(galenSuiteRunner, pageRunner, browser); {
            validationListener.onObject(pageValidation, "objectA1"); {
                validationListener.onSpecError(pageValidation, 
                        "objectA1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left"),
                        new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectA1")), asList("objectA1 is not inside other-object")));
                
                validationListener.onSpecSuccess(pageValidation, "objectA1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
            }
            validationListener.onObject(pageValidation, "objectA2"); {
                validationListener.onSpecSuccess(pageValidation, "objectA2", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
                validationListener.onSpecError(pageValidation, 
                        "objectA2", 
                        new SpecWidth(exact(10)).withOriginalText("width: 10px"),
                        new ValidationError(asList(new ErrorArea(new Rect(200, 300, 50, 30), "objectA2")), asList("objectA2 width is 20px instead of 10px")));
            }
        
        }
        suiteListener.onAfterPage(galenSuiteRunner, pageRunner, browser, asList(new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectA1")), asList("objectA1 is not inside other-object"))));
        
        
        pageValidation = new PageValidation(new MockedPage(null), null);
        Browser browser2 = new MockedBrowser("http://example.com/page2", new Dimension(600, 700));
        suiteListener.onBeforePage(galenSuiteRunner, pageRunner, browser2); {
            validationListener.onObject(pageValidation, "objectB1"); {
                validationListener.onSpecSuccess(pageValidation, "objectB1", new SpecWidth(between(10, 20)).withOriginalText("width: 10 to 20px"));
                
                validationListener.onSpecError(pageValidation, 
                        "objectB1", 
                        new SpecInside("other-object", asList(new Location(exact(10), asList(LEFT)))).withOriginalText("inside: other-object 10px left"),
                        new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectB1")), asList("objectB1 is not inside other-object", "second error message with <xml> &tags")));
            }
            validationListener.onObject(pageValidation, "objectB2"); {
                validationListener.onSpecSuccess(pageValidation, "objectB2", new SpecHeight(exact(100)).withOriginalText("height: 100px"));
            }
        
        }
        suiteListener.onAfterPage(galenSuiteRunner, pageRunner, browser2, asList(new ValidationError(asList(new ErrorArea(new Rect(10, 10, 100, 50), "objectB1")), asList("objectA1 is not inside other-object", "second error message"))));
        
        suiteListener.onSuiteFinished(galenSuiteRunner);
    }

}

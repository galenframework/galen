/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.api;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.LayoutReportNode;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.TestReportNode;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.SectionFilter;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.tests.GalenEmptyTest;
import net.mindengine.galen.tests.GalenTest;
import net.mindengine.galen.tests.TestSession;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.*;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class Galen {


    public static LayoutReport checkLayout(Browser browser, List<String> specPaths,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        PageSpecReader reader = new PageSpecReader(properties, browser);

        List<PageSpec> specs = new LinkedList<PageSpec>();

        for (String specPath : specPaths) {
            specs.add(reader.read(specPath));
        }

        return checkLayout(browser, specs, includedTags, excludedTags, validationListener);
    }

    public static LayoutReport checkLayout(Browser browser, List<PageSpec> specs,
                                   List<String> includedTags, List<String> excludedTags,
                                   ValidationListener validationListener) throws IOException {

        CombinedValidationListener listener = new CombinedValidationListener();
        listener.add(validationListener);

        LayoutReport layoutReport = new LayoutReport();
        try {
            layoutReport.setScreenshotFullPath(browser.createScreenshot());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        listener.add(new LayoutReportListener(layoutReport));

        Page page = browser.getPage();

        List<ValidationError> allValidationErrors = new LinkedList<ValidationError>();

        for (PageSpec spec : specs) {

            SectionFilter sectionFilter = new SectionFilter(includedTags, excludedTags);
            List<PageSection> pageSections = spec.findSections(sectionFilter);
            SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(browser, page, spec, listener, sectionFilter), listener);

            List<ValidationError> errors = sectionValidation.check();
            if (errors != null && errors.size() > 0) {
                allValidationErrors.addAll(errors);
            }
        }

        layoutReport.setValidationErrors(allValidationErrors);

        return layoutReport;
    }

    public static LayoutReport checkLayout(WebDriver driver, List<String> specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), specPath, includedTags, excludedTags, properties, validationListener);
    }

    public static LayoutReport checkLayout(WebDriver driver, String specPath,
                                           List<String> includedTags, List<String> excludedTags,
                                           Properties properties, ValidationListener validationListener) throws IOException {
        return checkLayout(new SeleniumBrowser(driver), asList(specPath), includedTags, excludedTags, properties, validationListener);
    }

}

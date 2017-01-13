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
package com.galenframework.suite.actions;

import com.galenframework.api.Galen;
import com.galenframework.browser.Browser;
import com.galenframework.browser.mutation.MutationExecBrowser;
import com.galenframework.browser.mutation.MutationRecordBrowser;
import com.galenframework.page.PageElement;
import com.galenframework.reports.TestReport;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.mutation.*;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.ValidationListener;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class GalenPageActionMutate extends GalenPageAction {
    private static final Map<String, Object> NO_JS_VARIABLES = emptyMap();
    private static final ValidationListener NO_LISTENER = null;
    private static final Map<String, Locator> NO_OBJECTS = null;

    private String specPath;
    private List<String> includedTags;
    private List<String> excludedTags;

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        SectionFilter sectionFilter = createSectionFilter();
        PageSpec pageSpec = parseSpec(browser, sectionFilter);

        File screenshotFile = browser.getPage().getScreenshotFile();
        MutationRecordBrowser mutationRecordBrowser = new MutationRecordBrowser(browser);


        LayoutReport initialLayoutReport = Galen.checkLayout(mutationRecordBrowser, pageSpec, sectionFilter, screenshotFile, validationListener);
        GalenUtils.attachLayoutReport(initialLayoutReport, report, specPath, includedTags);

        if (initialLayoutReport.errors() > 0) {
            throw new RuntimeException("There are errors in initial layout validation report");
        } else {
            MutationReport mutationReport = testAllMutations(mutationRecordBrowser.getRecordedElements(), browser, pageSpec, sectionFilter, screenshotFile);
            //TODO attach mutation report
            printMutationReport(mutationReport);
            //attachMutationReport(mutationReport, report);
        }
    }

    private SectionFilter createSectionFilter() {
        return new SectionFilter(getIncludedTags(), getExcludedTags());
    }

    private PageSpec parseSpec(Browser browser, SectionFilter sectionFilter) throws IOException {
        return new PageSpecReader().read(specPath, browser.getPage(), sectionFilter, getCurrentProperties(), NO_JS_VARIABLES, NO_OBJECTS);
    }

    //TODO remove this
    private void printMutationReport(MutationReport mutationReport) {
        System.out.println("************* Mutation  Report ************");
        System.out.println("Total passed: " + mutationReport.getTotalPassed());
        System.out.println("Total failed: " + mutationReport.getTotalFailed());
        System.out.println();
        mutationReport.getObjectMutationStatistics().forEach((elementName, statistic) -> {
            if (statistic.getFailed() > 0) {
                System.out.println("  " + elementName + " (" + statistic.getFailed() + " failed):");
                statistic.getFailedMutations().forEach(failedMutation -> System.out.println("    " + failedMutation));
            }
        });
    }

    private MutationReport testAllMutations(Map<String, PageElement> recordedElements, Browser browser, PageSpec pageSpec, SectionFilter sectionFilter, File screenshotFile) {
        List<PageMutation> mutations = recordedElements.entrySet().stream()
            .filter(nonViewport())
            .map(e-> generateMutationsFor(e.getKey())).flatMap(Collection::stream).collect(toList());

        MutationExecBrowser mutationExecBrowser = new MutationExecBrowser(browser, recordedElements);

        MutationReport mutationReport = new MutationReport();
        mutations.forEach(mutation -> testMutation(mutation, mutationReport, mutationExecBrowser, pageSpec, sectionFilter, screenshotFile));
        return mutationReport;
    }

    private void testMutation(PageMutation pageMutation, MutationReport mutationReport, MutationExecBrowser mutationExecBrowser, PageSpec pageSpec, SectionFilter sectionFilter, File screenshotFile) {
        mutationExecBrowser.setActiveMutations(toMutationMap(pageMutation.getPageElementMutations()));
        try {
            LayoutReport layoutReport = Galen.checkLayout(mutationExecBrowser, pageSpec, sectionFilter, screenshotFile, NO_LISTENER);

            if (layoutReport.errors() == 0) {
                mutationReport.reportFailedMutation(pageMutation);
            } else {
                mutationReport.reportSuccessMutation(pageMutation);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Mutation crashed: " + pageMutation.getName(), ex);
        }
    }

    private Map<String, AreaMutation> toMutationMap(List<PageElementMutation> pageElementMutations) {
        Map<String, AreaMutation> map = new HashMap<>();
        pageElementMutations.forEach(pem -> map.put(pem.getElementName(), pem.getAreaMutation()));
        return map;
    }

    private List<PageMutation> generateMutationsFor(String name) {
        return AreaMutation.generateStandardMutations(new MutationOptions()).stream()
            .map(areaMutation -> new PageMutation(name, singletonList(new PageElementMutation(name, areaMutation)))).collect(toList());
    }

    private Predicate<Map.Entry<String, PageElement>> nonViewport() {
        return e -> !e.getKey().equals("viewport");
    }

    public GalenPageActionMutate withSpec(String specPath) {
        this.specPath = specPath;
        return this;
    }

    public GalenPageActionMutate withIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
        return this;
    }

    public GalenPageActionMutate withExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
        return this;
    }

    public GalenPageActionMutate withOriginalCommand(String originalCommand) {
        setOriginalCommand(originalCommand);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }
}

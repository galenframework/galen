/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.api.mutation;

import com.galenframework.browser.Browser;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.suite.actions.mutation.MutationReport;
import com.galenframework.api.Galen;
import com.galenframework.browser.mutation.MutationExecBrowser;
import com.galenframework.browser.mutation.MutationRecordBrowser;
import com.galenframework.page.PageElement;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.suite.actions.mutation.*;
import com.galenframework.validation.ValidationListener;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;

public class GalenMutate {
    private static final Map<String, Object> NO_JS_VARIABLES = emptyMap();
    private static final ValidationListener NO_LISTENER = null;
    private static final Map<String, Locator> NO_OBJECTS = null;

    public static MutationReport checkAllMutations(Browser browser, String specPath, List<String> includedTags, List<String> excludedTags,
                                                   MutationOptions mutationOptions, Properties properties, ValidationListener validationListener) throws IOException {
        SectionFilter sectionFilter = new SectionFilter(includedTags, excludedTags);
        PageSpec pageSpec = parseSpec(specPath, browser, sectionFilter, properties);

        File screenshotFile = browser.getPage().getScreenshotFile();
        MutationRecordBrowser mutationRecordBrowser = new MutationRecordBrowser(browser);

        LayoutReport initialLayoutReport = Galen.checkLayout(mutationRecordBrowser, pageSpec, sectionFilter, screenshotFile, validationListener);

        MutationReport mutationReport;
        if (initialLayoutReport.errors() > 0) {
            mutationReport = createCrashedMutationReport("Cannot perform mutation testing. There are errors in initial layout validation report");
        } else {
            mutationReport = testAllMutations(mutationRecordBrowser.getRecordedElements(), browser, pageSpec, sectionFilter, mutationOptions, screenshotFile);
        }

        mutationReport.setInitialLayoutReport(initialLayoutReport);
        return mutationReport;
    }

    private static MutationReport createCrashedMutationReport(String error) {
        MutationReport mutationReport = new MutationReport();
        mutationReport.setError(error);
        return mutationReport;
    }

    private static PageSpec parseSpec(String specPath, Browser browser, SectionFilter sectionFilter, Properties properties) throws IOException {
        return new PageSpecReader().read(specPath, browser.getPage(), sectionFilter, properties, NO_JS_VARIABLES, NO_OBJECTS);
    }

    private static MutationReport testAllMutations(Map<String, PageElement> recordedElements, Browser browser,
                                                   PageSpec pageSpec, SectionFilter sectionFilter, MutationOptions mutationOptions,
                                                   File screenshotFile) {
        List<PageMutation> mutations = recordedElements.entrySet().stream()
            .filter(nonViewport())
            .map(e-> generateMutationsFor(e.getKey(), mutationOptions)).flatMap(Collection::stream).collect(toList());

        MutationExecBrowser mutationExecBrowser = new MutationExecBrowser(browser, recordedElements);

        MutationReport mutationReport = new MutationReport();
        mutations.forEach(mutation -> testMutation(mutation, mutationReport, mutationExecBrowser, pageSpec, sectionFilter, screenshotFile));
        return mutationReport;
    }

    private static void testMutation(PageMutation pageMutation, MutationReport mutationReport, MutationExecBrowser mutationExecBrowser, PageSpec pageSpec, SectionFilter sectionFilter, File screenshotFile) {
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

    private static Map<String, AreaMutation> toMutationMap(List<PageElementMutation> pageElementMutations) {
        Map<String, AreaMutation> map = new HashMap<>();
        pageElementMutations.forEach(pem -> map.put(pem.getElementName(), pem.getAreaMutation()));
        return map;
    }

    private static List<PageMutation> generateMutationsFor(String name, MutationOptions mutationOptions) {
        return AreaMutation.generateStandardMutations(mutationOptions).stream()
            .map(areaMutation -> new PageMutation(name, singletonList(new PageElementMutation(name, areaMutation)))).collect(toList());
    }

    private static Predicate<Map.Entry<String, PageElement>> nonViewport() {
        return e -> !e.getKey().equals("viewport");
    }
}

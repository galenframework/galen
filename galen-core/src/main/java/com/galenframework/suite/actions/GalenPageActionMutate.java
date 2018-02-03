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

import com.galenframework.api.mutation.GalenMutate;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.mutation.MutationReport;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.ValidationListener;
import java.util.*;

public class GalenPageActionMutate extends GalenPageAction {

    private String specPath;
    private List<String> includedTags;
    private List<String> excludedTags;

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        MutationReport mutationReport = GalenMutate.checkAllMutations(browser, specPath, includedTags, excludedTags, getCurrentProperties(), validationListener);
        if (mutationReport.getInitialLayoutReport() != null) {
            GalenUtils.attachLayoutReport(mutationReport.getInitialLayoutReport(), report, specPath, includedTags);
        }
        GalenUtils.attachMutationReport(mutationReport, report, specPath, includedTags);
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

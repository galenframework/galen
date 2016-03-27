/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.ValidationListener;
import com.galenframework.api.Galen;
import com.galenframework.browser.Browser;
import com.galenframework.reports.nodes.LayoutReportNode;
import com.galenframework.reports.TestReport;
import com.galenframework.reports.nodes.TestReportNode;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.galenframework.utils.GalenUtils.toCommaSeparated;

public class GalenPageActionCheck extends GalenPageAction {

    private static final File NO_SCREENSHOT = null;
    private String specPath;
    private List<String> includedTags;
    private List<String> excludedTags;
    private Map<String, Object> jsVariables;


    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws IOException {
        SectionFilter sectionFilter = new SectionFilter(getIncludedTags(), getExcludedTags());
        LayoutReport layoutReport = Galen.checkLayout(browser, specPath, sectionFilter, getCurrentProperties(), jsVariables, NO_SCREENSHOT, validationListener);
        GalenUtils.attachLayoutReport(layoutReport, report, specPath, includedTags);
    }


    public GalenPageActionCheck withSpec(String specPath) {
        setSpecPath(specPath);
        return this;
    }


    public GalenPageActionCheck withIncludedTags(List<String> includedTags) {
        this.setIncludedTags(includedTags);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public GalenPageActionCheck withExcludedTags(List<String> excludedTags) {
        this.setExcludedTags(excludedTags);
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(specPath)
            .append(includedTags)
            .append(excludedTags)
            .append(jsVariables)
            .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionCheck))
            return false;
        
        GalenPageActionCheck rhs = (GalenPageActionCheck)obj;
        
        return new EqualsBuilder()
            .append(specPath, rhs.specPath)
            .append(includedTags, rhs.includedTags)
            .append(excludedTags, rhs.excludedTags)
            .append(jsVariables, rhs.jsVariables)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("specPath", specPath)
            .append("includedTags", includedTags)
            .append("excludedTags", excludedTags)
            .append("jsVariables", jsVariables)
            .toString();
    }

    public void setSpecPath(String specPath) {
        this.specPath = specPath;
    }

    public GalenPageAction withOriginalCommand(String originalCommand) {
        setOriginalCommand(originalCommand);
        return this;
    }

    public void setJsVariables(Map<String, Object> jsVariables) {
        this.jsVariables = jsVariables;
    }

    public GalenPageActionCheck withJsVariables(Map<String, Object> jsVariables) {
        setJsVariables(jsVariables);
        return this;
    }
}

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
package net.mindengine.galen.suite.actions;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import net.mindengine.galen.api.Galen4J;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.reports.LayoutReportNode;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.TestReportNode;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.SectionFilter;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.tests.TestSession;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.CombinedValidationListener;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.LayoutReportListener;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionCheck extends GalenPageAction {

    private List<String> specs;
    private List<String> includedTags;
    private List<String> excludedTags;

    
    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws IOException {
        Galen4J.checkLayout(browser, getSpecs(), getIncludedTags(), getExcludedTags(), getCurrentProperties(), validationListener);
    }

    private Properties getCurrentProperties() {
        TestSession session = TestSession.current();
        if (session != null) {
            if (session.getProperties() != null && session.getProperties().getProperties() != null) {
                return session.getProperties().getProperties();
            }
        }
        return new Properties();
    }

    public GalenPageActionCheck withSpecs(List<String> specFilePaths) {
        this.setSpecs(specFilePaths);
        return this;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
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
            .append(specs)
            .append(includedTags)
            .append(excludedTags)
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
            .append(specs, rhs.specs)
            .append(includedTags, rhs.includedTags)
            .append(excludedTags, rhs.excludedTags)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("specs", specs)
            .append("includedTags", includedTags)
            .append("excludedTags", excludedTags)
            .toString();
    }

    public GalenPageAction withOriginalCommand(String originalCommand) {
        setOriginalCommand(originalCommand);
        return this;
    }
   
}

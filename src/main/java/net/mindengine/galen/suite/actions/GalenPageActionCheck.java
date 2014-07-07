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
        
        
        String reportTitle = "Check layout: " + toCommaSeparated(getSpecs()) + " included tags: " + toCommaSeparated(getIncludedTags());
        TestReportNode layoutReportNode = report.addNode(new LayoutReportNode(layoutReport, reportTitle));
        
        Page page = browser.getPage();
        PageSpecReader pageSpecReader = new PageSpecReader(getProperties(), browser);
        
        
        boolean isFailed = false;
        for (String specFilePath : specs) {
            PageSpec spec = pageSpecReader.read(specFilePath);
            
            SectionFilter sectionFilter = new SectionFilter(includedTags, excludedTags);
            List<PageSection> pageSections = spec.findSections(sectionFilter);
            SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(browser, page, spec, listener, sectionFilter), listener);
            
            List<ValidationError> errors = sectionValidation.check();
            if (errors != null && errors.size() > 0) {
                isFailed = true;
            }
        }
        
        if (isFailed) {
            layoutReportNode.setStatus(TestReportNode.Status.ERROR);
        }
        
    }

    private String toCommaSeparated(List<String> list) {
        if (list != null) {
            StringBuffer buff = new StringBuffer();
            boolean comma = false;
            for (String item : list) {
                if (comma) {
                    buff.append(',');
                }
                comma = true;
                buff.append(item);
            }
            return buff.toString();
        }
        return "";
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

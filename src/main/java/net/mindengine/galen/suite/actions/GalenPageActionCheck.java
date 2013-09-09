/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SectionValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class GalenPageActionCheck implements GalenPageAction {

    private List<String> specs;
    private List<String> includedTags;
    private List<String> excludedTags;

    
    @Override
    public List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) {
        List<ValidationError> allErrors = new LinkedList<ValidationError>();
        
        Page page = browser.getPage();
        PageSpecReader pageSpecReader = new PageSpecReader();
        
        for (String specFile : specs) {
            try {
                PageSpec spec = pageSpecReader.read(GalenUtils.findFile(specFile));
                List<PageSection> pageSections = spec.findSections(includedTags, excludedTags);
                
                SectionValidation sectionValidation = new SectionValidation(pageSections, new PageValidation(page, spec), validationListener);
                
                List<ValidationError> errors = sectionValidation.check();
                if (errors != null) {
                    allErrors.addAll(errors);
                }
            }
            catch (Exception e) {
                allErrors.add(ValidationError.fromException(e));
            }
        }
        
        return allErrors;
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
   
}

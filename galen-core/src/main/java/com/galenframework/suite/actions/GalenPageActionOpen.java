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

import com.galenframework.browser.Browser;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.validation.ValidationListener;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionOpen extends GalenPageAction {
    private String url;

    public GalenPageActionOpen(String url) {
        this.setUrl(url);
    }

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        browser.load(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("url", url).toString();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(url).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionOpen))
            return false;
        GalenPageActionOpen rhs = (GalenPageActionOpen)obj;
        
        return new EqualsBuilder().append(this.url, rhs.url).isEquals();
    }
}

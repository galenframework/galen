/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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

import java.io.IOException;

import com.galenframework.validation.ValidationListener;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.utils.GalenUtils;
import com.galenframework.validation.ValidationListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionInjectJavascript extends GalenPageAction{

    private String javascriptFilePath;

    public GalenPageActionInjectJavascript(String javascriptFilePath) {
        this.setJavascriptFilePath(javascriptFilePath);
    }
    public String getJavascriptFilePath() {
        return javascriptFilePath;
    }

    public void setJavascriptFilePath(String javascriptFilePath) {
        this.javascriptFilePath = javascriptFilePath;
    }
    
    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws IOException {
        String javascript = FileUtils.readFileToString(GalenUtils.findFile(javascriptFilePath));
        browser.executeJavascript(javascript);
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder() //@formatter:off
            .append(javascriptFilePath)
            .toHashCode(); //@formatter:on
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionInjectJavascript))
            return false;
        
        GalenPageActionInjectJavascript rhs = (GalenPageActionInjectJavascript)obj;
        
        return new EqualsBuilder() //@formatter:off
            .append(javascriptFilePath, rhs.javascriptFilePath)
            .isEquals(); //@formatter:on
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //@formatter:off
            .append("javascriptFilePath", javascriptFilePath)
            .toString(); //@formatter:on
    }
}

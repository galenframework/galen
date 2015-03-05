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
package net.mindengine.galen.suite.actions;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationListener;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionDumpPage extends GalenPageAction {

    private String pageDumpPath;
    private String specPath;
    private String pageName;
    private Integer maxHeight;
    private Integer maxWidth;

    public GalenPageActionDumpPage() {
    }

    public GalenPageActionDumpPage(String pageName, String specPath, String pageDumpPath) {
        this.pageName = pageName;
        this.specPath = specPath;
        this.pageDumpPath = pageDumpPath;
    }

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        Galen.dumpPage(browser, pageName, specPath, pageDumpPath, maxWidth, maxHeight, getCurrentProperties());
    }

    public String getPageDumpPath() {
        return pageDumpPath;
    }

    public void setPageDumpPath(String pageDumpPath) {
        this.pageDumpPath = pageDumpPath;
    }

    public String getSpecPath() {
        return specPath;
    }

    public void setSpecPath(String specPath) {
        this.specPath = specPath;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public GalenPageActionDumpPage withSpecPath(String specPath) {
        setSpecPath(specPath);
        return this;
    }

    public GalenPageActionDumpPage withPageName(String pageName) {
        setPageName(pageName);
        return this;
    }

    public GalenPageActionDumpPage withPageDumpPath(String pageDumpPath) {
        setPageDumpPath(pageDumpPath);
        return this;
    }

    public GalenPageActionDumpPage withMaxWidth(Integer maxWidth) {
        setMaxWidth(maxWidth);
        return this;
    }

    public GalenPageActionDumpPage withMaxHeight(Integer maxHeight) {
        setMaxHeight(maxHeight);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //@formatter:off
                .append(this.specPath)
                .append(this.pageName)
                .append(this.pageDumpPath)
                .append(this.maxWidth)
                .append(this.maxHeight)
                .toHashCode(); //@formatter:on
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionDumpPage))
            return false;

        GalenPageActionDumpPage rhs = (GalenPageActionDumpPage)obj;
        return new EqualsBuilder() //@formatter:off
                .append(specPath, rhs.specPath)
                .append(pageName, rhs.pageName)
                .append(pageDumpPath, rhs.pageDumpPath)
                .append(maxWidth, rhs.maxWidth)
                .append(maxHeight, rhs.maxHeight)
                .isEquals(); //@formatter:on
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //@formatter:off
                .append("specPath", specPath)
                .append("pageName", pageName)
                .append("pageDumpPath", pageDumpPath)
                .append("maxWidth", maxWidth)
                .append("maxHeight", maxHeight)
                .toString(); //@formatter:on
    }
}

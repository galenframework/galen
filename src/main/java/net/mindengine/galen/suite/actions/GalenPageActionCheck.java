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

import java.io.IOException;
import java.util.List;

import net.mindengine.galen.api.Galen;
import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.nodes.LayoutReportNode;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.reports.nodes.TestReportNode;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionCheck extends GalenPageAction {

    private List<String> specs;
    private List<String> includedTags;
    private List<String> excludedTags;

    @Override
    public void execute(final TestReport report, final Browser browser, final GalenPageTest pageTest, final ValidationListener validationListener)
            throws IOException {
        final LayoutReport layoutReport = Galen.checkLayout(browser, getSpecs(), getIncludedTags(), getExcludedTags(), getCurrentProperties(),
                validationListener);

        if (report != null) {
            final String reportTitle = "Check layout: " + toCommaSeparated(getSpecs()) + " included tags: " + toCommaSeparated(includedTags);
            final TestReportNode layoutReportNode = new LayoutReportNode(report.getFileStorage(), layoutReport, reportTitle);
            if (layoutReport.errors() > 0) {
                layoutReportNode.setStatus(TestReportNode.Status.ERROR);
            }
            report.addNode(layoutReportNode);
        }
    }

    public GalenPageActionCheck withSpecs(final List<String> specFilePaths) {
        this.setSpecs(specFilePaths);
        return this;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(final List<String> specs) {
        this.specs = specs;
    }

    public GalenPageActionCheck withIncludedTags(final List<String> includedTags) {
        this.setIncludedTags(includedTags);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(final List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public GalenPageActionCheck withExcludedTags(final List<String> excludedTags) {
        this.setExcludedTags(excludedTags);
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(final List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(specs).append(includedTags).append(excludedTags).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GalenPageActionCheck)) {
            return false;
        }

        final GalenPageActionCheck rhs = (GalenPageActionCheck) obj;

        return new EqualsBuilder().append(specs, rhs.specs).append(includedTags, rhs.includedTags).append(excludedTags, rhs.excludedTags).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("specs", specs).append("includedTags", includedTags).append("excludedTags", excludedTags).toString();
    }

    public GalenPageAction withOriginalCommand(final String originalCommand) {
        setOriginalCommand(originalCommand);
        return this;
    }

    private static String toCommaSeparated(final List<String> list) {
        if (list != null) {
            final StringBuilder builder = new StringBuilder();
            boolean comma = false;
            for (final String item : list) {
                if (comma) {
                    builder.append(',');
                }
                comma = true;
                builder.append(item);
            }
            return builder.toString();
        }
        return "";
    }

}

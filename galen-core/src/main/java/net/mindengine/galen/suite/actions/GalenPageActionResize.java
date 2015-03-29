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

import java.awt.Dimension;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionResize extends GalenPageAction {

    private int width;
    private int height;

    public GalenPageActionResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        browser.changeWindowSize(new Dimension(width, height));
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this) //@formatter:off
            .append("width", width)
            .append("height", height)
            .toString(); //@formatter:on
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder() //@formatter:off
            .append(width)
            .append(height)
            .toHashCode(); //@formatter:on
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionResize))
            return false;
        GalenPageActionResize rhs = (GalenPageActionResize)obj;
        
        return new EqualsBuilder() //@formatter:off
        .append(width, rhs.width)
        .append(height, rhs.height)
        .isEquals(); //@formatter:on
    }

}

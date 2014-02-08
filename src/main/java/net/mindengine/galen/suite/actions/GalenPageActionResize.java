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

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class GalenPageActionResize extends GalenPageAction {

    private static final List<ValidationError> NO_ERRORS = new LinkedList<ValidationError>();
    private int width;
    private int height;

    public GalenPageActionResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        browser.changeWindowSize(new Dimension(width, height));
        return NO_ERRORS;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("width", width)
            .append("height", height)
            .toString();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(width)
            .append(height)
            .toHashCode();
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
        
        return new EqualsBuilder()
        .append(width, rhs.width)
        .append(height, rhs.height)
        .isEquals();
    }

}

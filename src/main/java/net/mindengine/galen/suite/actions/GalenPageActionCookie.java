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

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionCookie extends GalenPageAction {

    private List<String> cookies;
    
    @Override
    public List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        if (cookies != null && cookies.size() > 0) {
            StringBuilder js = new StringBuilder();
            
            for (String cookie : cookies) {
                js.append("document.cookie=\"" + StringEscapeUtils.escapeJava(cookie) + "\";");
            }
            browser.executeJavascript(js.toString());
            browser.refresh();
        }
        return null;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

    public GalenPageActionCookie withCookies(String...cookieArray) {
        cookies = Arrays.asList(cookieArray);
        return this;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(cookies)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionCookie))
            return false;
        
        GalenPageActionCookie rhs = (GalenPageActionCookie) obj;
        return new EqualsBuilder()
            .append(cookies, rhs.cookies)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("cookies", cookies)
            .toString();
    }

}

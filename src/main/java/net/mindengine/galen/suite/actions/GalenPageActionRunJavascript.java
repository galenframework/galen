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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.browser.WebDriverWrapper;
import net.mindengine.galen.javascript.GalenJsExecutor;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionRunJavascript extends GalenPageAction{

    private static final List<ValidationError> NO_ERRORS = new LinkedList<ValidationError>();
    private String javascriptPath;
    private String jsonArguments;

    public GalenPageActionRunJavascript(String javascriptPath) {
        this.setJavascriptPath(javascriptPath);
    }
    
    
    @Override
    public List<ValidationError> execute(Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        
        File file = GalenUtils.findFile(javascriptPath);
        Reader scriptFileReader = new FileReader(file);
        
        
        GalenJsExecutor js = new GalenJsExecutor();
        js.putObject("browser", browser);
        provideWrappedWebDriver(js, browser);
        
        js.eval("var arg = " + jsonArguments);
        js.eval(scriptFileReader, javascriptPath);
               
        return NO_ERRORS;
    }
    
    private void provideWrappedWebDriver(GalenJsExecutor jsExecutor, Browser browser) {
        if (browser instanceof SeleniumBrowser) {
            SeleniumBrowser seleniumBrowser = (SeleniumBrowser) browser;
            WebDriverWrapper driver = new WebDriverWrapper(seleniumBrowser.getDriver());
            jsExecutor.putObject("driver", driver);
        }
        
    }


    public String getJavascriptPath() {
        return javascriptPath;
    }

    public void setJavascriptPath(String javascriptPath) {
        this.javascriptPath = javascriptPath;
    }

    public GalenPageAction withArguments(String jsonArguments) {
        this.setJsonArguments(jsonArguments);
        return this;
    }

    public String getJsonArguments() {
        return jsonArguments;
    }

    public void setJsonArguments(String jsonArguments) {
        this.jsonArguments = jsonArguments;
    }
    
    public GalenPageActionRunJavascript withJsonArguments(String jsonArguments) {
        setJsonArguments(jsonArguments);
        return this;
    }

    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(javascriptPath)
            .append(jsonArguments)
            .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionRunJavascript))
            return false;
        
        GalenPageActionRunJavascript rhs = (GalenPageActionRunJavascript)obj;
        
        return new EqualsBuilder()
            .append(javascriptPath, rhs.javascriptPath)
            .append(jsonArguments, rhs.jsonArguments)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("javascriptPath", javascriptPath)
            .append("jsonArguments", jsonArguments)
            .toString();
    }
}

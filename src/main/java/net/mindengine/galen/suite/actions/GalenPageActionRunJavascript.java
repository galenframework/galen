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
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.javascript.ScriptExecutor;
import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
        
        Context cx = Context.enter();
        
        ScriptableObject scope = new ImporterTopLevel(cx);
        
        ScriptableObject.putProperty(scope, "browser", Context.javaToJS(browser, scope));
        
        
        ScriptableObject.putProperty(scope, "global", Context.javaToJS(new ScriptExecutor(scope, cx, file.getParent()), scope));
        
        provideWrappedWebDriver(scope, browser);
        importAllMajorClasses(scope, cx);
        
        cx.evaluateString(scope, "var arg = " + jsonArguments, "<cmd>", 1, null);       
        cx.evaluateReader(scope, scriptFileReader, javascriptPath, 1, null);
        
        return NO_ERRORS;
    }
    
    private void provideWrappedWebDriver(ScriptableObject scope, Browser browser) {
        if (browser instanceof SeleniumBrowser) {
            SeleniumBrowser seleniumBrowser = (SeleniumBrowser) browser;
            WebDriverWrapper driver = new WebDriverWrapper(seleniumBrowser.getDriver());
            ScriptableObject.putProperty(scope, "driver", Context.javaToJS(driver, scope));
        }
        
    }


    private void importAllMajorClasses(ScriptableObject scope, Context cx)  {
        importClasses(scope, cx, new Class[]{
                Thread.class,
                WebDriverWrapper.class,
                By.class,
                WebElement.class,
                WebDriver.class,
                System.class,
                Actions.class
        });
    }


    private void importClasses(ScriptableObject scope, Context cx, Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            cx.evaluateString(scope, "importClass(" + clazz.getName() + ");", "<cmd>", 1, null);
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

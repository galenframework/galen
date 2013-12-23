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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        ScriptContext context = engine.getContext();
        context.setAttribute("name", "JavaScript", ScriptContext.ENGINE_SCOPE);
        
        engine.put("global", new ScriptExecutor(engine, file.getParent()));
        engine.put("browser", browser);
        
        provideWrappedWebDriver(engine, browser);

        importAllMajorClasses(engine);
        engine.eval("var arg = " + jsonArguments);
        engine.eval(scriptFileReader);
        return NO_ERRORS;
    }
    
    private void provideWrappedWebDriver(ScriptEngine engine, Browser browser) {
        if (browser instanceof SeleniumBrowser) {
            SeleniumBrowser seleniumBrowser = (SeleniumBrowser)browser;
            engine.put("browser", new WebDriverWrapper(seleniumBrowser.getDriver()));
        }
        
    }


    private void importAllMajorClasses(ScriptEngine engine) throws ScriptException {
        importClasses(engine, new Class[]{
                Thread.class,
                WebDriverWrapper.class,
                By.class,
                WebElement.class,
                WebDriver.class,
                System.class
        });
    }


    private void importClasses(ScriptEngine engine, Class<?>[] classes) throws ScriptException {
        for (Class<?> clazz : classes) {
            engine.eval("importClass(" + clazz.getName() + ")");
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

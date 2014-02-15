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
package net.mindengine.galen.browser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;


import net.mindengine.galen.suite.actions.javascript.ScriptExecutor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptableObject;
import org.openqa.selenium.WebDriver;

public class JsBrowserFactory implements BrowserFactory {

    private String scriptPath;
    private String[] args;

    public JsBrowserFactory(String scriptPath, String[] args) {
        this.scriptPath = scriptPath;
        this.args = args;
    }

    @Override
    public Browser openBrowser() {
        File file = new File(scriptPath);
        Context cx = Context.enter();
        ScriptableObject scope = new ImporterTopLevel(cx);
        ScriptableObject.putProperty(scope, "args", Context.javaToJS(args, scope));
        ScriptableObject.putProperty(scope, "global", Context.javaToJS(new ScriptExecutor(scope, cx, file.getParent()), scope));
        
        Reader scriptFileReader;
        Object result;
        try {
            scriptFileReader = new FileReader(file);
            result = cx.evaluateReader(scope, scriptFileReader, scriptPath, 1, null);
        } catch (Exception e) {
            throw new RuntimeException("Error opening browser", e);
        }
        
        if (result == null) {
            throw new RuntimeException("You need to return either WebDriver either Browser instance from script");
        }
        else {

            if (result instanceof NativeJavaObject) {
                NativeJavaObject nativeJavaObject = (NativeJavaObject)result;
                Object object = nativeJavaObject.unwrap();
                
                if (object instanceof WebDriver) {
                    return new SeleniumBrowser((WebDriver)object);
                }
                else if (object instanceof Browser){
                    return (Browser)object;
                }
                else {
                    throw new RuntimeException("Expecting WebDriver or Browser but got " + object.getClass()); 
                }
            }
            else {
                throw new RuntimeException("Expecting NativeJavaObject but got " + result.getClass());
            }
        }
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("scriptPath", this.scriptPath)
            .append("args", this.args)
            .toString();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(this.scriptPath)
        .append(this.args)
        .toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JsBrowserFactory)) {
            return false;
        }
        
        JsBrowserFactory rhs = (JsBrowserFactory)obj;
        return new EqualsBuilder()
            .append(this.scriptPath, rhs.scriptPath)
            .append(this.args, rhs.args)
            .isEquals();
    }

}

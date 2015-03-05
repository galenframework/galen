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
package net.mindengine.galen.browser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import net.mindengine.galen.javascript.GalenJsExecutor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mozilla.javascript.NativeJavaObject;
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
        
        GalenJsExecutor js = new GalenJsExecutor();
        js.putObject("args", args);
        
        Reader scriptFileReader;
        Object result;
        try {
            scriptFileReader = new FileReader(file);
            result = js.eval(scriptFileReader, scriptPath);
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
        return new ToStringBuilder(this) //@formatter:off
            .append("scriptPath", this.scriptPath)
            .append("args", this.args)
            .toString(); //@formatter:on
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder() //@formatter:off
        .append(this.scriptPath)
        .append(this.args)
        .toHashCode(); //@formatter:on
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
        return new EqualsBuilder() //@formatter:off
            .append(this.scriptPath, rhs.scriptPath)
            .append(this.args, rhs.args)
            .isEquals(); //@formatter:on
    }

}

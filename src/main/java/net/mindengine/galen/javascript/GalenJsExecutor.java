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
package net.mindengine.galen.javascript;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import net.mindengine.galen.browser.WebDriverWrapper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class GalenJsExecutor {
    
    private Context context;
    private ImporterTopLevel scope;
    private ScriptExecutor scriptExecutor;

    public GalenJsExecutor() {
        this.context = Context.enter();
        this.scope = new ImporterTopLevel(context);
        
        this.scriptExecutor = new ScriptExecutor();
        scope.defineProperty("load", scriptExecutor, ScriptableObject.DONTENUM);
        importAllMajorClasses();
    }

    private void importAllMajorClasses() {
        importClasses(new Class[]{
                Thread.class,
                WebDriverWrapper.class,
                By.class,
                WebElement.class,
                WebDriver.class,
                System.class,
                Actions.class
        });
    }
    
    private void importClasses(Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            context.evaluateString(scope, "importClass(" + clazz.getName() + ");", "<cmd>", 1, null);
        }
    }


    public void putObject(String name, Object object) {
        ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
    }

    public Object eval(String jsCode) {
        return context.evaluateString(scope, jsCode, "<cmd>", 1, null);
    }

    public Object eval(Reader scriptFileReader, String javascriptPath) throws IOException {
        File file = new File(javascriptPath);
        scriptExecutor.putContextPath(file.getParent());
        return context.evaluateReader(scope, scriptFileReader, javascriptPath, 1, null);
    }

}

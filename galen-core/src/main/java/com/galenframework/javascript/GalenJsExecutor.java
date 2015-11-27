/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.javascript;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import com.galenframework.api.Galen;
import com.galenframework.api.GalenPageDump;
import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.runner.events.TestSuiteEvent;
import com.galenframework.tests.GalenTest;
import com.galenframework.tests.TestSession;
import com.galenframework.utils.GalenUtils;
import com.galenframework.parser.VarsParserJsProcessable;
import com.galenframework.runner.events.TestEvent;
import com.galenframework.runner.events.TestRetryEvent;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GalenJsExecutor implements VarsParserJsProcessable {
    private final static Logger LOG = LoggerFactory.getLogger(GalenJsExecutor.class);

    private Context context;
    private ImporterTopLevel scope;
    private JsFunctionLoad loadFunction;

    public GalenJsExecutor() {
        this.context = Context.enter();
        this.scope = new ImporterTopLevel(context);
        
        this.loadFunction = new JsFunctionLoad();
        scope.defineProperty("load", loadFunction, ScriptableObject.DONTENUM);
        importAllMajorClasses();
    }

    private void importAllMajorClasses() {
        importClasses(new Class[]{
                Thread.class,
                By.class,
                WebElement.class,
                WebDriver.class,
                System.class,
                Actions.class,
                GalenTest.class,
                TestSession.class,
                GalenUtils.class,
                GalenJsApi.class,
                TestEvent.class,
                TestSuiteEvent.class,
                TestFilterEvent.class,
                TestRetryEvent.class,
                Galen.class,
                GalenPageDump.class
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
        loadFunction.putContextPath(file.getParent());
        return context.evaluateReader(scope, scriptFileReader, javascriptPath, 1, null);
    }


    /**
     * Used for processing js expressions in page spec reader. In case of failure in script returns null
     * @param script - JavaScript code
     * @return result of JavaScript code execution
     */
    @Override
    public String evalSafeToString(String script) {
        try {
            Object returnedObject = context.evaluateString(scope, script, "<cmd>", 1, null);
            return unwrapProcessedObjectToString(returnedObject);
        }
        catch (Exception ex) {
            LOG.error("Unknown error during processing javascript expressions.", ex);
            return null;
        }
    }

    private String unwrapProcessedObjectToString(Object returnedObject) {
        if (returnedObject != null) {
            if (returnedObject instanceof NativeJavaObject) {
                returnedObject = ((NativeJavaObject) returnedObject).unwrap();
            }

            if (returnedObject instanceof Double) {
                return Integer.toString(((Double) returnedObject).intValue());
            } else if (returnedObject instanceof Float) {
                return Integer.toString(((Float) returnedObject).intValue());
            } else return returnedObject.toString();
        } else return null;
    }

    /**
     * Used for processing js expressions in page spec reader. In case of failure throws an exception
     * @param script - JavaScript code
     * @return result of JavaScript code execution
     */
    @Override
    public String evalStrictToString(String script) {
        Object returnedObject = context.evaluateString(scope, script, "<cmd>", 1, null);
        String unwrappedObject = unwrapProcessedObjectToString(returnedObject);

        if (unwrappedObject != null) {
            return unwrappedObject;
        } else return "null";
    }

    public static String loadJsFromLibrary(String path) {
        try {
            InputStream is = GalenJsExecutor.class.getResourceAsStream("/js/" + path);
            return  IOUtils.toString(is);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public static String getVersion() {
        return ContextFactory.getGlobal().enterContext().getImplementationVersion();
    }

    public void runJavaScriptFromFile(String scriptPath) {
        loadFunction.load(scriptPath, context, scope);
    }

    public void evalScriptFromLibrary(String libraryName) {
        eval(loadJsFromLibrary(libraryName));
    }

    public ImporterTopLevel getScope() {
        return scope;
    }
}

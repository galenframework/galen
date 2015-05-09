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
package net.mindengine.galen.parser;

import net.mindengine.galen.javascript.JsFunctionLoad;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.suite.reader.Context;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class VarsParserJsProcessor implements VarsParserJsProcessable {

    private final static Logger LOG = LoggerFactory.getLogger(VarsParserJsProcessor.class);

    private final VarsParserJsFunctions jsFunctions;

    private Context varsContext;
    private org.mozilla.javascript.Context cx;
    private ImporterTopLevel scope;
    private JsFunctionLoad jsFunctionLoad = new JsFunctionLoad();
    private final PageSpecReader pageSpecReader;

    public VarsParserJsProcessor(Context varsContext, VarsParserJsFunctions jsFunctions, PageSpecReader pageSpecReader) {
        this.varsContext = varsContext;
        this.jsFunctions = jsFunctions;
        this.pageSpecReader = pageSpecReader;
        initJsProcessor();
    }

    @SuppressWarnings("serial")
    private void initJsProcessor() {
        this.cx = org.mozilla.javascript.Context.enter();
        this.scope = new ImporterTopLevel(cx);

        ScriptableObject.putProperty(scope, "_pageSpec", org.mozilla.javascript.Context.javaToJS(pageSpecReader, scope));
        executeScript(readScriptFromResources(("/js/GalenSpecProcessing.js")));

        if (jsFunctions != null) {
            scope.defineProperty("count", new BaseFunction() {
                @Override
                public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    if (args.length == 0 || !(args[0] instanceof String)) {
                        throw new IllegalArgumentException("Should take string argument");
                    }
                    return jsFunctions.count((String)args[0]);
                }
            }, ScriptableObject.DONTENUM);

            scope.defineProperty("find", new BaseFunction() {
                @Override
                public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    String pattern = null;

                    if (args.length == 0) {
                        throw new IllegalArgumentException("Should take one string argument, got none");
                    } else if (args[0] == null) {
                        throw new IllegalArgumentException("Pattern should not be null");
                    } else if (args[0] instanceof NativeJavaObject) {
                        NativeJavaObject njo = (NativeJavaObject)args[0];
                        pattern = (String) njo.unwrap();
                    } else {
                        pattern = (String)args[0];
                    }
                    return jsFunctions.find(pattern);
                }
            }, ScriptableObject.DONTENUM);

            scope.defineProperty("findAll", new BaseFunction() {
                @Override
                public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    String pattern = null;

                    if (args.length == 0) {
                        throw new IllegalArgumentException("Should take one string argument, got none");
                    } else if (args[0] == null) {
                        throw new IllegalArgumentException("Pattern should not be null");
                    } else if (args[0] instanceof NativeJavaObject) {
                        NativeJavaObject njo = (NativeJavaObject)args[0];
                        pattern = (String) njo.unwrap();
                    } else {
                        pattern = (String)args[0];
                    }

                    return jsFunctions.findAll(pattern);
                }
            }, ScriptableObject.DONTENUM);

            scope.defineProperty("load", new JsFunctionLoad(), ScriptableObject.DONTENUM);
        }

    }

    private String readScriptFromResources(String path) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String executeScript(String script) {
        try {
            Object returnedObject = cx.evaluateString(scope, script, "<cmd>", 1, null);
            if (returnedObject != null) {
                if (returnedObject instanceof Double) {
                    return Integer.toString(((Double) returnedObject).intValue());
                } else if (returnedObject instanceof Float) {
                    return Integer.toString(((Float) returnedObject).intValue());
                } else return returnedObject.toString();
            } else return null;
        }
        catch (Exception ex) {
            LOG.error("Unknown error during processing javascript expressions.", ex);
            return null;
        }
    }

    @Override
    public String evalSafeToString(String expression) {
        resetAllVariablesFromContext();
        return executeScript(expression);
    }

    private void resetAllVariablesFromContext() {
        if (varsContext != null) {
            for (Map.Entry<String, Object> parameter : varsContext.getParameters().entrySet()) {
                if (!conflictsWithFunctionNames(parameter.getKey())) {
                    ScriptableObject.putProperty(scope, parameter.getKey(), parameter.getValue());
                }
            }
        }
    }

    private boolean conflictsWithFunctionNames(String name) {
        if (name.equals("count")) {
            return true;
        }
        else if (name.equals("find")) {
            return true;
        }
        else if (name.equals("load")) {
            return true;
        }
        return false;
    }
    public Context getVarsContext() {
        return varsContext;
    }
    public void setVarsContext(Context varsContext) {
        this.varsContext = varsContext;
    }

    public void runJavascriptFromFile(String filePath, String contextPath) {
        String fullPath = filePath;
        if (!filePath.startsWith("/") && contextPath!= null && !contextPath.isEmpty()) {
            fullPath = contextPath + File.separator + filePath;
        }

        jsFunctionLoad.load(fullPath, this.cx, this.scope);
    }
}

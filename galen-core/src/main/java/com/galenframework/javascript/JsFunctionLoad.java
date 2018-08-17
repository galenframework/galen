/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.galenframework.utils.GalenUtils;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsFunctionLoad extends BaseFunction {

    private final static Logger LOG = LoggerFactory.getLogger(JsFunctionLoad.class);

    private static final long serialVersionUID = 1L;

    private Stack<String> contextPathStack = new Stack<>();

    private Set<String> loadedFileIds = new HashSet<>();

    public JsFunctionLoad() {
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0) {
            throw new RuntimeException("'load' function takes at least one argument");
        }

        for (Object arg : args) {
            if (arg instanceof NativeArray) {
                NativeArray array = (NativeArray)arg;
                for (int i = 0; i < array.getLength(); i++) {
                    Object path = array.get(i);
                    if (path != null) {
                        load(path.toString(), cx, scope);
                    } else {
                        throw new NullPointerException("Cannot have null argument in load function");
                    }
                }
            } else if (arg == null) {
                throw new NullPointerException("Cannot have null argument in load function");
            } else {
                load(arg.toString(), cx, scope);
            }
        }
        return null;
    }

    public void load(String filePath, Context cx, Scriptable scope) {
        try {
            String fullPath = constructFullPathToScript(filePath);
            loadScript(cx, scope, fullPath);
        } catch (Exception ex) {
            throw new RuntimeException("Could not load script: " + filePath, ex);
        }
    }

    private String constructFullPathToScript(String filePath) {
        if (filePath.startsWith("/")) {
            /*
             * In case load function is called with leading slash - it means that Galen should search for script from root
             * folder of the project first and only then load it as absolute path
             */
            String localPath = filePath.substring(1);
            if (new File(localPath).exists()) {
                return localPath;
            }
        } else {
            String contextPath = peekContextPathStack();
            if (contextPath != null && ! contextPath.isEmpty()) {
                return contextPath + File.separator + filePath;
            }
        }
        return filePath;
    }

    private String peekContextPathStack() {
        if (!contextPathStack.isEmpty()) {
            return contextPathStack.peek();
        }
        return null;
    }

    private void loadScript(Context cx, Scriptable scope, String fullPath) throws IOException {
        InputStream is = retrieveScriptAsInputStream(fullPath);

        String fileId = GalenUtils.calculateFileId(fullPath, is);

        if (!loadedFileIds.contains(fileId)) {

            File file = new File(fullPath);
            String parentPath = file.getParent();
            if (parentPath != null) {
                contextPathStack.push(file.getParent());
            }

            cx.evaluateReader(scope, new InputStreamReader(is), file.getAbsolutePath(), 1, null);
            loadedFileIds.add(fileId);

            if (!contextPathStack.isEmpty()) {
                contextPathStack.pop();
            }
        }
    }

    private InputStream retrieveScriptAsInputStream(String fullPath) throws FileNotFoundException {
        InputStream is = GalenUtils.findFileOrResourceAsStream(fullPath);
        if (is == null) {
            throw new FileNotFoundException(fullPath);
        }
        return is;
    }

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        LOG.info(message);
    }

    public void putContextPath(String contextPath) {
        if (contextPath != null) {
            contextPathStack.push(contextPath);
        }
    }

}

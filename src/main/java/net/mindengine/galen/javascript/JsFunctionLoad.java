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
package net.mindengine.galen.javascript;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.mindengine.galen.utils.GalenUtils;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.log4testng.Logger;

public class JsFunctionLoad extends BaseFunction {

  private final static Logger LOG = Logger.getLogger(JsFunctionLoad.class);

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private Stack<String> contextPathStack = new Stack<String>();

  private Set<String> loadedFileIds = new HashSet<String>();

  public JsFunctionLoad() {
  }

  @Override
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (args.length == 0) {
      throw new RuntimeException("'load' function takes at least one argument");
    }

    for (Object arg : args) {
      if (arg instanceof String) {
        load((String) arg, cx, scope);
      } else {
        throw new RuntimeException("'load' function takes only string arguments but got: " + arg.getClass());
      }
    }
    return null;
  }

  public void load(String filePath, Context cx, Scriptable scope) {
    String contextPath = ".";

    if (!contextPathStack.isEmpty()) {
      contextPath = contextPathStack.peek();
    }

    String fullPath = filePath;

    try {
      if (filePath.startsWith("/")) {
        /*
         * In case load function is called with leading slash - it means that Galen should search for script from root
         * folder of the project
         */
        fullPath = filePath.substring(1);
      } else {
        fullPath = contextPath + File.separator + filePath;
      }

      String fileId = GalenUtils.calculateFileId(fullPath);

      if (!loadedFileIds.contains(fileId)) {

        File file = new File(fullPath);
        String parentPath = file.getParent();
        if (parentPath != null) {
          contextPathStack.push(file.getParent());
        }

        InputStream is = GalenUtils.findFileOrResourceAsStream(fullPath);

        cx.evaluateReader(scope, new InputStreamReader(is), file.getAbsolutePath(), 1, null);
        loadedFileIds.add(fileId);

        if (!contextPathStack.isEmpty()) {
          contextPathStack.pop();
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException("Could not load script: " + fullPath, ex);
    }
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

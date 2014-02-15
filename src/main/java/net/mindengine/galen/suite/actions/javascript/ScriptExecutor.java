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
package net.mindengine.galen.suite.actions.javascript;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class ScriptExecutor {
    
    private String contextFolder;
    
    private Set<String> loadedFiles = new HashSet<String>();

    private ScriptableObject scope;
    private Context cx;

    public ScriptExecutor(ScriptableObject scope, Context cx, String contextFolder) {
        this.scope = scope;
        this.cx = cx;
        
        if (contextFolder != null && !contextFolder.isEmpty()) {
            this.contextFolder = contextFolder;
        }
        else { 
            this.contextFolder = ".";
        }
    }

    public void load(String filePath) throws IOException {
        File file = new File(contextFolder + File.separator + filePath);
        String absolutePath = file.getAbsolutePath();
        
        if (!loadedFiles.contains(absolutePath)) {
            cx.evaluateReader(scope, new FileReader(file), file.getAbsolutePath(), 1, null);
            loadedFiles.add(absolutePath);
        }
    }
    
    public void print(String message) {
        System.out.print(message);
    }
    
    public void println(String message) {
        System.out.println(message);
    }

}

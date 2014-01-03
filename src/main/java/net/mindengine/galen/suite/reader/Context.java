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
package net.mindengine.galen.suite.reader;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    private Map<String, Object> parameters = new HashMap<String, Object>(); 

    public Context withParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Object getValue(String paramName) {
        return parameters.get(paramName);
    }
    
    public void putValue(String name, Object value) {
        parameters.put(name, value);
    }

    public boolean containsValue(String paramName) {
        return parameters.containsKey(paramName);
    }

    
    public void addValuesFromMap(Map<String, String> map) {
        parameters.putAll(map);
    }
}

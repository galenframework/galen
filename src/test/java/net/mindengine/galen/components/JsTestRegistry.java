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
package net.mindengine.galen.components;

import java.util.LinkedList;
import java.util.List;

public class JsTestRegistry {

    private static final JsTestRegistry _instance = new JsTestRegistry();
    
    public static JsTestRegistry get() {
        return _instance;
    }

    private List<String> events = new LinkedList<String>();
    
    public void registerEvent(String name) {
        System.out.println(name);
        this.events.add(name);
    }
    
    public List<String> getEvents() {
        return this.events;
    }
    
    public void clear() {
        events.clear();
    }
    
}

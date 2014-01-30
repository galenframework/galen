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
package net.mindengine.galen.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GalenCommandLineParser {

    public GalenCommand parse(String[] args) {
        int i = 0;
        
        List<String> leftovers = new LinkedList<String>();
        Map<String, String> parameters = new HashMap<String, String>();
        
        
        while(i<args.length) {
            if (args[i].startsWith("--")) {
                if (i < args.length - 1) {
                    parameters.put(args[i].substring(2), args[i+1]);
                    i++;
                }
            }
            else {
                leftovers.add(args[i]);
            }
            i++;
        }
        
        return new GalenCommand(leftovers, parameters);
    }

}

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
package net.mindengine.galen.validation.specs;

import net.mindengine.galen.parser.SyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan Shubin 2014/11/09.
 */
public abstract class TextOperation {
    public abstract String apply(String text);


    private final static Map<String, TextOperation> _operations = new HashMap<String, TextOperation>(){{
        put("lowercase", new TextOperation() {
            @Override
            public String apply(String text) {
                return text.toLowerCase();
            }
        });

        put("uppercase", new TextOperation() {
            @Override
            public String apply(String text) {
                return text.toUpperCase();
            }
        });
    }};

    public static TextOperation find(String operation) {
        if (_operations.containsKey(operation)) {
            return _operations.get(operation);
        }
        else throw new SyntaxException("Unsupported text operation: " + operation);
    }
}


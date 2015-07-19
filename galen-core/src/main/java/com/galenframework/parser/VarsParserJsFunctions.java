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
package com.galenframework.parser;


public interface VarsParserJsFunctions {
    
    /**
     * Counts the amount of object matching regex
     * @param regex
     * @return
     */
    int count(String regex);

    /**
     * Finds page element with given name on page
     * @param name
     * @return
     */
    JsPageElement find(String name);


    /**
     * Finds all page elements matching given regex
     * @param regex - simple galen regex
     * @return
     */
    JsPageElement[] findAll(String regex);
}

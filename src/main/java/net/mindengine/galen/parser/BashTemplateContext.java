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

import net.mindengine.galen.suite.reader.Context;

public class BashTemplateContext extends Context {

    
    private BashTemplateContext parent;

    public BashTemplateContext(BashTemplateContext context) {
        this.parent = context;
    }

    public BashTemplateContext() {
    }

    public String process(String arguments) {
        return new BashTemplate(arguments).process(this);
    }


    @Override
    public Object getValue(String paramName) {
        if (super.containsValue(paramName)) {
            return super.getValue(paramName);
        }
        else if (parent != null) {
            return parent.getValue(paramName);
        }
        else {
            return null;
        }
    }

}

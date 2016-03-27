/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import java.util.Properties;

import com.galenframework.suite.reader.Context;

public class VarsContext extends Context {

    private VarsParser varsParser;
    private Properties properties;
    private VarsContext parent;

    public VarsContext(Properties properties) {
        this.properties = properties;
        this.varsParser = new VarsParser(this, properties);
    }

    public VarsContext(Properties properties, VarsContext parentContext) {
        this.parent = parentContext;
        this.properties = properties;
        this.varsParser = new VarsParser(this, properties);
    }


    public String process(String arguments) {
        return varsParser.parse(arguments);
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
    public Properties getProperties() {
        return properties;
    }
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public VarsContext copy() {
        return new VarsContext(this.properties, this);
    }

    public void setProperty(String name, String value) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.setProperty(name, value);
    }
}

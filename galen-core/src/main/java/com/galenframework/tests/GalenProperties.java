/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://galenframework.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.galenframework.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GalenProperties {

    Properties properties = new Properties();

    public void load(FileReader fileReader) throws IOException {
        properties.load(fileReader);
    }

    public void load(File file) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            this.load(fileReader);
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String get(String name) {
        String value = properties.getProperty(name);

        if (value == null) {
            return System.getProperty(name);
        } else return value;
    }

    public String get(String name, String defaultValue) {
        String value = get(name);
        if (value != null) {
            return value;
        } else return defaultValue;
    }

    public void set(String name, String value) {
        this.properties.setProperty(name, value);
    }

}

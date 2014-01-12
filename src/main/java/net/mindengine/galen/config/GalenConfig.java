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
package net.mindengine.galen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class GalenConfig {
    
    private final static GalenConfig instance = new GalenConfig();
    private int rangeApproximation;
    private List<String> reportingListeners;
    private String defaultBrowser;
    private Properties properties;
    
    private GalenConfig() {
        try {
            loadConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadConfig() throws IOException {
        this.properties = new Properties();
        File configFile = new File("config");
        
        if (configFile.exists()) {
            InputStream in = new FileInputStream(configFile);
            properties.load(in);
            in.close();
        }
        
        rangeApproximation = Integer.parseInt(readProperty(properties, "galen.range.approximation", "2"));
        reportingListeners = converCommaSeparatedList(readProperty(properties, "galen.reporting.listeners", ""));
        defaultBrowser = readProperty(properties, "galen.default.browser", "firefox");
    }

    private List<String> converCommaSeparatedList(String text) {
        String[] arr = text.split(",");
        
        List<String> list = new LinkedList<String>();
        for (String item : arr) {
            String itemText = item.trim();
            if (!itemText.isEmpty()) {
                list.add(itemText);
            }
        }
        return list;
    }

    private String readProperty(Properties prop, String name, String defaultValue) {
        return prop.getProperty(name, System.getProperty(name, defaultValue));
    }

    public synchronized static GalenConfig getConfig() {
        return instance;
    }
    
    public void reset() throws IOException {
        loadConfig();
    }

    public int getRangeApproximation() {
        return this.rangeApproximation;
    }

    public List<String> getReportingListeners() {
        return this.reportingListeners;
    }

    public String getDefaultBrowser() {
        return defaultBrowser;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
    
    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public Integer getIntProperty(String name, int defaultValue) {
        String value = properties.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        else {
            try {
                return Integer.parseInt(value);
            }
            catch (Exception e) {
                throw new RuntimeException(String.format("Couldn't parse property \"%s\" from config file", name));
            }
        }
    }

    public int getIntProperty(String name, int defaultValue, int min, int max) {
        int value = getIntProperty(name, defaultValue);
        if (value >= min && value <=max) {
            return value;
        }
        else {
            throw new RuntimeException(String.format("Property \"%s\"=%d in config file is not in allowed range [%d, %d]", name, value, min, max));
        }
    }

}

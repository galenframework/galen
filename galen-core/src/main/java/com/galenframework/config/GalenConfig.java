/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.galenframework.specs.SpecImage;
import com.galenframework.utils.GalenUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalenConfig {

    private final static Logger LOG = LoggerFactory.getLogger(GalenConfig.class);


    public final static GalenConfig instance = new GalenConfig();
    public static final String GALEN_USER_HOME_CONFIG_NAME = ".galen.config";
    private Properties properties;
    
    private GalenConfig() {
        try {
            loadConfig();
        }
        catch (Exception e) {
            LOG.trace("Cannot load galen config", e);
        }
    }


    public Properties getProperties() {
        return properties;
    }
    
    private void loadConfig() throws IOException {
        this.properties = new Properties();
        loadConfigFromUserHome();
        loadConfigFromLocal();
    }

    private void loadConfigFromUserHome() throws IOException {
        InputStream stream = GalenUtils.findFileOrResourceAsStream(System.getProperty("user.home") + "/" + GALEN_USER_HOME_CONFIG_NAME);
        if (stream != null) {
            loadFromStream(stream);
        }
    }

    private void loadConfigFromLocal() throws IOException {
        InputStream stream = GalenUtils.findFileOrResourceAsStream(readProperty(GalenProperty.GALEN_CONFIG_FILE));

        if (stream == null) {
            //fallback to old way of config name
            stream = GalenUtils.findFileOrResourceAsStream("config");
        }

        loadFromStream(stream);
    }

    private void loadFromStream(InputStream stream) throws IOException {
        if (stream != null) {
            properties.load(stream);
            stream.close();
        }
        setSystemPropertiesFromConfig();
    }

    private void setSystemPropertiesFromConfig() {
        Enumeration<?> names = properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            if (name.startsWith("$.")) {
                System.setProperty(name.substring(2), properties.getProperty(name));
            }
        }
    }

    public static void reloadConfigFromPath(String configPath) throws IOException {
        getConfig().reloadConfig(configPath);
    }

    private void reloadConfig(String configPath) throws IOException {
        this.properties = new Properties();
        InputStream stream = GalenUtils.findFileOrResourceAsStream(configPath);
        loadFromStream(stream);
    }

    private List<String> convertCommaSeparatedList(String text) {
        String[] arr = text.split(",");
        
        List<String> list = new LinkedList<>();
        for (String item : arr) {
            String itemText = item.trim();
            if (!itemText.isEmpty()) {
                list.add(itemText);
            }
        }
        return list;
    }

    public String readProperty(GalenProperty property) {
        return properties.getProperty(property.propertyName,
                System.getProperty(property.propertyName, property.defaultValue));
    }
    
    public String readMandatoryProperty(GalenProperty property) {
        String value = properties.getProperty(property.propertyName, System.getProperty(property.propertyName));

        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Missing property: " + property.propertyName);
        }
        return value;
    }


    public synchronized static GalenConfig getConfig() {
        return instance;
    }
    
    public void reset() throws IOException {
        loadConfig();
    }

    public int getRangeApproximation() {
        return Integer.parseInt(readProperty(GalenProperty.GALEN_RANGE_APPROXIMATION));
    }

    public List<String> getReportingListeners() {
        return convertCommaSeparatedList(readProperty(GalenProperty.GALEN_REPORTING_LISTENERS));
    }

    public String getDefaultBrowser() {
        return readProperty(GalenProperty.GALEN_DEFAULT_BROWSER);
    }

    public Integer getIntProperty(GalenProperty property) {
        String value = readProperty(property);
        try {
            return Integer.parseInt(value);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Couldn't parse property \"%s\" from config file", property.propertyName));
        }
    }

    
    public int getIntProperty(GalenProperty property, int min, int max) {
        int value = getIntProperty(property);
        if (value >= min && value <=max) {
            return value;
        } else {
            throw new RuntimeException(String.format("Property \"%s\"=%d in config file is not in allowed range [%d, %d]",
                    property.propertyName, value, min, max));
        }
    }

    public boolean getBooleanProperty(GalenProperty property) {
        String value = readProperty(property);
        return Boolean.parseBoolean(value);
    }

    public int getLogLevel() {
        String value = readProperty(GalenProperty.GALEN_LOG_LEVEL);
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        }
        else return 10;
    }

    public boolean shouldRunInHeadlessMode() {
        return getBooleanProperty(GalenProperty.GALEN_BROWSER_HEADLESS);
    }
    
    public boolean getUseFailExitCode() {
        return getBooleanProperty(GalenProperty.GALEN_USE_FAIL_EXIT_CODE);
    }

    public String getTestJsSuffix() {
        return readProperty(GalenProperty.TEST_JS_SUFFIX);
    }

    public boolean shouldAutoresizeScreenshots() {
        return getBooleanProperty(GalenProperty.SCREENSHOT_AUTORESIZE);
    }

    public boolean shouldCheckVisibilityGlobally() {
        return getBooleanProperty(GalenProperty.SPEC_GLOBAL_VISIBILITY_CHECK);
    }

    public int getImageSpecDefaultTolerance() {
        return getIntProperty(GalenProperty.SPEC_IMAGE_TOLERANCE);
    }

    public SpecImage.ErrorRate getImageSpecDefaultErrorRate() {
        return SpecImage.ErrorRate.fromString(readProperty(GalenProperty.SPEC_IMAGE_ERROR_RATE));
    }

    public void setProperty(GalenProperty property, String value) {
        properties.setProperty(property.propertyName, value);
    }

    public String getTestSuffix() {
        return readProperty(GalenProperty.TEST_SUFFIX);
    }

    public String getStringProperty(GalenProperty property) {
        return readProperty(property);
    }

}

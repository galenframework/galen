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
package com.galenframework.tests.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import com.galenframework.actions.GalenAction;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.GalenMain;
import com.galenframework.components.DummyCompleteListener;
import com.galenframework.config.GalenConfig;
import com.galenframework.parser.Expectations;
import com.galenframework.runner.CompleteListener;
import com.galenframework.specs.Range;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class GalenConfigTest {

    @AfterMethod
    public void resetConfigToDefault() throws IOException {
        deleteSystemProperty("galen.range.approximation");
        deleteSystemProperty("galen.reporting.listeners");
        deleteSystemProperty("galen.config.file");
        GalenConfig.getConfig().reset();
    }
    
    @Test public void shouldRead_configForLocalProject_fromFile() throws IOException {
        //copying file from resources to local folder
        //so it can be picked up by
        
        File configFile = new File("config");
        configFile.createNewFile();
        FileUtils.copyFile(new File(getClass().getResource("/config1").getFile()), configFile);
        
        GalenConfig config = GalenConfig.getConfig();
        config.reset();
        
        configFile.delete();
        
        MatcherAssert.assertThat(config.getRangeApproximation(), is(3));
        assertThat(config.getReportingListeners(), Matchers.contains("net.mindengine.CustomListener", "net.mindengine.CustomListener2"));
        config.reset();
    }

    @Test public void shouldRead_configFile_fromSpecifiedLocation() throws IOException {

        File configFile = new File("config2");
        configFile.createNewFile();
        FileUtils.copyFile(new File(getClass().getResource("/config2").getFile()), configFile);

        System.setProperty("galen.config.file", "config2");

        GalenConfig config = GalenConfig.getConfig();
        config.reset();

        MatcherAssert.assertThat(config.getRangeApproximation(), is(12345));
    }
    
    @Test public void shouldRead_configForLocalProject_fromSystemProperties() throws IOException {
        GalenConfig config = GalenConfig.getConfig();
        System.setProperty("galen.range.approximation", "5");
        System.setProperty("galen.reporting.listeners", "net.mindengine.system.CustomListener, net.mindengine.system.CustomListener2 ");
        
        config.reset();
        assertThat(config.getRangeApproximation(), is(5));
        assertThat(config.getReportingListeners(), Matchers.contains("net.mindengine.system.CustomListener", "net.mindengine.system.CustomListener2"));
        
        deleteSystemProperty("galen.range.approximation");
        deleteSystemProperty("galen.reporting.listeners");
        config.reset();
    }
    
    
    @Test public void shouldUseDefaultValues_whenConfigIsNotSet() throws IOException {
        deleteSystemProperty("galen.range.approximation");
        deleteSystemProperty("galen.reporting.listeners");
        
        GalenConfig config = GalenConfig.getConfig();
        config.reset();
        assertThat(config.getRangeApproximation(), is(2));
        assertThat(config.getReportingListeners().size(), is(0));
        
    }
    
    
    @Test public void shouldUseConfig_forRangeReader() throws IOException {
        GalenConfig config = GalenConfig.getConfig();
        System.setProperty("galen.range.approximation", "5");
        config.reset();
        
        StringCharReader reader = new StringCharReader("~ 20 px");
        Range range = Expectations.range().read(reader);
        
        assertThat(range, is(Range.between(15, 25)));
        
        deleteSystemProperty("galen.range.approximation");
    }

    @Test public void shouldUseConfig_forReportingListeners() throws IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        GalenConfig config = GalenConfig.getConfig();
        System.setProperty("galen.reporting.listeners", DummyCompleteListener.class.getName());
        config.reset();
        
        
        List<CompleteListener> listeners = GalenAction.getConfiguredListeners();
        assertThat(listeners, is(notNullValue()));
        assertThat(listeners.size(), is(1));
        assertThat(listeners.get(0), is(instanceOf(DummyCompleteListener.class)));
        
        deleteSystemProperty("galen.reporting.listeners");
    }
    
    private void deleteSystemProperty(String key) {
        Properties properties = System.getProperties();
        if (properties.containsKey(key)) {
            properties.remove(key);
        }
    }
}

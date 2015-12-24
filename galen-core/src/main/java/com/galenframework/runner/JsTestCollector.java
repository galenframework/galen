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
package com.galenframework.runner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.runner.events.TestSuiteEvent;
import com.galenframework.tests.GalenTest;
import com.galenframework.runner.events.TestFilterEvent;
import com.galenframework.runner.events.TestRetryEvent;

import com.galenframework.javascript.GalenJsExecutor;
import com.galenframework.runner.events.TestEvent;
import com.galenframework.runner.events.TestSuiteEvent;
import com.galenframework.tests.GalenTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsTestCollector {

    private final static Logger LOG = LoggerFactory.getLogger(JsTestCollector.class);
    private List<GalenTest> collectedTests = new LinkedList<>();
    private EventHandler eventHandler = new EventHandler();
    
    private GalenJsExecutor js = createExecutor();
    

    public JsTestCollector(List<GalenTest> tests) {
        this.collectedTests = tests;
    }

    private GalenJsExecutor createExecutor() {
        GalenJsExecutor jsExector = new GalenJsExecutor();
        jsExector.putObject("_galenCore", this);
        
        jsExector.eval(GalenJsExecutor.loadJsFromLibrary("GalenCore.js"));
        jsExector.eval(GalenJsExecutor.loadJsFromLibrary("GalenApi.js"));
        jsExector.eval(GalenJsExecutor.loadJsFromLibrary("GalenPages.js"));
        return jsExector;
    }

    public JsTestCollector() {
    }

    public void execute(File file) throws IOException {
        Reader scriptFileReader = null;
        try {
             scriptFileReader = new FileReader(file);
            js.eval(scriptFileReader, file.getAbsolutePath());
        } finally {
            if(scriptFileReader!=null) {
                try {
                    scriptFileReader.close();
                } catch (IOException e) {
                    LOG.error("Error during closing file reader", e);
                }
            }

        }
    }

    public void addTest(GalenTest test) {
        this.collectedTests.add(test);
    }
    
    public List<GalenTest> getCollectedTests() {
        return this.collectedTests;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    
    public void addBeforeTestSuiteEvent(TestSuiteEvent event) {
        eventHandler.getBeforeTestSuiteEvents().add(event);
    }
    
    public void addAfterTestSuiteEvent(TestSuiteEvent event) {
        eventHandler.getAfterTestSuiteEvents().add(event);
    }
    
    public void addBeforeTestEvent(TestEvent event) {
        eventHandler.getBeforeTestEvents().add(event);
    }
    
    public void addAfterTestEvent(TestEvent event) {
        eventHandler.getAfterTestEvents().add(event);
    }

    public void addTestFilterEvent(TestFilterEvent event) {
        eventHandler.getTestFilterEvents().add(event);
    }

    public void addTestRetryEvent(TestRetryEvent event) {
        eventHandler.getTestRetryEvents().add(event);
    }

}

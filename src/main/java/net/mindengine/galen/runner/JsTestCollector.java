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
package net.mindengine.galen.runner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.runner.events.TestFilterEvent;
import net.mindengine.galen.runner.events.TestRetryEvent;
import org.apache.commons.io.IOUtils;

import net.mindengine.galen.javascript.GalenJsExecutor;
import net.mindengine.galen.runner.events.TestEvent;
import net.mindengine.galen.runner.events.TestSuiteEvent;
import net.mindengine.galen.tests.GalenTest;

public class JsTestCollector {

    private List<GalenTest> collectedTests = new LinkedList<GalenTest>();
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
        Reader scriptFileReader = new FileReader(file);
        js.eval(scriptFileReader, file.getAbsolutePath());
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

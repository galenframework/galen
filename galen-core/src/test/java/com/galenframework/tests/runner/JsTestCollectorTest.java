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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.galenframework.components.JsTestRegistry;
import com.galenframework.runner.JsTestCollector;
import com.galenframework.tests.GalenTest;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class JsTestCollectorTest {


    private static final String TEST_DIR_PATH = "_test-js-multilevel";

    @BeforeClass
    public void init() throws IOException {
        FileUtils.forceMkdir(new File(TEST_DIR_PATH));
        FileUtils.copyFile(new File(getClass().getResource("/js-tests/multilevel/main2.test.js").getFile()), new File(TEST_DIR_PATH + File.separator + "main2.test.js"));
        FileUtils.copyFile(new File(getClass().getResource("/js-tests/multilevel/included.js").getFile()), new File(TEST_DIR_PATH + File.separator + "included.js"));
    }

    @AfterClass
    public void removeAllTempFiles() throws IOException {
        FileUtils.deleteDirectory(new File(TEST_DIR_PATH));
    }
    
    @Test
    public void shouldExecuteJavascript_andCollectTests() throws Exception {
        JsTestCollector testCollector = new JsTestCollector();
        
        JsTestRegistry.get().clear();
        testCollector.execute(new File(getClass().getResource("/js-tests/simple.test.js").getFile()));
        
        List<GalenTest> tests = testCollector.getCollectedTests();
        
        assertThat("Amount of tests should be", tests.size(), is(3));
        assertThat("Name of #1 test should be", tests.get(0).getName(), is("Test number 1"));
        assertThat("Name of #1 test should be", tests.get(1).getName(), is("Test number 2"));
        assertThat("Name of #1 test should be", tests.get(2).getName(), is("Test number 3"));

        tests.get(0).execute(null, null);
        tests.get(2).execute(null, null);
        
        assertThat("Events should be", JsTestRegistry.get().getEvents(), contains("Test #1 was invoked", "Test #3 was invoked"));
    }


    @Test
    public void shouldAllow_toUse_testFilter() throws IOException {
        JsTestCollector testCollector = new JsTestCollector();
        JsTestRegistry.get().clear();
        testCollector.execute(new File(getClass().getResource("/js-tests/testfilter.test.js").getFile()));

        List<GalenTest> tests = testCollector.getCollectedTests();
        assertThat(tests.get(0).getName(), is("Test A"));
        assertThat(tests.get(1).getName(), is("Test B"));
        assertThat(tests.get(2).getName(), is("Test C"));
        assertThat(tests.get(3).getName(), is("Test D"));
    }


    @Test
    public void shouldLoadOtherScripts_onlyOnce() throws IOException {
        JsTestCollector testCollector = new JsTestCollector();
        JsTestRegistry.get().clear();
        testCollector.execute(new File(getClass().getResource("/js-tests/multilevel/main.test.js").getFile()));
        testCollector.execute(new File(getClass().getResource("/js-tests/multilevel/folder/second.test.js").getFile()));


        List<String> events = JsTestRegistry.get().getEvents();
        assertThat("Events amount should be", events.size(), is(3));

        assertThat("Events should be", events, contains("included.js was loaded", "From main name is visible as Included object", "From second name is visible as Included object"));
    }


    @Test
    public void shouldLoadOtherScripts_fromRootProject_ifPathStartsWithSlash() throws IOException {
        JsTestCollector testCollector = new JsTestCollector();
        JsTestRegistry.get().clear();
        testCollector.execute(new File(new File(TEST_DIR_PATH) + File.separator + "main2.test.js"));

        List<String> events = JsTestRegistry.get().getEvents();
        assertThat("Events amount should be", events.size(), is(2));

        assertThat("Events should be", events, contains("included.js was loaded", "From main name is visible as Included object"));
    }


    @Test
    public void shouldAllow_toGroupTests() throws IOException {
        JsTestCollector testCollector = new JsTestCollector();
        JsTestRegistry.get().clear();
        testCollector.execute(new File(getClass().getResource("/js-tests/testgroups.test.js").getFile()));

        List<GalenTest> tests = testCollector.getCollectedTests();
        assertThat(tests.get(0).getName(), is("Test A"));
        assertThat(tests.get(0).getGroups(), contains("mobile"));

        assertThat(tests.get(1).getName(), is("Test B"));
        assertThat(tests.get(1).getGroups(), contains("mobile", "tablet", "desktop"));

        assertThat(tests.get(2).getName(), is("Test C"));
        assertThat(tests.get(2).getGroups(), contains("mobile", "tablet", "desktop"));

        assertThat(tests.get(3).getName(), is("Test D"));
    }
}

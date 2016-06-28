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
package com.galenframework.testng;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.support.GalenJavaTestBase;
import com.galenframework.support.GalenReportsContainer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * This class is used as a base test class for TestNG tests, see {@link GalenJavaTestBase}
 */
@Listeners(GalenTestNgReportsListener.class)
public abstract class GalenTestNgTestBase extends GalenJavaTestBase {

    /**
     * Initializes the TestReport instance with the name of current test method and stores it in {@link ThreadLocal}
     */
    @BeforeMethod(alwaysRun = true)
    public void initReport(Method method, Object[] arguments) {
        GalenTestInfo ti = createTestInfo(method, arguments);
        testInfo.set(ti);
        report.set(GalenReportsContainer.get().registerTest(ti));
    }

    /**
     * {@inheritDoc}
     */
    @BeforeMethod(alwaysRun = true)
    public void initDriver(Object[] args) {
        super.initDriver(args);
    }

    /**
     * {@inheritDoc}
     */
    @AfterMethod(alwaysRun = true)
    public void quitDriver() {
        super.quitDriver();
    }

    @AfterMethod
    public void provideTestEndDate() {
        GalenTestInfo ti = testInfo.get();
        if (ti != null) {
            ti.setEndedAt(new Date());
        }
    }

}


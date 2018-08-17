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
package com.galenframework.junit;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.support.GalenJavaTestBase;
import com.galenframework.support.GalenReportsContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Date;

import static java.lang.Thread.currentThread;

/**
 * This class is used as a base test class for Junit tests, see {@link GalenJavaTestBase}
 */
@RunWith(value = GalenReportTestRunner.class)
public abstract class GalenJUnitTestBase extends GalenJavaTestBase {

    /**
     * Initializes the TestReport instance with the name of current test method and stores it in {@link ThreadLocal}
     */
    @Before
    public void initReport() {
        GalenTestInfo ti = GalenTestInfo.fromString(getTestName());
        testInfo.set(ti);
        report.set(GalenReportsContainer.get().registerTest(ti));
    }

    public String getTestName() {
        return getCaller();
    }

    public abstract WebDriver createDriver();

    public WebDriver createDriver(Object[] args) {
        return createDriver();
    }


    /**
     * {@inheritDoc}
     */
    @Before
    public void initDriver() {
        super.initDriver(null);
    }

    /**
     * {@inheritDoc}
     */
    @After
    public void quitDriver() {
        super.quitDriver();
    }

    /**
     * {@inheritDoc}
     */
    @After
    public void provideTestEndDate() {
        GalenTestInfo ti = testInfo.get();
        if (ti != null) {
            ti.setEndedAt(new Date());
        }
    }


    private static String getCaller() {
        StackTraceElement[] elements = currentThread().getStackTrace();
        String callerMethodName = elements[3].getMethodName();
        String callerClassName = elements[3].getClassName();
        return callerClassName + "#>" + callerMethodName;
    }

}

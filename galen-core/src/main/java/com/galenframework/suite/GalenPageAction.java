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
package com.galenframework.suite;

import java.util.Properties;

import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.tests.TestSession;
import com.galenframework.validation.ValidationListener;
import com.galenframework.browser.Browser;
import com.galenframework.reports.TestReport;
import com.galenframework.tests.TestSession;
import com.galenframework.validation.ValidationListener;

public abstract class GalenPageAction {
    private String originalCommand;

    public abstract void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception;

    public String getOriginalCommand() {
        return originalCommand;
    }
    public void setOriginalCommand(String originalCommand) {
        this.originalCommand = originalCommand;
    }


    protected Properties getCurrentProperties() {
        TestSession session = TestSession.current();
        if (session != null) {
            if (session.getProperties() != null && session.getProperties().getProperties() != null) {
                return session.getProperties().getProperties();
            }
        }
        return new Properties();
    }
}

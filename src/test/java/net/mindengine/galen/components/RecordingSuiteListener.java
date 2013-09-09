/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.components;

import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.GalenSuiteRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;
import net.mindengine.galen.validation.ValidationError;

public class RecordingSuiteListener implements SuiteListener {

    StringBuffer recorded = new StringBuffer();
    
    public String getRecordedInvokations() {
        return recorded.toString();
    }

    @Override
    public void onAfterPage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser, List<ValidationError> errors) {
        record("<after-page errors=" + errors.size() + ">");
    }

    @Override
    public void onBeforePage(GalenSuiteRunner galenSuiteRunner, GalenPageTest pageTest, Browser browser) {
        record("<before-page>");
    }

    @Override
    public void onSuiteFinished(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        record("<suite-finished>");
    }

    @Override
    public void onSuiteStarted(GalenSuiteRunner galenSuiteRunner, GalenSuite suite) {
        record("<suite-started>");
    }

    private void record(String msg) {
        recorded.append(msg);
    }

}

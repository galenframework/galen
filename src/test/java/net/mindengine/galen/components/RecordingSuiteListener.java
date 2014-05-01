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
package net.mindengine.galen.components;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.runner.GalenBasicTestRunner;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.runner.SuiteListener;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.tests.GalenBasicTest;

public class RecordingSuiteListener implements SuiteListener {

    StringBuffer recorded = new StringBuffer();
    
    public String getRecordedInvokations() {
        return recorded.toString();
    }

    @Override
    public void onAfterPage(GalenBasicTestRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        record("<after-page>");
    }

    @Override
    public void onBeforePage(GalenBasicTestRunner galenSuiteRunner, GalenPageRunner pageRunner, GalenPageTest pageTest, Browser browser) {
        record("<before-page>");
    }

    @Override
    public void onSuiteFinished(GalenBasicTestRunner galenSuiteRunner, GalenBasicTest suite) {
        record("<suite-finished>");
    }

    @Override
    public void onSuiteStarted(GalenBasicTestRunner galenSuiteRunner, GalenBasicTest suite) {
        record("<suite-started>");
    }

    private void record(String msg) {
        recorded.append(msg);
    }

}

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
package net.mindengine.galen.suite;

import java.util.List;

import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionCookie;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionOpen;
import net.mindengine.galen.suite.actions.GalenPageActionResize;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

public class GalenPageActions {

    public static GalenPageActionInjectJavascript injectJavascript(String javascriptFilePath) {
        return new GalenPageActionInjectJavascript(javascriptFilePath);
    }

    public static GalenPageActionCheck check(List<String> specFilePaths) {
        return new GalenPageActionCheck().withSpecs(specFilePaths);
    }

    public static GalenPageActionRunJavascript runJavascript(String javascriptPath) {
        return new GalenPageActionRunJavascript(javascriptPath);
    }

    public static GalenPageActionOpen open(String url) {
        return new GalenPageActionOpen(url);
    }

    public static GalenPageAction resize(int width, int height) {
        return new GalenPageActionResize(width, height);
    }

    public static GalenPageAction cookie(String cookie) {
        return new GalenPageActionCookie().withCookies(cookie);
    }

}

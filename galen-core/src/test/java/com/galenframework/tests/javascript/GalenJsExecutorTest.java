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
package com.galenframework.tests.javascript;

import com.galenframework.components.JsTestRegistry;
import com.galenframework.javascript.GalenJsExecutor;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class GalenJsExecutorTest {

    @Test
    public void loadFunction_shouldAlsoTake_arrayOfStrings() {
        JsTestRegistry.get().clear();

        GalenJsExecutor js = new GalenJsExecutor();
        js.runJavaScriptFromFile("/javascript/load-array.js");

        assertThat(JsTestRegistry.get().getEvents(), contains("Loaded script from 1 file",
                "Loaded script from 2 file"));
    }


}

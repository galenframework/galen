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
package com.galenframework.actions;

import com.galenframework.javascript.GalenJsExecutor;
import com.galenframework.runner.CombinedListener;
import com.galenframework.utils.VersionUtil;
import org.openqa.selenium.By;

import java.io.PrintStream;

public class GalenActionVersion extends GalenAction {

    public GalenActionVersion(String[] arguments, PrintStream outStream, PrintStream errStream, CombinedListener listener) {
        super(arguments, outStream, errStream, listener);
    }

    @Override
    public void execute() {
        outStream.println("Galen Framework");
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "unknown";
        } else {
            version = version.replace("-SNAPSHOT", "");
        }
        outStream.println("Version: " + version);
        outStream.println("JavaScript executor: " + GalenJsExecutor.getVersion());
        outStream.println("Selenium version: " + VersionUtil.getVersion(By.class, "Selenium-Version"));

    }
}

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

import com.galenframework.GalenMain;
import com.galenframework.javascript.GalenJsExecutor;
import com.galenframework.runner.CombinedListener;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;

public class GalenActionVersion extends GalenAction {

    private final static Logger LOG = LoggerFactory.getLogger(GalenActionVersion.class);

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
        String seleniumVersion = "";
        try {
            seleniumVersion = IOUtils.toString(GalenMain.class.getResourceAsStream("/META-INF/maven/org.seleniumhq.selenium/selenium-java/pom.properties"));
        } catch (IOException e) {
            LOG.debug("Cannot read selenium version from classpath", e);
        }
        outStream.println("Selenium version: " + seleniumVersion);

    }
}

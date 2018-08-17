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
package com.galenframework.actions;

import com.galenframework.api.GalenPageDump;
import com.galenframework.browser.Browser;
import com.galenframework.browser.SeleniumBrowserFactory;
import com.galenframework.parser.SyntaxException;

import java.io.IOException;
import java.io.PrintStream;

public class GalenActionDump extends GalenAction {
    private final GalenActionDumpArguments dumpArguments;

    public GalenActionDump(String[] arguments, PrintStream outStream, PrintStream errStream) {
        super(arguments, outStream, errStream);
        this.dumpArguments = GalenActionDumpArguments.parse(arguments);
    }

    @Override
    public void execute() throws IOException {
        loadConfigIfNeeded(getDumpArguments().getConfig());

        SeleniumBrowserFactory browserFactory = new SeleniumBrowserFactory();
        Browser browser = browserFactory.openBrowser();

        try {
            if (dumpArguments.getUrl() == null || dumpArguments.getUrl().isEmpty()) {
                throw new SyntaxException("--url parameter is not defined");
            }
            if (dumpArguments.getPaths() == null || dumpArguments.getPaths().size() == 0) {
                throw new SyntaxException("You should specify a spec file with which you want to make a page dump");
            }
            if (dumpArguments.getExport() == null || dumpArguments.getExport().isEmpty()) {
                throw new SyntaxException("--export parameter is not defined");
            }

            if (dumpArguments.getScreenSize() != null) {
                browser.changeWindowSize(dumpArguments.getScreenSize());
            }

            browser.load(dumpArguments.getUrl());

            new GalenPageDump(dumpArguments.getUrl())
                    .setMaxWidth(dumpArguments.getMaxWidth())
                    .setMaxHeight(dumpArguments.getMaxHeight())
                    .dumpPage(browser, dumpArguments.getPaths().get(0), dumpArguments.getExport());

            outStream.println("Done!");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            browser.quit();
        }

    }

    public GalenActionDumpArguments getDumpArguments() {
        return dumpArguments;
    }
}

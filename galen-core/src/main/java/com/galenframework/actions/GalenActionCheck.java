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

import com.galenframework.browser.SeleniumBrowserFactory;
import com.galenframework.runner.CombinedListener;
import com.galenframework.runner.EventHandler;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionCheck;
import com.galenframework.tests.GalenBasicTest;
import com.galenframework.tests.GalenTest;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class GalenActionCheck extends GalenAction {
    private final GalenActionCheckArguments checkArguments;
    private final CombinedListener listener;

    public GalenActionCheck(String[] arguments, PrintStream outStream, PrintStream errStream, CombinedListener listener) {
        super(arguments, outStream, errStream, listener);
        this.checkArguments = GalenActionCheckArguments.parse(arguments);
        this.listener = createListeners(listener);
    }

    @Override
    public void execute() {
        verifyArgumentsForPageCheck();

        List<GalenTest> galenTests = new LinkedList<GalenTest>();

        for (String pageSpecPath : checkArguments.getPaths()) {
            GalenBasicTest test = new GalenBasicTest();
            test.setName(pageSpecPath);
            test.setPageTests(asList(new GalenPageTest()
                    .withTitle("Simple check")
                    .withUrl(checkArguments.getUrl())
                    .withSize(checkArguments.getScreenSize())
                    .withBrowserFactory(new SeleniumBrowserFactory())
                    .withActions(
                            asList((GalenPageAction) new GalenPageActionCheck().withSpec(pageSpecPath).withIncludedTags(checkArguments.getIncludedTags())
                                    .withExcludedTags(checkArguments.getExcludedTags()).withOriginalCommand(originalCommand(arguments))))));
            galenTests.add(test);
        }

        GalenActionTestArguments testArguments = new GalenActionTestArguments();
        testArguments.setHtmlReport(checkArguments.getHtmlReport());
        testArguments.setJsonReport(checkArguments.getJsonReport());
        testArguments.setJunitReport(checkArguments.getJunitReport());
        testArguments.setTestngReport(checkArguments.getTestngReport());

        GalenActionTest.runTests(new EventHandler(), galenTests, testArguments, listener);
    }

    private String originalCommand(String[] arguments) {
        StringBuilder builder = new StringBuilder("check ");
        for (String argument : arguments) {
            builder.append(" ");
            builder.append(argument);
        }
        return builder.toString();
    }

    private void verifyArgumentsForPageCheck() {
        if (checkArguments.getUrl() == null) {
            throw new IllegalArgumentException("Url is not specified");
        }

        if (checkArguments.getScreenSize() == null) {
            throw new IllegalArgumentException("Screen size is not specified");
        }

        if (checkArguments.getPaths().size() < 1) {
            throw new IllegalArgumentException("There are no specs specified");
        }

    }

    public GalenActionCheckArguments getCheckArguments() {
        return checkArguments;
    }
}

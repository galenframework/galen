/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.util;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.GalenMain;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.config.GalenProperty;
import net.mindengine.galen.runner.GalenArguments;

import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

public abstract class GalenBaseTestRunner {

    private static final Logger LOG = LoggerFactory.getLogger("GalenBaseLayoutTests");

    public static final TestDevice SMALL_PHONE = new TestDevice("small-phone", new Dimension(280, 400), asList("small-phone", "phone", "mobile"));

    public static final TestDevice NORMAL_PHONE = new TestDevice("normal-phone", new Dimension(320, 450), asList("normal-phone", "phone", "mobile"));

    public static final TestDevice TABLET = new TestDevice("tablet", new Dimension(768, 1024), asList("tablet", "mobile"));

    public static final TestDevice DESKTOP = new TestDevice("desktop", new Dimension(1024, 800), asList("desktop", "desktop"));

    public static final TestDevice FULLHD = new TestDevice("fullhd", new Dimension(1920, 1080), asList("fullhd", "desktop"));

    public void verifyPage(final String uri, final TestDevice pDevice, final String specPath) throws Exception {
        String projectPath = new File("").getAbsolutePath();
        String completeUrl = uri.startsWith("http://") ? uri : "file://" + new File("").getAbsolutePath() + "/src/test/resources/" + uri;
        String defaultBrowser = System.getProperty(GalenProperty.GALEN_DEFAULT_BROWSER.name(), "firefox");
        GalenConfig.getConfig().setProperty(GalenProperty.GALEN_DEFAULT_BROWSER, defaultBrowser);
        LOG.info("Opening url " + completeUrl + " in browser " + defaultBrowser);
        new GalenMain().execute(new GalenArguments().withUrl(completeUrl).withPaths(Arrays.asList(specPath)).withAction("check")
                .withIncludedTags(pDevice.getTags().toString()).withHtmlReport(projectPath + "/target/galen-html")
                .withScreenSize(new java.awt.Dimension(pDevice.getScreenSize().getWidth(), pDevice.getScreenSize().getHeight())));
    }

    @DataProvider
    public Object[][] devices() {
        return new Object[][] {// @formatter:off
              { SMALL_PHONE },
              { NORMAL_PHONE },
              { TABLET },
              { DESKTOP },
              { FULLHD }, // @formatter:on
        };
    }

    public static class TestDevice {

        private final String name;
        private final Dimension screenSize;
        private final List<String> tags;

        public TestDevice(String name, Dimension screenSize, List<String> tags) {
            this.name = name;
            this.screenSize = screenSize;
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public Dimension getScreenSize() {
            return screenSize;
        }

        public List<String> getTags() {
            return tags;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TestDevice [");
            if (name != null) {
                builder.append("name=");
                builder.append(name);
            }
            builder.append("]");
            return builder.toString();
        }
    }
}
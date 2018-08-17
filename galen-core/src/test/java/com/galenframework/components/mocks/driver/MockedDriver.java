/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.components.mocks.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.utils.GalenUtils;
import com.galenframework.rainbow4j.Rainbow4J;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.Logs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MockedDriver implements WebDriver, TakesScreenshot, JavascriptExecutor {
    private MockedDriverPage page;
    private String currrentUrl;

    private Dimension screenSize = new Dimension(1024, 768);
    private List<String> allExecutedJavascript = new LinkedList<>();

    private List<Object> expectedJavaScriptReturnValues;

    public MockedDriver() {
    }

    public MockedDriver(String initialUrl) {
        this.currrentUrl = initialUrl;
        get(initialUrl);
    }


    @Override
    public void get(String url) {
        this.currrentUrl = url;
        InputStream stream = getClass().getResourceAsStream(url);
        if (stream == null) {
            throw new RuntimeException("Cannot find resource: " + url);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.page = mapper.readValue(stream, MockedDriverPage.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing page: " + url, e);
        }
    }

    @Override
    public String getCurrentUrl() {
        return this.currrentUrl;
    }

    @Override
    public String getTitle() {
        return page.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = new LinkedList<>();

        for (MockedPageItem item : page.getItems()) {
            if (item.matches(by)) {
                elements.add(item.asWebElement());
            }
        }
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        List<WebElement> elements = findElements(by);
        if (elements.size() > 0) {
            return elements.get(0);
        }
        else throw new NoSuchElementException(by.toString());
    }

    @Override
    public String getPageSource() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void quit() {

    }

    @Override
    public Set<String> getWindowHandles() {
        return null;
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return null;
    }

    @Override
    public Navigation navigate() {
        return null;
    }

    @Override
    public Options manage() {
        return new Options() {
            @Override
            public void addCookie(Cookie cookie) {
            }

            @Override
            public void deleteCookieNamed(String s) {
            }

            @Override
            public void deleteCookie(Cookie cookie) {
            }

            @Override
            public void deleteAllCookies() {
            }

            @Override
            public Set<Cookie> getCookies() {
                return null;
            }

            @Override
            public Cookie getCookieNamed(String s) {
                return null;
            }

            @Override
            public Timeouts timeouts() {
                return null;
            }

            @Override
            public ImeHandler ime() {
                return null;
            }

            @Override
            public Window window() {
                return new Window() {
                    @Override
                    public void setSize(Dimension dimension) {
                        MockedDriver.this.screenSize = dimension;
                    }

                    @Override
                    public void setPosition(Point point) {

                    }

                    @Override
                    public Dimension getSize() {
                        return MockedDriver.this.screenSize;
                    }

                    @Override
                    public Point getPosition() {
                        return null;
                    }

                    @Override
                    public void maximize() {

                    }

                    @Override
                    public void fullscreen() {

                    }
                };
            }

            @Override
            public Logs logs() {
                return null;
            }
        };
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> xOutputType) throws WebDriverException {
        if (xOutputType.equals(OutputType.FILE)) {
            return (X) new File(getClass().getResource("/mocks/pages/screenshot.png").getFile());
        }
        else if (xOutputType.equals(OutputType.BYTES)) {
            File file = new File(getClass().getResource("/mocks/pages/screenshot.png").getFile());

            BufferedImage image = null;
            try {
                image = Rainbow4J.loadImage(file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return (X)((DataBufferByte) image.getData().getDataBuffer()).getData();

        }
        else throw new RuntimeException("Cannot make screenshot");
    }

    private final AtomicInteger jsExecutionSteps = new AtomicInteger(0);

    @Override
    public Object executeScript(String s, Object... objects) {
        allExecutedJavascript.add(s);

        if (expectedJavaScriptReturnValues != null) {
            int step = jsExecutionSteps.getAndIncrement();
            return expectedJavaScriptReturnValues.get(step);
        } else {
            if (s.equals(GalenUtils.JS_RETRIEVE_DEVICE_PIXEL_RATIO)) {
                return 1L;
            } else return null;
        }
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        allExecutedJavascript.add(s);
        if (expectedJavaScriptReturnValues != null) {
            return expectedJavaScriptReturnValues.get(jsExecutionSteps.getAndIncrement());
        } else {
            return null;
        }
    }

    public List<String> getAllExecutedJavascript() {
        return allExecutedJavascript;
    }

    public List<Object> getExpectedJavaScriptReturnValues() {
        return expectedJavaScriptReturnValues;
    }

    public void setExpectedJavaScriptReturnValues(List<Object> expectedJavaScriptReturnValues) {
        this.expectedJavaScriptReturnValues = expectedJavaScriptReturnValues;
    }
}

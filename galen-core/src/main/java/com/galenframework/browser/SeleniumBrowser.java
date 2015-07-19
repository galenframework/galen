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
package com.galenframework.browser;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;


import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.utils.GalenUtils;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.Page;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.utils.GalenUtils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class SeleniumBrowser implements Browser {

    private WebDriver driver;

    public SeleniumBrowser(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public void changeWindowSize(Dimension windowSize) {
        if (GalenConfig.getConfig().getBooleanProperty(GalenProperty.GALEN_BROWSER_VIEWPORT_ADJUSTSIZE)) {
            GalenUtils.autoAdjustBrowserWindowSizeToFitViewport(driver, windowSize.width, windowSize.height);
        } else {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(windowSize.width, windowSize.height));
        }
    }

    @Override
    public void load(String url) {
        driver.get(url);
    }

    @Override
    public Object executeJavascript(String javascript) {
        return ((JavascriptExecutor)driver).executeScript(javascript);
    }

    @Override
    public Page getPage() {
        return new SeleniumPage(driver);
    }

    @Override
    public String getUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public Dimension getScreenSize() {
        org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
        return new Dimension(windowSize.getWidth(), windowSize.getHeight());
    }

    @Override
    public File createScreenshot() {
        try {
            if (GalenConfig.getConfig().getBooleanProperty(GalenProperty.SCREENSHOT_FULLPAGE)) {
                return GalenUtils.makeFullScreenshot(driver);
            }
            else return makeSimpleScreenshot();
        } catch (Exception e) {
            throw new RuntimeException("Error making screenshot", e);
        }
    }
    
    private File makeSimpleScreenshot() throws IOException {
        return GalenUtils.takeScreenshot(driver);
    }
    
    @Override
    public void refresh() {
        driver.navigate().refresh();
    }

}

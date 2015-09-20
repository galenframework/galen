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
package com.galenframework.page.selenium;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.page.Rect;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;

public enum AreaFinder {
    NATIVE(new FindArea() {

        @Override
        public Rect findArea(WebPageElement webPageElement) {
            WebElement webElement = webPageElement.getWebElement();

            Point location = webElement.getLocation();
            Dimension size = webElement.getSize();
            return new Rect(location.getX(), location.getY(), size.getWidth(), size.getHeight());
        }
    }),

    JSBASED(new FindArea() {
        @Override
        public Rect findArea(WebPageElement webPageElement) {
            List<Number> rect = (List<Number>)((JavascriptExecutor)webPageElement.getDriver()).executeScript(JSBASED_SCRIPT, webPageElement.getWebElement());
            return new Rect(rect.get(0).intValue(), rect.get(1).intValue(), rect.get(2).intValue(), rect.get(3).intValue());
        }
    }),

    JSBASED_NATIVE(new FindArea() {
        @Override
        public Rect findArea(WebPageElement webPageElement) {
            try {
                return JSBASED.findArea(webPageElement);
            } catch (Exception ex) {
                return NATIVE.findArea(webPageElement);
            }
        }
    }),

    CUSTOM(new FindArea() {
        @Override
        public Rect findArea(WebPageElement webPageElement) {
            String script = GalenConfig.getConfig().getStringProperty(GalenProperty.GALEN_BROWSER_PAGELEMENT_AREAFINDER_CUSTOM_SCRIPT);
            List<Number> rect = (List<Number>)((JavascriptExecutor)webPageElement.getDriver()).executeScript(script, webPageElement.getWebElement());
            return new Rect(rect.get(0).intValue(), rect.get(1).intValue(), rect.get(2).intValue(), rect.get(3).intValue());

        }
    });

    private final FindArea areaFinder;


    private AreaFinder(FindArea findArea) {
        this.areaFinder = findArea;
    }

    private static interface FindArea {
        Rect findArea(WebPageElement webPageElement);
    }


    public Rect findArea(WebPageElement webPageElement) {
        return areaFinder.findArea(webPageElement);
    }

    private static final String JSBASED_SCRIPT = "var element = arguments[0], " +
                    "scrollTop = window.pageYOffset || document.documentElement.scrollTop, " +
                    "scrollLeft = window.pageXOffset || document.documentElement.scrollLeft, " +
                    "rect = element.getBoundingClientRect(); return [rect.left + scrollLeft, rect.top + scrollTop, rect.width, rect.height];";
}

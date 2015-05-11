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
package net.mindengine.galen.page.selenium;

import net.mindengine.galen.specs.page.Locator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class ByChain {

    private final By by;
    private final int index;
    private final ByChain next;

    public ByChain(By by, int index, ByChain next) {
        this.by = by;
        this.index = index;
        this.next = next;
    }

    public List<WebElement> findElements(SearchContext searchContext) {
        List<WebElement> elements = searchContext.findElements(by);

        if (next != null) {
            if (index > 0) {
                if (index <= elements.size()) {
                    return next.findElements(elements.get(index - 1));
                } else {
                    return Collections.emptyList();
                }

            } else {
                throw new IllegalArgumentException("Index is incorrect. Cannot find child elements without index");
            }
        } else {
            return elements;
        }
    }

    public WebElement findElement(SearchContext searchContext) {
        List<WebElement> elements = searchContext.findElements(by);

        if (next != null) {
            if (index > 0 ) {
                if (index <= elements.size()) {
                    return next.findElement(elements.get(index - 1));
                }
            } else {
                if (elements.size() > 0) {
                    return next.findElement(elements.get(0));
                }
            }
        } else {
            if (index > 0) {
                if (index <= elements.size()) {
                    return elements.get(index - 1);
                }
            } else {
                if (elements.size() > 0) {
                    return elements.get(0);
                }
            }
        }

        throw new NoSuchElementException(by.toString() + " | index " + index);
    }

    public static ByChain fromLocator(Locator locator) {
        return fromLocator(locator, null);
    }

    private static ByChain fromLocator(Locator locator, ByChain nextChain) {
        if (locator == null) {
            throw new IllegalArgumentException("Locator shouldn't be null");
        }

        ByChain byChain = new ByChain(convertToBy(locator), locator.getIndex(), nextChain);
        if (locator.getParent() != null) {
            byChain = fromLocator(locator.getParent(), byChain);
        }

        return  byChain;
    }

    private static By convertToBy(Locator locator) {
        if ("xpath".equals(locator.getLocatorType())) {
            return By.xpath(locator.getLocatorValue());
        }
        else if ("id".equals(locator.getLocatorType())) {
            return By.id(locator.getLocatorValue());
        }
        else if ("css".equals(locator.getLocatorType())) {
            return By.cssSelector(locator.getLocatorValue());
        }
        else return null;
    }
}

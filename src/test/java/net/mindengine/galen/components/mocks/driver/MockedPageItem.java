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
package net.mindengine.galen.components.mocks.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

public class MockedPageItem {
    private String locator;
    private String text;
    private Integer[] area;

    private String locatorType;
    private String locatorValue;
    private boolean visible = true;

    private String getLocatorType() {
        return locatorType;
    }

    private String getLocatorValue() {
        return locatorValue;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;

        int id = locator.indexOf(":");
        if (id > 0) {
            this.locatorType = locator.substring(0, id).trim();
            this.locatorValue = locator.substring(id + 1).trim();
        }
        else throw new RuntimeException("Incorrect locator: " + locator);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer[] getArea() {
        return area;
    }

    public void setArea(Integer[] area) {
        this.area = area;
    }

    public boolean matches(By by) {
        if (by instanceof By.ByCssSelector) {
            String selector = (String)takeFieldValueViaReflection(by, "selector");
            if("css".equals(getLocatorType()) && selector.equals(getLocatorValue()) ) {
                return true;
            }
        }
        else if (by instanceof By.ById) {
            String selector = (String)takeFieldValueViaReflection(by, "id");
            if("id".equals(getLocatorType()) && selector.equals(getLocatorValue()) ) {
                return true;
            }
        }
        else if (by instanceof By.ByXPath) {
            String selector = (String)takeFieldValueViaReflection(by, "xpathExpression");
            if("xpath".equals(getLocatorType()) && selector.equals(getLocatorValue()) ) {
                return true;
            }
        }

        return false;
    }

    private Object takeFieldValueViaReflection(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WebElement asWebElement() {
        return new MockedDriverElement(this);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

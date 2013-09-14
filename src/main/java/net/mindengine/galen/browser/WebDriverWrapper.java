/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.browser;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

public class WebDriverWrapper {

    private WebDriver driver;

    public WebDriverWrapper(WebDriver driver) {
        this.driver = driver;
    }
    
    
    public void close() {
        driver.close();
    }

    
    public WebElementWrapper findElement(By by) {
        try {
            return new WebElementWrapper(driver.findElement(by));
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    
    public WebElementWrapper[] findElements(By by) {
        List<WebElement> elementsList = driver.findElements(by);
        return convertToArray(elementsList);
    }


    public static WebElementWrapper[] convertToArray(List<WebElement> elementsList) {
        WebElementWrapper[] elements = new WebElementWrapper[elementsList.size()];
        
        int i=0;
        for (WebElement element : elementsList) {
            elements[i] = new WebElementWrapper(element);
            i++;
        }
        return elements;
    }
    
    
    

    
    public void get(String url) {
        driver.get(url);
    }

    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    
    public String getPageSource() {
        return driver.getPageSource();
    }

    
    public String getTitle() {
        return driver.getTitle();
    }

    
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    
    public String[] getWindowHandles() {
        Set<String> handlesSet = driver.getWindowHandles();
        if (handlesSet != null) {
            return handlesSet.toArray(new String[]{});
        }
        else return new String[]{};
    }

    
    public Options manage() {
        return driver.manage();
    }

    
    public Navigation navigate() {
        return driver.navigate();
    }

    
    public void quit() {
        driver.quit();
    }

    
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

}

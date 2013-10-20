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

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumGridBrowser extends SeleniumBrowser {

    public SeleniumGridBrowser(RemoteWebDriver driver) {
        super(driver);
    }
    
    @Override
    public String createScreenshot() {
        WebDriver augmentedDriver = new Augmenter().augment(getDriver()); 
        File file = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
        return file.getAbsolutePath();
    }
 

}

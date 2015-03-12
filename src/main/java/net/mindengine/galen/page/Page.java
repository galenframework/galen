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
package net.mindengine.galen.page;

import java.awt.image.BufferedImage;
import java.io.File;

import net.mindengine.galen.specs.page.Locator;

public interface Page {

    PageElement getObject(Locator objectLocator);
    
    PageElement getObject(String objectName, Locator objectLocator);

    PageElement getSpecialObject(String objectName);

    int getObjectCount(Locator locator);

    Page createObjectContextPage(Locator mainObjectLocator);

    /**
     * Saves a screenshot file for the specific page, so later it will be returned in createScreenshot or getScreenshotImage methods
     * @param screenshotFile
     */
    void setScreenshot(File screenshotFile);


    /**
     * Takes a screenshot and reads it inot BufferedImage
     * @return
     */
    BufferedImage getScreenshotImage();

    /**
     * Creates a new screenshot and returns it if there was no screenshot provided via setScreenshot method.
     * @return
     */
    File createScreenshot();

    String getTitle();

    void switchToFrame(PageElement mainObject);

    void switchToParentFrame();

    Page createFrameContext(PageElement mainObject);
}

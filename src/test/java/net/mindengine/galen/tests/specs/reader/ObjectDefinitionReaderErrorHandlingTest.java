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
package net.mindengine.galen.tests.specs.reader;

import static org.mockito.Mockito.*;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.selenium.SeleniumPage;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;
import net.mindengine.galen.specs.reader.page.StateObjectDefinition;
import net.mindengine.galen.parser.VarsContext;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

public class ObjectDefinitionReaderErrorHandlingTest {

    @Test(expectedExceptions={SyntaxException.class})
    public void shouldHandleNullPointer() throws Exception {
      // given
      PageSpecReader pageSpecReader = mock(PageSpecReader.class);
      Page page = mock(Page.class);
      VarsContext varsContext = mock(VarsContext.class);
      PageSpec pageSpec = new PageSpec();
      StateObjectDefinition stateObjectDefinition = new StateObjectDefinition(pageSpec, pageSpecReader);
      String line = "myObject-* css .avc";
      Place place = new Place("", 1);
      // when
      when(page.getObjectCount(any(Locator.class))).thenThrow(new NullPointerException());
      when(pageSpecReader.getPage()).thenReturn(page);
      when(varsContext.process(line)).thenReturn(line);
      stateObjectDefinition.process(varsContext, line, place);
    }
    
    @Test(expectedExceptions={WebDriverException.class})
    public void shouldHandleNullPointerInSeleniumPageAsWebDriverError() throws Exception {
      // given
      PageSpecReader pageSpecReader = mock(PageSpecReader.class);
      WebDriver driver = mock(WebDriver.class);
      VarsContext varsContext = mock(VarsContext.class);
      SeleniumPage seleniumPage = new SeleniumPage(driver);
      PageSpec pageSpec = new PageSpec();
      StateObjectDefinition stateObjectDefinition = new StateObjectDefinition(pageSpec, pageSpecReader);
      String line = "myObject-* css .avc";
      Place place = new Place("", 1);
      // when
      when(driver.findElements(any(By.class))).thenThrow(new NullPointerException());
      when(pageSpecReader.getPage()).thenReturn(seleniumPage);
      when(varsContext.process(line)).thenReturn(line);
      stateObjectDefinition.process(varsContext, line, place);
    }
    
    @Test(expectedExceptions={WebDriverException.class})
    public void shouldHandleRethrowWebDriverError() throws Exception {
      // given
      PageSpecReader pageSpecReader = mock(PageSpecReader.class);
      WebDriver driver = mock(WebDriver.class);
      VarsContext varsContext = mock(VarsContext.class);
      SeleniumPage seleniumPage = new SeleniumPage(driver);
      PageSpec pageSpec = new PageSpec();
      StateObjectDefinition stateObjectDefinition = new StateObjectDefinition(pageSpec, pageSpecReader);
      String line = "myObject-* css .avc";
      Place place = new Place("", 1);
      // when
      when(driver.findElements(any(By.class))).thenThrow(new WebDriverException());
      when(pageSpecReader.getPage()).thenReturn(seleniumPage);
      when(varsContext.process(line)).thenReturn(line);
      stateObjectDefinition.process(varsContext, line, place);
    }
}

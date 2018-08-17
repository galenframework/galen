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
package com.galenframework.tests.action;

import static java.util.Arrays.asList;
import static com.galenframework.specs.page.Locator.css;
import static com.galenframework.specs.page.Locator.id;
import static com.galenframework.specs.page.Locator.xpath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.galenframework.components.MockedBrowser;
import com.galenframework.components.validation.MockedInvisiblePageElement;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.page.PageElement;
import com.galenframework.reports.TestReport;
import com.galenframework.specs.page.Locator;
import com.galenframework.suite.actions.GalenPageActionWait;
import com.galenframework.suite.actions.GalenPageActionWait.UntilType;

import org.testng.annotations.Test;

public class GalenPageActionWaitTest {
    
    
    private MockedPage mockedPage = createMockedPage();
    
    @Test public void shouldWait_forAllElements() throws Exception {
        GalenPageActionWait wait = new GalenPageActionWait();
        wait.setTimeout(1000);
        wait.setUntilElements(asList(
                until(UntilType.VISIBLE, css("div.list")),
                until(UntilType.HIDDEN, id("qwe")),
                until(UntilType.EXIST, xpath("//div[@id='wqe']")),
                until(UntilType.GONE, css("qweqwewqee"))
                ));
        MockedBrowser browser = new MockedBrowser(null, null, new MockedPage());
        browser.setMockedPage(mockedPage);
        wait.execute(new TestReport(), browser, null, null);
    }
    
    @Test
    public void shouldThrowException() throws Exception {
        GalenPageActionWait wait = new GalenPageActionWait();
        wait.setTimeout(1000);
        wait.setUntilElements(asList(
                until(UntilType.HIDDEN, css("div.list")),
                until(UntilType.VISIBLE, id("qwe")),
                until(UntilType.GONE, xpath("//div[@id='wqe']")),
                until(UntilType.EXIST, css("qweqwewqee"))
                ));
        MockedBrowser browser = new MockedBrowser(null, null, new MockedPage());
        browser.setMockedPage(mockedPage);
        
        
        TimeoutException exception = null;
        try {
            wait.execute(new TestReport(), browser, null, null);
        }
        catch(TimeoutException e) {
            exception = e;
        }
        
        assertThat("Exception should be thrown", exception, notNullValue());
        assertThat("Exception message should be", exception.getMessage(), is("Failed waiting for:\n" +
        		" - hidden css: div.list\n" +
        		" - visible id: qwe\n" +
        		" - gone xpath: //div[@id='wqe']\n" +
        		" - exist css: qweqwewqee\n"));
    }
    
    
    @SuppressWarnings("serial")
    private MockedPage createMockedPage() {
        MockedPage page = new MockedPage();
        page.setLocatorElements(new HashMap<String, PageElement>() {{
            put("css: div.list", visibleElement());
            put("id: qwe", invisibleElement());
            put("xpath: //div[@id='wqe']", visibleElement());
        }});
        return page;
    }

    private GalenPageActionWait.Until until(UntilType type, Locator locator) {
        return new GalenPageActionWait.Until(type, locator);
    }

    protected PageElement invisibleElement() {
        return new MockedInvisiblePageElement(0, 0, 0, 0);
    }

    protected PageElement visibleElement() {
        return new MockedPageElement(0, 0, 0, 0);
    }
}

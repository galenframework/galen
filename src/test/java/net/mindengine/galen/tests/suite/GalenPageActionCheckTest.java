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
package net.mindengine.galen.tests.suite;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;
import net.mindengine.galen.components.MockedBrowser;
import net.mindengine.galen.components.validation.MockedInvisiblePageElement;
import net.mindengine.galen.components.validation.MockedPage;
import net.mindengine.galen.components.validation.MockedPageElement;
import net.mindengine.galen.components.validation.TestValidationListener;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;

import org.testng.annotations.Test;

public class GalenPageActionCheckTest {
    
    
    @SuppressWarnings("serial")
    @Test public void shouldTest_conditionalBlocks_simple_whenConditionPasses() throws IOException {
        GalenPageActionCheck check = new GalenPageActionCheck();
        check.setSpecs(Arrays.asList(getClass().getResource("/specs/spec-conditional-simple.spec").getFile()));
        
        MockedBrowser mockedBrowser = new MockedBrowser("http://galenframework.com", new Dimension(640, 480));
        mockedBrowser.setMockedPage(new MockedPage(new HashMap<String, PageElement>(){{
            put("textfield", new MockedPageElement(0, 0, 100, 100));
            put("button-1", new MockedPageElement(0, 0, 200, 100));
            put("button-2", new MockedPageElement(0, 100, 200, 10));
        }}));
        
        TestValidationListener validationListener = new TestValidationListener();
        check.execute(mockedBrowser, new GalenPageTest(), validationListener);
        
        Assert.assertEquals(
                "<o textfield>\n" +
                "<SpecHeight textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n" +
                "<o button-1>\n" +
                "<SpecAbove button-1>\n" +
                "</o button-1>\n" +
                "<o button-2>\n" + 
                "<SpecBelow button-2>\n" +
                "</o button-2>\n" 
        , validationListener.getInvokations());
    }
    
    @SuppressWarnings("serial")
    @Test public void shouldTest_conditionalBlocks_simple_whenConditionPasses_butStatementInverted() throws IOException {
        GalenPageActionCheck check = new GalenPageActionCheck();
        check.setSpecs(Arrays.asList(getClass().getResource("/specs/spec-conditional-simple-inverted.spec").getFile()));
        
        MockedBrowser mockedBrowser = new MockedBrowser("http://galenframework.com", new Dimension(640, 480));
        mockedBrowser.setMockedPage(new MockedPage(new HashMap<String, PageElement>(){{
            put("textfield", new MockedPageElement(0, 0, 100, 100));
            put("button-1", new MockedPageElement(0, 0, 200, 100));
            put("button-2", new MockedPageElement(0, 100, 200, 10));
        }}));
        
        TestValidationListener validationListener = new TestValidationListener();
        check.execute(mockedBrowser, new GalenPageTest(), validationListener);
        
        Assert.assertEquals(
                "<o textfield>\n" +
                "<SpecHeight textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n"
        , validationListener.getInvokations());
    }
    
    @SuppressWarnings("serial")
    @Test public void shouldTest_conditionalBlocks_simple_whenConditionFails_inverted() throws IOException {
        GalenPageActionCheck check = new GalenPageActionCheck();
        check.setSpecs(Arrays.asList(getClass().getResource("/specs/spec-conditional-simple-inverted.spec").getFile()));
        
        MockedBrowser mockedBrowser = new MockedBrowser("http://galenframework.com", new Dimension(640, 480));
        mockedBrowser.setMockedPage(new MockedPage(new HashMap<String, PageElement>(){{
            put("textfield", new MockedPageElement(0, 0, 100, 100));
            put("button-1", new MockedInvisiblePageElement(0, 0, 100, 100));
            put("button-2", new MockedInvisiblePageElement(0, 0, 100, 100));
        }}));

        
        TestValidationListener validationListener = new TestValidationListener();
        check.execute(mockedBrowser, new GalenPageTest(), validationListener);
        
        Assert.assertEquals(
                "<o textfield>\n" +
                "<SpecHeight textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n" +
                "<o button-1>\n" +
                "<SpecAbove button-1>\n" +
                "<e><msg>\"button-1\" is not visible on page</msg></e>\n" +
                "</o button-1>\n" +
                "<o button-2>\n" +
                "<SpecBelow button-2>\n" +
                "<e><msg>\"button-2\" is not visible on page</msg></e>\n" +
                "</o button-2>\n" 
        , validationListener.getInvokations());
    }
    
    @SuppressWarnings("serial")
    @Test public void shouldTest_conditionalBlocks_simple_whenConditionFails() throws IOException {
        GalenPageActionCheck check = new GalenPageActionCheck();
        check.setSpecs(Arrays.asList(getClass().getResource("/specs/spec-conditional-simple.spec").getFile()));
        
        MockedBrowser mockedBrowser = new MockedBrowser("http://galenframework.com", new Dimension(640, 480));
        mockedBrowser.setMockedPage(new MockedPage(new HashMap<String, PageElement>(){{
            put("textfield", new MockedPageElement(0, 0, 100, 100));
            put("button-1", new MockedInvisiblePageElement(0, 0, 100, 100));
            put("button-2", new MockedInvisiblePageElement(0, 0, 100, 100));
        }}));

        
        TestValidationListener validationListener = new TestValidationListener();
        check.execute(mockedBrowser, new GalenPageTest(), validationListener);
        
        Assert.assertEquals(
                "<o textfield>\n" +
                "<SpecHeight textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n"
        , validationListener.getInvokations());
    }
    
    @SuppressWarnings("serial")
    @Test public void shouldTest_conditionalBlocks_simpleOtherwise_whenConditionFails() throws IOException {
        GalenPageActionCheck check = new GalenPageActionCheck();
        check.setSpecs(Arrays.asList(getClass().getResource("/specs/spec-conditional-simple-otherwise.spec").getFile()));
        
        MockedBrowser mockedBrowser = new MockedBrowser("http://galenframework.com", new Dimension(640, 480));
        mockedBrowser.setMockedPage(new MockedPage(new HashMap<String, PageElement>(){{
            put("textfield", new MockedPageElement(0, 0, 100, 100));
            put("button-1", new MockedInvisiblePageElement(0, 0, 100, 100));
            put("button-2", new MockedInvisiblePageElement(0, 0, 100, 100));
        }}));

        
        TestValidationListener validationListener = new TestValidationListener();
        check.execute(mockedBrowser, new GalenPageTest(), validationListener);
        
        Assert.assertEquals(
                "<o textfield>\n" +
                "<SpecHeight textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n" +
                "<o textfield>\n" +
                "<SpecWidth textfield>\n" +
                "</o textfield>\n"
        , validationListener.getInvokations());
    }
    
    
    
}

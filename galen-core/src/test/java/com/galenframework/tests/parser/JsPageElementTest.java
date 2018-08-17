/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.parser;

import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.PageElement;
import com.galenframework.parser.JsPageElement;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Ivan Shubin on 2014/11/20.
 */
public class JsPageElementTest {

    @Test
    public void shouldSupportMethods() {
        JsPageElement pageElement = new JsPageElement("someobject", new MockedPageElement(10, 20, 400, 40));

        assertThat(pageElement.top(), is(20));
        assertThat(pageElement.bottom(), is(60));
        assertThat(pageElement.left(), is(10));
        assertThat(pageElement.right(), is(410));

        assertThat(pageElement.width(), is(400));
        assertThat(pageElement.height(), is(40));
    }

}

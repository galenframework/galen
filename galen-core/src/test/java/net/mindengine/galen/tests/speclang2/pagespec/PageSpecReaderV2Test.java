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
package net.mindengine.galen.tests.speclang2.pagespec;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.components.mocks.driver.MockedDriver;
import net.mindengine.galen.speclang2.reader.pagespec.PageSpecReaderV2;
import net.mindengine.galen.specs.page.CorrectionsRect;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PageSpecReaderV2Test {

    private static final Browser NO_BROWSER = null;

    @Test
    public void shouldRead_objectDefinitions() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions.gspec");


        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("header", new Locator("css", "#header"));
            put("header-icon", new Locator("css", "#header img"));
            put("button", new Locator("xpath", "//div[@id='button']"));
            put("cancel-link", new Locator("id", "cancel"));
            put("caption", new Locator("css", "#wrapper")
                    .withCorrections(new CorrectionsRect(
                            new CorrectionsRect.Correction(0, CorrectionsRect.Type.PLUS),
                            new CorrectionsRect.Correction(100, CorrectionsRect.Type.PLUS),
                            new CorrectionsRect.Correction(5, CorrectionsRect.Type.MINUS),
                            new CorrectionsRect.Correction(7, CorrectionsRect.Type.PLUS)
                    )));
        }}));
    }


    @Test
    public void shouldRead_objectDefinitions_withMultiObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions-multi-objects.gspec",
                new SeleniumBrowser(new MockedDriver("/speclang2/mocks/menu-items.json")));

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("menu-item-1", new Locator("css", "#menu li", 1));
            put("menu-item-2", new Locator("css", "#menu li", 2));
            put("menu-item-3", new Locator("css", "#menu li", 3));
            put("menu-item-4", new Locator("css", "#menu li", 4));
        }}));
    }

    @Test
    public void shouldRead_objectDefinitions_withMultiLevelObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions-multi-level-objects.gspec",
                new SeleniumBrowser(new MockedDriver("/speclang2/mocks/multi-level-objects.json")));

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("header", new Locator("css", "#header"));
            put("header.icon", new Locator("css", "img")
                    .withParent(new Locator("css", "#header")));

            put("box-1", new Locator("css", ".box", 1));
            put("box-1.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 1)));

            put("box-2", new Locator("css", ".box", 2));
            put("box-2.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 2)));

            put("box-3", new Locator("css", ".box", 3));
            put("box-3.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 3)));
        }}));
    }

    private PageSpec readPageSpec(String resource) throws IOException {
        return readPageSpec(resource, NO_BROWSER);
    }

    private PageSpec readPageSpec(String resource, Browser browser) throws IOException {
        return new PageSpecReaderV2().read(resource, browser);
    }

}

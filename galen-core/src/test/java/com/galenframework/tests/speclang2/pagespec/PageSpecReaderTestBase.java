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
package com.galenframework.tests.speclang2.pagespec;

import com.galenframework.components.validation.MockedInvisiblePageElement;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.ObjectSpecs;
import com.galenframework.specs.page.PageSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class PageSpecReaderTestBase {
    public static final Page EMPTY_PAGE = new MockedPage();
    public static final List<String> EMPTY_TAGS = Collections.emptyList();
    public static final Properties NO_PROPERTIES = null;
    public static final Map<String, Object> NO_VARS = null;
    public static final Map<String, Locator> EMPTY_OBJECTS = null;

    public PageSpec readPageSpec(String resource) throws IOException {
        return readPageSpec(resource, EMPTY_PAGE, EMPTY_TAGS, EMPTY_TAGS);
    }

    public PageSpec readPageSpec(String resource, Page page) throws IOException {
        return readPageSpec(resource, page, EMPTY_TAGS, EMPTY_TAGS);
    }

    public PageSpec readPageSpec(String resource, Page page, List<String> tags, List<String> excludedTags) throws IOException {
        return new PageSpecReader().read(resource, page, new SectionFilter(tags, excludedTags), NO_PROPERTIES, NO_VARS, EMPTY_OBJECTS);
    }

    public MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }

    protected PageElement invisibleElement(int left, int top, int width, int height) {
        return new MockedInvisiblePageElement(left, top, width, height);
    }

    public String firstAppearingSpecIn(PageSpec pageSpec) {
        return pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText();
    }

    public ObjectSpecs firstAppearingObjectIn(PageSpec pageSpec) {
        return pageSpec.getSections().get(0).getObjects().get(0);
    }
}

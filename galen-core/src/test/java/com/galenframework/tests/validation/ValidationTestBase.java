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
package com.galenframework.tests.validation;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

import com.galenframework.components.validation.MockedAbsentPageElement;
import com.galenframework.components.validation.MockedInvisiblePageElement;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.Rainbow4J;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.*;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;

public abstract class ValidationTestBase {
    public static final List<LayoutMeta> NULL_META = null;
    public static final List<ValidationObject> NO_AREA = null;
    public static final Spec NO_SPEC = null;

    @Test(dataProvider="provideGoodSamples")
    public void shouldPassValidation(Spec spec, MockedPage page) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);
        ValidationError error = validation.check("object", spec).getError();

        assertThat(error, is(nullValue()));
    }

    public PageSpec createMockedPageSpec(MockedPage page) {
        PageSpec pageSpec = new PageSpec();

        for (String objectName : page.getElements().keySet()) {
            pageSpec.getObjects().put(objectName, new Locator("id", objectName));
        }
        return pageSpec;
    }

    @Test(dataProvider="provideBadSamples")
    public void shouldGiveError(ValidationResult expectedResult, Spec spec, MockedPage page) {
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation validation = new PageValidation(null, page, pageSpec, null, null);

        ValidationResult realResult = validation.check("object", spec);
        assertThat(realResult, is(expectedResult));
    }

    @DataProvider
    public abstract Object[][] provideGoodSamples();

    @DataProvider
    public abstract Object[][] provideBadSamples();

    public MockedPage page(HashMap<String, PageElement> elements) {
        return new MockedPage(elements);
    }

    public MockedPage page(HashMap<String, PageElement> elements, BufferedImage screenshotImage) {
        return new MockedPage(elements, screenshotImage);
    }


    public MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }

    public Location location(Range exact, Side...sides) {
        return new Location(exact, asList(sides));
    }

    public ValidationResult validationResult(List<ValidationObject> areas, List<String> messages, List<LayoutMeta> meta) {
        return new ValidationResult(NO_SPEC, areas, new ValidationError(messages), meta);
    }

    public List<ValidationObject> areas(ValidationObject...errorAreas) {
        return asList(errorAreas);
    }

    public List<String> messages(String...messages) {
        return asList(messages);
    }

    public PageElement invisibleElement(int left, int top, int width, int height) {
        return new MockedInvisiblePageElement(left, top, width, height);
    }

    public MockedPageElement absentElement(int left, int top, int width, int height) {
        return new MockedAbsentPageElement(left, top, width, height);
    }

    public List<ValidationObject> singleArea(Rect rect, String tooltip) {
        return asList(new ValidationObject(rect, tooltip));
    }

    public BufferedImage loadTestImage(String imagePath) {
        try {
            return Rainbow4J.loadImage(getClass().getResource(imagePath).getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

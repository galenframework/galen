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
package com.galenframework.tests.speclang2.pagespec;

import com.galenframework.specs.Spec;
import com.galenframework.specs.page.ObjectSpecs;
import com.galenframework.specs.page.PageSpec;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PageSpecReaderImportLibTest extends PageSpecReaderTestBase {

    @Test
    public void should_allow_import_of_galen_extras_lib() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/lib-import/import-galen-extras.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(0));
        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getSections().get(0).getObjects().size(), is(2));

        assertObjectSpecs(pageSpec.getSections().get(0).getSections().get(0).getObjects().get(0),
            "menu.item-1",
            asList("aligned horizontally all menu.item-2 1px", "left-of menu.item-2 -1 to 1px"));

        assertObjectSpecs(pageSpec.getSections().get(0).getSections().get(0).getObjects().get(1),
            "menu.item-2",
            asList("aligned horizontally all menu.item-3 1px", "left-of menu.item-3 -1 to 1px"));

    }

    private void assertObjectSpecs(ObjectSpecs object, String expectedObjectName, List<String> expectedSpecs) {
        assertThat(object.getObjectName(), is(expectedObjectName));
        assertThat(object.getSpecs().size(), is(2));
        assertThat(object.getSpecs().stream().map(Spec::getOriginalText).collect(toList()), is(expectedSpecs));
    }
}

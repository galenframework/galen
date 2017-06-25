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
package com.galenframework.tests.specgen;

import com.galenframework.generator.PageSpecGenerationResult;
import com.galenframework.generator.SpecGenerator;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SpecGeneratorTest {

    @Test
    public void should_generate_simple_spec_from_page_dump() throws IOException {
        SpecGenerator specGenerator = new SpecGenerator();
        PageSpecGenerationResult result = specGenerator.generate(getClass().getResourceAsStream("/generator/simple-page.json"));
        assertThat("Should generate complete page spec",
            SpecGenerator.generatePageSpec(result),
            is(IOUtils.toString(getClass().getResourceAsStream("/generator/simple-page.expected.gspec"))));
        System.out.println(SpecGenerator.generatePageSpec(result));
    }
}

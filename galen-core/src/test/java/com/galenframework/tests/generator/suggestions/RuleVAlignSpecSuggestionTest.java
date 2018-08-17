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
package com.galenframework.tests.generator.suggestions;

import com.galenframework.generator.*;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.generator.suggestions.RuleVAlignSpecSuggestion;
import com.galenframework.page.Rect;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class RuleVAlignSpecSuggestionTest {
    @Test
    public void should_suggest_vertical_aligned_rule() {
        RuleVAlignSpecSuggestion suggestionTest = new RuleVAlignSpecSuggestion();
        SuggestionTestResult result = suggestionTest.test(
            new SuggestionOptions(asList("menu.item-1", "menu.item-2", "menu.item-3")),
            new SpecGeneratorOptions(),
            new PageItemNode(new PageItem("menu.item-1", new Rect(0, 20, 100, 20))),
            new PageItemNode(new PageItem("menu.item-2", new Rect(0, 40, 100, 20))),
            new PageItemNode(new PageItem("menu.item-3", new Rect(0, 60, 100, 20)))
        );

        assertThat(result.getGeneratedObjectSpecs(), is(nullValue()));
        assertThat(result.getGeneratedRules().size(), is(1));
        assertThat(result.getGeneratedRules().keySet(), contains("menu.item-1"));
        List<SpecStatement> statements = result.getGeneratedRules().get("menu.item-1");
        assertThat(statements.size(), is(1));
        assertThat(statements.get(0).getStatement(), is("| menu.item-* are aligned vertically above each other with 0px margin"));

    }
}

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
package com.galenframework.tests.generator.builders;

import com.galenframework.generator.*;
import com.galenframework.generator.builders.SpecBuilderInside;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.page.Rect;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class SpecBuilderInsideTest {
    private static final PageItemNode HEADER_ITEM_NODE = new PageItemNode(new PageItem("header", new Rect(10, 10, 980, 100)));
    public static final PageItemNode SCREEN_ITEM_NODE = new PageItemNode(new PageItem("screen", new Rect(0, 0, 1000, 500)));

    @Test
    public void should_build_spec_inside_without_any_edges() {
        SpecBuilderInside sbi = new SpecBuilderInside(HEADER_ITEM_NODE, SCREEN_ITEM_NODE);
        List<SpecStatement> specStatements = sbi.buildSpecs(emptyList(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("inside screen"));

        assertThat(statement.getAssertions().size(), is(0));
    }

    @Test
    public void should_build_spec_inside_with_single_edge() {
        SpecBuilderInside sbi = new SpecBuilderInside(HEADER_ITEM_NODE, SCREEN_ITEM_NODE);
        List<SpecStatement> specStatements = sbi.addLeftEdge().buildSpecs(emptyList(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("inside screen 10px left"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("header", AssertionEdge.EdgeType.left),
            new AssertionEdge("screen", AssertionEdge.EdgeType.left))));
    }

    @Test
    public void should_build_spec_inside_with_multiple_edges() {
        SpecBuilderInside sbi = new SpecBuilderInside(HEADER_ITEM_NODE, SCREEN_ITEM_NODE);
        List<SpecStatement> specStatements = sbi
            .addLeftEdge()
            .addTopEdge()
            .addRightEdge()
            .addBottomEdge()
            .buildSpecs(emptyList(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("inside screen 10px top left right"));

        assertThat(statement.getAssertions().size(), is(3));
        assertThat(statement.getAssertions(), containsInAnyOrder(
            new SpecAssertion(
                new AssertionEdge("header", AssertionEdge.EdgeType.left),
                new AssertionEdge("screen", AssertionEdge.EdgeType.left)
            ),
            new SpecAssertion(
                new AssertionEdge("header", AssertionEdge.EdgeType.right),
                new AssertionEdge("screen", AssertionEdge.EdgeType.right)
            ),
            new SpecAssertion(
                new AssertionEdge("header", AssertionEdge.EdgeType.top),
                new AssertionEdge("screen", AssertionEdge.EdgeType.top)
            )
        ));
    }
}

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
package com.galenframework.tests.generator.builders;

import com.galenframework.generator.*;
import com.galenframework.generator.builders.SpecBuilderAbove;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.generator.raycast.EdgesContainer;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SpecBuilderAboveTest {

    @Test
    public void should_generate_spec_above() {
        SpecBuilderAbove specBuilderAbove = new SpecBuilderAbove(
            new PageItem("title", new Rect(10, 10, 100, 30)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("description")),
                new Point(10, 50),
                new Point(110, 50)
            )
        );

        List<SpecStatement> specStatements = specBuilderAbove.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("above description 10px"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("title", AssertionEdge.EdgeType.bottom),
            new AssertionEdge("description", AssertionEdge.EdgeType.top))));
    }

    @Test
    public void should_generate_spec_above_without_ranges() {
        SpecBuilderAbove specBuilderAbove = new SpecBuilderAbove(
            new PageItem("title", new Rect(10, 10, 100, 30)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("description")),
                new Point(10, 250),
                new Point(110, 250)
            )
        );

        List<SpecStatement> specStatements = specBuilderAbove.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("above description"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("title", AssertionEdge.EdgeType.bottom),
            new AssertionEdge("description", AssertionEdge.EdgeType.top))));
    }
}

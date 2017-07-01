package com.galenframework.tests.generator.builders;

import com.galenframework.generator.*;
import com.galenframework.generator.builders.SpecBuilderBelow;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.generator.raycast.EdgesContainer;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SpecBuilderBelowTest {
    @Test
    public void should_generate_spec_below() {
        SpecBuilderBelow specBuilderBelow = new SpecBuilderBelow(
            new PageItem("description", new Rect(10, 50, 100, 30)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("title")),
                new Point(10, 30),
                new Point(110, 30)
            )
        );

        List<SpecStatement> specStatements = specBuilderBelow.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("below title 20px"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("description", AssertionEdge.EdgeType.top),
            new AssertionEdge("title", AssertionEdge.EdgeType.bottom))));
    }

    @Test
    public void should_generate_spec_below_without_ranges() {
        SpecBuilderBelow specBuilderBelow = new SpecBuilderBelow(
            new PageItem("description", new Rect(10, 250, 100, 30)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("title")),
                new Point(10, 30),
                new Point(110, 30)
            )
        );

        List<SpecStatement> specStatements = specBuilderBelow.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("below title"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("description", AssertionEdge.EdgeType.top),
            new AssertionEdge("title", AssertionEdge.EdgeType.bottom))));
    }
}

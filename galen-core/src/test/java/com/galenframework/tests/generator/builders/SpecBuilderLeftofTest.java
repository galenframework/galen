package com.galenframework.tests.generator.builders;

import com.galenframework.generator.*;
import com.galenframework.generator.builders.SpecBuilderLeftOf;
import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.generator.raycast.EdgesContainer;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SpecBuilderLeftofTest {

    @Test
    public void should_generate_spec_leftof() {
        SpecBuilderLeftOf specBuilderLeftOf = new SpecBuilderLeftOf(
            new PageItem("icon", new Rect(10, 10, 50, 50)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("caption")), new Point(70, 10), new Point(70, 50))
        );

        List<SpecStatement> specStatements = specBuilderLeftOf.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("left-of caption 10px"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("icon", AssertionEdge.EdgeType.right),
            new AssertionEdge("caption", AssertionEdge.EdgeType.left))));
    }

    @Test
    public void should_generate_spec_leftof_without_range() {
        SpecBuilderLeftOf specBuilderLeftOf = new SpecBuilderLeftOf(
            new PageItem("icon", new Rect(10, 10, 50, 50)),
            new EdgesContainer.Edge(new PageItemNode(new PageItem("caption")), new Point(170, 10), new Point(170, 50))
        );

        List<SpecStatement> specStatements = specBuilderLeftOf.buildSpecs(new LinkedList<>(), new SpecGeneratorOptions());

        assertThat(specStatements.size(), is(1));
        SpecStatement statement = specStatements.get(0);
        assertThat(statement.getStatement(), is("left-of caption"));

        assertThat(statement.getAssertions().size(), is(1));
        assertThat(statement.getAssertions().get(0), is(new SpecAssertion(
            new AssertionEdge("icon", AssertionEdge.EdgeType.right),
            new AssertionEdge("caption", AssertionEdge.EdgeType.left))));
    }
}

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
package com.galenframework.generator.builders;

import com.galenframework.generator.AssertionEdge;
import com.galenframework.generator.PageItemNode;
import com.galenframework.generator.SpecAssertion;
import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.page.Point;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.galenframework.generator.builders.SBIEdge.*;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}

class SBIEdgeResult {
    public final String validation;
    public final String edgeName;
    public final AssertionEdge assertionEdge;

    public SBIEdgeResult(String validation, String edgeName, AssertionEdge assertionEdge) {
        this.validation = validation;
        this.edgeName = edgeName;
        this.assertionEdge = assertionEdge;
    }
}

enum SBIEdge {
    TOP(0, (parent, points, options) -> {
        int distance = points[0].getTop() - parent.getPageItem().getArea().getTop();
        String validation;
        if (distance > options.getMinimalStickyParentDistance()) {
            if (parent.getMinimalPaddingTop() >= 0 && parent.getMinimalPaddingTop() <= options.getMinimalStickyParentDistance()) {
                validation = ">= " + parent.getMinimalPaddingTop() + "px";
            } else {
                validation = ">= 0px";
            }
        } else {
            validation = distance + "px";
        }
        return new SBIEdgeResult(validation, "top", new AssertionEdge(parent.getPageItem().getName(), AssertionEdge.EdgeType.top));
    }),

    LEFT(1, (parent, points, options) -> {
        int distance = points[0].getLeft() - parent.getPageItem().getArea().getLeft();
        String validation;
        if (distance > options.getMinimalStickyParentDistance()) {
            if (parent.getMinimalPaddingLeft() >= 0 && parent.getMinimalPaddingLeft() <= options.getMinimalStickyParentDistance()) {
                validation = ">= " + parent.getMinimalPaddingLeft() + "px";
            } else {
                validation = ">= 0px";
            }
        } else {
            validation = distance + "px";
        }
        return new SBIEdgeResult(validation, "left", new AssertionEdge(parent.getPageItem().getName(), AssertionEdge.EdgeType.left));
    }),

    RIGHT(2, (parent, points, options) -> {
        int distance = parent.getPageItem().getArea().getRight() - points[1].getLeft();
        String validation;
        if (distance > options.getMinimalStickyParentDistance()) {
            if (parent.getMinimalPaddingRight() >= 0 && parent.getMinimalPaddingRight() <= options.getMinimalStickyParentDistance()) {
                validation = ">= " + parent.getMinimalPaddingRight() + "px";
            } else {
                validation = ">= 0px";
            }
        } else {
            validation = distance + "px";
        }
        return new SBIEdgeResult(validation, "right", new AssertionEdge(parent.getPageItem().getName(), AssertionEdge.EdgeType.right));
    }),

    BOTTOM(3, (parent, points, options) -> {
        int distance = parent.getPageItem().getArea().getBottom() - points[3].getTop();
        String validation;
        if (distance > options.getMinimalStickyParentDistance()) {
            if (parent.getMinimalPaddingBottom() >= 0 && parent.getMinimalPaddingBottom() <= options.getMinimalStickyParentDistance()) {
                validation = ">= " + parent.getMinimalPaddingBottom() + "px";
            } else {
                validation = ">= 0px";
            }
        } else {
            validation = distance + "px";
        }
        return new SBIEdgeResult(validation, "bottom", new AssertionEdge(parent.getPageItem().getName(), AssertionEdge.EdgeType.bottom));
    });

    private static Pair<String, AssertionEdge> pair(String specText, AssertionEdge assertionEdge) {
        return new ImmutablePair<>(specText, assertionEdge);
    }

    public final int order;
    private final TriFunction<PageItemNode, Point[], SpecGeneratorOptions, SBIEdgeResult> distanceFunc;
    SBIEdge(int order, TriFunction<PageItemNode, Point[], SpecGeneratorOptions, SBIEdgeResult> distanceFunc) {
        this.order = order;
        this.distanceFunc = distanceFunc;
    }

    public SBIEdgeResult build(PageItemNode parent, Point[] points, SpecGeneratorOptions options) {
        return this.distanceFunc.apply(parent, points, options);
    }
}

public class SpecBuilderInside implements SpecBuilder {
    public static final String S_INSIDE = "s_inside";
    private final Point[] points;
    private final PageItemNode parent;
    private final PageItemNode itemNode;

    private List<SBIEdge> sbiEdges = new LinkedList<>();

    public SpecBuilderInside(PageItemNode itemNode, PageItemNode parent) {
        this.itemNode = itemNode;
        this.points = itemNode.getPageItem().getArea().getPoints();
        this.parent = parent;
    }

    @Override
    public String getName() {
        return S_INSIDE;
    }

    @Override
    public String[] getArgs() {
        return new String[] {itemNode.getPageItem().getName(), parent.getPageItem().getName()};
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {
        List<SpecAssertion> assertions = new LinkedList<>();

        boolean isPartly = false;
        for (Point p: points) {
            int offset = parent.getPageItem().getArea().calculatePointOffsetDistance(p);
            if (offset > 0) {
                isPartly = true;
            }
        }
        StringBuilder s = new StringBuilder("inside ");
        if (isPartly) {
            s.append("partly ");
        }
        s.append(parent.getPageItem().getName());

        if (!sbiEdges.isEmpty()) {
            s.append(" ");
            final boolean[] isFirst = {true};
            Collections.sort(sbiEdges, (a, b) -> a.order > b.order? 1: -1);
            List<Pair<String, List<SBIEdgeResult>>> groupedResults = sbiEdges.stream()
                .map(se -> se.build(parent, points, options))
                .collect(groupingBy(r -> r.validation, toList()))
                .entrySet().stream()
                .map(e -> new ImmutablePair<>(e.getKey(), e.getValue())).collect(toList());

            Collections.sort(groupedResults, (a, b) -> a.getKey().startsWith(">") ? 1: -1);

            groupedResults.forEach(pair -> {
                if (!isFirst[0]) {
                    s.append(", ");
                }
                s.append(pair.getKey());
                for (SBIEdgeResult result: pair.getValue()) {
                    s.append(' ').append(result.edgeName);
                    assertions.add(new SpecAssertion(new AssertionEdge(itemNode.getPageItem().getName(), result.assertionEdge.getEdgeType()), result.assertionEdge));
                }
                isFirst[0] = false;
            });
        }
        return singletonList(new SpecStatement(s.toString(), assertions));
    }

    public SpecBuilderInside addRightEdge() {
        sbiEdges.add(RIGHT);
        return this;
    }

    public SpecBuilderInside addLeftEdge() {
        sbiEdges.add(LEFT);
        return this;
    }

    public SpecBuilderInside addBottomEdge() {
        sbiEdges.add(BOTTOM);
        return this;
    }

    public SpecBuilderInside addTopEdge() {
        sbiEdges.add(TOP);
        return this;
    }
}

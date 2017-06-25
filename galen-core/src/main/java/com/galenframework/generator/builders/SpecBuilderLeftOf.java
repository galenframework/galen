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
import com.galenframework.generator.AssertionType;
import com.galenframework.generator.SpecAssertion;
import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.generator.raycast.EdgesContainer.Edge;
import com.galenframework.generator.math.Point;

import java.util.LinkedList;
import java.util.List;

import static com.galenframework.generator.builders.SpecBuilderRightOf.S_RIGHT_OF;
import static java.util.Collections.singletonList;

public class SpecBuilderLeftOf extends AbstractSpecBuilder {
    public static final String S_LEFT_OF = "s_left_of";
    private String itemName;
    private final Point[] points;
    private final Edge rightEdge;

    public SpecBuilderLeftOf(String itemName, Point[] points, Edge rightEdge) {
        this.itemName = itemName;
        this.points = points;
        this.rightEdge = rightEdge;
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {

        StringBuilder s = new StringBuilder("left-of ");
        s.append(rightEdge.itemNode.getPageItem().getName());
        int distance = rightEdge.p1.getLeft() - points[1].getLeft();
        if (distance <= options.getMinimalStickyHorizontalDistance()) {
            s.append(' ').append(distance).append("px");
        }

        extendSpecFilters(excludedFilters, S_RIGHT_OF);
        return singletonList(new SpecStatement(s.toString(), singletonList(new SpecAssertion(
           AssertionEdge.right(itemName), AssertionEdge.left(rightEdge)
        ))));
    }

    @Override
    public String getName() {
        return S_LEFT_OF;
    }

    @Override
    public String[] getArgs() {
        return new String[] {itemName, rightEdge.itemNode.getPageItem().getName()};
    }
}

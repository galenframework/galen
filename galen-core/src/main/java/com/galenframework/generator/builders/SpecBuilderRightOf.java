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
import com.galenframework.generator.SpecAssertion;
import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.generator.raycast.EdgesContainer.Edge;
import com.galenframework.generator.math.Point;

import java.util.List;

import static com.galenframework.generator.builders.SpecBuilderLeftOf.S_LEFT_OF;
import static java.util.Collections.singletonList;

public class SpecBuilderRightOf extends AbstractSpecBuilder {
    public static final String S_RIGHT_OF = "s_right_of";
    private String itemName;
    private final Point[] points;
    private final Edge leftEdge;

    public SpecBuilderRightOf(String itemName, Point[] points, Edge leftEdge) {
        this.itemName = itemName;
        this.points = points;
        this.leftEdge = leftEdge;
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {
        StringBuilder s = new StringBuilder("right-of ");
        s.append(leftEdge.itemNode.getPageItem().getName());
        int distance = points[0].getLeft() - leftEdge.p1.getLeft();
        if (distance <= options.getMinimalStickyHorizontalDistance()) {
            s.append(' ').append(distance).append("px");
        }

        extendSpecFilters(excludedFilters, S_LEFT_OF);
        return singletonList(new SpecStatement(s.toString(), singletonList(new SpecAssertion(
            AssertionEdge.left(itemName), AssertionEdge.right(leftEdge)
        ))));
    }

    @Override
    public String getName() {
        return S_RIGHT_OF;
    }

    @Override
    public String[] getArgs() {
        return new String[]{itemName, leftEdge.itemNode.getPageItem().getName()};
    }
}

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

import com.galenframework.generator.*;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.generator.raycast.EdgesContainer.Edge;

import java.util.List;

import static com.galenframework.generator.builders.SpecBuilderBelow.S_BELOW;
import static java.util.Collections.singletonList;

public class SpecBuilderAbove extends AbstractSpecBuilder {
    public static final String S_ABOVE = "s_above";
    private final PageItem pageItem;
    private final Edge bottomEdge;

    public SpecBuilderAbove(PageItem pageItem, Edge bottomEdge) {
        this.pageItem = pageItem;
        this.bottomEdge = bottomEdge;
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {
        StringBuilder s = new StringBuilder("above ");
        s.append(bottomEdge.itemNode.getPageItem().getName());
        int distance = bottomEdge.p1.getTop() - pageItem.getArea().getBottom();
        if (distance <= options.getMinimalStickyVerticalDistance()) {
            s.append(' ').append(distance).append("px");
        }

        extendSpecFilters(excludedFilters, S_BELOW);
        return singletonList(new SpecStatement(s.toString(), singletonList(new SpecAssertion(
            AssertionEdge.bottom(pageItem.getName()),
            AssertionEdge.top(bottomEdge)
        ))));
    }

    @Override
    public String getName() {
        return S_ABOVE;
    }

    @Override
    public String[] getArgs() {
        return new String[] {pageItem.getName(), bottomEdge.itemNode.getPageItem().getName()};
    }
}

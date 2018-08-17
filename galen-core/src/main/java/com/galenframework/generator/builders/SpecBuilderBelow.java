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
package com.galenframework.generator.builders;


import com.galenframework.generator.AssertionEdge;
import com.galenframework.generator.PageItem;
import com.galenframework.generator.SpecAssertion;
import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.generator.raycast.EdgesContainer;

import java.util.List;

import static com.galenframework.generator.builders.SpecBuilderAbove.S_ABOVE;
import static java.util.Collections.singletonList;

public class SpecBuilderBelow extends AbstractSpecBuilder {
    public static final String S_BELOW = "s_below";
    private final EdgesContainer.Edge topEdge;
    private final PageItem pageItem;

    public SpecBuilderBelow(PageItem pageItem, EdgesContainer.Edge topEdge) {
        this.pageItem = pageItem;
        this.topEdge = topEdge;
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {
        StringBuilder s = new StringBuilder("below ");
        s.append(topEdge.itemNode.getPageItem().getName());
        int distance = pageItem.getArea().getTop() - topEdge.p1.getTop();
        if (distance <= options.getMinimalStickyVerticalDistance()) {
            s.append(' ').append(distance).append("px");
        }

        extendSpecFilters(excludedFilters, S_ABOVE);
        return singletonList(new SpecStatement(s.toString(), singletonList(new SpecAssertion(
            AssertionEdge.top(pageItem.getName()),
            AssertionEdge.bottom(topEdge)
        ))));
    }

    @Override
    public String getName() {
        return S_BELOW;
    }

    @Override
    public String[] getArgs() {
        return new String[]{pageItem.getName(), topEdge.itemNode.getPageItem().getName()};
    }
}

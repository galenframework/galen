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
package com.galenframework.generator.suggestions;

import com.galenframework.generator.AssertionEdge;
import com.galenframework.generator.SuggestionTestResult;
import com.galenframework.generator.filters.AnyTwoArgsSpecFilter;
import com.galenframework.page.Rect;

import java.util.*;

import static com.galenframework.generator.builders.SpecBuilderAbove.S_ABOVE;
import static com.galenframework.generator.builders.SpecBuilderBelow.S_BELOW;
import static com.galenframework.generator.suggestions.CenteredInsideSpecSuggestion.S_CENTERED_INSIDE;
import static com.galenframework.generator.suggestions.VAlignSpecSuggestion.S_V_ALIGN;

public class RuleVAlignSpecSuggestion  extends AbstractRuleAlignSpecSuggestion {
    public static final String R_V_ALIGN = "r_v_align";

    @Override
    public String getName() {
        return R_V_ALIGN;
    }


    @Override
    protected AssertionEdge.EdgeType nextEdgeType() {
        return AssertionEdge.EdgeType.top;
    }

    @Override
    protected AssertionEdge.EdgeType previousEdgeType() {
        return AssertionEdge.EdgeType.bottom;
    }

    @Override
    protected String getAlignmentWay() {
        return "vertically above each other";
    }

    @Override
    protected SuggestionTestResult enrichWithFilters(SuggestionTestResult suggestionTestResult, List<String> filterArgs) {
        return suggestionTestResult
            .addFilter(new AnyTwoArgsSpecFilter(R_V_ALIGN, filterArgs))
            .addFilter(new AnyTwoArgsSpecFilter(S_ABOVE, filterArgs))
            .addFilter(new AnyTwoArgsSpecFilter(S_BELOW, filterArgs))
            .addFilter(new AnyTwoArgsSpecFilter(S_V_ALIGN, filterArgs))
            .addFilter(new AnyTwoArgsSpecFilter(S_CENTERED_INSIDE, filterArgs));
    }

    @Override
    protected int calculateDiff(Rect area1, Rect area2) {
        return area2.getTop() - area1.getBottom();
    }

    @Override
    protected boolean areAligned(Rect area1, Rect area2) {
        return area1.getLeft() != area2.getLeft() || area1.getRight() != area2.getRight();
    }

}

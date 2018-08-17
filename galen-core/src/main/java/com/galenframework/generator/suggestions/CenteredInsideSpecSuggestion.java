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

import com.galenframework.generator.*;
import com.galenframework.page.Rect;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class CenteredInsideSpecSuggestion extends TwoArgsSpecSuggestion {

    public static final String S_CENTERED_INSIDE = "s_centered_inside";

    @Override
    public String getName() {
        return S_CENTERED_INSIDE;
    }

    @Override
    protected SuggestionTestResult testThem(SuggestionOptions options, String name1, Rect area1, String name2, Rect area2) {
        int diffLeft = area2.getLeft() - area1.getLeft();
        int diffRight = area1.getRight() - area2.getRight();

        int gap = Math.abs(diffLeft - diffRight);
        if (gap < 2) {
            return new SuggestionTestResult()
                .addObjectSpec(name2, new SpecStatement(
                    format("centered horizontally inside %s 1px", name1),
                    asList(
                        new SpecAssertion(AssertionEdge.left(name1), AssertionEdge.left(name2)),
                        new SpecAssertion(AssertionEdge.right(name1), AssertionEdge.right(name2))
                    )
                ));
        }
        return null;
    }
}

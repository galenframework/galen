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

import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.SuggestionOptions;
import com.galenframework.generator.SuggestionTestResult;
import com.galenframework.page.Rect;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class VAlignSpecSuggestion extends TwoArgsSpecSuggestion {

    public static final String S_V_ALIGN = "s_v_align";

    @Override
    public String getName() {
        return S_V_ALIGN;
    }

    @Override
    protected SuggestionTestResult testThem(SuggestionOptions options, String name1, Rect area1, String name2, Rect area2) {
        int leftAlignDiff = Math.abs(area1.getLeft() - area2.getLeft());
        int rightAlignDiff = Math.abs(area1.getRight() - area2.getRight());

        String spec = null;
        if (leftAlignDiff == 0 && rightAlignDiff == 0) {
            spec = format("aligned vertically all %s", name2);
        } else if (leftAlignDiff == 0) {
            spec = format("aligned vertically left %s", name2);
        } else if (rightAlignDiff == 0) {
            spec = format("aligned vertically right %s", name2);
        } else if ((area2.getLeft() - area1.getLeft()) - (area1.getRight() - area2.getRight()) == 0) {
            spec = format("aligned vertically centered %s", name2);
        }

        if (spec != null) {
            return new SuggestionTestResult()
                .addObjectSpecs(name1, asList(new SpecStatement(spec)));
        } else {
            return null;
        }
    }
}

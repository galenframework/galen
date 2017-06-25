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
package com.galenframework.generator.suggestions;

import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.SuggestionOptions;
import com.galenframework.generator.SuggestionTestResult;
import com.galenframework.generator.math.Rect;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class HAlignSpecSuggestion extends TwoArgsSpecSuggestion {

    public static final String S_H_ALIGN = "s_h_align";

    @Override
    public String getName() {
        return S_H_ALIGN;
    }

    @Override
    protected SuggestionTestResult testThem(SuggestionOptions options, String name1, Rect area1, String name2, Rect area2) {
        int topAlignDiff = Math.abs(area1.getTop() - area2.getTop());
        int bottomAlignDiff = Math.abs(area1.getBottom() - area2.getBottom());

        String spec = null;
        if (topAlignDiff == 0 && bottomAlignDiff == 0) {
            spec = format("aligned horizontally all %s", name2);
        } else if (topAlignDiff == 0) {
            spec = format("aligned horizontally top %s", name2);
        } else if (bottomAlignDiff == 0) {
            spec = format("aligned horizontally bottom %s", name2);
        } else if (area2.getTop() - area1.getTop() == area1.getBottom() - area2.getBottom()) {
            spec = format("aligned horizontally centered %s", name2);
        }

        if (spec != null) {
            return new SuggestionTestResult()
                .addObjectSpecs(name1, asList(new SpecStatement(spec)));
        } else {
            return null;
        }
    }
}

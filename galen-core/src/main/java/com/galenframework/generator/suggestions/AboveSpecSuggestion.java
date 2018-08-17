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

public class AboveSpecSuggestion extends TwoArgsSpecSuggestion {

    public static final String S_ABOVE = "s_above";

    @Override
    public String getName() {
        return S_ABOVE;
    }

    @Override
    protected SuggestionTestResult testThem(SuggestionOptions options, String name1, Rect area1, String name2, Rect area2) {
        int diff =  area2.getTop() - area1.getBottom();
        if (diff >=0 && diff <= 50) {
            return new SuggestionTestResult()
                .addObjectSpecs(name1,
                    asList(new SpecStatement(format("above %s %dpx", name2, diff)))
                );
        }
        return null;
    }
}

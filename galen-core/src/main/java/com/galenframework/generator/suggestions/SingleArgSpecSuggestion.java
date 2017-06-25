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

import com.galenframework.generator.PageItemNode;
import com.galenframework.generator.SuggestionOptions;
import com.galenframework.generator.SuggestionTestResult;

public abstract class SingleArgSpecSuggestion implements SpecSuggestion {
    @Override
    public SuggestionTestResult test(SuggestionOptions options, PageItemNode... pins) {
        if (pins != null && pins.length == 1) {
            return testIt(options, pins[0]);
        }
        return null;
    }

    protected abstract SuggestionTestResult testIt(SuggestionOptions options, PageItemNode pin);

}

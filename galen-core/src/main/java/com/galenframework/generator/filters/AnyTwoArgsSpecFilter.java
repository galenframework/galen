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
package com.galenframework.generator.filters;

import java.util.List;

public class AnyTwoArgsSpecFilter implements  SpecFilter {
    private String specSuggestionName;
    private List<String> arguments;

    public AnyTwoArgsSpecFilter(String specSuggestionName, List<String> arguments) {
        this.specSuggestionName = specSuggestionName;
        this.arguments = arguments;
    }

    @Override
    public boolean matches(String specSuggestionName, String... args) {
        if (this.specSuggestionName.equals(specSuggestionName)) {
            int countMatching = 0;
            for (String arg : args) {
                if (this.arguments.contains(arg)) {
                    countMatching++;

                    if (countMatching >= 2) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public String getSpecSuggestionName() {
        return specSuggestionName;
    }

    public void setSpecSuggestionName(String specSuggestionName) {
        this.specSuggestionName = specSuggestionName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}

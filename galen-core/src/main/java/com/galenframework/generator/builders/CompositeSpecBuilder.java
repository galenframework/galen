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

import com.galenframework.generator.SpecStatement;
import com.galenframework.generator.filters.SpecFilter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CompositeSpecBuilder implements SpecBuilder {
    private List<SpecBuilder> specBuilders = new LinkedList<>();

    public void add(SpecBuilder specBuilder) {
        specBuilders.add(specBuilder);
    }

    @Override
    public List<SpecStatement> buildSpecs(List<SpecFilter> excludedFilters, SpecGeneratorOptions options) {
        return specBuilders.stream()
            .filter(sb -> !matchesExcludedFilter(excludedFilters, sb.getName(), sb.getArgs()))
            .map(sb -> sb.buildSpecs(excludedFilters, options))
            .filter(s -> s != null)
            .flatMap(Collection::stream)
            .collect(toList());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getArgs() {
        return null;
    }

    private boolean matchesExcludedFilter(List<SpecFilter> excludedFilters, String specBuilderName, String[] args) {
        for (SpecFilter specFilter : excludedFilters) {
            if (specFilter.matches(specBuilderName, args)) {
                return true;
            }
        }
        return false;
    }
}

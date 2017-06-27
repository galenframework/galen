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

import com.galenframework.generator.*;
import com.galenframework.page.Rect;

import java.util.*;

import static com.galenframework.generator.SpecGeneratorUtils.findNamingPattern;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public abstract class AbstractRuleAlignSpecSuggestion implements SpecSuggestion {

    @Override
    public SuggestionTestResult test(SuggestionOptions options, PageItemNode... pins) {
        Set<Integer> diffs = new HashSet<>();
        int previousDiff = 0;

        if (pins != null && pins.length > 1) {
            for (int i = 0; i < pins.length - 1; i++) {
                Rect area1 = pins[i].getPageItem().getArea();
                Rect area2 = pins[i + 1].getPageItem().getArea();
                if (areAligned(area1, area2)) {
                    return null;
                }

                int diff = calculateDiff(area1, area2);
                if (diff > 70) {
                    return null;
                }
                if (!diffs.isEmpty()) {
                    if (Math.abs(previousDiff - diff) > 3) {
                        return null;
                    }
                }
                diffs.add(diff);
                previousDiff = diff;
            }

            String rule = null;

            if (diffs.size() == 1) {
                rule = format("| %s are aligned " + getAlignmentWay() + " with %dpx margin", constructNames(options, pins), diffs.iterator().next());
            } else if (diffs.size() > 1) {
                rule = format("| %s are aligned " + getAlignmentWay() + " with ~%dpx margin", constructNames(options, pins), findAverageDiff(diffs));
            }
            if (rule != null) {

                List<String> filterArgs = Arrays.stream(pins).map(p -> p.getPageItem().getName()).collect(toList());

                return enrichWithFilters(new SuggestionTestResult()
                    .addGeneratedRule(pins[0].getPageItem().getName(), new SpecStatement(rule, createAssertions(pins))), filterArgs);
            }
        }
        return null;
    }

    protected List<SpecAssertion> createAssertions(PageItemNode[] pins) {
        List<SpecAssertion> assertions = new LinkedList<>();
        for (int i = 0; i < pins.length - 1; i++) {
            assertions.add(new SpecAssertion(
                new AssertionEdge(pins[i].getPageItem().getName(), previousEdgeType()),
                new AssertionEdge(pins[i + 1].getPageItem().getName(), nextEdgeType())
            ));
        }
        return assertions;
    }

    protected abstract AssertionEdge.EdgeType nextEdgeType();

    protected abstract AssertionEdge.EdgeType previousEdgeType();

    protected abstract String getAlignmentWay();

    protected abstract SuggestionTestResult enrichWithFilters(SuggestionTestResult suggestionTestResult, List<String> filterArgs);

    protected abstract int calculateDiff(Rect area1, Rect area2);

    protected abstract boolean areAligned(Rect area1, Rect area2);

    private int findAverageDiff(Set<Integer> diffs) {
        int sum = 0;
        for (Integer diff: diffs) {
            sum += diff;
        }
        return sum / diffs.size();
    }

    private String constructNames(SuggestionOptions options, PageItemNode[] pins) {
        String pattern = findNamingPattern(options.getAllObjectNames(), pins);
        if (pattern != null) {
            return pattern;
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (PageItemNode pin: pins) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(pin.getPageItem().getName());
            first = false;
        }
        return builder.toString();
    }

}

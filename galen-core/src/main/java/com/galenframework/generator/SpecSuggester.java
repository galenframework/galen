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
package com.galenframework.generator;

import com.galenframework.generator.builders.*;
import com.galenframework.generator.filters.SpecFilter;
import com.galenframework.generator.raycast.EdgesContainer;
import com.galenframework.generator.raycast.EdgesContainer.Edge;
import com.galenframework.generator.suggestions.*;
import com.galenframework.page.Point;

import java.util.*;
import java.util.function.Function;

public class SpecSuggester {
    public static List<SpecSuggestion> horizontallyOrderComplexRulesSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new RuleHAlignSpecSuggestion());
    }};

    public static List<SpecSuggestion> verticallyOrderComplexRulesSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new RuleVAlignSpecSuggestion());
    }};

    //TODO enable spec aligned suggestions
    public static List<SpecSuggestion> horizontallyOrderSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new HAlignSpecSuggestion());
    }};
    public static List<SpecSuggestion> verticallyOrderSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new VAlignSpecSuggestion());
    }};
    public static List<SpecSuggestion> singleItemSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new SizeSpecSuggestion());
    }};


    public static List<SpecSuggestion> parentSuggestions = new ArrayList<SpecSuggestion>() {{
        add(new CenteredInsideSpecSuggestion());
    }};

    private final SuggestionOptions options;
    List<SpecFilter> excludedFilters = new LinkedList<>();

    public SpecSuggester(SuggestionOptions options) {
        this.options = options;
    }

    public SuggestionTestResult suggestSpecsForMultipleObjects(List<PageItemNode> pins, List<SpecSuggestion> suggestions) {
        SuggestionTestResult globalResult = new SuggestionTestResult();

        List<PageItemNode[]> pinsVariations = generateSequentialVariations(pins.toArray(new PageItemNode[pins.size()]));

        for (PageItemNode[] pinsVariation : pinsVariations) {
            String[] namesArray = Arrays.stream(pinsVariation).map(p -> p.getPageItem().getName()).toArray(String[]::new);

            for (SpecSuggestion suggestion : suggestions) {
                if (!matchesExcludedFilter(suggestion.getName(), namesArray)) {
                    SuggestionTestResult result = suggestion.test(options, pinsVariation);
                    globalResult.merge(result);

                    if (result != null && result.isValid()) {
                        if (result.getFilters() != null) {
                            excludedFilters.addAll(result.getFilters());
                        }
                    }
                }
            }
        }

        return globalResult;
    }

    private List<PageItemNode[]> generateSequentialVariations(PageItemNode[] pageItemNodes) {
        List<PageItemNode[]> variations = new LinkedList<>();
        if (pageItemNodes != null && pageItemNodes.length > 1) {
            variations.add(pageItemNodes);
        }

        for (int amount = pageItemNodes.length - 1; amount > 1; amount --) {
            for (int offset = 0; offset <= pageItemNodes.length - amount; offset ++) {
                PageItemNode[] variation = new PageItemNode[amount];
                for (int i = 0; i < amount; i++) {
                    variation[i] = pageItemNodes[offset + i];
                }
                variations.add(variation);
            }
        }
        return variations;
    }


    public SuggestionTestResult suggestSpecsForTwoObjects(List<PageItemNode> pins, List<SpecSuggestion> suggestions) {
        SuggestionTestResult globalResult = new SuggestionTestResult();

        for (int i = 0; i < pins.size() - 1; i++) {
            for (int j = i + 1; j < pins.size(); j++) {
                for (SpecSuggestion suggestion : suggestions) {
                    if (!matchesExcludedFilter(suggestion.getName(), pins.get(i).getPageItem().getName(), pins.get(j).getPageItem().getName())) {
                        SuggestionTestResult result = suggestion.test(options, pins.get(i), pins.get(j));
                        globalResult.merge(result);

                        if (result != null && result.isValid()) {
                            if (result.getFilters() != null) {
                                excludedFilters.addAll(result.getFilters());
                            }
                        }
                    }
                }
            }
        }
        return globalResult;
    }

    public SuggestionTestResult suggestSpecsForSingleObject(List<PageItemNode> pins, List<SpecSuggestion> suggestions) {
        SuggestionTestResult globalResult = new SuggestionTestResult();

        for (PageItemNode pin: pins) {
            for (SpecSuggestion suggestion : suggestions) {
                if (!matchesExcludedFilter(suggestion.getName(), pin.getPageItem().getName())) {
                    SuggestionTestResult result = suggestion.test(options, pin);
                    globalResult.merge(result);
                    if (result != null && result.isValid()) {
                        if (result.getFilters() != null) {
                            excludedFilters.addAll(result.getFilters());
                        }
                    }
                }
            }
        }
        return globalResult;
    }

    public SuggestionTestResult suggestSpecsRayCasting(PageItemNode parent, List<PageItemNode> pins) {
        SuggestionTestResult globalResult = new SuggestionTestResult();

        EdgesContainer edges = EdgesContainer.create(parent, pins);
        Map<String, CompositeSpecBuilder> allSpecBuilders = new HashMap<>();

        for (PageItemNode pin : pins) {
            Point[] points = pin.getPageItem().getArea().getPoints();

            Edge closestRightEdge = rayCastRight(pin, new Edge(pin, points[1], points[2]), edges.getRightEdges());
            Edge closestLeftEdge = rayCastLeft(pin, new Edge(pin, points[0], points[3]), edges.getLeftEdges());
            Edge closestBottomEdge = rayCastBottom(pin, new Edge(pin, points[3], points[2]), edges.getBottomEdges());
            Edge closestTopEdge = rayCastTop(pin, new Edge(pin, points[0], points[1]), edges.getTopEdges());

            CompositeSpecBuilder compositeSpecBuilder = new CompositeSpecBuilder();
            allSpecBuilders.put(pin.getPageItem().getName(), compositeSpecBuilder);

            SpecBuilderInside sbInside = new SpecBuilderInside(pin, pin.getParent());
            compositeSpecBuilder.add(sbInside);

            if (closestRightEdge != null) {
                if (closestRightEdge.itemNode == pin.getParent()) {
                    closestRightEdge.itemNode.updateMinimalPaddingRight(closestRightEdge.p1.getLeft() - points[1].getLeft());
                    sbInside.addRightEdge();
                } else {
                    compositeSpecBuilder.add(new SpecBuilderLeftOf(pin.getPageItem().getName(), points, closestRightEdge));
                }
            }

            if (closestLeftEdge != null) {
                if (closestLeftEdge.itemNode == pin.getParent()) {
                    closestLeftEdge.itemNode.updateMinimalPaddingLeft(points[0].getLeft() - closestLeftEdge.p1.getLeft());
                    sbInside.addLeftEdge();
                } else {
                    compositeSpecBuilder.add(new SpecBuilderRightOf(pin.getPageItem().getName(), points, closestLeftEdge));
                }
            }

            if (closestBottomEdge != null) {
                if (closestBottomEdge.itemNode == pin.getParent()) {
                    closestBottomEdge.itemNode.updateMinimalPaddingBottom(closestBottomEdge.p1.getTop() - points[3].getTop());
                    sbInside.addBottomEdge();
                } else {
                    compositeSpecBuilder.add(new SpecBuilderAbove(pin.getPageItem(), closestBottomEdge));
                }
            }

            if (closestTopEdge != null) {
                if (closestTopEdge.itemNode == pin.getParent()) {
                    closestTopEdge.itemNode.updateMinimalPaddingTop(points[0].getTop() - closestTopEdge.p1.getTop());
                    sbInside.addTopEdge();
                } else {
                    compositeSpecBuilder.add(new SpecBuilderBelow(pin.getPageItem(), closestTopEdge));
                }
            }
        }

        Map<String, List<SpecStatement>> objectSpecs = new HashMap<>();
        allSpecBuilders.forEach((itemName, specBuilder) -> {
            List<SpecStatement> specs = specBuilder.buildSpecs(excludedFilters, new SpecGeneratorOptions());
            if (specs != null && !specs.isEmpty()) {
                objectSpecs.put(itemName, specs);
            }
        });

        globalResult.setGeneratedObjectSpecs(objectSpecs);
        return globalResult;
    }

    private Edge rayCastTop(PageItemNode pin, Edge edge, List<Edge> edges) {
        return findClosestEdge(pin, edges, (otherEdge) -> {
            if (otherEdge.isInTopZoneOf(edge)) {
                return edge.p1.getTop() - otherEdge.p1.getTop();
            }
            return -1;
        });
    }

    private Edge rayCastBottom(PageItemNode pin, Edge edge, List<Edge> edges) {
        return findClosestEdge(pin, edges, (otherEdge) -> {
            if (otherEdge.isInBottomZoneOf(edge)) {
                return otherEdge.p1.getTop() - edge.p1.getTop();
            }
            return -1;
        });
    }

    private Edge rayCastRight(PageItemNode pin, Edge edge, List<Edge> edges) {
        return findClosestEdge(pin, edges, (otherEdge) -> {
            if (otherEdge.isInRightZoneOf(edge)) {
                return otherEdge.p1.getLeft() - edge.p1.getLeft();
            }
            return -1;
        });
    }
    private Edge rayCastLeft(PageItemNode pin, Edge edge, List<Edge> edges) {
        return findClosestEdge(pin, edges, (otherEdge) -> {
            if (otherEdge.isInLeftZoneOf(edge)) {
                return  edge.p1.getLeft() - otherEdge.p1.getLeft();
            }
            return -1;
        });
    }

    private Edge findClosestEdge(PageItemNode pin, List<Edge> otherEdges, Function<Edge, Integer> distanceCalculator) {
        Edge closestEdge = null;
        int distance = 1000000;
        for (Edge otherEdge : otherEdges) {
            if (otherEdge.itemNode != pin) {
                int d = distanceCalculator.apply(otherEdge);
                if (d >= 0 && distance > d) {
                    distance = d;
                    closestEdge = otherEdge;
                }
            }
        }
        return closestEdge;

    }

    private boolean matchesExcludedFilter(String suggestionId, String...args) {
        for (SpecFilter specFilter : excludedFilters) {
            if (specFilter.matches(suggestionId, args)) {
                return true;
            }
        }
        return false;
    }

}

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

import com.galenframework.generator.builders.SpecGeneratorOptions;
import com.galenframework.generator.model.GmPageSpec;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.galenframework.generator.SpecGeneratorUtils.findNamingPattern;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;


public class SpecGenerator {
    private PageItemJsonMapper piJsonMapper = new PageItemJsonMapper();

    public PageSpecGenerationResult generate(InputStream stream, SpecGeneratorOptions specGeneratorOptions) throws IOException {
        List<PageItem> pageItems = piJsonMapper.loadItems(stream);
        return generate(pageItems, specGeneratorOptions);
    }

    private PageSpecGenerationResult generate(List<PageItem> pageItems, SpecGeneratorOptions specGeneratorOptions) {
        Set<String> allObjectNames = extractItemNamesOnAllPages(pageItems);
        return generate(pageItems, allObjectNames, specGeneratorOptions);
    }

    private Set<String> extractItemNamesOnAllPages(List<PageItem> pageItems) {
        return pageItems.stream().map(PageItem::getName).distinct().collect(Collectors.toSet());
    }

    private PageSpecGenerationResult generate(List<PageItem> pageItems, Set<String> allObjectNames, SpecGeneratorOptions specGeneratorOptions) {
        List<PageItem> convertedItems = new LinkedList<>();
        PageItem screenItem = null;
        Size largestSize = new Size();
        for (PageItem pageItem : pageItems) {
            if (!"viewport".equals(pageItem.getName())) {
                convertedItems.add(pageItem);

                if (screenItem == null && "screen".equals(pageItem.getName())) {
                    screenItem = pageItem;
                }
                if (largestSize.width < pageItem.getArea().getWidth()) {
                    largestSize.width = pageItem.getArea().getWidth();
                    largestSize.height = pageItem.getArea().getHeight();
                }
            }
        }

        // Sorting items by size first and then by location
        convertedItems.sort(bySizeAndLocation());
        removeDuplicatedElements(convertedItems);
        List<PageItemNode> rootPins = restructurePageItems(convertedItems);

        List<String> objectNamesPerPage = new LinkedList<>();
        rootPins.forEach(p -> p.visitTree(pin -> {
            objectNamesPerPage.add(pin.getPageItem().getName());
            if (pin.getChildren() != null) {
                sortPinsHorizontally(pin.getChildren());
            }
        }));

        SuggestionTestResult results = new SuggestionTestResult();
        rootPins.forEach(p -> p.visitTree(pin -> results.merge(proposeSpecsFor(pin, objectNamesPerPage, specGeneratorOptions))));

        List<String> missingObjects = proposeAbsenseSpecs(results, pageItems, allObjectNames);
        // adding missing objects to pins. For now we will put missing objects inside a first root pin

        missingObjects.forEach(missingObjectName -> {
            new PageItemNode(new PageItem(missingObjectName)).moveToParent(rootPins.get(0));
            objectNamesPerPage.add(missingObjectName);
        });

        return new PageSpecGenerationResult(largestSize, objectNamesPerPage, rootPins, results);
    }

    private List<String> proposeAbsenseSpecs(SuggestionTestResult results, List<PageItem> pageItems, Set<String> allObjectNames) {
        Set<String> allItemsOnCurrentPage = pageItems.stream().map(PageItem::getName).collect(Collectors.toSet());
        List<String> missingObjectNames = new LinkedList<>();

        allObjectNames.stream().filter(itemName -> !allItemsOnCurrentPage.contains(itemName)).forEach(itemName -> {
            results.getGeneratedObjectSpecs().put(itemName, singletonList(new SpecStatement("absent")));
            missingObjectNames.add(itemName);
        });

        return missingObjectNames;
    }

    private void removeDuplicatedElements(List<PageItem> convertedItems) {
        ListIterator<PageItem> it = convertedItems.listIterator();
        if (it.hasNext()) {
            PageItem item = it.next();
            while (it.hasNext()) {
                PageItem nextItem = it.next();
                if (nextItem.getArea().equals(item.getArea())) {
                    it.remove();
                } else {
                    item = nextItem;
                }
            }
        }
    }

    private Comparator<PageItem> bySizeAndLocation() {
        return (a, b) -> {
            int size = a.getArea().getWidth() * a.getArea().getHeight() - b.getArea().getWidth() * b.getArea().getHeight();
            if (size != 0) {
                return size;
            } else {
                int diff = a.getArea().getLeft() - b.getArea().getLeft();
                if (diff != 0) {
                    return diff;
                } else {
                    return a.getArea().getTop() - b.getArea().getTop();
                }
            }
        };
    }

    /**
     * Orders page items into a tree by their area. Tries to fit one item inside another
     * @param items
     * @return A list of pins which are root elements (don't have a parent)
     */
    private List<PageItemNode> restructurePageItems(List<PageItem> items) {
        List<PageItemNode> pins = items.stream().map(PageItemNode::new).collect(toList());
        for (PageItemNode pinA : pins) {
            for (PageItemNode pinB: pins) {
                if (pinA != pinB) {
                    if (isInside(pinA.getPageItem().getArea(), pinB.getPageItem().getArea())) {
                        if (pinB.getParent() == pinA) {
                            throw new RuntimeException(format("The following objects have identical areas: %s, %s. Please remove one of the objects", pinA.getPageItem().getName(), pinB.getPageItem().getName()));
                        }
                        pinA.moveToParent(pinB);
                        break;
                    }
                }
            }
        }
        return pins.stream().filter(pin -> pin.getParent() == null && pin.getChildren().size() > 0).collect(toList());
    }

    private SuggestionTestResult proposeSpecsFor(PageItemNode pin, List<String> objectNamesPerPage, SpecGeneratorOptions specGeneratorOptions) {
        SuggestionTestResult allResults = new SuggestionTestResult();

        SpecSuggester specSuggester = new SpecSuggester(new SuggestionOptions(objectNamesPerPage));
        if (pin.getParent() != null) {
            allResults.merge(specSuggester.suggestSpecsForTwoObjects(asList(pin.getParent(), pin), SpecSuggester.parentSuggestions, specGeneratorOptions));
        }

        if (pin.getChildren() != null && !pin.getChildren().isEmpty()) {
            List<PageItemNode> horizontallySortedPins = pin.getChildren();
            List<PageItemNode> verticallySortedPins = copySortedVertically(pin.getChildren());

            if (specGeneratorOptions.isUseGalenExtras()) {
                allResults.merge(specSuggester.suggestSpecsForMultipleObjects(horizontallySortedPins, SpecSuggester.horizontallyOrderComplexRulesSuggestions, specGeneratorOptions));
                allResults.merge(specSuggester.suggestSpecsForMultipleObjects(verticallySortedPins, SpecSuggester.verticallyOrderComplexRulesSuggestions, specGeneratorOptions));
            }
            allResults.merge(specSuggester.suggestSpecsRayCasting(pin, horizontallySortedPins, specGeneratorOptions));
            allResults.merge(specSuggester.suggestSpecsForSingleObject(horizontallySortedPins, SpecSuggester.singleItemSuggestions, specGeneratorOptions));
        }
        return allResults;
    }

    private void sortPinsHorizontally(List<PageItemNode> pins) {
        Collections.sort(pins, (a,b) -> {
            int ax = a.getPageItem().getArea().getLeft();
            int ay = a.getPageItem().getArea().getTop();
            int bx = b.getPageItem().getArea().getLeft();
            int by = b.getPageItem().getArea().getTop();

            if (ax != bx) {
                return ax - bx;
            } else {
                return ay - by;
            }
        });
    }

    private List<PageItemNode> copySortedVertically(List<PageItemNode> pins) {
        ArrayList<PageItemNode> sortedPins = new ArrayList<>(pins);
        Collections.sort(sortedPins, (a,b) -> {
            int ax = a.getPageItem().getArea().getLeft();
            int ay = a.getPageItem().getArea().getTop();
            int bx = b.getPageItem().getArea().getLeft();
            int by = b.getPageItem().getArea().getTop();

            if (ay != by) {
                return ay - by;
            } else {
                return ax - bx;
            }
        });
        return sortedPins;
    }

    private boolean isInside(Rect area, Rect areaParent) {
        for (Point p : area.getPoints()) {
            if (!areaParent.contains(p)) {
                return false;
            }
        }
        return true;
    }

    public static String generateSpecSections(PageSpecGenerationResult result) {
        StringBuilder finalSpec = new StringBuilder();
        GmPageSpec pageSpecGM = GmPageSpec.create(result);
        finalSpec.append(pageSpecGM.render());
        return finalSpec.toString();
    }

    public static String generatePageSpec(PageSpecGenerationResult result) {
        return new StringBuilder()
            .append(SpecGenerator.generateSpecSections(result))
            .toString();
    }
}

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

import com.galenframework.page.Point;
import com.galenframework.page.Rect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
    public static final String SECTION_BUILDER = "sectionBuilder".intern();
    private PageItemJsonMapper piJsonMapper = new PageItemJsonMapper();

    public PageSpecGenerationResult generate(InputStream stream) throws IOException {
        List<PageItem> pageItems = piJsonMapper.loadItems(stream);
        return generate(pageItems);
    }

    private PageSpecGenerationResult generate(List<PageItem> pageItems) {
        Set<String> allObjectNames = extractItemNamesOnAllPages(pageItems);
        return generate(pageItems, allObjectNames);
    }

    private Set<String> extractItemNamesOnAllPages(List<PageItem> pageItems) {
        return pageItems.stream().map(PageItem::getName).distinct().collect(Collectors.toSet());
    }

    private PageSpecGenerationResult generate(List<PageItem> pageItems, Set<String> allObjectNames) {
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
        rootPins.forEach(p -> p.visitTree(pin -> results.merge(proposeSpecsFor(pin, objectNamesPerPage))));

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

    private SuggestionTestResult proposeSpecsFor(PageItemNode pin, List<String> objectNamesPerPage) {
        SuggestionTestResult allResults = new SuggestionTestResult();

        SpecSuggester specSuggester = new SpecSuggester(new SuggestionOptions(objectNamesPerPage));
        if (pin.getParent() != null) {
            allResults.merge(specSuggester.suggestSpecsForTwoObjects(asList(pin.getParent(), pin), SpecSuggester.parentSuggestions));
        }

        if (pin.getChildren() != null && !pin.getChildren().isEmpty()) {
            List<PageItemNode> horizontallySortedPins = pin.getChildren();
            List<PageItemNode> verticallySortedPins = copySortedVertically(pin.getChildren());

            allResults.merge(specSuggester.suggestSpecsForMultipleObjects(horizontallySortedPins, SpecSuggester.horizontallyOrderComplexRulesSuggestions));
            allResults.merge(specSuggester.suggestSpecsForMultipleObjects(verticallySortedPins, SpecSuggester.verticallyOrderComplexRulesSuggestions));
            allResults.merge(specSuggester.suggestSpecsRayCasting(pin, horizontallySortedPins));
            allResults.merge(specSuggester.suggestSpecsForSingleObject(horizontallySortedPins, SpecSuggester.singleItemSuggestions));
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
        return generateSpecSections(result, "");
    }

    public static String generateSpecSections(PageSpecGenerationResult result, String initialIndentation) {
        Map<String, Map<String, List<SpecStatement>>> optimizedObjectSpecs = convertWithoutOptimizationObjectSpecs(
            result.getObjects(),
            result.getObjectNames(),
            result.getSuggestionResults().getGeneratedObjectSpecs()
        );

        sortSpecsWithinEachObject(optimizedObjectSpecs);

        StringBuilder finalSpec = new StringBuilder();
        List<Pair<String, StringBuilder>> sections = generateSpecSections(
            result.getObjects(),
            result.getSuggestionResults().getGeneratedRules(),
            optimizedObjectSpecs,
            initialIndentation
        );

        sections.forEach(section -> {
            String sectionText = section.getValue().toString();
            if (!sectionText.isEmpty()) {
                finalSpec.append(initialIndentation).append("= ").append(section.getKey()).append(" =\n");
                finalSpec.append(sectionText);
            }
        });
        return finalSpec.toString();
    }

    private static void sortSpecsWithinEachObject(Map<String, Map<String, List<SpecStatement>>> optimizedObjectSpecs) {
        optimizedObjectSpecs.forEach((name, objectSpecs) -> {
            objectSpecs.forEach((n, specs) -> {
                Collections.sort(specs, (a,b) -> a.getStatement().compareTo(b.getStatement()));
            });
        });
    }

    private static Map<String, Map<String, List<SpecStatement>>> convertWithoutOptimizationObjectSpecs(List<PageItemNode> objects, List<String> objectNames, Map<String, List<SpecStatement>> generatedObjectSpecs) {
        Map<String, Map<String, List<SpecStatement>>> converted = new HashMap<>();
        generatedObjectSpecs.forEach((name, list) -> {
            converted.put(name, new HashMap<String, List<SpecStatement>>(){{put(name, list);}});
        });
        return converted;
    }


    private static List<Pair<String, StringBuilder>> generateSpecSections(List<PageItemNode> objects, Map<String, List<SpecStatement>> generatedRules, Map<String, Map<String, List<SpecStatement>>> optimizedObjectSpecs, String indentation) {
        List<Pair<String, StringBuilder>> sections = new LinkedList<>();
        StringBuilder skeletonSectionBuilder = new StringBuilder();
        sections.add(new ImmutablePair<>("Skeleton", skeletonSectionBuilder));

        //TODO fix this for the cases when there are more than one root notes. Could be done by making sure that we always have single root element upfront. if not -> make a boundary box
        PageItemNode screenPin = objects.get(0);
        screenPin.setMetaData(SECTION_BUILDER, skeletonSectionBuilder);

        screenPin.getChildren().forEach(bigPin -> {
            StringBuilder sectionBuilder = new StringBuilder();
            sections.add(new ImmutablePair<>(bigPin.getPageItem().getName() + " elements", sectionBuilder));
            bigPin.visitTree(p -> {
                if (p == bigPin) {
                    p.setMetaData(SECTION_BUILDER, skeletonSectionBuilder);
                } else {
                    p.setMetaData(SECTION_BUILDER, sectionBuilder);
                }
            });
        });

        objects.forEach(p -> p.visitTree(pin -> {
            StringBuilder sectionBuilder = (StringBuilder)pin.getMeta(SECTION_BUILDER);

            if (generatedRules != null) {
                List<SpecStatement> rules = generatedRules.get(pin.getPageItem().getName());
                if (rules != null) {
                    rules.forEach((rule) -> sectionBuilder.append(indentation).append("    ").append(rule.getStatement()).append('\n'));
                    if (rules.size() > 0) {
                        sectionBuilder.append('\n');
                    }
                }
            }
            Map<String, List<SpecStatement>> objectSpecs = optimizedObjectSpecs.get(pin.getPageItem().getName());
            if (objectSpecs != null) {
                objectSpecs.forEach((name, specs) -> {
                    sectionBuilder.append(indentation).append("    ").append(name).append(":\n");
                    specs.forEach(spec -> sectionBuilder.append(indentation).append("    ").append("    ").append(spec.getStatement()).append('\n'));
                    sectionBuilder.append('\n');
                });
            }
        }));
        return sections;
    }

    private static boolean isItemABigPin(PageItemNode pin) {
        return pin.getParent() != null && pin.getParent().getPageItem().getName().equals("screen");
    }

    private static boolean isItemPlacedInsideBigPin(PageItemNode pin) {
        return pin.getParent() != null && pin.getParent().getParent() != null && pin.getParent().getParent().getPageItem().getName().equals("screen");
    }

    private static List<ObjectDeclaration> generateSpecObjectsDeclaration(List<String> objectNames, List<PageItemNode> objects) {
        int largestLength = objectNames.stream().max((a, b) -> a.length() > b.length()? 1: -1).get().length();
        List<ObjectDeclaration> objectDeclarations = new LinkedList<>();
        objects.forEach(pin -> generateSpecObjectsDeclaration(objectDeclarations, pin, objectNames, largestLength));
        return objectDeclarations;
    }

    private static void generateSpecObjectsDeclaration(List<ObjectDeclaration> objectDeclarations, PageItemNode pin, List<String> objectNames, int largestLength) {
        String pinName = pin.getPageItem().getName();
        if (!"screen".equals(pinName) && ! "viewport".equals(pinName)) {
            objectDeclarations.add(new ObjectDeclaration(pinName, "div[data-id=\"" + pinName + "\"]"));
        }
        if (pin.getChildren() != null && !pin.getChildren().isEmpty()) {
            PageItemNode[] childPins = pin.getChildren().toArray(new PageItemNode[pin.getChildren().size()]);

            String namePattern = findNamingPattern(objectNames, childPins);
            if (namePattern != null) {
                String itemCssName = patternToCssName(namePattern);
                objectDeclarations.add(new ObjectDeclaration(namePattern, "ul." + itemCssName + " > li"));
                //TODO think about child elements in each of these
            } else {
                for (PageItemNode childPin : childPins) {
                    generateSpecObjectsDeclaration(objectDeclarations, childPin, objectNames, largestLength);
                }
            }
        }
    }

    public static String generateHtml(List<String> objectNames, List<PageItemNode> objects) {
        StringBuilder s = new StringBuilder(
            "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n");
        objects.forEach(pin -> generateHtml(s, objectNames, pin, "  "));
        s.append("</body>\n");
        s.append("</html>");

        return s.toString();
    }

    private static void generateHtml(StringBuilder s, List<String> objectNames, PageItemNode pin, String indentation) {
        if (!pin.getPageItem().getName().equals("screen")) {
            s.append(indentation).append("<div data-id=\"").append(pin.getPageItem().getName()).append("\">");
        }

        if (pin.getChildren() != null && !pin.getChildren().isEmpty()) {
            s.append('\n');

            PageItemNode[] childPins = pin.getChildren().toArray(new PageItemNode[pin.getChildren().size()]);

            String namePattern = findNamingPattern(objectNames, childPins);
            if (namePattern != null) {
                String itemCssName = patternToCssName(namePattern);

                s.append(indentation).append("  ").append("<ul class=\"").append(itemCssName).append("\">\n");
                for (PageItemNode childPin : childPins) {
                    s.append(indentation).append("    ").append("<li>");
                    if (childPin.getChildren() != null && !childPin.getChildren().isEmpty()) {
                        s.append('\n');
                        childPin.getChildren().forEach(childChildPin -> generateHtml(s, objectNames, childChildPin, indentation + "      "));

                        s.append(indentation).append("    ");
                    } else {
                        s.append("  ");
                    }
                    s.append("</li>\n");
                }
                s.append(indentation).append("  ").append("</ul>\n");
            } else {
                for (PageItemNode childPin : childPins) {
                    generateHtml(s, objectNames, childPin, indentation + "  ");
                }
            }

            s.append(indentation);
        } else {
            s.append("  ");
        }

        if (!pin.getPageItem().getName().equals("screen")) {
            s.append("</div>\n");
        }
    }

    public static String generateObjectDeclaration(PageSpecGenerationResult result) {
        final Set<String> knownItemNames = new HashSet<>();
        final StringBuilder s = new StringBuilder("@objects\n");
        List<ObjectDeclaration> objectDeclarations = new LinkedList<>();

        final int[] maxSymbolsArr = {0};
        generateSpecObjectsDeclaration(result.getObjectNames(), result.getObjects()).stream().forEach(objectDeclaration -> {
            if (!knownItemNames.contains(objectDeclaration.getObjectName())) {
                objectDeclarations.add(objectDeclaration);
                knownItemNames.add(objectDeclaration.getObjectName());

                int size = objectDeclaration.getObjectName().length();
                if (maxSymbolsArr[0] < size) {
                    maxSymbolsArr[0] = size;
                }
            }
        });

        int maxSymbols = maxSymbolsArr[0];

        for (ObjectDeclaration objectDeclaration: objectDeclarations) {
            s.append("    ")
                .append(objectDeclaration.getObjectName())
                .append(makeSpaces(2 + maxSymbols - objectDeclaration.getObjectName().length()))
                .append(objectDeclaration.getLocator())
                .append("\n");
        }

        return s.toString();
    }

    private static String makeSpaces(int amount) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            s.append(' ');
        }
        return s.toString();
    }

    private static String patternToCssName(String namePattern) {
        return namePattern.replaceAll("[^A-Za-z0-9]", " ").trim().replace(" ", "-");
    }

    public static String generatePageSpec(PageSpecGenerationResult result) {
        return new StringBuilder()
            .append(SpecGenerator.generateObjectDeclaration(result))
            .append('\n')
            .append(SpecGenerator.generateSpecSections(result))
            .toString();
    }
}

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
package com.galenframework.speclang2.pagespec;

import com.galenframework.parser.ExpectWord;
import com.galenframework.parser.Expectations;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.page.Page;
import com.galenframework.specs.page.Locator;
import com.galenframework.parser.StringCharReader;
import com.galenframework.utils.GalenUtils;

import java.util.*;

import static java.lang.String.format;

public class ObjectDefinitionProcessor {
    public static final String GROUPED = "@grouped";
    private final PageSpecHandler pageSpecHandler;
    private static final String CORRECTIONS_SYMBOL = "@";

    public ObjectDefinitionProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    private Stack<List<String>> groupStack = new Stack<>();

    public List<StructNode> process(StringCharReader reader, StructNode structNode) {
        if (!reader.getTheRest().isEmpty()) {
            throw new SyntaxException(structNode.getPlace(), "Objects definition does not take any arguments");
        }

        if (structNode.getChildNodes() != null) {
            groupStack = new Stack<>();

            for (StructNode childNode : structNode.getChildNodes()) {
                parseItem(childNode);
            }
        }
        return Collections.emptyList();
    }

    private void parseItem(StructNode objectNode) {
        parseItem(objectNode, null, null);
    }

    private void parseItem(StructNode objectNode, String parentName, Locator parentLocator) {
        processObject(objectNode, parentName, parentLocator);
    }

    private void processObject(StructNode objectNode, String parentName, Locator parentLocator) {
        StringCharReader reader = new StringCharReader(pageSpecHandler.processExpressionsIn(objectNode).getName());

        String objectName = reader.readWord();

        if (parentName != null) {
            objectName = parentName + "." + objectName;
        }

        String locatorText = null;
        List<String> groups = null;
        CorrectionsRect corrections = null;

        while(reader.hasMore()) {
            String word = expectCorrectionsOrId(objectNode, reader, objectName);

            if (word.equals(CORRECTIONS_SYMBOL)) {
                corrections = Expectations.corrections().read(reader);
            } else if (word.equals(GROUPED)) {
                groups = parseInlineGroupsInBrackets(reader);
            }
            else {
                locatorText = word + reader.getTheRest();
                reader.moveToTheEnd();
            }
        }

        if (locatorText == null) {
            throw new SyntaxException("Missing locator");
        }

        Locator locator = readLocatorFromString(objectNode, objectName, locatorText.trim());
        locator.setCorrections(corrections);

        if (parentLocator != null) {
            locator.setParent(parentLocator);
        }

        if (objectName.contains("*")) {
            addMultiObjectsToSpec(objectNode, objectName, locator, groups);
        } else {
            addObjectToSpec(objectNode, objectName, locator, groups);
        }
    }

    private List<String> parseInlineGroupsInBrackets(StringCharReader reader) {
        if (reader.firstNonWhiteSpaceSymbol() == '(') {
            reader.readUntilSymbol('(');
            return GalenUtils.fromCommaSeparated(reader.readUntilSymbol(')'));
        } else {
            throw new SyntaxException("Missing '(' for group definitions");
        }
    }

    private void addObjectToSpec(StructNode objectNode, String objectName, Locator locator, List<String> groupsForThisObject) {
        if (!objectName.matches("[0-9a-zA-Z_\\.\\-]*")) {
            throw new SyntaxException("Invalid object name: " + objectName);
        }
        pageSpecHandler.addObjectToSpec(objectName, locator);

        List<String> allCurrentGroups = getAllCurrentGroups();
        if (allCurrentGroups != null && !allCurrentGroups.isEmpty()) {
            pageSpecHandler.applyGroupsToObject(objectName, allCurrentGroups);
        }

        if (groupsForThisObject != null && !groupsForThisObject.isEmpty()) {
            pageSpecHandler.applyGroupsToObject(objectName, groupsForThisObject);
        }

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {
            for (StructNode subObjectNode : objectNode.getChildNodes()) {
                parseItem(pageSpecHandler.processExpressionsIn(subObjectNode), objectName, locator);
            }
        }
    }


    private void addMultiObjectsToSpec(StructNode objectNode, String objectName, Locator locator, List<String> groupsForThisObject) {
        Page page = pageSpecHandler.getPage();
        int count = page.getObjectCount(locator);

        for (int index = 1; index <= count; index++) {
            addObjectToSpec(objectNode, objectName.replace("*", Integer.toString(index)),
                    new Locator(locator.getLocatorType(), locator.getLocatorValue(), index).withParent(locator.getParent()),
                    groupsForThisObject);
        }
    }

    private Locator readLocatorFromString(StructNode structNode, String objectName, String locatorText) {

        if (locatorText.isEmpty()) {
            throw new SyntaxException(structNode.getPlace(),
                    "Missing locator for object \"" + objectName + "\"");
        }

        StringCharReader reader = new StringCharReader(locatorText);

        String firstWord = reader.readWord();
        String locatorValue = reader.getTheRest().trim();

        if ("id".equals(firstWord) ||
                "css".equals(firstWord) ||
                "xpath".equals(firstWord)) {
            return createLocator(objectName, firstWord, locatorValue);
        }
        else {
            return identifyLocator(locatorText);
        }
    }

    private Locator identifyLocator(String locatorText) {
        if (locatorText.startsWith("/")) {
            return new Locator("xpath", locatorText);
        }
        else {
            return new Locator("css", locatorText);
        }
    }

    private Locator createLocator(String objectName, String type, String value) {
        if (value == null || value.isEmpty()) {
            throw new SyntaxException("Locator for object \"" + objectName + "\" is not defined correctly");
        }
        return new Locator(type, value);
    }

    private String expectCorrectionsOrId(StructNode structNode, StringCharReader reader, String objectName) {
        String word = new ExpectWord().stopOnTheseSymbols('(').read(reader).trim();
        if (word.isEmpty()) {
            throw new SyntaxException(structNode.getPlace(),
                    format("Missing locator for object \"%s\"", objectName));
        }
        return word;
    }

    private List<String> getAllCurrentGroups() {
        List<String> allCurrentGroups = new LinkedList<>();

        Iterator<List<String>> it = groupStack.iterator();
        while(it.hasNext()) {
            for (String groupName : it.next()) {
                if (!allCurrentGroups.contains(groupName)) {
                    allCurrentGroups.add(groupName);
                }
            }
        }
        return allCurrentGroups;
    }
}

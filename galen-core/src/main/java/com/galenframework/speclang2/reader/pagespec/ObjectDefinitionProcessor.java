/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.speclang2.reader.pagespec;

import com.galenframework.parser.ExpectWord;
import com.galenframework.parser.Expectations;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.page.Page;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.suite.reader.Line;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public class ObjectDefinitionProcessor {
    private final PageSpecHandler pageSpecHandler;
    private static final String CORRECTIONS_SYMBOL = "@";

    public ObjectDefinitionProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode structNode) {
        if (!reader.getTheRest().isEmpty()) {
            throw new SyntaxException(new Line(structNode.getSource(), structNode.getFileLineNumber()), "Objects definition does not take any arguments");
        }

        if (structNode.getChildNodes() != null) {
            for (StructNode childNode : structNode.getChildNodes()) {
                parseObject(childNode);
            }
        }
        return Collections.emptyList();
    }


    private void parseObject(StructNode objectNode) {
        parseObject(objectNode, null, null);
    }

    private void parseObject(StructNode objectNode, String parentName, Locator parentLocator) {
        StringCharReader reader = new StringCharReader(objectNode.getName());

        String objectName = reader.readWord();

        if (parentName != null) {
            objectName = parentName + "." + objectName;
        }

        String word = expectCorrectionsOrId(objectNode, reader, objectName);
        String locatorText;

        CorrectionsRect corrections = null;
        if (word.equals(CORRECTIONS_SYMBOL)) {
            corrections = Expectations.corrections().read(reader);
            locatorText = reader.getTheRest();
        }
        else {
            locatorText = word + reader.getTheRest();
        }

        Locator locator = readLocatorFromString(objectNode, objectName, locatorText.trim());
        locator.setCorrections(corrections);

        if (parentLocator != null) {
            locator.setParent(parentLocator);
        }

        if (objectName.contains("*")) {
            addMultiObjectsToSpec(objectNode, objectName, locator);
        } else {
            addObjectToSpec(objectNode, objectName, locator);
        }
    }

    private void addObjectToSpec(StructNode objectNode, String objectName, Locator locator) {
        pageSpecHandler.addObjectToSpec(objectName, locator);

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {
            for (StructNode subObjectNode : objectNode.getChildNodes()) {
                parseObject(pageSpecHandler.processExpressionsIn(subObjectNode), objectName, locator);
            }
        }
    }

    private void addMultiObjectsToSpec(StructNode objectNode, String objectName, Locator locator) {
        Page page = pageSpecHandler.getPage();
        int count = page.getObjectCount(locator);

        for (int index = 1; index <= count; index++) {
            addObjectToSpec(objectNode, objectName.replace("*", Integer.toString(index)),
                    new Locator(locator.getLocatorType(), locator.getLocatorValue(), index));
        }
    }

    private Locator readLocatorFromString(StructNode structNode, String objectName, String locatorText) {

        if (locatorText.isEmpty()) {
            throw new SyntaxException(new Line(structNode.getSource(), structNode.getFileLineNumber()),
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
            throw new SyntaxException(new Line(structNode.getSource(), structNode.getFileLineNumber()),
                    format("Missing locator for object \"%s\"", objectName));
        }
        return word;
    }
}

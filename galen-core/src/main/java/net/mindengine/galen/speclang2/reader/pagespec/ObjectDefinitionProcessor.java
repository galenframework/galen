/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.speclang2.reader.pagespec;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.parser.ExpectWord;
import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.CorrectionsRect;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.suite.reader.Line;

import static java.lang.String.format;

public class ObjectDefinitionProcessor {
    private final PageSpecProcessor pageSpecProcessor;
    private static final String CORRECTIONS_SYMBOL = "@";

    public ObjectDefinitionProcessor(PageSpecProcessor pageSpecProcessor) {
        this.pageSpecProcessor = pageSpecProcessor;
    }

    public void process(StringCharReader reader, StructNode structNode) {
        if (!reader.getTheRest().isEmpty()) {
            throw new SyntaxException(new Line(structNode.getSource(), structNode.getFileLineNumber()), "Objects definition does not take any arguments");
        }


        if (structNode.getChildNodes() != null) {
            for (StructNode childNode : structNode.getChildNodes()) {
                parseObject(childNode);
            }
        }
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
        pageSpecProcessor.addObjectToSpec(objectName, locator);

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {
            for (StructNode subObjectNode : objectNode.getChildNodes()) {
                parseObject(subObjectNode, objectName, locator);
            }
        }
    }

    private void addMultiObjectsToSpec(StructNode objectNode, String objectName, Locator locator) {
        Page page = pageSpecProcessor.getBrowser().getPage();
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

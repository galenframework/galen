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

    private void parseObject(StructNode childNode) {
        StringCharReader reader = new StringCharReader(childNode.getName());

        String objectName = reader.readWord();

        String word = expectCorrectionsOrId(childNode, reader, objectName);
        String locatorText;

        CorrectionsRect corrections = null;
        if (word.equals(CORRECTIONS_SYMBOL)) {
            corrections = Expectations.corrections().read(reader);
            locatorText = reader.getTheRest();
        }
        else {
            locatorText = word + reader.getTheRest();
        }

        Locator locator = readLocatorFromString(childNode, objectName, locatorText.trim());
        locator.setCorrections(corrections);
        pageSpecProcessor.addObjectToSpec(objectName, locator);

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

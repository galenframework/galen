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
package net.mindengine.galen.specs.reader.page;

import static java.lang.String.format;
import static net.mindengine.galen.parser.Expectations.word;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import net.mindengine.galen.parser.ExpectWord;
import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.specs.page.CorrectionsRect;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.specs.reader.StringCharReader;

import org.apache.commons.lang3.StringUtils;

public class StateObjectDefinition extends State {

    private static final String CORRECTIONS_SYMBOL = "@";
    private PageSpec pageSpec;
    private PageSpecReader pageSpecReader;

    public StateObjectDefinition(PageSpec pageSpec, PageSpecReader pageSpecReader) {
        this.pageSpec = pageSpec;
        this.pageSpecReader = pageSpecReader;
    }

    @Override
    public void process(VarsContext varsContext, String line, Place place) {
        line = varsContext.process(line);
        StringCharReader reader = new StringCharReader(line);
        
        String objectName = expectWord(reader, "Object name is not defined correctly");
        
        try {
            String word = expectCorrectionsOrId(reader, objectName);
            String locatorText;

            CorrectionsRect corrections = null;
            if (word.equals(CORRECTIONS_SYMBOL)) {
                corrections = Expectations.corrections().read(reader);
                locatorText = reader.getTheRest();
            }
            else {
                locatorText = word + reader.getTheRest();
            }

            Locator locator = readLocatorFromString(objectName, locatorText.trim());
            locator.setCorrections(corrections);
            addObjectToSpec(objectName, locator);
        }
        catch (SyntaxException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SyntaxException(UNKNOWN_LINE, "Object \"" + objectName + "\" has incorrect locator", e);
        }
    }

    private Locator readLocatorFromString(String objectName, String locatorText) {

        if (locatorText.isEmpty()) {
            throw new SyntaxException("Missing locator for object \"" + objectName + "\"");
        }

        StringCharReader reader = new StringCharReader(locatorText);

        String firstWord = word().read(reader);
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

    private void addObjectToSpec(String objectName, Locator locator) {
        if (objectName.contains("*")) {
            addMultiObject(objectName, locator);
        }
        else {
            pageSpec.addObject(objectName, locator);
        }
    }

    private void addMultiObject(String objectName, Locator locator) {
        if (StringUtils.countMatches(objectName, "*") > 1) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect object name: " + objectName);
        }
        else {
            
            if (pageSpecReader.getPage() != null) {
                pageSpec.updateMultiObject(pageSpecReader.getPage(), objectName, locator);
            }
            else {
                pageSpec.addMultiObject(objectName, locator);
            }
        }
    }

    private String expectCorrectionsOrId(StringCharReader reader, String objectName) {
        String word = new ExpectWord().stopOnTheseSymbols('(').read(reader).trim();
        if (word.isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, format("Missing locator for object \"%s\"", objectName));
        }
        return word;
    }

    private String expectWord(StringCharReader reader, String errorMessage) {
        String word = new ExpectWord().read(reader).trim();
        if (word.isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, errorMessage);
        }
        return word;
    }

}

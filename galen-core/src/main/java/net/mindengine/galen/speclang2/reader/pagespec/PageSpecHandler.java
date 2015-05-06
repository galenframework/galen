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

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.javascript.GalenJsExecutor;
import net.mindengine.galen.parser.*;
import net.mindengine.galen.speclang2.reader.specs.SpecReaderV2;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.suite.reader.Context;

import java.util.*;


public class PageSpecHandler implements VarsParserJsFunctions {
    private final PageSpec pageSpec;
    private final Browser browser;
    private SpecReaderV2 specReaderV2 = new SpecReaderV2();
    private GalenJsExecutor jsExecutor = new GalenJsExecutor();
    private VarsParser varsParser = new VarsParser(new Context(), new Properties(), jsExecutor);
    private List<String> tags;

    public PageSpecHandler(PageSpec pageSpec, Browser browser, List<String> tags) {
        this.pageSpec = pageSpec;
        this.browser = browser;
        this.tags = tags;
    }

    public PageSpec buildPageSpec() {
        return pageSpec;
    }

    public Browser getBrowser() {
        return browser;
    }


    public void addSection(PageSection section) {
        pageSpec.addSection(section);
    }

    public SpecReaderV2 getSpecReaderV2() {
        return specReaderV2;
    }

    public void setSpecReaderV2(SpecReaderV2 specReaderV2) {
        this.specReaderV2 = specReaderV2;
    }

    public void addObjectToSpec(String objectName, Locator locator) {
        pageSpec.addObject(objectName, locator);
    }

    public List<String> getSortedObjectNames() {
        List<String> list = new ArrayList<String>(pageSpec.getObjects().keySet());
        Collections.sort(list);
        return list;
    }


    @Override
    public int count(String regex) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public JsPageElement find(String name) {
        throw new RuntimeException("not yet implemented");
    }

    @Override
    public JsPageElement[] findAll(String regex) {
        throw new RuntimeException("not yet implemented");
    }

    public void setGlobalVariable(String name, String value, StructNode source) {
        if (!isValidVariableName(name)) {
            throw new SyntaxException(source, "Invalid name for variable");
        }
        jsExecutor.putObject(name, value);
    }

    private boolean isValidVariableName(String name) {
        if (name.isEmpty()) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            int symbol = (int)name.charAt(i);
            if (!(symbol > 64 && symbol < 91) //checking uppercase letters
                    && !(symbol > 96 && symbol < 123) //checking lowercase letters
                    && !(symbol > 47 && symbol < 58 && i > 0) //checking numbers and that its not the first letter
                    && symbol != 95) /*underscore*/ {
                return false;
            }
        }
        return true;
    }


    public VarsParser getVarsParser() {
        return varsParser;
    }

    public void setVarsParser(VarsParser varsParser) {
        this.varsParser = varsParser;
    }

    public StructNode processExpressionsIn(StructNode originNode) {
        String result = getVarsParser().parse(originNode.getName());

        StructNode processedNode = new StructNode(result);
        processedNode.setFileLineNumber(originNode.getFileLineNumber());
        processedNode.setSource(originNode.getSource());
        processedNode.setChildNodes(originNode.getChildNodes());
        return processedNode;
    }

    public void setGlobalVariables(Map<String, String> variables, StructNode originNode) {
        for(Map.Entry<String, String> variable : variables.entrySet()) {
            setGlobalVariable(variable.getKey(), variable.getValue(), originNode);
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

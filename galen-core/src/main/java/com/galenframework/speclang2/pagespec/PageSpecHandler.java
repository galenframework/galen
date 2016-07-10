/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import com.galenframework.page.selenium.ScreenElement;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.page.selenium.ViewportElement;
import com.galenframework.parser.*;
import com.galenframework.specs.page.PageSection;
import com.galenframework.javascript.GalenJsExecutor;
import com.galenframework.page.AbsentPageElement;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.speclang2.specs.SpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.rules.Rule;
import com.galenframework.speclang2.pagespec.rules.RuleParser;
import com.galenframework.suite.reader.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import org.mozilla.javascript.*;


public class PageSpecHandler implements VarsParserJsFunctions {
    private final PageSpec pageSpec;
    private final Page page;
    private final String contextPath;
    private final SpecReader specReader;
    private final GalenJsExecutor jsExecutor;
    private final VarsParser varsParser;
    private final List<Pair<Rule, PageRule>> pageRules;
    private final List<String> processedImports = new LinkedList<>();
    private final List<String> processedScripts = new LinkedList<>();
    private final Properties properties;
    private final Map<String, Object> jsVariables;
    private final SectionFilter sectionFilter;

    public PageSpecHandler(PageSpec pageSpec, Page page,
                           SectionFilter sectionFilter,
                           String contextPath, Properties properties,
                           Map<String, Object> jsVariables
    ) {
        this.pageSpec = pageSpec;
        this.page = page;
        this.sectionFilter = sectionFilter;
        this.contextPath = contextPath;
        this.specReader = new SpecReader();
        this.jsExecutor = createGalenJsExecutor(this);
        this.pageRules = new LinkedList<>();
        this.jsVariables = jsVariables;

        if (properties != null) {
            this.properties = properties;
        } else {
            this.properties = new Properties();
        }
        this.varsParser = new VarsParser(new Context(), this.properties, jsExecutor);

        if (jsVariables != null) {
            setGlobalVariables(jsVariables);
        }
    }



    public PageSpecHandler(PageSpecHandler copy, String contextPath) {
        this.pageSpec = copy.pageSpec;
        this.page = copy.page;
        this.contextPath = contextPath;
        this.specReader = copy.specReader;
        this.jsExecutor = copy.jsExecutor;
        this.varsParser = copy.varsParser;
        this.sectionFilter = copy.sectionFilter;
        this.pageRules = copy.pageRules;
        this.properties = copy.properties;
        this.jsVariables = copy.jsVariables;
    }

    private static GalenJsExecutor createGalenJsExecutor(final PageSpecHandler pageSpecHandler) {
        GalenJsExecutor js = new GalenJsExecutor();
        js.putObject("_pageSpecHandler", pageSpecHandler);
        js.evalScriptFromLibrary("GalenSpecProcessing.js");

        if (pageSpecHandler.page instanceof SeleniumPage) {
            SeleniumPage seleniumPage = (SeleniumPage) pageSpecHandler.page;
            js.putObject("screen", new JsPageElement("screen", new ScreenElement(seleniumPage.getDriver())));
            js.putObject("viewport", new JsPageElement("viewport", new ViewportElement(seleniumPage.getDriver())));
        }

        js.getScope().defineProperty("isVisible", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                if (args.length == 0) {
                    throw new IllegalArgumentException("Should take string argument, got nothing");
                }

                if (args[0] == null) {
                    throw new IllegalArgumentException("Object name should be null");
                }
                return pageSpecHandler.isVisible(args[0].toString());
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("isPresent", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                if (args.length == 0) {
                    throw new IllegalArgumentException("Should take string argument, got nothing");
                }
                if (args[0] == null) {
                    throw new IllegalArgumentException("Object name should be null");
                }
                return pageSpecHandler.isPresent(args[0].toString());
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("count", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                if (args.length == 0) {
                    throw new IllegalArgumentException("Should take string argument, got nothing");
                }
                if (args[0] == null) {
                    throw new IllegalArgumentException("Object name should be null");
                }
                return pageSpecHandler.count(args[0].toString());
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("find", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return pageSpecHandler.find(getSingleStringArgument(args));
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("findAll", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return pageSpecHandler.findAll(getSingleStringArgument(args));
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("first", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return pageSpecHandler.first(getSingleStringArgument(args));
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("last", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return pageSpecHandler.last(getSingleStringArgument(args));
            }
        }, ScriptableObject.DONTENUM);
        return js;
    }

    private static String getSingleStringArgument(Object[] args) {
        String singleArgument = null;
        if (args.length == 0) {
            throw new IllegalArgumentException("Should take one string argument, got none");
        } else if (args[0] == null) {
            throw new IllegalArgumentException("Pattern should not be null");
        } else if (args[0] instanceof NativeJavaObject) {
            NativeJavaObject njo = (NativeJavaObject) args[0];
            singleArgument = njo.unwrap().toString();
        } else {
            singleArgument = args[0].toString();
        }

        return singleArgument;
    }

    public Object isVisible(String objectName) {
        for (Map.Entry<String, Locator> object : pageSpec.getObjects().entrySet()) {
            if (object.getKey().equals(objectName)) {
                PageElement pageElement = page.getObject(object.getKey(), object.getValue());

                return pageElement != null && pageElement.isPresent() && pageElement.isVisible();
            }
        }

        return Boolean.FALSE;
    }

    public Object isPresent(String objectName) {
        for (Map.Entry<String, Locator> object : pageSpec.getObjects().entrySet()) {
            if (object.getKey().equals(objectName)) {
                PageElement pageElement = page.getObject(object.getKey(), object.getValue());
                return pageElement != null && pageElement.isPresent();
            }
        }
        return Boolean.FALSE;
    }

    public PageSpec buildPageSpec() {
        PageSpec cleanedSpec = new PageSpec();
        cleanedSpec.setObjects(pageSpec.getObjects());
        cleanedSpec.setSections(cleanEmptySections(pageSpec.getSections()));
        cleanedSpec.setObjectGroups(pageSpec.getObjectGroups());
        return cleanedSpec;
    }

    private List<PageSection> cleanEmptySections(List<PageSection> sections) {
        List<PageSection> cleanedSections = new LinkedList<>();

        for (PageSection pageSection : sections) {
            PageSection cleanedSection = pageSection.cleanSection();

            if (!pageSection.isEmpty()) {
                cleanedSections.add(cleanedSection);
            }
        }

        return cleanedSections;
    }


    public void addSection(PageSection section) {
        PageSection sameSection = findSection(section.getName());
        if (sameSection != null) {
            sameSection.mergeSection(section);
        } else {
            pageSpec.addSection(section);
        }
    }

    private PageSection findSection(String name) {
        for (PageSection pageSection : pageSpec.getSections()) {
            if (pageSection.getName().equals(name)) {
                return pageSection;
            }
        }
        return null;
    }

    public SpecReader getSpecReader() {
        return specReader;
    }

    public void addObjectToSpec(String objectName, Locator locator) {
        pageSpec.addObject(objectName, locator);
    }



    @Override
    public int count(String regex) {
        List<String> objectNames = pageSpec.findOnlyExistingMatchingObjectNames(regex);
        return objectNames.size();
    }

    @Override
    public JsPageElement find(String name) {
        List<String> objectNames = pageSpec.findOnlyExistingMatchingObjectNames(name);
        if (!objectNames.isEmpty()) {
            String objectName = objectNames.get(0);

            Locator locator = pageSpec.getObjects().get(objectName);
            if (locator != null && page != null) {
                PageElement pageElement = page.getObject(objectName, locator);
                if (pageElement != null) {
                    return new JsPageElement(objectName, pageElement);
                }
            }
        }

        return new JsPageElement(name, new AbsentPageElement());
    }

    @Override
    public JsPageElement[] findAll(String objectsStatements) {
        List<String> objectNames = pageSpec.findAllObjectsMatchingStrictStatements(objectsStatements);
        List<JsPageElement> jsElements = new ArrayList<>(objectNames.size());

        for (String objectName : objectNames) {
            Locator locator = pageSpec.getObjects().get(objectName);
            PageElement pageElement = null;
            if (locator != null) {
                pageElement = page.getObject(objectName, locator);
            }

            if (pageElement != null) {
                jsElements.add(new JsPageElement(objectName, pageElement));
            } else {
                jsElements.add(new JsPageElement(objectName, new AbsentPageElement()));
            }
        }
        return jsElements.toArray(new JsPageElement[jsElements.size()]);
    }

    @Override
    public JsPageElement first(String objectsStatements) {
        return extractSingleElement(objectsStatements, list -> list.get(0));
    }

    @Override
    public JsPageElement last(String objectsStatements) {
        return extractSingleElement(objectsStatements, list -> list.get(list.size() - 1));
    }

    private JsPageElement extractSingleElement(String objectsStatements, FilterFunction<String> filterFunction) {
        List<String> objectNames = pageSpec.findAllObjectsMatchingStrictStatements(objectsStatements);

        PageElement pageElement = null;
        String objectName = objectsStatements;

        if (!objectNames.isEmpty()) {
            objectName = filterFunction.filter(objectNames);

            Locator locator = pageSpec.getObjects().get(objectName);
            if (locator != null) {
                pageElement = page.getObject(objectName, locator);
            }
        }

        if (pageElement != null) {
            return new JsPageElement(objectName, pageElement);
        } else {
            return new JsPageElement(objectName, new AbsentPageElement());
        }
    }


    public void setGlobalVariable(String name, Object value, StructNode source) {
        if (!isValidVariableName(name)) {
            throw new SyntaxException(source, "Invalid name for variable: " + name);
        }

        if (value != null && value instanceof NativeJavaObject) {
            jsExecutor.putObject(name, ((NativeJavaObject) value).unwrap());
        } else {
            jsExecutor.putObject(name, value);
        }
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

    public StructNode processExpressionsIn(StructNode originNode) {

        String result;
        try {
            result = getVarsParser().parse(originNode.getName());
        } catch (Exception ex) {
            throw new SyntaxException(originNode, "JavaScript error inside statement", ex);
        }

        StructNode processedNode = new StructNode(result);
        processedNode.setLine(originNode.getLine());
        processedNode.setChildNodes(originNode.getChildNodes());
        return processedNode;
    }

    public void setGlobalVariables(Map<String, Object> variables, StructNode originNode) {
        for(Map.Entry<String, Object> variable : variables.entrySet()) {
            setGlobalVariable(variable.getKey(), variable.getValue(), originNode);
        }
    }

    public void setGlobalVariables(Map<String, Object> variables) {
        setGlobalVariables(variables, StructNode.UNKNOWN_SOURCE);
    }

    public String getContextPath() {
        return contextPath;
    }

    public List<PageSection> getPageSections() {
        return pageSpec.getSections();
    }

    public void runJavaScriptFromFile(String scriptPath) {
        jsExecutor.runJavaScriptFromFile(scriptPath);
    }

    public String getFullPathToResource(String scriptPath) {
        if (contextPath != null) {
            return contextPath + "/" + scriptPath;
        } else {
            return scriptPath;
        }
    }

    public void addRule(String ruleText, PageRule pageRule) {
        Rule rule = new RuleParser().parse(ruleText);
        pageRules.add(new ImmutablePair<>(rule, pageRule));
    }

    public List<Pair<Rule, PageRule>> getPageRules() {
        return pageRules;
    }

    public void runJavaScript(String completeScript) {
        jsExecutor.eval(completeScript);
    }

    public List<String> getProcessedImports() {
        return processedImports;
    }

    public List<String> getProcessedScripts() {
        return processedScripts;
    }

    public Page getPage() {
        return page;
    }

    public Properties getProperties() {
        return properties;
    }

    public Map<String, Object> getJsVariables() {
        return jsVariables;
    }

    public SectionFilter getSectionFilter() {
        return sectionFilter;
    }

    public void applyGroupsToObject(String objectName, List<String> groups) {
        if (!objectName.isEmpty()) {
            for (String groupName : groups) {
                groupName = groupName.trim();

                List<String> groupObjectsList = pageSpec.getObjectGroups().get(groupName);
                if (groupObjectsList != null) {
                    if (!groupObjectsList.contains(objectName)) {
                        groupObjectsList.add(objectName);
                    }
                } else {
                    groupObjectsList = new LinkedList<>();
                    groupObjectsList.add(objectName);
                    pageSpec.getObjectGroups().put(groupName, groupObjectsList);
                }
            }
        }
    }

    public List<String> findAllObjectsMatchingStrictStatements(String objectStatements) {
        return pageSpec.findAllObjectsMatchingStrictStatements(objectStatements);
    }
}

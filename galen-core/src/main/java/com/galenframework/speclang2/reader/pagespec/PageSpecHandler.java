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

import com.galenframework.parser.*;
import com.galenframework.speclang2.AlphanumericComparator;
import com.galenframework.specs.page.PageSection;
import com.galenframework.javascript.GalenJsExecutor;
import com.galenframework.page.AbsentPageElement;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.speclang2.reader.specs.SpecReader;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.reader.page.PageSpec;
import com.galenframework.specs.reader.page.SectionFilter;
import com.galenframework.specs.reader.page.rules.Rule;
import com.galenframework.specs.reader.page.rules.RuleParser;
import com.galenframework.suite.reader.Context;
import com.galenframework.utils.GalenUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Pattern;

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
                String pattern = null;

                if (args.length == 0) {
                    throw new IllegalArgumentException("Should take one string argument, got none");
                } else if (args[0] == null) {
                    throw new IllegalArgumentException("Pattern should not be null");
                } else if (args[0] instanceof NativeJavaObject) {
                    NativeJavaObject njo = (NativeJavaObject) args[0];
                    pattern = njo.unwrap().toString();
                } else {
                    pattern = args[0].toString();
                }
                return pageSpecHandler.find(pattern);
            }
        }, ScriptableObject.DONTENUM);

        js.getScope().defineProperty("findAll", new BaseFunction() {
            @Override
            public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                String pattern;

                if (args.length == 0) {
                    throw new IllegalArgumentException("Should take one string argument, got none");
                } else if (args[0] == null) {
                    throw new IllegalArgumentException("Pattern should not be null");
                } else if (args[0] instanceof NativeJavaObject) {
                    NativeJavaObject njo = (NativeJavaObject) args[0];
                    pattern = njo.unwrap().toString();
                } else {
                    pattern = args[0].toString();
                }

                return pageSpecHandler.findAll(pattern);
            }
        }, ScriptableObject.DONTENUM);

        return js;
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

    public List<String> getSortedObjectNames() {
        List<String> list = new ArrayList<String>(pageSpec.getObjects().keySet());
        Collections.sort(list, new AlphanumericComparator());
        return list;
    }


    @Override
    public int count(String regex) {
        Pattern pattern = GalenUtils.convertObjectNameRegex(regex);

        int counter = 0;

        for (Map.Entry<String, Locator> entry : pageSpec.getObjects().entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                counter += 1;
            }
        }

        return counter;
    }

    @Override
    public JsPageElement find(String name) {
        Pattern pattern = GalenUtils.convertObjectNameRegex(name);

        if (pageSpec != null) {
            for (Map.Entry<String, Locator> entry : pageSpec.getObjects().entrySet()) {
                String objectName = entry.getKey();
                if (pattern.matcher(objectName).matches()) {
                    Locator locator = entry.getValue();
                    if (locator != null && page != null) {
                        PageElement pageElement = page.getObject(objectName, locator);
                        if (pageElement != null) {
                            return new JsPageElement(objectName, pageElement);
                        }
                    }
                }
            }
        }

        return new JsPageElement(name, new AbsentPageElement());
    }

    @Override
    public JsPageElement[] findAll(String objectsStatements) {
        List<String> objectNames = findAllObjectsMatchingStrictStatements(objectsStatements);
        List<JsPageElement> jsElements = new ArrayList<>(objectNames.size());

        for (String objectName : objectNames) {
            Locator locator = pageSpec.getObjects().get(objectName);
            if (locator != null) {
                PageElement pageElement = page.getObject(objectName, locator);
                if (pageElement != null) {
                    jsElements.add(new JsPageElement(objectName, pageElement));
                } else {
                    jsElements.add(new JsPageElement(objectName, new AbsentPageElement()));
                }
            }
        }
        return jsElements.toArray(new JsPageElement[jsElements.size()]);
    }

    public List<String> findAllObjectsMatchingStrictStatements(String objectExpression) {
        String[] parts = objectExpression.split(",");

        List<String> allSortedObjectNames = getSortedObjectNames();
        List<String> resultingObjectNames = new LinkedList<String>();

        for (String part : parts) {
            String singleExpression = part.trim();
            if (!singleExpression.isEmpty()) {
                if (GalenUtils.isObjectGroup(singleExpression)) {
                    resultingObjectNames.addAll(findOjectsInGroup(GalenUtils.extractGroupName(singleExpression)));
                } else if (GalenUtils.isObjectsSearchExpression(singleExpression)) {
                    Pattern objectPattern = GalenUtils.convertObjectNameRegex(singleExpression);
                    for (String objectName : allSortedObjectNames) {
                        if (objectPattern.matcher(objectName).matches()) {
                            resultingObjectNames.add(objectName);
                        }
                    }
                } else {
                    resultingObjectNames.add(singleExpression);
                }
            }
        }
        return resultingObjectNames;
    }

    public void setGlobalVariable(String name, Object value, StructNode source) {
        if (!isValidVariableName(name)) {
            throw new SyntaxException(source, "Invalid name for variable: " + name);
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

    public StructNode processStrictExpressionsIn(StructNode originNode) {
        return processExpressionsIn(originNode, true);
    }

    public StructNode processExpressionsIn(StructNode originNode) {
        return processExpressionsIn(originNode, false);
    }

    private StructNode processExpressionsIn(StructNode originNode, boolean strict) {
        String result;

        if (strict) {
            result = getVarsParser().parseStrict(originNode.getName());
        } else {
            result = getVarsParser().parse(originNode.getName());
        }

        StructNode processedNode = new StructNode(result);
        processedNode.setFileLineNumber(originNode.getFileLineNumber());
        processedNode.setSource(originNode.getSource());
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
        pageRules.add(new ImmutablePair<Rule, PageRule>(rule, pageRule));
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

    public List<String> findOjectsInGroup(String groupName) {
        if (pageSpec.getObjectGroups().containsKey(groupName)) {
            return pageSpec.getObjectGroups().get(groupName);
        } else {
            return Collections.emptyList();
        }
    }
}

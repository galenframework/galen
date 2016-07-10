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

import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.page.PageSection;
import com.galenframework.specs.page.SpecGroup;
import com.galenframework.parser.StringCharReader;
import com.galenframework.speclang2.pagespec.rules.Rule;
import com.galenframework.parser.Expectations;
import com.galenframework.parser.StructNode;
import com.galenframework.specs.Spec;
import com.galenframework.specs.page.ObjectSpecs;
import com.galenframework.specs.Place;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

public class PageSectionProcessor {
    public static final String NO_OBJECT_NAME = null;
    private final PageSpecHandler pageSpecHandler;
    private final PageSection parentSection;

    public PageSectionProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
        this.parentSection = null;
    }

    public PageSectionProcessor(PageSpecHandler pageSpecHandler, PageSection parentSection) {
        this.pageSpecHandler = pageSpecHandler;
        this.parentSection = parentSection;
    }


    public void process(StructNode sectionNode) throws IOException {
        if (sectionNode.getChildNodes() != null) {
            String sectionName = sectionNode.getName().substring(1, sectionNode.getName().length() - 1).trim();
            PageSection section = findSection(sectionName);
            if (section == null) {
                section = new PageSection(sectionName, sectionNode.getPlace());
                if (parentSection != null) {
                    parentSection.addSubSection(section);
                } else {
                    pageSpecHandler.addSection(section);
                }
            }
            processSection(section, sectionNode.getChildNodes());
        }
    }

    private void processSection(PageSection section, List<StructNode> childNodes) throws IOException {
        for (StructNode sectionChildNode : childNodes) {
            String childPlace = sectionChildNode.getName();
            if (isSectionDefinition(childPlace)) {
                new PageSectionProcessor(pageSpecHandler, section).process(sectionChildNode);
            } else if (isRule(childPlace)) {
                processSectionRule(section, sectionChildNode);
            } else if (isObject(childPlace)) {
                processObject(section, sectionChildNode);
            } else {
                throw new SyntaxException(sectionChildNode, "Unknown statement: " + childPlace);
            }
        }
    }

    private void processSectionRule(PageSection section, StructNode ruleNode) throws IOException {
        String ruleText = ruleNode.getName().substring(1).trim();

        Pair<PageRule, Map<String, String>> rule = findAndProcessRule(ruleText, ruleNode);

        PageSection ruleSection = new PageSection(ruleText, ruleNode.getPlace());
        section.addSubSection(ruleSection);

        List<StructNode> resultingNodes;
        try {
            resultingNodes = rule.getKey().apply(pageSpecHandler, ruleText, NO_OBJECT_NAME, rule.getValue(), ruleNode.getChildNodes());
            processSection(ruleSection, resultingNodes);
        } catch (Exception ex) {
            throw new SyntaxException(ruleNode, "Error processing rule: " + ruleText, ex);
        }
    }

    private Pair<PageRule, Map<String, String>> findAndProcessRule(String ruleText, StructNode ruleNode) {
        ListIterator<Pair<Rule, PageRule>> iterator = pageSpecHandler.getPageRules().listIterator(pageSpecHandler.getPageRules().size());
        /*
        It is important to make a reversed iteration over all rules so that
        it is possible for the end user to override previously defined rules
         */

        while (iterator.hasPrevious()) {
            Pair<Rule, PageRule> rulePair = iterator.previous();
            Matcher matcher = rulePair.getKey().getPattern().matcher(ruleText);
            if (matcher.matches()) {
                int index = 1;

                Map<String, String> parameters = new HashMap<>();

                for (String parameterName : rulePair.getKey().getParameters()) {
                    String value = matcher.group(index);
                    pageSpecHandler.setGlobalVariable(parameterName, value, ruleNode);

                    parameters.put(parameterName, value);
                    index += 1;
                }

                return new ImmutablePair<>(rulePair.getValue(), parameters);
            }
        }
        throw new SyntaxException(ruleNode, "Couldn't find rule matching: " + ruleText);
    }

    private void processObjectLevelRule(ObjectSpecs objectSpecs, StructNode sourceNode) throws IOException {
        String ruleText = sourceNode.getName().substring(1).trim();
        Pair<PageRule, Map<String, String>> rule = findAndProcessRule(ruleText, sourceNode);

        try {
            pageSpecHandler.setGlobalVariable("objectName", objectSpecs.getObjectName(), sourceNode);

            List<StructNode> specNodes = rule.getKey().apply(pageSpecHandler, ruleText, objectSpecs.getObjectName(), rule.getValue(), sourceNode.getChildNodes());


            SpecGroup specGroup = new SpecGroup();
            specGroup.setName(ruleText);
            objectSpecs.addSpecGroup(specGroup);

            for (StructNode specNode : specNodes) {
                specGroup.addSpec(pageSpecHandler.getSpecReader().read(specNode.getName(), pageSpecHandler.getContextPath()));
            }
        } catch (Exception ex) {
            throw new SyntaxException(sourceNode, "Error processing rule: " + ruleText, ex);
        }
    }



    private boolean isRule(String nodeText) {
        return nodeText.startsWith("|");
    }

    private PageSection findSection(String sectionName) {
        if (parentSection != null) {
            return findSection(sectionName, parentSection.getSections());
        } else {
            return findSection(sectionName, pageSpecHandler.getPageSections());
        }
    }

    private PageSection findSection(String sectionName, List<PageSection> sections) {
        for (PageSection section : sections) {
            if (section.getName().equals(sectionName)) {
                return section;
            }
        }
        return null;
    }

    private void processObject(PageSection section, StructNode objectNode) throws IOException {
        String name = objectNode.getName();
        String objectExpression = name.substring(0, name.length() - 1).trim();

        List<String> objectNames = pageSpecHandler.findAllObjectsMatchingStrictStatements(objectExpression);

        for (String objectName : objectNames) {
            if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {
                ObjectSpecs objectSpecs = findObjectSpecsInSection(section, objectName);
                if (objectSpecs == null) {
                    objectSpecs = new ObjectSpecs(objectName);
                    section.addObjects(objectSpecs);
                }

                for (StructNode specNode : objectNode.getChildNodes()) {
                    if (isRule(specNode.getName())) {
                        processObjectLevelRule(objectSpecs, specNode);
                    } else {
                        processSpec(objectSpecs, specNode);
                    }
                }
            }
        }
    }

    private void processSpec(ObjectSpecs objectSpecs, StructNode specNode) {
        if (specNode.getChildNodes() != null && !specNode.getChildNodes().isEmpty()) {
            throw new SyntaxException(specNode, "Specs cannot have inner blocks");
        }
        String specText = specNode.getName();
        boolean onlyWarn = false;
        if (specText.startsWith("%")) {
            specText = specText.substring(1);
            onlyWarn = true;
        }

        String alias = null;
        StringCharReader reader = new StringCharReader(specText);
        if (reader.firstNonWhiteSpaceSymbol() == '"') {
            alias = Expectations.doubleQuotedText().read(reader);
            specText = reader.getTheRest();
        }



        Spec spec;
        try {
            spec = pageSpecHandler.getSpecReader().read(specText, pageSpecHandler.getContextPath());
        } catch (SyntaxException ex) {
            ex.setPlace(specNode.getPlace());
            throw ex;
        }
        spec.setOnlyWarn(onlyWarn);
        spec.setAlias(alias);
        if (specNode.getPlace() != null) {
            spec.setPlace(new Place(specNode.getPlace().getFilePath(), specNode.getPlace().getLineNumber()));
        }
        spec.setProperties(pageSpecHandler.getProperties());
        spec.setJsVariables(pageSpecHandler.getJsVariables());

        objectSpecs.getSpecs().add(spec);
    }

    private ObjectSpecs findObjectSpecsInSection(PageSection section, String objectName) {
        if (section.getObjects() != null) {
            for (ObjectSpecs objectSpecs : section.getObjects()) {
                if (objectSpecs.getObjectName().equals(objectName)) {
                    return objectSpecs;
                }
            }
        }
        return null;
    }


    private boolean isObject(String childPlace) {
        return childPlace.endsWith(":");
    }

    public static boolean isSectionDefinition(String name) {
        return name.startsWith("=") && name.endsWith("=");
    }
}

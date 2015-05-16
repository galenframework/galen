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

import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.page.SpecGroup;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.specs.reader.page.rules.Rule;
import net.mindengine.galen.suite.reader.Line;
import net.mindengine.galen.utils.GalenUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                section = new PageSection(sectionName);
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
            String childLine = sectionChildNode.getName();
            if (isSectionDefinition(childLine)) {
                new PageSectionProcessor(pageSpecHandler, section).process(sectionChildNode);
            } else if (isObject(childLine)) {
                processObject(section, sectionChildNode);
            } else if (isRule(childLine)) {
                processSectionRule(section, sectionChildNode);
            } else {
                throw new SyntaxException(sectionChildNode, "Unknown statement: " + childLine);
            }
        }
    }

    private void processSectionRule(PageSection section, StructNode ruleNode) throws IOException {
        String ruleText = ruleNode.getName().substring(1).trim();

        Pair<PageRule, Map<String, String>> rule = findAndProcessRule(ruleText, ruleNode);

        PageSection ruleSection = new PageSection(ruleText);
        section.addSubSection(ruleSection);

        List<StructNode> resultingNodes;
        try {
            resultingNodes = rule.getKey().apply(pageSpecHandler, ruleText, NO_OBJECT_NAME, rule.getValue());
        } catch (Exception ex) {
            throw new SyntaxException(ruleNode, "Error processing custom rule", ex);
        }
        processSection(ruleSection, resultingNodes);
    }

    private Pair<PageRule, Map<String, String>> findAndProcessRule(String ruleText, StructNode ruleNode) {
        for (Pair<Rule, PageRule> rulePair : pageSpecHandler.getPageRules()) {
            Matcher matcher = rulePair.getKey().getPattern().matcher(ruleText);
            if (matcher.matches()) {
                int index = 1;

                Map<String, String> parameters = new HashMap<String, String>();

                for (String parameterName : rulePair.getKey().getParameters()) {
                    String value = matcher.group(index);
                    pageSpecHandler.setGlobalVariable(parameterName, value, ruleNode);

                    parameters.put(parameterName, value);
                    index += 1;
                }

                return new ImmutablePair<PageRule, Map<String, String>>(rulePair.getValue(), parameters);
            }
        }
        throw new SyntaxException(ruleNode, "Could find rule matching: " + ruleText);
    }

    private void processObjectLevelRule(ObjectSpecs objectSpecs, StructNode sourceNode) throws IOException {
        String ruleText = sourceNode.getName().substring(1).trim();
        Pair<PageRule, Map<String, String>> rule = findAndProcessRule(ruleText, sourceNode);

        pageSpecHandler.setGlobalVariable("objectName", objectSpecs.getObjectName(), sourceNode);

        List<StructNode> specNodes = rule.getKey().apply(pageSpecHandler, ruleText, objectSpecs.getObjectName(), rule.getValue());


        SpecGroup specGroup = new SpecGroup();
        specGroup.setName(ruleText);
        objectSpecs.addSpecGroup(specGroup);

        for (StructNode specNode : specNodes) {
            specGroup.addSpec(pageSpecHandler.getSpecReaderV2().read(specNode.getName(), pageSpecHandler.getContextPath()));
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

        List<String> objectNames = findAllMatchingObjectNamesForExpression(objectExpression, objectNode);

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

    private List<String> findAllMatchingObjectNamesForExpression(String objectExpression, StructNode source) {
        String[] parts = objectExpression.split(",");

        List<String> resultingObjectNames = new LinkedList<String>();

        for (String part : parts) {
            String singleExpression = part.trim();

            if (singleExpression.isEmpty()) {
                throw new SyntaxException(source, "Incorrect object expression");
            }

            if (GalenUtils.isObjectExpression(singleExpression)) {
                Pattern objectPattern = GalenUtils.convertObjectNameRegex(singleExpression);
                for (String objectName : pageSpecHandler.getSortedObjectNames()) {
                    if (objectPattern.matcher(objectName).matches()) {
                        resultingObjectNames.add(objectName);
                    }
                }
            } else {
                resultingObjectNames.add(singleExpression);
            }
        }
        return resultingObjectNames;
    }

    private void processSpec(ObjectSpecs objectSpecs, StructNode specNode) {
        try {
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


            Spec spec = pageSpecHandler.getSpecReaderV2().read(specText, pageSpecHandler.getContextPath());
            spec.setOnlyWarn(onlyWarn);
            spec.setAlias(alias);
            spec.setProperties(pageSpecHandler.getProperties());
            spec.setJsVariables(pageSpecHandler.getJsVariables());

            objectSpecs.getSpecs().add(spec);
        } catch (SyntaxException ex) {
            ex.setLine(new Line(specNode.getSource(), specNode.getFileLineNumber()));
            throw ex;
        }
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


    private boolean isObject(String childLine) {
        return childLine.endsWith(":");
    }

    public static boolean isSectionDefinition(String name) {
        return name.startsWith("=") && name.endsWith("=");
    }
}

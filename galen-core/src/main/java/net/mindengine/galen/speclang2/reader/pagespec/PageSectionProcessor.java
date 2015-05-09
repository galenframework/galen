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

import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.rules.Rule;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

public class PageSectionProcessor {
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

        for (Pair<Rule, PageRule> rulePair : pageSpecHandler.getPageRules()) {
            Matcher matcher = rulePair.getKey().getPattern().matcher(ruleText);
            if (matcher.matches()) {
                int index = 1;
                for (String parameterName : rulePair.getKey().getParameters()) {
                    pageSpecHandler.setGlobalVariable(parameterName, matcher.group(index), ruleNode);
                    index += 1;
                }

                rulePair.getValue().apply(pageSpecHandler, ruleText);

                PageSection ruleSection = new PageSection(ruleText);
                section.addSubSection(ruleSection);

                List<StructNode> resultingNodes = rulePair.getValue().apply(pageSpecHandler, ruleText);
                processSection(ruleSection, resultingNodes);
                return;
            }
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
        String objectName = name.substring(0, name.length() - 1).trim();

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {
            ObjectSpecs objectSpecs = findObjectSpecsInSection(section, objectName);
            if (objectSpecs == null) {
                objectSpecs = new ObjectSpecs(objectName);
                section.addObjects(objectSpecs);
            }

            for (StructNode specNode : objectNode.getChildNodes()) {
                String specText = specNode.getName();
                objectSpecs.getSpecs().add(pageSpecHandler.getSpecReaderV2().read(specText, pageSpecHandler.getContextPath()));
            }
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

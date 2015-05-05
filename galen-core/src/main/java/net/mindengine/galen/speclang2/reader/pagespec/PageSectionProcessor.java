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

import net.mindengine.galen.parser.ProcessedStructNode;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;

import java.io.IOException;
import java.util.List;

public class PageSectionProcessor {
    private final PageSpecProcessor pageSpecProcessor;
    private final PageSection parentSection;

    public PageSectionProcessor(PageSpecProcessor pageSpecProcessor) {
        this.pageSpecProcessor = pageSpecProcessor;
        this.parentSection = null;
    }

    public PageSectionProcessor(PageSpecProcessor pageSpecProcessor, PageSection parentSection) {
        this.pageSpecProcessor = pageSpecProcessor;
        this.parentSection = parentSection;
    }


    public void process(ProcessedStructNode sectionNode, String contextPath) throws IOException {
        PageSection section = new PageSection();
        section.setName(sectionNode.getName().substring(1, sectionNode.getName().length() - 1).trim());

        if (sectionNode.getChildNodes() != null) {
            List<ProcessedStructNode> allProcessedChildNodes = new LogicProcessor(pageSpecProcessor).process(sectionNode.getChildNodes());

            for (ProcessedStructNode processedChildNode : allProcessedChildNodes) {

                String childLine = processedChildNode.getName();

                if (isSectionDefinition(childLine)) {
                    new PageSectionProcessor(pageSpecProcessor, section).process(processedChildNode, contextPath);
                } else if (isObject(childLine)) {
                    processObject(section, processedChildNode, contextPath);
                } else {
                    throw processedChildNode.createSyntaxException("Unknown statement: " + childLine);
                }
            }
        }

        if (parentSection != null) {
            parentSection.addSubSection(section);
        } else {
            pageSpecProcessor.addSection(section);
        }
    }

    private void processObject(PageSection section, ProcessedStructNode objectNode, String contextPath) throws IOException {
        String name = objectNode.getName();
        String objectName = name.substring(0, name.length() - 1).trim();

        ObjectSpecs objectSpecs = new ObjectSpecs(objectName);

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {

            for (StructNode specNode : objectNode.getChildNodes()) {
                ProcessedStructNode processedSpecNode = pageSpecProcessor.processExpressionsIn(specNode);
                String specText = processedSpecNode.getName();
                objectSpecs.getSpecs().add(pageSpecProcessor.getSpecReaderV2().read(specText, contextPath));
            }

            section.addObjects(objectSpecs);
        }
    }


    private boolean isObject(String childLine) {
        return childLine.endsWith(":");
    }

    public static boolean isSectionDefinition(String name) {
        return name.startsWith("=") && name.endsWith("=");
    }
}

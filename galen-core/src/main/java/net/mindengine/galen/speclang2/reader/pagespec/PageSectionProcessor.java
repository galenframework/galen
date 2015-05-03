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
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;

import java.io.IOException;

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


    public void process(StructNode structNode, String contextPath) throws IOException {
        PageSection section = new PageSection();
        section.setName(structNode.getName().substring(1, structNode.getName().length() - 1).trim());

        if (structNode.getChildNodes() != null) {
            for (StructNode childNode : structNode.getChildNodes()) {
                String childLine = childNode.getName();

                if (isSectionDefinition(childLine)) {
                    new PageSectionProcessor(pageSpecProcessor, section).process(childNode, contextPath);
                } else if (isObject(childLine)) {
                    processObject(childLine, section, childNode, contextPath);
                } else {
                    throw childNode.createSyntaxException("Unknown statement: " + childLine);
                }
            }
        }

        if (parentSection != null) {
            parentSection.addSubSection(section);
        } else {
            pageSpecProcessor.addSection(section);
        }
    }

    private void processObject(String childLine, PageSection section, StructNode objectNode, String contextPath) throws IOException {
        String objectName = childLine.substring(0, childLine.length() - 1).trim();

        ObjectSpecs objectSpecs = new ObjectSpecs(objectName);

        if (objectNode.getChildNodes() != null && objectNode.getChildNodes().size() > 0) {

            for (StructNode specNode : objectNode.getChildNodes()) {
                String specText = specNode.getName();
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

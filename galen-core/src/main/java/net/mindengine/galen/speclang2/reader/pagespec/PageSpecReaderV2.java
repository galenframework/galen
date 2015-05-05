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
import net.mindengine.galen.parser.IndentationStructureParser;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.utils.GalenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PageSpecReaderV2 {

    public PageSpec read(String path, Browser browser) throws IOException {
        String contextPath = GalenUtils.getParentForFile(path);
        return read(GalenUtils.findFileOrResourceAsStream(path), path, contextPath, browser);
    }

    public PageSpec read(InputStream inputStream, String source, String contextPath, Browser browser) throws IOException {
        IndentationStructureParser structParser = new IndentationStructureParser();
        List<StructNode> structs = structParser.parse(inputStream, source);

        PageSpec pageSpec = new PageSpec();

        PageSpecProcessor pageSpecProcessor = new PageSpecProcessor(pageSpec, browser);

        List<StructNode> allProcessedChildNodes = new LogicProcessor(pageSpecProcessor).process(structs);

        for (StructNode structNode : allProcessedChildNodes) {
            processNode(structNode, pageSpecProcessor, contextPath);
        }

        return pageSpecProcessor.buildPageSpec();
    }

    private void processNode(StructNode node, PageSpecProcessor pageSpecProcessor, String contextPath) throws IOException {
        if (isSpecialInstruction(node.getName())) {
            pageSpecProcessor.processSpecialInstruction(node);
        } else if (PageSectionProcessor.isSectionDefinition(node.getName())) {
            new PageSectionProcessor(pageSpecProcessor).process(node, contextPath);
        } else {
            throw node.createSyntaxException("Unknown statement: " + node.getName());
        }
    }

    private boolean isSpecialInstruction(String name) {
        String firstWord = new StringCharReader(name).readWord();

        return firstWord.equals("@objects");
    }

}

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

import net.mindengine.galen.parser.IndentationStructureParser;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.utils.GalenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImportProcessor {
    private final PageSpecHandler pageSpecHandler;

    public ImportProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode statementNode) throws IOException {

        List<StructNode> importedNodes = new LinkedList<StructNode>();

        if (reader.hasMoreNormalSymbols()) {
            importedNodes.addAll(importPageSpec(reader.getTheRest().trim()));
        }

        if (statementNode.getChildNodes() != null) {
            for (StructNode childNode : statementNode.getChildNodes()) {
                importedNodes.addAll(importPageSpec(childNode.getName()));
            }
        }

        return importedNodes;
    }

    private List<StructNode> importPageSpec(String filePath) throws IOException {
        String fullPath = pageSpecHandler.getContextPath() + "/" + filePath;

        InputStream stream = GalenUtils.findFileOrResourceAsStream(fullPath);
        List<StructNode> structs = new IndentationStructureParser().parse(stream, fullPath);

        PageSpecHandler childPageSpecHandler = new PageSpecHandler(pageSpecHandler, GalenUtils.getParentForFile(fullPath));

        List<StructNode> allProcessedChildNodes = new LogicProcessor(childPageSpecHandler).process(structs);
        new PostProcessor(childPageSpecHandler).process(allProcessedChildNodes);

        return Collections.emptyList();
    }
}

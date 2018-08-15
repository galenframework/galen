/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
import com.galenframework.parser.IndentationStructureParser;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.StringCharReader;
import com.galenframework.utils.GalenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImportProcessor implements StructNodeProcessor {
    protected final PageSpecHandler pageSpecHandler;

    public ImportProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    @Override
    public List<StructNode> process(StringCharReader reader, StructNode statementNode) throws IOException {

        List<StructNode> importedNodes = new LinkedList<>();

        if (reader.hasMoreNormalSymbols()) {
            importedNodes.addAll(importPageSpec(reader.getTheRest().trim(), statementNode));
        }

        if (statementNode.getChildNodes() != null) {
            for (StructNode childNode : statementNode.getChildNodes()) {
                importedNodes.addAll(importPageSpec(childNode.getName(), childNode));
            }
        }

        return importedNodes;
    }

    protected List<StructNode> importPageSpec(String filePath, StructNode origin) throws IOException {
        String fullPath = filePath;

        if (pageSpecHandler.getContextPath() != null) {
            fullPath = pageSpecHandler.getContextPath() + "/" + filePath;
        }

        return loadPageSpec(origin, fullPath);
    }

    protected List<StructNode> loadPageSpec(StructNode origin, String fullPath) throws IOException {
        String fileId = GalenUtils.calculateFileId(fullPath);
        if (!pageSpecHandler.getProcessedImports().contains(fileId)) {
            pageSpecHandler.getProcessedImports().add(fileId);

            InputStream stream = GalenUtils.findFileOrResourceAsStream(fullPath);

            if (stream == null) {
                throw new SyntaxException(origin, "Cannot find file: " + fullPath);
            }

            List<StructNode> structs = new IndentationStructureParser().parse(stream, fullPath);

            PageSpecHandler childPageSpecHandler = new PageSpecHandler(pageSpecHandler, GalenUtils.getParentForFile(fullPath));

            List<StructNode> allProcessedChildNodes = new MacroProcessor(childPageSpecHandler).process(structs);
            new PostProcessor(childPageSpecHandler).process(allProcessedChildNodes);

        }
        return Collections.emptyList();
    }
}

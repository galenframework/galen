/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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

import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;

import java.io.IOException;
import java.util.List;

public class PostProcessor {
    private final PageSpecHandler pageSpecHandler;

    public PostProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public void process(List<StructNode> allProcessedChildNodes) throws IOException {
        for (StructNode structNode : allProcessedChildNodes) {
            processNode(structNode, pageSpecHandler);
        }
    }

    private void processNode(StructNode node, PageSpecHandler pageSpecHandler) throws IOException {
        if (PageSectionProcessor.isSectionDefinition(node.getName())) {
            new PageSectionProcessor(pageSpecHandler).process(node);
        } else {
            throw new SyntaxException(node, "Unknown statement: " + node.getName());
        }
    }
}

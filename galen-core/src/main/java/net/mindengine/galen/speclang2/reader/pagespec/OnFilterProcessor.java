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
import net.mindengine.galen.specs.reader.StringCharReader;

import java.util.Collections;
import java.util.List;

public class OnFilterProcessor {
    private final PageSpecHandler pageSpecHandler;

    public OnFilterProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode statementNode) {
        String rest = reader.getTheRest().trim().trim();
        if (rest.isEmpty()) {
            throw new SyntaxException(statementNode, "Missing tags");
        }

        String[] filterTags = rest.split(",");

        List<String> pageSpecTags = pageSpecHandler.getTags();


        for (String filterTag : filterTags) {
            String trimmedFilterTag = filterTag.trim();

            if (trimmedFilterTag.equals("*")
                   || (pageSpecTags != null && pageSpecTags.contains(trimmedFilterTag))) {
                return statementNode.getChildNodes();
            }
        }

        return Collections.emptyList();
    }
}

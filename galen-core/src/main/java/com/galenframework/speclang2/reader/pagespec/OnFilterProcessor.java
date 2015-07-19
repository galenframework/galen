/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.speclang2.reader.pagespec;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.reader.StringCharReader;

import java.util.Collections;
import java.util.LinkedList;
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

        List<String> pageSpecTags = pageSpecHandler.getTags();
        List<String> filterTags = parseTagsFrom(rest);

        if (!containsExcludedTags(filterTags, pageSpecHandler.getExcludedTags())) {
            for (String filterTag : filterTags) {
                if (filterTag.equals("*")
                        || (pageSpecTags != null && pageSpecTags.contains(filterTag))) {
                    return statementNode.getChildNodes();
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> parseTagsFrom(String text) {
        String[] filterTagsArr = text.split(",");
        List<String> list = new LinkedList<>();

        for (String filterTag : filterTagsArr) {
            String trimmedFilterTag = filterTag.trim();
            list.add(trimmedFilterTag);
        }

        return list;
    }


    private boolean containsExcludedTags(List<String> filterTags, List<String> excludedTags) {
        if (excludedTags != null) {
            for (String excludedTag : excludedTags) {
                if (filterTags.contains(excludedTag)) {
                    return true;
                }
            }
        }
        return false;
    }
}

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

import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.utils.GalenUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GroupsDefinitionProcessor {
    private final PageSpecHandler pageSpecHandler;

    public GroupsDefinitionProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode structNode) {
        if (!reader.getTheRest().isEmpty()) {
            throw new SyntaxException(structNode, "Groups definition does not take any arguments");
        }

        if (structNode.getChildNodes() != null) {
            for (StructNode groupNode : structNode.getChildNodes()) {
                if (groupNode.getChildNodes() != null && !groupNode.getChildNodes().isEmpty()) {
                    throw new SyntaxException(structNode, "groups should be declared in single line");
                }

                processGroupNode(groupNode);
            }
        }
        return Collections.emptyList();
    }

    private void processGroupNode(StructNode groupNode) {
        StringCharReader reader = new StringCharReader(groupNode.getName());

        List<String> groups = new LinkedList<>();

        if (reader.firstNonWhiteSpaceSymbol() == '(') {
            groups = readMultipleGroups(reader);
        } else {
            String groupName  = reader.readWord().trim();
            groups.add(groupName);
        }

        String objectStatements = reader.getTheRest().trim();

        if (objectStatements.isEmpty()) {
            throw new SyntaxException(groupNode, "Missing object statements");
        }

        List<String> objects = pageSpecHandler.findAllObjectsMatchingStatements(objectStatements);

        if (!groups.isEmpty()) {
            for (String object : objects) {
                pageSpecHandler.applyGroupsToObject(object, groups);
            }
        }
    }

    private List<String> readMultipleGroups(StringCharReader reader) {
        reader.readUntilSymbol('(');
        String commaSeparatedGroups = reader.readUntilSymbol(')');
        return GalenUtils.fromCommaSeparated(commaSeparatedGroups);
    }
}

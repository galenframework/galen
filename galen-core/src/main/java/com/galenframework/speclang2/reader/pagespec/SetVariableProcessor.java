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
package com.galenframework.speclang2.reader.pagespec;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.reader.StringCharReader;

import java.util.Collections;
import java.util.List;

public class SetVariableProcessor {
    private final PageSpecHandler pageSpecHandler;

    public SetVariableProcessor(PageSpecHandler pageSpecHandler) {
        this.pageSpecHandler = pageSpecHandler;
    }

    public List<StructNode> process(StringCharReader reader, StructNode structNode) {
        if (reader.hasMore()) {
            structNode.setName(reader.getTheRest());
            processVariableStatement(structNode);
        }
        if (structNode.getChildNodes() != null) {
            for (StructNode childNode : structNode.getChildNodes()) {
                processVariableStatement(pageSpecHandler.processExpressionsIn(childNode));
            }
        }
        return Collections.emptyList();
    }

    private void processVariableStatement(StructNode structNode) {
        StringCharReader reader = new StringCharReader(structNode.getName());
        String name = reader.readWord();

        if (name.isEmpty()) {
            throw new SyntaxException(structNode, "Missing variable name");
        }

        String value = reader.getTheRest().trim();
        this.pageSpecHandler.setGlobalVariable(name, value, structNode);
    }
}

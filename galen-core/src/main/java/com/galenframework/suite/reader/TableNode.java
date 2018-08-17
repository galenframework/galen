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
package com.galenframework.suite.reader;


import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.VarsContext;
import com.galenframework.specs.Place;

public class TableNode extends Node<Void>{

    public TableNode(String text, Place place) {
        super(text, place);
    }

    @Override
    public Void build(VarsContext context) {
        
        String name = getArguments().trim();
        if (name.isEmpty()) {
            throw new SyntaxException(getPlace(), "Table name should not be empty");
        }
        
        Table table = new Table();
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TableRowNode) {
                TableRowNode rowNode = (TableRowNode) childNode;
                try {
                    table.addRow(rowNode.build(context), rowNode.getPlace());
                }
                catch (SyntaxException e) {
                    e.setPlace(childNode.getPlace());
                    throw e;
                }
            }
        }
        
        context.putValue(name, table);
        
        return null;
    }

    @Override
    public Node<?> processNewNode(String text, Place place) {
        add(new TableRowNode(text, place));
        return this;
    }

}

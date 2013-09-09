/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.suite.reader;


import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;

public class TableNode extends Node<Void>{

    public TableNode(Line line) {
        super(line);
    }

    @Override
    public Void build(BashTemplateContext context) {
        
        String name = getArguments().trim();
        if (name.isEmpty()) {
            throw new SyntaxException(getLine(), "Table name should not be empty");
        }
        
        Table table = new Table();
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TableRowNode) {
                TableRowNode rowNode = (TableRowNode) childNode;
                try {
                    table.addRow(rowNode.build(context));
                }
                catch (SyntaxException e) {
                    e.setLine(childNode.getLine());
                    throw e;
                }
            }
        }
        
        context.putValue(name, table);
        
        return null;
    }

    @Override
    public Node<?> processNewNode(Line line) {
        add(new TableRowNode(line));
        return this;
    }

}

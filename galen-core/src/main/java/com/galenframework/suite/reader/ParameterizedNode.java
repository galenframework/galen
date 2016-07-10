/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import static java.lang.String.format;

import java.util.*;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.VarsContext;
import com.galenframework.tests.GalenBasicTest;

public class ParameterizedNode extends Node<List<GalenBasicTest>>{

    private Node<?> toParameterize;
    
    private boolean disabled = false;
    private List<String> groups;

    public ParameterizedNode(String text, Line line) {
        super(text, line);
    }

    @Override
    public List<GalenBasicTest> build(VarsContext context) {
        
        Table table = createTable(context);
        
        final VarsContext parameterizedContext = new VarsContext(new Properties(), context);
        final List<GalenBasicTest> tests = new LinkedList<>();
        
        table.forEach(values -> {
            parameterizedContext.addValuesFromMap(values);

            if (toParameterize instanceof ParameterizedNode) {
                ParameterizedNode parameterizedNode = (ParameterizedNode)toParameterize;
                tests.addAll(wrapTestsWithGroups(parameterizedNode.build(parameterizedContext), groups));
            }
            else if (toParameterize instanceof TestNode) {
                TestNode suiteNode = (TestNode) toParameterize;
                tests.add(wrapTestWithGroups(suiteNode.build(parameterizedContext), groups));
            }
        });
        
        return tests;
    }

    private List<GalenBasicTest> wrapTestsWithGroups(List<GalenBasicTest> tests, List<String> groups) {
        if (groups != null) {
            for (GalenBasicTest test : tests) {
                wrapTestWithGroups(test, groups);
            }
        }
        return tests;
    }

    private GalenBasicTest wrapTestWithGroups(GalenBasicTest test, List<String> groups) {
        if (groups != null) {
            if (test.getGroups() != null) {
                test.getGroups().addAll(groups);
            }
            else {
                test.setGroups(groups);
            }
        }

        return test;
    }

    private Table createTable(VarsContext context) {
        String line = getArguments().trim();
        
        Table tableFromChild = buildFromChild(context);
        
        Table table = null;
        if (!line.isEmpty()) {
            int indexOfFirstSpace = line.indexOf(' ');
            if (indexOfFirstSpace < 0) {
                throw new SyntaxException(getLine(), "Incorrect syntax.");
            }
            String firstWord = line.substring(0, indexOfFirstSpace).toLowerCase();
            
            if (!firstWord.equals("using")) {
                throw new SyntaxException(getLine(), "Unknown statement: " + firstWord);
            }
            
            String leftover = line.substring(indexOfFirstSpace);
                
            String[] tableNames = leftover.split(",");
            for (String tableName : tableNames) {
                String trimmedTableName = tableName.trim();
                if (!trimmedTableName.isEmpty()) {
                    Table contextTable = (Table) context.getValue(trimmedTableName);
                    if (contextTable == null) {
                        throw new SyntaxException(getLine(), format("Table with name \"%s\" does not exist", trimmedTableName));
                    }
                    
                    if (table == null) {
                        table = contextTable;
                    }
                    else {
                        try {
                            table.mergeWith(contextTable);
                        }
                        catch (Exception ex) {
                            throw new SyntaxException(getLine(), format("Cannot merge table \"%s\". Perhaps it has different amount of columns", trimmedTableName));
                        } 
                    }
                }
            }
            
            try {
                table.mergeWith(tableFromChild);
            }
            catch (Exception ex) {
                throw new SyntaxException(getLine(), format("Cannot merge in-built table. It probably has different amount of columns then in \"%s\"", line));
            }
            
        }
        else {
            table = tableFromChild;
        }
        
        return table;
    }

    private Table buildFromChild(VarsContext context) {
        Table table = new Table();
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TableRowNode) {
                TableRowNode row = (TableRowNode)childNode;
                table.addRow(row.build(context), row.getLine());
            }
        }
        return table;
    }

    @Override
    public Node<?> processNewNode(String text, Line line) {
        add(new TableRowNode(text, line));
        return this;
    }

    public void setToParameterize(Node<?> node) {
        this.toParameterize = node;        
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isEnabled() {
        return !disabled;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getGroups() {
        return groups;
    }
}

package net.mindengine.galen.suite.reader;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenSuite;

public class ParameterizedNode extends Node<List<GalenSuite>>{

    private Node<?> toParameterize;

    public ParameterizedNode(Line line) {
        super(line);
    }

    @Override
    public List<GalenSuite> build(BashTemplateContext context) {
        
        Table table = createTable(context);
        
        final BashTemplateContext parameterizedContext = new BashTemplateContext(context);
        final List<GalenSuite> suites = new LinkedList<GalenSuite>();
        
        table.forEach(new RowVisitor() {
            @Override
            public void visit(Map<String, String> values) {
                parameterizedContext.addValuesFromMap(values);
                
                if (toParameterize instanceof ParameterizedNode) {
                    ParameterizedNode parameterizedNode = (ParameterizedNode)toParameterize;
                    suites.addAll(parameterizedNode.build(parameterizedContext));
                }
                else if (toParameterize instanceof SuiteNode) {
                    SuiteNode suiteNode = (SuiteNode) toParameterize;
                    suites.add(suiteNode.build(parameterizedContext));
                }
            }
        });
        
        return suites;
    }

    private Table createTable(BashTemplateContext context) {
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

    private Table buildFromChild(BashTemplateContext context) {
        Table table = new Table();
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TableRowNode) {
                TableRowNode row = (TableRowNode)childNode;
                try {
                    table.addRow(row.build(context));
                }
                catch (SyntaxException e) {
                    throw new SyntaxException(row.getLine(), e.getMessage());
                }
                
            }
        }
        return table;
    }

    @Override
    public Node<?> processNewNode(Line line) {
        add(new TableRowNode(line));
        return this;
    }

    public void setToParameterize(Node<?> node) {
        this.toParameterize = node;        
    }

}

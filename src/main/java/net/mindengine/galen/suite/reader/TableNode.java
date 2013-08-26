package net.mindengine.galen.suite.reader;


import net.mindengine.galen.parser.BashTemplateContext;

public class TableNode extends Node<Void>{

    public TableNode(String arguments) {
        super(arguments);
    }

    @Override
    public Void build(BashTemplateContext context) {
        
        String name = getArguments().trim();
        if (name.isEmpty()) {
            throw new SuiteReaderException("Table name should not be empty");
        }
        
        Table table = new Table();
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof TableRowNode) {
                TableRowNode rowNode = (TableRowNode) childNode;
                 table.addRow(rowNode.build(context));
            }
        }
        
        context.putValue(name, table);
        
        return null;
    }

    @Override
    public Node<?> processNewNode(String line) {
        add(new TableRowNode(line));
        return this;
    }

}

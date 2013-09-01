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

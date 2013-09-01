package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;

public class TableRowNode extends Node<List<String>> {

    public TableRowNode(Line line) {
        super(line);
    }

    @Override
    public List<String> build(BashTemplateContext context) {
        String rowText = getArguments().trim();
        
        if (!rowText.startsWith("|")) {
            throw new SyntaxException(getLine(), "Incorrect format. Should start with '|'");
        }
        if (!rowText.endsWith("|")) {
            throw new SyntaxException(getLine(), "Incorrect format. Should end with '|'");
        }
        
        String[] rawCells = rowText.split("\\|");
        
        List<String> cells = new LinkedList<String>();
        if (rawCells.length > 1) {
            for (int i=1; i<rawCells.length; i++) {
                cells.add(context.process(rawCells[i].trim()));
            }
        }
        else throw new SyntaxException(getLine(), "Incorrect row. Use '|' symbol to split values");
        
        return cells;
    }

    @Override
    public Node<?> processNewNode(Line line) {
        throw new SyntaxException(line, "Wrong nesting");
    }

    
    
   
}

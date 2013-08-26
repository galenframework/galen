package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SuiteParserException;

public class TableRowNode extends Node<List<String>> {

    public TableRowNode(String arguments) {
        super(arguments);
    }

    @Override
    public List<String> build(BashTemplateContext context) {
        String[] rawCells = getArguments().split("\\|");
        
        List<String> cells = new LinkedList<String>();
        if (rawCells.length > 2) {
            for (int i=1; i<rawCells.length; i++) {
                cells.add(context.process(rawCells[i].trim()));
            }
        }
        else throw new SuiteReaderException("Incorrect row. Use '|' symbol to split values");
        
        return cells;
    }

    @Override
    public Node<?> processNewNode(String line) {
        throw new SuiteParserException("Wrong nesting");
    }

    
    
   
}

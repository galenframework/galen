package net.mindengine.galen.suite.reader;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.parser.SyntaxException;

public class Table {

    private List<String> headers;
    private List<List<String>> rows = new LinkedList<List<String>>();

    public Table() {
    }

    public void addRow(List<String> row) {
        if (headers == null) {
            headers = row;
        }
        else {
            if (row.size() != headers.size()) {
                throw new SyntaxException(UNKNOWN_LINE, "Amount of cells in a row is not the same in header");
            }
            rows.add(row);
        }
        
    }

    public void mergeWith(Table table) {
        if (table.headers != null && table.rows.size() > 0) {
            if (table.headers.size() != headers.size()) {
                throw new SyntaxException(UNKNOWN_LINE, "Cannot merge tables. Amount of columns should be same");
            }
            else {
                for (List<String> row : table.rows) {
                    rows.add(row);
                }
            }
        }
    }

    public void forEach(RowVisitor visitor) {
        for (List<String> row : rows) {
            int index = -1;
            Map<String, String> values = new HashMap<String, String>();
            for (String cell : row) {
                index++;
                values.put(headers.get(index), cell);
            }
            
            visitor.visit(values);
        }
    }
    
    

}

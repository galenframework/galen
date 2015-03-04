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
package net.mindengine.galen.suite.reader;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import net.mindengine.galen.parser.SyntaxException;

public class Table {

    private List<String> headers;
    private final List<List<String>> rows = new LinkedList<List<String>>();

    public Table() {
    }

    public void addRow(final List<String> row) {
        if (headers == null) {
            headers = row;
        } else {
            if (row.size() != headers.size()) {
                throw new SyntaxException(UNKNOWN_LINE, "Amount of cells in a row is not the same in header");
            }
            rows.add(row);
        }

    }

    public void mergeWith(final Table table) {
        if (CollectionUtils.isNotEmpty(table.headers) && CollectionUtils.isNotEmpty(table.rows)) {
            if (table.headers.size() != headers.size()) {
                throw new SyntaxException(UNKNOWN_LINE, "Cannot merge tables. Amount of columns should be same");
            } else {
                for (final List<String> row : table.rows) {
                    rows.add(row);
                }
            }
        }
    }

    public void forEach(final RowVisitor visitor) {
        for (final List<String> row : rows) {
            int index = -1;
            final Map<String, String> values = new HashMap<String, String>();
            for (final String cell : row) {
                index++;
                values.put(headers.get(index), cell);
            }

            visitor.visit(values);
        }
    }

}

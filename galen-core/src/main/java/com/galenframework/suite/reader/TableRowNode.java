/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import java.util.LinkedList;
import java.util.List;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.VarsContext;
import com.galenframework.specs.Place;

public class TableRowNode extends Node<List<String>> {

    public TableRowNode(String text, Place place) {
        super(text, place);
    }

    @Override
    public List<String> build(VarsContext context) {
        String rowText = getArguments().trim();
        
        if (!rowText.startsWith("|")) {
            throw new SyntaxException(getPlace(), "Incorrect format. Should start with '|'");
        }
        if (!rowText.endsWith("|")) {
            throw new SyntaxException(getPlace(), "Incorrect format. Should end with '|'");
        }
        
        String[] rawCells = rowText.split("\\|");
        
        List<String> cells = new LinkedList<>();
        if (rawCells.length > 1) {
            for (int i=1; i<rawCells.length; i++) {
                cells.add(context.process(rawCells[i].trim()));
            }
        }
        else throw new SyntaxException(getPlace(), "Incorrect row. Use '|' symbol to split values");
        
        return cells;
    }

    @Override
    public Node<?> processNewNode(String text, Place place) {
        throw new SyntaxException(place, "Wrong nesting");
    }

    
    
   
}

/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.speclang2.specs;

import com.galenframework.parser.Expectations;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.Location;
import com.galenframework.specs.Side;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecOn;
import com.galenframework.parser.StringCharReader;

import java.util.LinkedList;
import java.util.List;

public class SpecOnProcessor implements SpecProcessor {
    @Override
    public Spec process(StringCharReader reader, String contextPath) {

        List<String> allEdges = new LinkedList<String>();

        boolean edgesAreNotRead = true;

        while(edgesAreNotRead && reader.hasMore()) {
            String word = reader.readWord();
            if (word.equals("edge")) {
                edgesAreNotRead = false;
            } else {
                allEdges.add(word);
            }
        }

        if (edgesAreNotRead) {
            throw new SyntaxException("Missing \"edge\"");
        }

        if (allEdges.size() > 2) {
            throw new SyntaxException("Too many sides. Should use only 2");
        }

        Side sideHorizontal = Side.TOP;
        Side sideVertical = Side.LEFT;

        boolean isFirstHorizontal = false;
        if (allEdges.size() > 0) {
            Side side = Side.fromString(allEdges.get(0));
            if (side == Side.TOP || side == Side.BOTTOM) {
                isFirstHorizontal = true;
                sideHorizontal = side;
            }
            else sideVertical = side;
        }

        if (allEdges.size() > 1) {
            Side side = Side.fromString(allEdges.get(1));
            if (side == Side.TOP || side == Side.BOTTOM) {
                if (isFirstHorizontal) {
                    throw new SyntaxException("Cannot use theses sides: " + allEdges.get(0) + " " + allEdges.get(1));
                }
                sideHorizontal = side;
            }
            else {
                if (!isFirstHorizontal) {
                    throw new SyntaxException("Cannot use theses sides: " + allEdges.get(0) + " " + allEdges.get(1));
                }
                sideVertical = side;
            }
        }

        String objectName = reader.readWord();
        if (objectName.isEmpty()) {
            throw new SyntaxException("Missing object name");
        }

        List<Location> locations = Expectations.locations().read(reader);

        return new SpecOn(objectName, sideHorizontal, sideVertical, locations);
    }
}

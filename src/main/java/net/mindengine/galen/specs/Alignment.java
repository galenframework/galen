/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.specs;

import static java.lang.String.format;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.parser.SyntaxException;

public enum Alignment {

    CENTERED, TOP, BOTTOM, LEFT, RIGHT, ALL;

    public static Alignment parse(String alignmentText) {
        if (alignmentText.equals("centered")) {
            return CENTERED;
        }
        else if (alignmentText.equals("top")) {
            return TOP;
        }
        else if (alignmentText.equals("bottom")) {
            return BOTTOM;
        }
        else if (alignmentText.equals("left")) {
            return LEFT;
        }
        else if (alignmentText.equals("right")) {
            return RIGHT;
        }
        else if (alignmentText.equals("all")) {
            return ALL;
        }
        else throw new SyntaxException(UNKNOWN_LINE, format("Unknown alignment \"%s\"", alignmentText));
    }
    
    @Override
    public String toString() {
        switch(this) {
        case CENTERED:
            return "centered";
        case TOP:
            return "top";
        case BOTTOM:
            return "bottom";
        case LEFT:
            return "left";
        case RIGHT:
            return "right";
        case ALL:
            return "all";
        }
        return "";
    }

    public boolean isOneOf(Alignment...alignments) {
        for(Alignment a : alignments) {
            if (this == a) {
                return true;
            }
        }
        return false;
    }
    
}

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

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.parser.SyntaxException;

public enum Side {
    
    LEFT, RIGHT, TOP, BOTTOM;

    public static Side fromString(String side) {
        if("left".equals(side)){
            return LEFT;
        }
        else if("right".equals(side)){
            return RIGHT;
        }
        else if("top".equals(side)){
            return TOP;
        }
        else if("bottom".equals(side)){
            return BOTTOM;
        }
        throw new SyntaxException(UNKNOWN_LINE,String.format("Unknown side: \"%s\"", side));
    }

    @Override
    public String toString() {
        switch(this) {
        case LEFT:
            return "left";
        case RIGHT:
            return "right";
        case TOP:
            return "top";
        case BOTTOM:
            return "bottom";
        }
        return "unknown side";
    }
    
    public static List<Side> sides(Side...sides) {
        return Arrays.asList(sides);
    }
    
}

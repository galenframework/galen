package net.mindengine.galen.specs;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.specs.reader.IncorrectSpecException;

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
        throw new IncorrectSpecException(String.format("Unknown side: \"%s\"", side));
    }

    
    public static List<Side> sides(Side...sides) {
        return Arrays.asList(sides);
    }
    
}

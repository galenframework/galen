package net.mindengine.galen.specs;

import net.mindengine.galen.specs.reader.IncorrectSpecException;

public enum Location {
    
    LEFT, RIGHT, TOP, BOTTOM;

    public static Location fromString(String location) {
        if("left".equals(location)){
            return LEFT;
        }
        else if("right".equals(location)){
            return RIGHT;
        }
        else if("top".equals(location)){
            return TOP;
        }
        else if("bottom".equals(location)){
            return BOTTOM;
        }
        throw new IncorrectSpecException(String.format("Unknown location: \"%s\"", location));
    }

    
}

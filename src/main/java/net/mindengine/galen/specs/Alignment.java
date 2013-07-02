package net.mindengine.galen.specs;

import static java.lang.String.format;
import net.mindengine.galen.specs.reader.IncorrectSpecException;

public enum Alignment {

    CENTERED, TOP, BOTTOM, LEFT, RIGHT;

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
        else throw new IncorrectSpecException(format("Unknown alignment \"%s\"", alignmentText));
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

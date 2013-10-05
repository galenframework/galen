package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.SpecOn;

public class SpecValidationOn extends SpecValidationGeneral<SpecOn> {

    @Override
    protected int getOffsetForSide(Rect mainArea, Rect secondArea, Side side) {
        if (side == Side.LEFT) {
            return secondArea.getLeft() - mainArea.getLeft();
        }
        else if (side == Side.TOP) {
            return secondArea.getTop() - mainArea.getTop();
        }
        else if (side == Side.RIGHT) {
            return mainArea.getLeft() - secondArea.getLeft();
        }
        else if (side == Side.BOTTOM) {
            return mainArea.getTop() - secondArea.getTop();
        }
        else {
            return 0;
        }
    }

}

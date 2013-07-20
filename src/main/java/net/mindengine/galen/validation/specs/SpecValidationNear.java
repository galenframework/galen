package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.validation.PageValidation;

public class SpecValidationNear extends SpecValidationGeneral<SpecNear> {

    public SpecValidationNear(PageValidation pageValidation) {
        super(pageValidation);
    }

    @Override
    protected int getOffsetForSide(Rect mainArea, Rect secondArea, Side side) {
        if (side == Side.LEFT) {
            return secondArea.getLeft() - (mainArea.getLeft() + mainArea.getWidth());
        }
        else if (side == Side.TOP) {
            return secondArea.getTop() - (mainArea.getTop() + mainArea.getHeight());
        }
        else if (side == Side.RIGHT) {
            return mainArea.getLeft() - (secondArea.getLeft() + secondArea.getWidth());
        }
        else if (side == Side.BOTTOM) {
            return mainArea.getTop() - (secondArea.getTop() + secondArea.getHeight());
        }
        else {
            return 0;
        }
    }

}

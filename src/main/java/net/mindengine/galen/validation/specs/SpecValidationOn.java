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
package net.mindengine.galen.validation.specs;

import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.SpecOn;

public class SpecValidationOn extends SpecValidationGeneral<SpecOn> {

    @Override
    protected int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, SpecOn spec) {
        if (side == Side.LEFT) {
            if (spec.getSideVertical() == Side.LEFT) {
                return secondArea.getLeft() - mainArea.getLeft();
            }
            else return secondArea.getLeft() + secondArea.getWidth() - mainArea.getLeft();
        }
        else if (side == Side.TOP) {
            if (spec.getSideHorizontal() == Side.TOP) {
                return secondArea.getTop() - mainArea.getTop();
            }
            else return secondArea.getTop() + secondArea.getHeight() - mainArea.getTop();
        }
        else if (side == Side.RIGHT) {
            if (spec.getSideVertical() == Side.LEFT) {
                return mainArea.getLeft() - secondArea.getLeft();
            }
            else return mainArea.getLeft() - secondArea.getLeft() - secondArea.getWidth();
        }
        else if (side == Side.BOTTOM) {
            if (spec.getSideHorizontal() == Side.TOP) {
                return mainArea.getTop() - secondArea.getTop();
            }
            else return mainArea.getTop() - secondArea.getTop() - secondArea.getHeight();
        }
        else {
            return 0;
        }
    }

}

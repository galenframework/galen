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
package com.galenframework.validation.specs;

import com.galenframework.page.Rect;
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecNear;

public class SpecValidationNear extends SpecValidationGeneral<SpecNear> {

    @Override
    protected int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, SpecNear spec) {
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

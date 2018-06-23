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
import com.galenframework.specs.Range;
import com.galenframework.specs.Side;
import com.galenframework.specs.Spec;
import com.galenframework.specs.SpecNear;
import com.galenframework.validation.MetaBasedValidation;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.SimpleValidationResult;

public class SpecValidationNear extends SpecValidationComplex<SpecNear> {

    @Override
    protected SimpleValidationResult validateSide(String objectName, SpecNear spec, Range range, Side side, Rect mainArea, Rect secondArea, PageValidation pageValidation) {
        return  MetaBasedValidation.forObjectsWithRange(objectName, spec.getObject(), range)
                .withFirstEdge(side.opposite())
                .withSecondEdge(side)
                .withInvertedCalculation(side == Side.LEFT || side == Side.TOP)
                .validate(mainArea, secondArea, pageValidation, side);
    }
}

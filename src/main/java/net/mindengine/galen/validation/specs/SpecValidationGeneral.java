/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.SpecComplex;
import net.mindengine.galen.validation.*;

/**
 * Used for specs 'inside' and 'near'
 * 
 * @author ishubin
 *
 * @param <T>
 */
public abstract class SpecValidationGeneral<T extends SpecComplex> extends SpecValidation<T> {

    @Override
    public ValidationResult check(final PageValidation pageValidation, final String objectName, final T spec) throws ValidationErrorException {
        final PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        final PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());

        final Rect mainArea = mainObject.getArea();
        final Rect secondArea = secondObject.getArea();

        final List<String> messages = new LinkedList<String>();

        for (final Location location : spec.getLocations()) {
            final String message = verifyLocation(mainArea, secondArea, location, pageValidation, spec);
            if (message != null) {
                messages.add(message);
            }
        }

        final List<ValidationObject> validationObjects = new LinkedList<ValidationObject>();
        validationObjects.add(new ValidationObject(mainArea, objectName));
        validationObjects.add(new ValidationObject(secondArea, spec.getObject()));

        if (CollectionUtils.isNotEmpty(messages)) {
            throw new ValidationErrorException().withMessage(createMessage(messages, objectName)).withValidationObjects(validationObjects);
        }

        return new ValidationResult(validationObjects);
    }

    private String createMessage(final List<String> messages, final String objectName) {
        final StringBuilder builder = new StringBuilder();

        builder.append(format("\"%s\" ", objectName));
        boolean comma = false;
        for (final String message : messages) {
            if (comma) {
                builder.append(", ");
            }
            builder.append("is ");
            builder.append(message);
            comma = true;
        }
        return builder.toString();
    }

    protected String verifyLocation(final Rect mainArea, final Rect secondArea, final Location location, final PageValidation pageValidation, final T spec) {
        final List<String> messages = new LinkedList<String>();
        Range range;

        try {
            range = pageValidation.convertRange(location.getRange());
        } catch (final Exception ex) {
            return format("Cannot convert range: " + ex.getMessage());
        }

        for (final Side side : location.getSides()) {
            final int offset = getOffsetForSide(mainArea, secondArea, side, spec);
            if (!range.holds(offset)) {
                messages.add(format("%dpx %s", offset, side));
            }
        }

        if (CollectionUtils.isNotEmpty(messages)) {
            final StringBuilder builder = new StringBuilder();
            boolean comma = false;
            for (final String message : messages) {
                if (comma) {
                    builder.append(" and ");
                }
                builder.append(message);
                comma = true;
            }

            builder.append(' ');
            builder.append(range.getErrorMessageSuffix());
            return builder.toString();
        } else {
            return null;
        }
    }

    protected abstract int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, T spec);

}

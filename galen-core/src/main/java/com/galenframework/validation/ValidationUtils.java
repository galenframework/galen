/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.validation;

import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.*;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class ValidationUtils {

    public interface OffsetProvider {
        int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, Spec spec);
    }

    public static String verifyLocation(Rect mainArea, Rect secondArea,
                                                        Location location, PageValidation pageValidation, Spec spec,
                                                        OffsetProvider offsetProvider) {
        List<String> messages = new LinkedList<>();

        Range range = location.getRange();

        for (Side side : location.getSides()) {
            int offset = offsetProvider.getOffsetForSide(mainArea, secondArea, side, spec);
            double calculatedOffset = pageValidation.convertValue(range, offset);

            if (!range.holds(calculatedOffset)) {
                if (range.isPercentage()) {
                    int precision = range.findPrecision();

                    messages.add(format("%s%% [%dpx] %s", new RangeValue(calculatedOffset, precision).toString(), offset, side));
                } else {
                    messages.add(format("%dpx %s", offset, side));
                }
            }
        }

        if (messages.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            boolean comma = false;
            for (String message : messages) {
                if (comma) {
                    buffer.append(" and ");
                }
                buffer.append(message);
                comma = true;
            }

            buffer.append(' ');
            buffer.append(range.getErrorMessageSuffix());
            if (range.isPercentage()) {
                int objectValue = pageValidation.getObjectValue(range.getPercentageOfValue());
                buffer.append(' ');
                buffer.append(rangeCalculatedFromPercentage(range, objectValue));
            }
            return buffer.toString();
        }
        else {
            return null;
        }
    }

    public static String rangeCalculatedFromPercentage(Range range, int objectValue) {
        if (range.getRangeType() == Range.RangeType.BETWEEN) {
            int from = (int)((objectValue * range.getFrom().asDouble()) / 100.0);
            int to = (int)((objectValue * range.getTo().asDouble()) / 100.0);

            return String.format("[%d to %dpx]", from, to);
        } else {
            RangeValue rangeValue = takeNonNullValue(range.getFrom(), range.getTo());
            int converted = (int)((objectValue * rangeValue.asDouble()) / 100.0);
            return "[" + converted + "px]";
        }
    }

    private static RangeValue takeNonNullValue(RangeValue from, RangeValue to) {
        if (from != null) {
            return from;
        } else if (to != null) {
            return to;
        } else {
            throw new NullPointerException("Both range values are null");
        }
    }

    public static String joinErrorMessagesForObject(List<String> messages, String objectName) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(format("\"%s\" is ", objectName));
        buffer.append(joinMessages(messages, " and "));
        return buffer.toString();
    }


    public static String joinMessages(List<String> messages, String separator) {
        StringBuffer buffer = new StringBuffer();

        boolean comma = false;
        for (String message : messages) {
            if (comma) {
                buffer.append(separator);
            }
            buffer.append(message);
            comma = true;
        }
        return buffer.toString();
    }
}

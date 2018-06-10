package com.galenframework.validation;

import com.galenframework.page.Rect;
import com.galenframework.specs.*;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class ValidationUtils {

    public interface OffsetProvider {
        int getOffsetForSide(Rect mainArea, Rect secondArea, Side side, Spec spec);
    }

    public static String verifyLocation(Rect mainArea, Rect secondArea, Location location, PageValidation pageValidation, Spec spec, OffsetProvider offsetProvider) {
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
        else return null;
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

    public static String createMessage(List<String> messages, String objectName) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(format("\"%s\" ", objectName));
        boolean comma = false;
        for (String message : messages) {
            if (comma) {
                buffer.append(", ");
            }
            buffer.append("is ");
            buffer.append(message);
            comma = true;
        }
        return buffer.toString();
    }
}

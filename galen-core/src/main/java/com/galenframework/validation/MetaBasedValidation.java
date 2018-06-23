package com.galenframework.validation;

import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Range;
import com.galenframework.specs.RangeValue;
import com.galenframework.specs.Side;

import static java.lang.String.format;

/**
 * This class is used in order to validate distance between edges of two objects.
 * It is used by different specs so that it can also construct layout meta for each validation
 */
public class MetaBasedValidation {
    private final String firstObject;
    private final String secondObject;
    private final Range expectedRange;
    private Side firstEdge = Side.LEFT;
    private Side secondEdge = Side.LEFT;
    private boolean isInverted = false;

    private MetaBasedValidation(String firstObject, String secondObject, Range expectedRange) {

        this.firstObject = firstObject;
        this.secondObject = secondObject;
        this.expectedRange = expectedRange;
    }

    public static MetaBasedValidation forObjectsWithRange(String firstObject, String secondObject, Range expectedRange) {
        return new MetaBasedValidation(firstObject, secondObject, expectedRange);
    }

    public MetaBasedValidation withBothEdges(Side side) {
        this.firstEdge = side;
        this.secondEdge = side;
        return this;
    }


    public SimpleValidationResult validate(Rect firstArea, Rect secondArea, PageValidation pageValidation, Side originalSpecSide) {
        int offset = getOffset(firstArea, secondArea);
        double calculatedOffset = pageValidation.convertValue(expectedRange, offset);

        ;
        if (!expectedRange.holds(calculatedOffset)) {
            if (expectedRange.isPercentage()) {
                int precision = expectedRange.findPrecision();


                String actualDistance = format("%s%% [%dpx]", new RangeValue(calculatedOffset, precision).toString(), offset);
                return SimpleValidationResult.error(
                    format("%s %s", actualDistance, originalSpecSide),
                    LayoutMeta.distance(firstObject, firstEdge, secondObject, secondEdge, expectedRange.prettyString("%"), actualDistance)
                );
            } else {
                return SimpleValidationResult.error(
                    format("%dpx %s", offset, originalSpecSide),
                    LayoutMeta.distance(firstObject, firstEdge, secondObject, secondEdge, expectedRange.prettyString(), offset + "px")
                );
            }
        }
        return SimpleValidationResult.success(LayoutMeta.distance(firstObject, firstEdge, secondObject, secondEdge, expectedRange.prettyString(), offset + "px"));
    }

    private int getOffset(Rect firstArea, Rect secondArea) {
        int offset = firstArea.getEdgePosition(firstEdge) - secondArea.getEdgePosition(secondEdge);
        if (isInverted) {
            return -offset;
        } else {
            return offset;
        }
    }

    public MetaBasedValidation withInvertedCalculation(boolean isInverted) {
        this.isInverted = isInverted;
        return this;
    }

    public MetaBasedValidation withFirstEdge(Side firstEdge) {
        this.firstEdge = firstEdge;
        return this;
    }

    public MetaBasedValidation withSecondEdge(Side secondEdge) {
        this.secondEdge = secondEdge;
        return this;
    }
}

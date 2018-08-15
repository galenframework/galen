package com.galenframework.validation.specs;

import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Location;
import com.galenframework.specs.Range;
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecComplex;
import com.galenframework.validation.*;

import java.util.LinkedList;
import java.util.List;

import static com.galenframework.validation.ValidationUtils.joinErrorMessagesForObject;
import static com.galenframework.validation.ValidationUtils.joinMessages;
import static com.galenframework.validation.ValidationUtils.rangeCalculatedFromPercentage;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public abstract class SpecValidationComplex<T extends SpecComplex> extends SpecValidation<T> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        PageElement secondObject = pageValidation.findPageElement(spec.getObject());
        checkAvailability(secondObject, spec.getObject());

        Rect mainArea = mainObject.getArea();
        Rect secondArea = secondObject.getArea();

        List<ValidationObject> objects = asList(new ValidationObject(mainArea, objectName), new ValidationObject(secondArea, spec.getObject()));

        doCustomValidations(objectName, mainArea, secondArea, spec, objects);

        List<LayoutMeta> layoutMeta = validateAllSides(pageValidation, objectName, mainArea, secondArea, spec, objects);
        return new ValidationResult(spec, objects).withMeta(layoutMeta);
    }

    protected void doCustomValidations(String objectName, Rect mainArea, Rect secondArea, T spec, List<ValidationObject> objects) throws ValidationErrorException {
    }

    protected abstract SimpleValidationResult validateSide(String objectName, T spec, Range range, Side side, Rect mainArea, Rect secondArea, PageValidation pageValidation);


    protected List<LayoutMeta> validateAllSides(PageValidation pageValidation, String objectName, Rect mainArea, Rect secondArea, T spec, List<ValidationObject> validationObjects) throws ValidationErrorException {
        List<LayoutMeta> meta = new LinkedList<>();

        List<String> errorMessages = new LinkedList<>();
        for (Location location : spec.getLocations()) {
            Range range = location.getRange();

            List<String> perLocationErrors = new LinkedList<>();

            for (Side side : location.getSides()) {
                SimpleValidationResult svr = validateSide(objectName, spec, range, side, mainArea, secondArea, pageValidation);
                meta.add(svr.getMeta());

                if (svr.isError()) {
                    perLocationErrors.add(svr.getError());
                }
            }

            if (!perLocationErrors.isEmpty()) {
                errorMessages.add(convertPerLocationErrors(pageValidation, range, perLocationErrors));
            }

        }

        if (errorMessages.size() > 0) {
            throw new ValidationErrorException()
                .withMessage(joinErrorMessagesForObject(errorMessages, objectName))
                .withValidationObjects(validationObjects)
                .withMeta(meta);
        }
        return meta;

    }

    private String convertPerLocationErrors(PageValidation pageValidation, Range range, List<String> perLocationErrors) {
        String calculatedFromPercentage = "";
        if (range.isPercentage()) {
            calculatedFromPercentage = " " + rangeCalculatedFromPercentage(range, pageValidation.getObjectValue(range.getPercentageOfValue()));
        }
        return format("%s %s%s", joinMessages(perLocationErrors, " and "), range.getErrorMessageSuffix(), calculatedFromPercentage);
    }
}

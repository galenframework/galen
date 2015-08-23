/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import com.galenframework.page.PageElement;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.SpecCount;

import javax.xml.bind.ValidationException;
import java.util.*;

import static java.lang.String.format;

public class SpecValidationCount extends SpecValidation<SpecCount> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecCount spec) throws ValidationErrorException {
        List<String> matchingNames = pageValidation.getPageSpec().findMatchingObjectNames(spec.getPattern());
        Map<String, PageElement> reportElements;
        Map<String, PageElement> filteredElements;

        String filterName;

        if (spec.getFetchType() == SpecCount.FetchType.ANY) {
            filteredElements = findAllObjects(pageValidation, matchingNames);
            reportElements = filteredElements;
            filterName = "";
        } else if (spec.getFetchType() == SpecCount.FetchType.VISIBLE) {
            filteredElements = findVisibleObjects(pageValidation, matchingNames);
            reportElements = filteredElements;
            filterName = " visible";
        } else if (spec.getFetchType() == SpecCount.FetchType.ABSENT) {
            filteredElements = findAbsentObjects(pageValidation, matchingNames);
            reportElements = Collections.emptyMap();
            filterName = " absent";
        } else {
            throw new ValidationErrorException("Unknown filter: " + spec.getFetchType().toString().toLowerCase());
        }


        if (spec.getAmount().holds(filteredElements.size())) {
            return new ValidationResult(convertToValidationObjects(reportElements));
        } else {
            throw new ValidationErrorException()
                    .withValidationObjects(convertToValidationObjects(reportElements))
                    .withMessage(format("There are %d%s objects matching \"%s\" %s",
                            filteredElements.size(),
                            filterName,
                            spec.getPattern(),
                            spec.getAmount().getErrorMessageSuffix("")));
        }
    }

    private List<ValidationObject> convertToValidationObjects(Map<String, PageElement> reportElements) {
        List<ValidationObject> validationObjects = new LinkedList<>();
        for (Map.Entry<String, PageElement> element : reportElements.entrySet()) {
            validationObjects.add(new ValidationObject(element.getValue().getArea(), element.getKey()));
        }
        return validationObjects;
    }


    private Map<String, PageElement> findAllObjects(PageValidation pageValidation, List<String> matchingNames) {
        Map<String, PageElement> objects = new HashMap<>();
        for (String name : matchingNames) {
            objects.put(name, pageValidation.findPageElement(name));
        }
        return objects;
    }

    private Map<String, PageElement> findVisibleObjects(PageValidation pageValidation, List<String> matchingNames) {
        Map<String, PageElement> objects = new HashMap<>();
        for (String name : matchingNames) {
            PageElement pageElement = pageValidation.findPageElement(name);
            if (pageElement.isVisible() && pageElement.isPresent()) {
                objects.put(name, pageElement);
            }
        }
        return objects;
    }

    private Map<String, PageElement> findAbsentObjects(PageValidation pageValidation, List<String> matchingNames) {
        Map<String, PageElement> objects = new HashMap<>();
        for (String name : matchingNames) {
            PageElement pageElement = pageValidation.findPageElement(name);
            if (!pageElement.isVisible() || !pageElement.isPresent()) {
                objects.put(name, pageElement);
            }
        }
        return objects;
    }
}

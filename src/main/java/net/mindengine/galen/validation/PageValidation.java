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
package net.mindengine.galen.validation;

import static java.lang.String.format;

import java.lang.reflect.Method;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.IncorrectSpecException;
import net.mindengine.galen.specs.reader.page.PageSpec;

public class PageValidation {

    private Page page;
    private PageSpec pageSpec;

    public PageValidation(Page page, PageSpec pageSpec) {
        this.setPage(page);
        this.setPageSpec(pageSpec);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ValidationError check(String objectName, Spec spec) {
        SpecValidation specValidation = ValidationFactory.getValidation(spec, this);
        return specValidation.check(this, objectName, spec);
    }

    public PageSpec getPageSpec() {
        return pageSpec;
    }

    public void setPageSpec(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    public Range convertRangeFromPercentageToPixels(Range range) {
        String valuePath = range.getPercentageOfValue();
        int index = valuePath.indexOf("/");
        if (index > 0 && index < valuePath.length() - 1) {
            String objectName = valuePath.substring(0, index);
            String fieldPath = valuePath.substring(index + 1);
            
            Locator locator = pageSpec.getObjectLocator(objectName);
            if (locator != null) {
                PageElement object = page.getObject(objectName, locator);
                if (object != null) {
                    Object objectValue = getObjectValue(object, fieldPath);
                    int value = convertToInt(objectValue);
                    return Range.between((range.getFrom() * value) / 100.0, (range.getTo() * value) / 100.0);
                }
                else throw new IncorrectSpecException(format("Cannot find object \"%s\" using locator %s \"%s\"", objectName, locator.getLocatorType(), locator.getLocatorValue()));
            }
            else throw new IncorrectSpecException(format("Locator for object \"%s\" is not specified", objectName));
        }
        else throw new IncorrectSpecException(format("Value path is incorrect %s", valuePath));
    }

    private int convertToInt(Object objectValue) {
        if (objectValue == null) {
            throw new NullPointerException("The returned value is null");
        }
        else {
            if (objectValue instanceof Integer) {
                return ((Integer)objectValue).intValue();
            }
            else if (objectValue instanceof Double) {
                return ((Double)objectValue).intValue();
            }
            else {
                throw new IncorrectSpecException(format("Cannot convert value to integer. The obtained value is of %s type", objectValue.getClass()));
            }
        }
    }

    private Object getObjectValue(Object object, String fieldPath) {
        int index = fieldPath.indexOf("/");
        if (index > 0 && index < fieldPath.length() - 1) {
            
            String fieldName = fieldPath.substring(0, index);
            String leftOverPath = fieldPath.substring(index + 1);
            if (leftOverPath.isEmpty()) {
                throw new IncorrectSpecException(format("Cannot read path %s", fieldPath));
            }
            
            Object field = getField(object, fieldName);
            if (field == null) {
                throw new NullPointerException(format("\"%s\" returned null", fieldName));
            }
            return getObjectValue(field, leftOverPath);
        }
        else {
            return getField(object, fieldPath);
        }
    }

    private Object getField(Object object, String fieldName) {
        try {
            Method getterMethod = object.getClass().getMethod(getterForField(fieldName));
            return getterMethod.invoke(object);
        } catch (Exception e) {
            throw new IncorrectSpecException(format("Cannot read field: \"%s\"", fieldName));
        }
    }

    private String getterForField(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public Range convertRange(Range range) {
        if (range != null) {
            if (range.isPercentage()) {
                return convertRangeFromPercentageToPixels(range);
            }
        }
        return range;
    }

}

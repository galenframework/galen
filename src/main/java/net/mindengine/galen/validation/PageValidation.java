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
package net.mindengine.galen.validation;

import static java.lang.String.format;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.lang.reflect.Method;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.SectionFilter;

public class PageValidation {

    private Browser browser;
    private Page page;
    private PageSpec pageSpec;
    private ValidationListener validationListener;
    private SectionFilter sectionFilter;

    public PageValidation(Browser browser, Page page, PageSpec pageSpec, ValidationListener validationListener, SectionFilter sectionFilter) {
        this.setBrowser(browser);
        this.setPage(page);
        this.setPageSpec(pageSpec);
        this.setValidationListener(validationListener);
        this.setSectionFilter(sectionFilter);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ValidationResult check(String objectName, Spec spec) {
        SpecValidation specValidation = ValidationFactory.getValidation(spec, this);

        ValidationResult result = check(specValidation, objectName, spec);

        if (spec.isOnlyWarn() && result.getError() != null) {
            result.getError().setOnlyWarn(true);
        }
        return result;
    }

    private ValidationResult check(SpecValidation specValidation, String objectName, Spec spec) {
        try {
            return specValidation.check(this, objectName, spec);
        }
        catch (ValidationErrorException ex) {
            return ex.asValidationResult();
        }
    }

    public PageSpec getPageSpec() {
        return pageSpec;
    }

    public void setPageSpec(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    public PageElement findPageElement(String objectName) {
        Locator objectLocator = pageSpec.getObjectLocator(objectName);
        if (objectLocator != null) {
            return page.getObject(objectName, objectLocator);
        }
        else {
            return page.getSpecialObject(objectName);
        }
    }
    
    public Range convertRangeFromPercentageToPixels(Range range) {
        String valuePath = range.getPercentageOfValue();
        int index = valuePath.indexOf("/");
        if (index > 0 && index < valuePath.length() - 1) {
            String objectName = valuePath.substring(0, index);
            String fieldPath = valuePath.substring(index + 1);
            
            Locator locator = pageSpec.getObjectLocator(objectName);
            PageElement pageElement = findPageElementOnPage(objectName, locator);
            
            if (pageElement != null) {
                Object objectValue = getObjectValue(pageElement, fieldPath);
                int value = convertToInt(objectValue);
                
                
                Double valueA = range.getFrom();
                Double valueB = range.getTo();
                if (valueA != null) {
                    valueA = valueA * value / 100.0;
                }
                if (valueB != null) {
                    valueB = valueB * value / 100.0;
                }
                
                return new Range(valueA, valueB).withType(range.getRangeType());
            }
            else throw new SyntaxException(UNKNOWN_LINE, format("Locator for object \"%s\" is not specified", objectName));
        }
        else throw new SyntaxException(UNKNOWN_LINE, format("Value path is incorrect %s", valuePath));
    }

    private PageElement findPageElementOnPage(String objectName, Locator locator) {
        if (locator != null) {
            return page.getObject(objectName, locator);
        }
        else {
            return page.getSpecialObject(objectName);
        }
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
                throw new SyntaxException(UNKNOWN_LINE, format("Cannot convert value to integer. The obtained value is of %s type", objectValue.getClass()));
            }
        }
    }

    private Object getObjectValue(Object object, String fieldPath) {
        int index = fieldPath.indexOf("/");
        if (index > 0 && index < fieldPath.length() - 1) {
            
            String fieldName = fieldPath.substring(0, index);
            String leftOverPath = fieldPath.substring(index + 1);
            if (leftOverPath.isEmpty()) {
                throw new SyntaxException(UNKNOWN_LINE, format("Cannot read path %s", fieldPath));
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
            throw new SyntaxException(UNKNOWN_LINE, format("Cannot read field: \"%s\"", fieldName));
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

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public SectionFilter getSectionFilter() {
        return sectionFilter;
    }

    public void setSectionFilter(SectionFilter sectionFilter) {
        this.sectionFilter = sectionFilter;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

}

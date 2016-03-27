/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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

import java.util.LinkedList;
import java.util.List;

import com.galenframework.specs.page.ObjectSpecs;
import com.galenframework.specs.page.PageSection;
import com.galenframework.specs.page.SpecGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.galenframework.specs.Spec;

public class SectionValidation {
    
    private final static Logger LOG = LoggerFactory.getLogger(SectionValidation.class);

    private List<PageSection> pageSections;
    private PageValidation pageValidation;
    private ValidationListener validationListener;

    public SectionValidation(List<PageSection> pageSections, PageValidation pageValidation, ValidationListener validationListener) {
        this.pageSections = pageSections;
        this.pageValidation = pageValidation;
        this.validationListener = validationListener;
    }

    public List<ValidationResult> check() {
        List<ValidationResult> validationResults = new LinkedList<>();
        
        for (PageSection section : pageSections) {
            validationResults.addAll(checkPageSection(section));
        }
        return validationResults;
    }

    private List<ValidationResult> checkPageSection(PageSection section) {
        List<ValidationResult> validationResult= new LinkedList<>();
        validationResult.addAll(checkSection(section));
        return validationResult;
    }

    private void tellAfterSection(PageSection section) {
        if (validationListener != null) {
            validationListener.onAfterSection(pageValidation, section);
        }
    }

    private void tellBeforeSection(PageSection section) {
        if (validationListener != null) {
            validationListener.onBeforeSection(pageValidation, section);
        }
    }

    private List<ValidationResult> checkObjects(List<ObjectSpecs> objects) {
        List<ValidationResult> validationResults = new LinkedList<>();
        for (ObjectSpecs object : objects) {
            tellOnObject(object.getObjectName());

            validationResults.addAll(checkObject(object.getObjectName(), object.getSpecs()));

            validationResults.addAll(checkSpecGroups(object.getObjectName(), object.getSpecGroups()));

            tellOnAfterObject(object.getObjectName());
        }
        return validationResults;
    }

    private List<ValidationResult> checkSpecGroups(String objectName, List<SpecGroup> specGroups) {
        List<ValidationResult> validationResults = new LinkedList<>();
        if (specGroups != null) {
            for (SpecGroup specGroup : specGroups) {
                tellOnSpecGroup(specGroup);

                validationResults.addAll(checkObject(objectName, specGroup.getSpecs()));

                tellOnAfterSpecGroup(specGroup);
            }
        }
        return validationResults;
    }

    private List<ValidationResult> checkSection(PageSection section) {
        tellBeforeSection(section);

        List<ValidationResult> result  = new LinkedList<>();

        if (section.getSections() != null) {
            for (PageSection subSection : section.getSections()) {
                result.addAll(checkSection(subSection));
            }
        }

        result.addAll(checkObjects(section.getObjects()));

        tellAfterSection(section);

        return result;
    }

    private void tellOnAfterObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onAfterObject(pageValidation, objectName);
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation after object", e);
            }
        } 
    }

    private void tellOnObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onObject(pageValidation, objectName);
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation on object", e);
            }
        }
    }

    private void tellOnSpecGroup(SpecGroup specGroup) {
        if (validationListener != null) {
            try {
                validationListener.onSpecGroup(pageValidation, specGroup.getName());
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation of spec group", e);
            }
        }
    }

    private void tellOnAfterSpecGroup(SpecGroup specGroup) {
        if (validationListener != null) {
            try {
                validationListener.onAfterSpecGroup(pageValidation, specGroup.getName());
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation of spec group", e);
            }
        }
    }
    private List<ValidationResult> checkObject(String objectName, List<Spec> specs) {
        List<ValidationResult> validationResults = new LinkedList<>();

        for (Spec spec : specs) {
            tellBeforeSpec(pageValidation, objectName, spec);

            ValidationResult result = pageValidation.check(objectName, spec);
            if (result.getError()!= null) {
                validationResults.add(result);
                tellOnSpecError(pageValidation, objectName, spec, result);
            }
            else {
                tellOnSpecSuccess(pageValidation, objectName, spec, result);
            }
        }

        return validationResults;
    }

    private void tellBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {
        try {
            if (validationListener != null) {
                validationListener.onBeforeSpec(pageValidation, objectName, spec);
            }
        } catch (Exception e) {
            LOG.trace("Unknown error during before spec event", e);
        }
    }

    private void tellOnSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        try {
            if (validationListener != null) {
                validationListener.onSpecError(pageValidation, objectName, spec, result);
            }
        }
        catch (Exception e) {
            LOG.trace("Unknown error during tell spec error", e);
        }
    }

    private void tellOnSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        try {
            if (validationListener != null) {
                validationListener.onSpecSuccess(pageValidation, objectName, spec, result);
            }
        }
        catch (Exception e) {
            LOG.trace("Unknown error during tell spec success", e);
        }
    }

}

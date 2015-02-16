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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.ConditionalBlock;
import net.mindengine.galen.specs.page.ConditionalBlockStatement;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;

import static net.mindengine.galen.validation.ValidationResult.doesNotHaveErrors;

public class SectionValidation {
    
    private final static Logger LOG = LoggerFactory.getLogger(SectionValidation.class);

    private static final GalenPageRunner UNKNOWN_PAGE_RUNNER = null;
    private static final boolean SHOULD_REPORT = true;
    private static final boolean SHOULD_NOT_REPORT = false;
    private static final List<ValidationResult> EMPTY_RESULTS = new LinkedList<ValidationResult>();
    private List<PageSection> pageSections;
    private PageValidation pageValidation;
    private ValidationListener validationListener;

    public SectionValidation(List<PageSection> pageSections, PageValidation pageValidation, ValidationListener validationListener) {
        this.pageSections = pageSections;
        this.pageValidation = pageValidation;
        this.validationListener = validationListener;
    }

    public List<ValidationResult> check() {
        
        //Fetching all multi objects from page before validation
        pageValidation.getPageSpec().updateMultiObjects(pageValidation.getPage());
        
        List<ValidationResult> validationResults = new LinkedList<ValidationResult>();
        
        for (PageSection section : pageSections) {
            validationResults.addAll(checkSection(section));
        }
        return validationResults;
    }

    private List<ValidationResult> checkSection(PageSection section) {
        tellBeforeSection(section);
        List<ValidationResult> validationResult= new LinkedList<ValidationResult>();
        validationResult.addAll(checkObjects(section.getObjects()));
        
        List<ConditionalBlock> conditionalBlocks = section.getConditionalBlocks();
        if (conditionalBlocks != null) {
            for (ConditionalBlock block : conditionalBlocks) {
                validationResult.addAll(checkConditionalBlock(block));
            }
        }
        
        tellAfterSection(section);
        return validationResult;
    }

    private void tellAfterSection(PageSection section) {
        if (validationListener != null) {
            validationListener.onAfterSection(UNKNOWN_PAGE_RUNNER, pageValidation, section);
        }
    }

    private void tellBeforeSection(PageSection section) {
        if (validationListener != null) {
            validationListener.onBeforeSection(UNKNOWN_PAGE_RUNNER, pageValidation, section);
        }
    }

    private List<ValidationResult> checkObjects(List<ObjectSpecs> objects, boolean shouldReport) {
        List<ValidationResult> validationResults = new LinkedList<ValidationResult>();
        for (ObjectSpecs object : objects) {
            List<String> allObjectNames = findAllObjectNames(object.getObjectName());
            for (String objectName : allObjectNames) {
                if (shouldReport) {
                    tellOnObject(objectName);
                }
                
                validationResults.addAll(checkObject(objectName, object.getSpecs(), shouldReport));
                
                if (shouldReport) {
                    tellOnAfterObject(objectName);
                }
            }
        }
        return validationResults;
    }

    private List<ValidationResult> checkConditionalBlock(ConditionalBlock block) {
        if (oneOfConditionsApplies(block.getStatements())) {
            return checkObjects(block.getBodyObjects());
        }
        else if (block.getOtherwiseObjects() != null) {
            return checkObjects(block.getOtherwiseObjects());
        }
        else return EMPTY_RESULTS;
    }

    private List<ValidationResult> checkObjects(List<ObjectSpecs> bodyObjects) {
        return checkObjects(bodyObjects, SHOULD_REPORT);
    }

    private boolean oneOfConditionsApplies(List<ConditionalBlockStatement> statements) {
        for (ConditionalBlockStatement statement : statements) {
            List<ValidationResult> validationResults = checkObjectsSilently(statement.getObjects());
            
            boolean statementStatus =  doesNotHaveErrors(validationResults);
            if (statement.isInverted()) {
                statementStatus = !statementStatus;
            }
            
            if (statementStatus) {
                return true;
            }
        }
        return false;
    }


    private List<ValidationResult> checkObjectsSilently(List<ObjectSpecs> objects) {
        return checkObjects(objects, SHOULD_NOT_REPORT);
    }

    
    private List<String> findAllObjectNames(String objectsDefinition) {
        List<String> objectNames = new LinkedList<String>();
        
        String names[] = objectsDefinition.split(",");
        
        for (String name : names) {
            name = name.trim();
            if (!name.isEmpty()) {
                if (isRegularExpression(name)) {
                    objectNames.addAll(fetchUsingRegex(name));
                }
                else {
                    objectNames.add(name);
                }
            }
        }
        return objectNames;
    }

    private List<String> fetchUsingRegex(String simpleRegex) {
        String regex = simpleRegex.replace("#", "[0-9]+").replace("*", "[a-zA-Z0-9_]+");
        Pattern pattern = Pattern.compile(regex);
        
        List<String> objectNames = new LinkedList<String>();
        for (String objectName : pageValidation.getPageSpec().getObjects().keySet()) {
            if (pattern.matcher(objectName).matches()) {
                objectNames.add(objectName);
            }
        }
        
        return objectNames;
    }

    private boolean isRegularExpression(String name) {
        if (name.contains("*") || name.contains("#")) {
            return true;
        }
        else return false;
    }

    private void tellOnAfterObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onAfterObject(UNKNOWN_PAGE_RUNNER, pageValidation, objectName);
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation after object", e);
            }
        } 
    }

    private void tellOnObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onObject(UNKNOWN_PAGE_RUNNER, pageValidation, objectName);
            }
            catch (Exception e) {
                LOG.trace("Unknown error during validation on object", e);
            }
        }
    }

    private List<ValidationResult> checkObject(String objectName, List<Spec> specs, boolean shouldReport) {
        List<ValidationResult> validationResults = new LinkedList<ValidationResult>();
        for (Spec spec : specs) {
            
            ValidationResult result = pageValidation.check(objectName, spec);
            if (result.getError()!= null) {
                validationResults.add(result);
                if (shouldReport) {
                    tellOnSpecError(pageValidation, objectName, spec, result);
                }
            }
            else if (shouldReport) {
                tellOnSpecSuccess(pageValidation, objectName, spec, result);
            }
        }
        return validationResults;
    }

    private void tellOnSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        try {
            if (validationListener != null) {
                validationListener.onSpecError(UNKNOWN_PAGE_RUNNER, pageValidation, objectName, spec, result);
            }
        }
        catch (Exception e) {
            LOG.trace("Unknown error during tell spec error", e);
        }
    }

    private void tellOnSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult result) {
        try {
            if (validationListener != null) {
                validationListener.onSpecSuccess(UNKNOWN_PAGE_RUNNER, pageValidation, objectName, spec, result);
            }
        }
        catch (Exception e) {
            LOG.trace("Unknown error during tell spec success", e);
        }
    }

}

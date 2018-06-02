/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.Spec;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class ValidationErrorException extends Exception {

	private List<String> errorMessages;
    private ImageComparison imageComparison;
    private List<ValidationObject> validationObjects;
    private List<ValidationResult> childValidationResults;
    private List<LayoutMeta> meta;


    public ValidationErrorException() {
		super();
	}
	
	public ValidationErrorException(List<ValidationObject> validationObjects, List<String> errorMessages) {
        this.validationObjects = validationObjects;
		this.errorMessages = errorMessages;
	}
	
    public ValidationErrorException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        withMessage(paramString);
    }
    
    public ValidationErrorException withMessage(String message) {
		if (errorMessages == null) {
			errorMessages = new LinkedList<>();
		}
		errorMessages.add(message);
		
		return this;
	}

	public ValidationErrorException withValidationObject(ValidationObject validationObject) {
    	if (this.validationObjects== null) {
    		this.validationObjects = new LinkedList<>();
    	}
        this.validationObjects.add(validationObject);

    	return this;
    }
    
    public ValidationErrorException(String paramString) {
        super(paramString);
        withMessage(paramString);
    }

    public ValidationErrorException(Throwable paramThrowable) {
        super(paramThrowable);
        setErrorMessages(asList(paramThrowable.getClass().getName() + ": " + paramThrowable.getMessage()));
    }

    public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public List<ValidationObject> getValidationObjects() {
		return validationObjects;
	}

	public void setValidationObjects(List<ValidationObject> validationObjects) {
        this.validationObjects = validationObjects;
	}

	/**
     * 
     */
    private static final long serialVersionUID = -1566513657187992205L;

    public ValidationErrorException withMessages(List<String> messages) {
        setErrorMessages(messages);
        return this;
    }

    public ValidationResult asValidationResult(Spec spec) {
        ValidationResult validationResult = new ValidationResult(
                spec,
                this.getValidationObjects(),
                new ValidationError(this.getErrorMessages(), this.getImageComparison()), this.getMeta());

        validationResult.setChildValidationResults(childValidationResults);
        return validationResult;
    }

    public ImageComparison getImageComparison() {
        return imageComparison;
    }

    public void setImageComparison(ImageComparison imageComparison) {
        this.imageComparison = imageComparison;
    }

    public ValidationErrorException withImageComparison(ImageComparison imageComparison) {
        setImageComparison(imageComparison);
        return this;
    }

    public ValidationErrorException withValidationObjects(List<ValidationObject> validationObjects) {
        if (this.validationObjects == null) {
            this.validationObjects = validationObjects;
        } else {
            this.validationObjects.addAll(validationObjects);
        }
        return this;
    }

    public ValidationErrorException withChildValidationResults(List<ValidationResult> childValidationResults) {
        setChildValidationResults(childValidationResults);
        return this;
    }

    public void setChildValidationResults(List<ValidationResult> childValidationResults) {
        this.childValidationResults = childValidationResults;
    }

    public List<ValidationResult> getChildValidationResults() {
        return childValidationResults;
    }

    public List<LayoutMeta> getMeta() {
        return meta;
    }

    public void setMeta(List<LayoutMeta> meta) {
        this.meta = meta;
    }
}

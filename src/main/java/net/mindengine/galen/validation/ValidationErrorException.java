/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

public class ValidationErrorException extends Exception {

	private List<ErrorArea> errorAreas;
	private List<String> errorMessages;
	
	public ValidationErrorException() {
		super();
	}
	
	public ValidationErrorException(List<ErrorArea> errorAreas, List<String> errorMessages) {
		this.errorAreas = errorAreas;
		this.errorMessages = errorMessages;
	}
	
    public ValidationErrorException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        withMessage(paramString);
    }
    
    public ValidationErrorException withMessage(String message) {
		if (errorMessages == null) {
			errorMessages = new LinkedList<String>();
		}
		errorMessages.add(message);
		
		return this;
	}

	public ValidationErrorException withErrorArea(ErrorArea errorArea) {
    	if (errorAreas == null) {
    		errorAreas = new LinkedList<ErrorArea>();
    	}
    	errorAreas.add(errorArea);
    	
    	return this;
    }
    
    public ValidationErrorException(String paramString) {
        super(paramString);
        withMessage(paramString);
    }

    public ValidationErrorException(Throwable paramThrowable) {
        super(paramThrowable);
    }

    public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public List<ErrorArea> getErrorAreas() {
		return errorAreas;
	}

	public void setErrorAreas(List<ErrorArea> errorAreas) {
		this.errorAreas = errorAreas;
	}

	/**
     * 
     */
    private static final long serialVersionUID = -1566513657187992205L;

    public ValidationErrorException withMessages(List<String> messages) {
        setErrorMessages(messages);
        return this;
    }

}

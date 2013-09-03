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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ValidationError {

    private List<ErrorArea> errorAreas;
    private List<String> messages;

    public ValidationError(List<ErrorArea> errorAreas, List<String> messages) {
        this.setErrorAreas(errorAreas);
        this.messages = messages;
    }

    public ValidationError() {
        
    }
        

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(getErrorAreas()).append(messages).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ValidationError))
            return false;
        
        ValidationError rhs = (ValidationError)obj;
        return new EqualsBuilder().append(getErrorAreas(), rhs.getErrorAreas()).append(messages, rhs.messages).isEquals();
    }
    
    @Override
    public String toString() {
        return format("Error{%s, areas=%s}", messages, getErrorAreas());
    }

    public ValidationError withArea(ErrorArea errorArea) {
        if (getErrorAreas() == null) {
            setErrorAreas(new LinkedList<ErrorArea>());
        }
        getErrorAreas().add(errorArea);
        return this;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public ValidationError withMessage(String message) {
        if (messages == null) {
            messages = new LinkedList<String>();
        }
        messages.add(message);
        return this;
    }

    public ValidationError withErrorAreas(List<ErrorArea> errorAreas) {
        this.setErrorAreas(errorAreas);
        return this;
    }

    public List<ErrorArea> getErrorAreas() {
        return errorAreas;
    }

    public void setErrorAreas(List<ErrorArea> errorAreas) {
        this.errorAreas = errorAreas;
    }

    public static ValidationError fromException(Exception e) {
        return new ValidationError().withMessage(ExceptionUtils.getMessage(e));
    }

}

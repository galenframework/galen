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

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.page.Rect;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ValidationError {

    private Rect area;
    private List<String> messages;

    public ValidationError(Rect area, String...errorMessages) {
        this.area = area;
        this.messages = Arrays.asList(errorMessages);
    }

    public ValidationError(String...errorMessages) {
        this.messages = Arrays.asList(errorMessages);
    }

    public ValidationError(Rect area, List<String> messages) {
        this.area = area;
        this.messages = messages;
    }

    public Rect getArea() {
        return this.area;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(area).append(messages).toHashCode();
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
        return new EqualsBuilder().append(area, rhs.area).append(messages, rhs.messages).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("area", area)
            .append("messages", messages)
            .toString();
    }

    public ValidationError withArea(Rect objectArea) {
        this.area = objectArea;
        return this;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

}

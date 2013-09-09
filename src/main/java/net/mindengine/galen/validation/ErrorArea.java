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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import net.mindengine.galen.page.Rect;

public class ErrorArea {

    private Rect rect;
    private String message;
    
    public ErrorArea() {
    }
    
    public ErrorArea(Rect rect, String message) {
        super();
        this.rect = rect;
        this.message = message;
    }
    
    public Rect getRect() {
        return rect;
    }
    public void setRect(Rect rect) {
        this.rect = rect;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(rect).append(message).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ErrorArea))
            return false;
        
        ErrorArea rhs = (ErrorArea)obj;
        return new EqualsBuilder().append(rect, rhs.rect).append(message, rhs.message).isEquals();
    }
    
    @Override
    public String toString() {
        return format("ErrorArea{rect: %s, message: %s}", rect, message);
    }
    
}

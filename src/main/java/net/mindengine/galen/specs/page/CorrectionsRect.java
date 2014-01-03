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
package net.mindengine.galen.specs.page;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CorrectionsRect {
    
    public static enum Type {
        MINUS, PLUS, EQUALS
    }
    
    public static class Correction {
        public Correction(int value, Type type) {
            super();
            this.value = value;
            this.type = type;
        }
        private int value;
        private Type type = Type.PLUS;
        public int getValue() {
            return value;
        }
        public void setValue(int value) {
            this.value = value;
        }
        public Type getType() {
            return type;
        }
        public void setType(Type type) {
            this.type = type;
        }
        public int correct(int oldValue) {
            if (type == Type.PLUS) {
                return oldValue + value;
            }
            else if (type == Type.MINUS) {
                return oldValue - value;
            }
            else if (type == Type.EQUALS) {
                return value;
            }
            else {
                return oldValue;
            }
        } 
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (!(obj instanceof Correction))
                return false;
            
            Correction rhs = (Correction)obj;
            return new EqualsBuilder()
                .append(this.value, rhs.value)
                .append(this.type, rhs.type)
                .isEquals();
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(value)
                .append(type)
                .toHashCode();
        }
        
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("value", value)
                .append("type", type)
                .toString();
        }
    }
    
    
    public CorrectionsRect(Correction left, Correction top, Correction width, Correction height) {
        super();
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    private Correction left;
    private Correction top;
    private Correction width;
    private Correction height;
    
    public Correction getLeft() {
        return left;
    }
    public void setLeft(Correction left) {
        this.left = left;
    }
    public Correction getTop() {
        return top;
    }
    public void setTop(Correction top) {
        this.top = top;
    }
    public Correction getWidth() {
        return width;
    }
    public void setWidth(Correction width) {
        this.width = width;
    }
    public Correction getHeight() {
        return height;
    }
    public void setHeight(Correction height) {
        this.height = height;
    }
    
    public static CorrectionsRect simpleCorrectionRect(int left, int top, int width, int height) {
        return new CorrectionsRect(simpleCorrectionValue(left),
                simpleCorrectionValue(top),
                simpleCorrectionValue(width),
                simpleCorrectionValue(height));
    }
    
    private static Correction simpleCorrectionValue(int value) {
        Type type = Type.PLUS;
        if (value < 0) {
            type = Type.MINUS;
        }
        return new Correction(Math.abs(value), type);
    }
    

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof CorrectionsRect))
            return false;
        
        CorrectionsRect rhs = (CorrectionsRect)obj;
        return new EqualsBuilder()
            .append(this.left, rhs.left)
            .append(this.top, rhs.top)
            .append(this.width, rhs.width)
            .append(this.height, rhs.height)
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.left)
            .append(this.top)
            .append(this.width)
            .append(this.height)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("left", this.left)
            .append("top", this.top)
            .append("width", this.width)
            .append("height", this.height)
            .toString();
    }
}

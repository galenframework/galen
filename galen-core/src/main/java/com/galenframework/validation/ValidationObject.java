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

import com.galenframework.page.Rect;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ValidationObject {

    private Rect area;
    private String name;

    public ValidationObject(Rect area, String name) {
        this.area = area;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rect getArea() {
        return area;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //@formatter:off
                .append(name)
                .append(area)
                .toHashCode(); //@formatter:on
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ValidationObject))
            return false;

        ValidationObject rhs = (ValidationObject)obj;
        return new EqualsBuilder() //@formatter:off
                .append(name, rhs.name)
                .append(area, rhs.area)
                .isEquals(); //@formatter:on
    }

    @Override
    public String toString() {
        return format("Object{name=%s, area=%s}", name, area);
    }
}

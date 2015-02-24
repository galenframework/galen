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
package net.mindengine.galen.components.specs;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by ishubin on 2015/02/22.
 */
public class ExpectedSpecObject {

    private String expectedName;
    private List<String> specs;

    public ExpectedSpecObject(String expectedName) {
        this.expectedName = expectedName;
    }

    public ExpectedSpecObject withSpecs(String...specs) {
        this.specs = asList(specs);
        return this;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public static List<ExpectedSpecObject> convertSection(PageSection pageSection) {
        List<ExpectedSpecObject> objects = new LinkedList<ExpectedSpecObject>();

        for (ObjectSpecs objectSpecs : pageSection.getObjects()) {
            ExpectedSpecObject object = new ExpectedSpecObject(objectSpecs.getObjectName());
            List<String> specs = new LinkedList<String>();

            for (Spec spec : objectSpecs.getSpecs()) {
                specs.add(spec.getOriginalText());
            }

            object.setSpecs(specs);
            objects.add(object);
        }

        return objects;
    }

    public String getExpectedName() {
        return expectedName;
    }

    public void setExpectedName(String expectedName) {
        this.expectedName = expectedName;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(expectedName)
                .append(specs)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExpectedSpecObject)) {
            return false;
        }
        ExpectedSpecObject rhs = (ExpectedSpecObject)obj;

        return new EqualsBuilder()
                .append(expectedName, rhs.expectedName)
                .append(specs, rhs.specs)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expectedName", expectedName)
                .append("specs", specs)
                .toString();
    }
}

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
import net.mindengine.galen.specs.page.SpecGroup;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ExpectedSpecObject {

    private String expectedName;
    private List<String> specs = new LinkedList<String>();
    private Map<String, List<String>> specGroups = new HashMap<String, List<String>>();

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
            ExpectedSpecObject object = convertExpectedSpecObject(objectSpecs);
            objects.add(object);
        }

        return objects;
    }

    private static ExpectedSpecObject convertExpectedSpecObject(ObjectSpecs objectSpecs) {
        ExpectedSpecObject object = new ExpectedSpecObject(objectSpecs.getObjectName());
        List<String> specs = convertSpecs(objectSpecs.getSpecs());
        object.setSpecs(specs);

        Map<String, List<String>> specGroups = new HashMap<String, List<String>>();

        for (SpecGroup specGroup : objectSpecs.getSpecGroups()) {
            specGroups.put(specGroup.getName(), convertSpecs(specGroup.getSpecs()));
        }

        object.setSpecGroups(specGroups);
        return object;
    }

    private static List<String> convertSpecs(List<Spec> originalSpecs) {
        List<String> specs = new LinkedList<String>();
        for (Spec spec : originalSpecs) {
            specs.add(spec.getOriginalText());
        }
        return specs;
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
        return new HashCodeBuilder() //@formatter:off
                .append(expectedName)
                .append(specs)
                .append(specGroups)
                .toHashCode(); //@formatter:on
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

        return new EqualsBuilder() //@formatter:off
                .append(expectedName, rhs.expectedName)
                .append(specs, rhs.specs)
                .append(specGroups, rhs.specGroups)
                .isEquals(); //@formatter:on
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //@formatter:off
                .append("expectedName", expectedName)
                .append("specs", specs)
                .append("specGroups", specGroups)
                .toString(); //@formatter:on
    }

    public void setSpecGroups(Map<String, List<String>> specGroups) {
        this.specGroups = specGroups;
    }

    public ExpectedSpecObject withSpecGroup(String name, List<String> specs) {
        specGroups.put(name, specs);
        return this;
    }
}

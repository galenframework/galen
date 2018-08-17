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
package com.galenframework.generator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SpecAssertion {
    private AssertionType assertionType = AssertionType.distance;
    private AssertionEdge edge1;
    private AssertionEdge edge2;

    public SpecAssertion() {
    }

    public SpecAssertion(AssertionEdge edge1, AssertionEdge edge2) {
        this.edge1 = edge1;
        this.edge2 = edge2;
    }

    public AssertionType getAssertionType() {
        return assertionType;
    }

    public void setAssertionType(AssertionType assertionType) {
        this.assertionType = assertionType;
    }


    public AssertionEdge getEdge1() {
        return edge1;
    }

    public void setEdge1(AssertionEdge edge1) {
        this.edge1 = edge1;
    }

    public AssertionEdge getEdge2() {
        return edge2;
    }

    public void setEdge2(AssertionEdge edge2) {
        this.edge2 = edge2;
    }

    public void setEdges(AssertionEdge edge1, AssertionEdge edge2) {
        this.edge1 = edge1;
        this.edge2 = edge2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpecAssertion that = (SpecAssertion) o;

        return new EqualsBuilder()
            .append(assertionType, that.assertionType)
            .append(edge1, that.edge1)
            .append(edge2, that.edge2)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(assertionType)
            .append(edge1)
            .append(edge2)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("assertionType", assertionType)
            .append("edge1", edge1)
            .append("edge2", edge2)
            .toString();
    }
}

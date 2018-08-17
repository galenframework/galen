/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports.model;


import com.galenframework.specs.Side;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LayoutMeta {

    private ObjectEdge from;
    private ObjectEdge to;
    private String expectedDistance;
    private String realDistance;

    public ObjectEdge getFrom() {
        return from;
    }

    public void setFrom(ObjectEdge from) {
        this.from = from;
    }

    public ObjectEdge getTo() {
        return to;
    }

    public void setTo(ObjectEdge to) {
        this.to = to;
    }

    public String getExpectedDistance() {
        return expectedDistance;
    }

    public void setExpectedDistance(String expectedDistance) {
        this.expectedDistance = expectedDistance;
    }

    public String getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(String realDistance) {
        this.realDistance = realDistance;
    }


    public LayoutMeta() {
    }

    public LayoutMeta(ObjectEdge from, ObjectEdge to, String expectedDistance, String realDistance) {
        this.from = from;
        this.to = to;
        this.expectedDistance = expectedDistance;
        this.realDistance = realDistance;
    }

    public static class ObjectEdge {
        private String object;
        private Side edge;

        public ObjectEdge() {
        }

        public ObjectEdge(String object, Side edge) {
            this.object = object;
            this.edge = edge;
        }

        public Side getEdge() {
            return edge;
        }

        public void setEdge(Side edge) {
            this.edge = edge;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("object", object)
                    .append("edge", edge)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            ObjectEdge that = (ObjectEdge) o;

            return new EqualsBuilder()
                    .append(object, that.object)
                    .append(edge, that.edge)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(object)
                    .append(edge)
                    .toHashCode();
        }
    }


    public static LayoutMeta distance(String firstObject, Side firstEdge, String secondObject, Side secondEdge, String expectedDistance, String realDistance) {
        return new LayoutMeta(new ObjectEdge(firstObject, firstEdge), new ObjectEdge(secondObject, secondEdge), expectedDistance, realDistance);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .append("expectedDistance", expectedDistance)
                .append("realDistance", realDistance)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LayoutMeta that = (LayoutMeta) o;

        return new EqualsBuilder()
                .append(from, that.from)
                .append(to, that.to)
                .append(expectedDistance, that.expectedDistance)
                .append(realDistance, that.realDistance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(to)
                .append(expectedDistance)
                .append(realDistance)
                .toHashCode();
    }
}

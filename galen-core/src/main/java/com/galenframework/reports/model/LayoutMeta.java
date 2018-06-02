package com.galenframework.reports.model;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LayoutMeta {

    private String type;
    private ObjectEdge from;
    private ObjectEdge to;
    private String expectedDistance;
    private String realDistance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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


    public static class ObjectEdge {
        private String object;
        private String edge;

        public String getEdge() {
            return edge;
        }

        public void setEdge(String edge) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
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
                .append(type, that.type)
                .append(from, that.from)
                .append(to, that.to)
                .append(expectedDistance, that.expectedDistance)
                .append(realDistance, that.realDistance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(from)
                .append(to)
                .append(expectedDistance)
                .append(realDistance)
                .toHashCode();
    }
}

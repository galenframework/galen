package net.mindengine.galen.specs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Range {

    public Range(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }
    private Integer from;
    private Integer to;
    public Integer getFrom() {
        return from;
    }
    public Integer getTo() {
        return to;
    }
    public static Range exact(int number) {
        return new Range(number, null);
    }
    public static Range between(int from, int to) {
        return new Range(Math.min(from, to), Math.max(from, to));
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19).append(from).append(to).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range rhs = (Range)obj;
        return new EqualsBuilder().append(from, rhs.from).append(to, rhs.to).isEquals();
    }
    
    @Override
    public String toString() {
        return "Range(" + from + ", " + to + ")";
    }
}

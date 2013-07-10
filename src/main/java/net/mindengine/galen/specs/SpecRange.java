package net.mindengine.galen.specs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class SpecRange extends Spec {

    private Range range;

    public SpecRange(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(range).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof SpecRange))
            return false;
        
        SpecRange rhs = (SpecRange)obj;
        return new EqualsBuilder().append(range, rhs.range).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("range", range)
            .toString();
    }
}

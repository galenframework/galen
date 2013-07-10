package net.mindengine.galen.specs;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SpecAbsent extends Spec {

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof SpecAbsent))
            return false;
        
        return true;
    }
}

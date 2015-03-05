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
        return new ToStringBuilder(this) //@formatter:off
            .append("range", range)
            .toString(); //@formatter:on
    }
}

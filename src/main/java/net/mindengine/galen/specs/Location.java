/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Location {
    
    private Range range;
    private List<Side> sides;
    
    public Location(Range range, List<Side> sides) {
        setRange(range);
        setSides(sides);
    }
    public Range getRange() {
        return range;
    }
    public void setRange(Range range) {
        this.range = range;
    }
    public List<Side> getSides() {
        return sides;
    }
    public void setSides(List<Side> sides) {
        this.sides = sides;
    }

    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(range).append(sides).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Location))
            return false;
        
        Location rhs = (Location)obj;
        return new EqualsBuilder().append(range, rhs.range).append(sides, rhs.sides).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("range", range)
            .append("sides", sides)
            .toString();
    }
    
    public static List<Location> locations(Location...locations) {
        return Arrays.asList(locations);
    }
    
}

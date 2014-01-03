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

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class SpecComplex extends Spec {

    private String object;
    private List<Location> locations;
    
    public SpecComplex(String objectName, List<Location> locations) {
        setObject(objectName);
        setLocations(locations);
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public List<Location> getLocations() {
        return locations;
    }
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
    
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(object).append(locations).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof SpecComplex))
            return false;
        
        SpecComplex rhs = (SpecComplex)obj;
        return new EqualsBuilder().append(object, rhs.object).append(locations, rhs.locations).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("object", object)
            .append("locations", locations)
            .toString();
    }
}

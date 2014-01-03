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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SpecOn extends SpecComplex {
    
    private Side sideVertical;
    private Side sideHorizontal;

    public SpecOn(String objectName, Side sideHorizontal, Side sideVertical, List<Location> locations) {
        super(objectName, locations);
        
        this.setSideVertical(sideVertical);
        this.setSideHorizontal(sideHorizontal);
    }

    public Side getSideVertical() {
        return sideVertical;
    }

    public void setSideVertical(Side sideVertical) {
        this.sideVertical = sideVertical;
    }

    public Side getSideHorizontal() {
        return sideHorizontal;
    }

    public void setSideHorizontal(Side sideHorizontal) {
        this.sideHorizontal = sideHorizontal;
    }

    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("sideVertical", sideVertical)
            .append("sideHorizontal", sideHorizontal)
            .append("object", getObject())
            .append("locations", getLocations())
            .toString();
    }
}

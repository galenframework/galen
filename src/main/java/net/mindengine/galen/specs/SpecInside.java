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

public class SpecInside extends SpecComplex {

    private boolean partly = false;
    public SpecInside(String objectName, List<Location> locations) {
        super(objectName, locations);
    }
    
    public boolean getPartly() {
        return partly;
    }
    public void setPartly(boolean partly) {
        this.partly = partly;
    }
    public SpecInside withPartlyCheck() {
        setPartly(true);
        return this;
    }
    

}

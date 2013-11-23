/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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

public class SpecObjectWithErrorRate extends Spec {

    private String object;
    private int errorRate = 0;
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public int getErrorRate() {
        return errorRate;
    }
    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }
    
}

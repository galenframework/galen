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
package net.mindengine.galen.specs.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Spec;

public class SpecListProccessor implements SpecProcessor {

    private SpecListInit specInit;

    public SpecListProccessor(SpecListInit specListInit) {
        this.specInit = specListInit;
    }

    @Override
    public Spec processSpec(String specName, String paramsText) {
        if (paramsText == null || paramsText.isEmpty()) {
            throw new IncorrectSpecException("Missing parameters for spec");
        }
        else {
            
            String []arr = paramsText.split(",");
            List<String> childObjectList = new LinkedList<String>();
            for (String item : arr) {
                item = item.trim();
                if (!item.isEmpty()) {
                    childObjectList.add(item);
                }
            }
            
            return specInit.init(specName, childObjectList);
        }
    }

}

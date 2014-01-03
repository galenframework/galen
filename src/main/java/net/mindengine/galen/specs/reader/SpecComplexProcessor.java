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
package net.mindengine.galen.specs.reader;

import java.util.List;

import net.mindengine.galen.parser.Expectation;
import net.mindengine.galen.specs.Spec;

public class SpecComplexProcessor implements SpecProcessor {

    private List<Expectation<?>> toExpect;
    private SpecComplexInit specInit;

    public SpecComplexProcessor(List<Expectation<?>> toExpect, SpecComplexInit specInit) {
        this.toExpect = toExpect;
        this.specInit = specInit;
    }

    @Override
    public Spec processSpec(String specName, String paramsText, String contextPath) {
        StringCharReader reader = new StringCharReader(paramsText);
        
        Object[]args = new Object[toExpect.size()];
        int i=0;
        for(Expectation<?> expectation : toExpect) {
            args[i] = expectation.read(reader);
            i++;
        }
        return specInit.init(specName, args);
    }

}

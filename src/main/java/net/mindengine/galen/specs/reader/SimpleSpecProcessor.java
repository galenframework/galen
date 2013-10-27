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

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Spec;

public class SimpleSpecProcessor implements SpecProcessor {

    private SpecInit specInit;

    public SimpleSpecProcessor(SpecInit specInit) {
        this.specInit = specInit;
    }

    @Override
    public Spec processSpec(String specName, String paramsText, String contextPath) {
        if (paramsText != null && !paramsText.isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, "This spec doesn't take any parameters");
        }
        return specInit.init();
    }

}

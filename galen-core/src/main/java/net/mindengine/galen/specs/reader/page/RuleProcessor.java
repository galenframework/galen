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
package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;

import java.io.IOException;
import java.util.Properties;

public interface RuleProcessor {

    void processRule(ObjectSpecs object, String ruleText, VarsContext varsContext, PageSection section,
                     Properties properties, String contextPath, PageSpecReader pageSpecReader) throws IOException;
}

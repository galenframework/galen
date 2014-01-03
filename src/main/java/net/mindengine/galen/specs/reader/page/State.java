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
package net.mindengine.galen.specs.reader.page;

import java.io.IOException;

import net.mindengine.galen.specs.page.PageSection;

public abstract class State {

    public abstract void process(String line) throws IOException;

    public boolean isObjectDefinition() {
        return this instanceof StateObjectDefinition;
    }

    public static State objectDefinition(PageSpec pageSpec) {
        return new StateObjectDefinition(pageSpec);
    }

    public static State startedSection(PageSection section, String contextPath) {
        return new StateDoingSection(section, contextPath);
    }

}

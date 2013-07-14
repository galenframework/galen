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
package net.mindengine.galen.specs.reader.page;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.page.PageSection;


public class PageSpec {

    private Map<String, Locator> objects = new HashMap<String, Locator>();
    private List<PageSection> sections = new LinkedList<PageSection>();

    public Map<String, Locator> getObjects() {
        return this.objects;
    }

    public void setObjects(Map<String, Locator> objects) {
        this.objects = objects;
    }

    public List<PageSection> getSections() {
        return this.sections;
    }

    public void setSections(List<PageSection> sections) {
        this.sections = sections;
    }

    public void addSection(PageSection section) {
        sections.add(section);
    }

    public void addObject(String objectName, Locator locator) {
        objects.put(objectName, locator);
    }

}

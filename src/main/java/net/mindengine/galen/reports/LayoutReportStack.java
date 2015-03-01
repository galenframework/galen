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
package net.mindengine.galen.reports;

import net.mindengine.galen.reports.model.*;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.validation.ValidationObject;

import java.util.List;
import java.util.Stack;

/**
 * Created by ishubin on 2015/02/15
 */
public class LayoutReportStack {

    private LayoutObject currentObject = null;
    private final LayoutReport layoutReport;
    private final Stack<LayoutSection> sectionStack = new Stack<LayoutSection>();
    private LayoutSpec currentSpec;
    private List<LayoutSpec> currentSpecCollector;


    public LayoutReportStack(LayoutReport layoutReport) {
        this.layoutReport = layoutReport;
    }

    public void pushSection(PageSection pageSection) {
        LayoutSection section = new LayoutSection(pageSection.getName());

        if (!sectionStack.isEmpty()) {
            sectionStack.peek().addSection(section);
        }
        else {
            layoutReport.getSections().add(section);
        }
        sectionStack.push(section);
    }

    public void popSection() {
        sectionStack.pop();
    }

    public LayoutSection peekSection() {
        return sectionStack.peek();
    }

    public LayoutObject getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(LayoutObject currentObject) {
        this.currentObject = currentObject;
        this.currentSpecCollector = currentObject.getSpecs();
    }

    public void setCurrentSpec(LayoutSpec currentSpec) {
        this.currentSpec = currentSpec;
    }

    public LayoutSpec getCurrentSpec() {
        return currentSpec;
    }

    public void putObjects(List<ValidationObject> validationObjects) {
        for (ValidationObject validationObject : validationObjects) {
            layoutReport.getObjects().put(validationObject.getName(), new LayoutObjectDetails(validationObject.getArea().toIntArray()));
        }
    }

    public List<LayoutSpec> getCurrentSpecCollector() {
        return currentSpecCollector;
    }

    public void setCurrentSpecCollector(List<LayoutSpec> currentSpecCollector) {
        this.currentSpecCollector = currentSpecCollector;
    }
}

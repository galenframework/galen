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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.mindengine.galen.reports.model.*;
import net.mindengine.galen.reports.nodes.TestReportNode;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.validation.*;

public class LayoutReportListener implements ValidationListener {

    private Stack<LayoutReportStack> reportStack = new Stack<LayoutReportStack>();
    private LayoutReport rootLayoutReport;

    public LayoutReportListener(LayoutReport layoutReport) {
        this.rootLayoutReport = layoutReport;
        reportStack.push(new LayoutReportStack(layoutReport));
    }


    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        currentReport().pushSection(pageSection);
    }

    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        currentReport().popSection();
    }

    @Override
    public void onSubLayout(PageValidation pageValidation, String objectName) {
        LayoutReport subLayout = new LayoutReport();
        currentReport().getCurrentSpec().setSubLayout(subLayout);
        reportStack.push(new LayoutReportStack(subLayout));
    }

    @Override
    public void onAfterSubLayout(PageValidation pageValidation, String objectName) {
        reportStack.pop();
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        // Searching for the same object if it was already reported
        LayoutObject object = currentSection().findObject(objectName);
        if (object == null) {
            object = new LayoutObject();
            object.setName(objectName);
            currentSection().getObjects().add(object);
        }
        currentReport().setCurrentObject(object);
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
    }


    public LayoutSpec addSpecToReport(Spec originalSpec, ValidationResult result) {
        LayoutSpec spec = new LayoutSpec();
        spec.setPlace(originalSpec.getPlace());
        spec.setName(originalSpec.getOriginalText());

        if (originalSpec.getAlias() != null) {
            LayoutSpecGroup group = new LayoutSpecGroup();
            group.setName(originalSpec.getAlias());
            group.addSpec(spec);
            currentReport().getCurrentObject().addSpecGroup(group);
        } else {
            currentReport().getCurrentSpecCollector().add(spec);
        }

        currentReport().putObjects(result.getValidationObjects());
        spec.setHighlight(convertToObjectNames(result.getValidationObjects()));

        if (result.getError() != null) {
            spec.setErrors(result.getError().getMessages());
        }

        currentReport().setCurrentSpec(spec);

        return spec;
    }
    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec, ValidationResult result) {
        LayoutSpec spec = addSpecToReport(originalSpec, result);

        if (originalSpec.isOnlyWarn()) {
            spec.setStatus(TestReportNode.Status.WARN);
        } else {
            spec.setStatus(TestReportNode.Status.ERROR);
        }
        try {
            if (result.getError().getImageComparison() != null) {
                spec.setImageComparison(convertImageComparison(objectName, result.getError().getImageComparison()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSpecGroup(PageValidation pageValidation, String specGroupName) {
        LayoutSpecGroup specGroup = new LayoutSpecGroup();
        specGroup.setName(specGroupName);
        specGroup.setSpecs(new LinkedList<LayoutSpec>());

        currentReport().getCurrentObject().addSpecGroup(specGroup);
        currentReport().setCurrentSpecCollector(specGroup.getSpecs());
    }

    @Override
    public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {
        currentReport().setCurrentSpecCollector(currentReport().getCurrentObject().getSpecs());
    }


    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec, ValidationResult result) {
        addSpecToReport(originalSpec, result);
    }

    private LayoutImageComparison convertImageComparison(String objectName, ImageComparison imageComparison) throws IOException {
        LayoutImageComparison layoutImageComparison = new LayoutImageComparison();

        layoutImageComparison.setActualImage(rootLayoutReport.registerImageFile(objectName + "-actual", imageComparison.getOriginalFilteredImage()));
        layoutImageComparison.setExpectedImage(rootLayoutReport.registerImageFile(objectName + "-expected", imageComparison.getSampleFilteredImage()));
        layoutImageComparison.setComparisonMapImage(rootLayoutReport.registerImageFile(objectName + "-map", imageComparison.getComparisonMap()));

        return layoutImageComparison;
    }

    private List<String> convertToObjectNames(List<ValidationObject> validationObjects) {
        List<String> names = new LinkedList<String>();
        if (validationObjects != null) {
            for (ValidationObject validationObject : validationObjects) {
                names.add(validationObject.getName());
            }
        }
        return names;
    }


    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        // not needed here
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        // not needed here
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        // not needed here
    }


    private LayoutReportStack currentReport() {
        return reportStack.peek();
    }

    private LayoutSection currentSection() {
        return reportStack.peek().peekSection();
    }


    private LayoutObject getCurrentObject() {
        return reportStack.peek().getCurrentObject();
    }

}

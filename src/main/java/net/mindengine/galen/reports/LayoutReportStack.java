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


    public LayoutReportStack(LayoutReport layoutReport) {
        this.layoutReport = layoutReport;
    }

    public void pushSection(PageSection pageSection) {
        LayoutSection section = new LayoutSection(pageSection.getName());
        layoutReport.getSections().add(section);
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

}

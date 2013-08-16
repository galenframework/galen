package net.mindengine.galen.validation;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;

public class SectionValidation {

    private List<PageSection> pageSections;
    private PageValidation pageValidation;
    private ValidationListener validationListener;

    public SectionValidation(List<PageSection> pageSections, PageValidation pageValidation, ValidationListener validationListener) {
        this.pageSections = pageSections;
        this.pageValidation = pageValidation;
        this.validationListener = validationListener;
    }

    public List<ValidationError> check() {
        List<ValidationError> errors = new LinkedList<ValidationError>();
        
        for (PageSection section : pageSections) {
            errors.addAll(checkSection(section));
        }
        return errors;
    }

    private List<ValidationError> checkSection(PageSection section) {
        List<ValidationError> errors = new LinkedList<ValidationError>();
        for (ObjectSpecs object : section.getObjects()) {
            tellOnObject(object.getObjectName());
            errors.addAll(checkObject(object));
        }
        return errors;
    }

    private void tellOnObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onObject(pageValidation, objectName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<ValidationError> checkObject(ObjectSpecs object) {
        List<ValidationError> errors = new LinkedList<ValidationError>();
        for (Spec spec : object.getSpecs()) {
            
            
            ValidationError error = pageValidation.check(object.getObjectName(), spec);
            if (error != null) {
                errors.add(error);
                tellOnSpecError(pageValidation, object.getObjectName(), spec, error);
            }
            else tellOnSpecSuccess(pageValidation, object.getObjectName(), spec);
        }
        return errors;
    }

    private void tellOnSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        try {
            if (validationListener != null) {
                validationListener.onSpecError(pageValidation, objectName, spec, error);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tellOnSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        try {
            if (validationListener != null) {
                validationListener.onSpecSuccess(pageValidation, objectName, spec);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

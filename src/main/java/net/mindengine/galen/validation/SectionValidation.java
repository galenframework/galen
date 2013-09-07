package net.mindengine.galen.validation;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
            
            List<String> allObjectNames = findAllObjectNames(object.getObjectName());
            
            for (String objectName : allObjectNames) {
                tellOnObject(objectName);
                errors.addAll(checkObject(objectName, object.getSpecs()));
                tellOnAfterObject(objectName);
            }
        }
        return errors;
    }

    private List<String> findAllObjectNames(String objectsDefinition) {
        List<String> objectNames = new LinkedList<String>();
        
        String names[] = objectsDefinition.split(",");
        
        for (String name : names) {
            name = name.trim();
            if (!name.isEmpty()) {
                if (isRegularExpression(name)) {
                    objectNames.addAll(fetchUsingRegex(name));
                }
                else {
                    objectNames.add(name);
                }
            }
        }
        return objectNames;
    }

    private List<String> fetchUsingRegex(String simpleRegex) {
        String regex = simpleRegex.replace("*", ".*");
        Pattern pattern = Pattern.compile(regex);
        
        List<String> objectNames = new LinkedList<String>();
        for (String objectName : pageValidation.getPageSpec().getObjects().keySet()) {
            if (pattern.matcher(objectName).matches()) {
                objectNames.add(objectName);
            }
        }
        
        return objectNames;
    }

    private boolean isRegularExpression(String name) {
        if (name.contains("*")) {
            return true;
        }
        else return false;
    }

    private void tellOnAfterObject(String objectName) {
        if (validationListener != null) {
            try {
                validationListener.onAfterObject(pageValidation, objectName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } 
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

    private List<ValidationError> checkObject(String objectName, List<Spec> specs) {
        List<ValidationError> errors = new LinkedList<ValidationError>();
        for (Spec spec : specs) {
            
            
            ValidationError error = pageValidation.check(objectName, spec);
            if (error != null) {
                errors.add(error);
                tellOnSpecError(pageValidation, objectName, spec, error);
            }
            else tellOnSpecSuccess(pageValidation, objectName, spec);
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

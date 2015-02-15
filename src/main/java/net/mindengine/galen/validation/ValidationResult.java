package net.mindengine.galen.validation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class ValidationResult {

    private List<ValidationObject> objects = new LinkedList<ValidationObject>();

    public List<ValidationObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ValidationObject> objects) {
        this.objects = objects;
    }

}

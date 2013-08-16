package net.mindengine.galen.components.validation;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.ValidationError;
import net.mindengine.galen.validation.ValidationListener;

public class TestValidationListener implements ValidationListener {

    private StringBuffer invokations = new StringBuffer();

    @Override
    public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationError error) {
        StringBuffer buffer = new StringBuffer();
        for (String message : error.getMessages()) {
            buffer.append("<msg>");
            buffer.append(message);
            buffer.append("</msg>");
        }
        append("<e>" + buffer.toString() + "</e>");
    }

    @Override
    public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec) {
        append("<" + spec.getClass().getSimpleName() + " " + objectName + ">");
    }

    @Override
    public void onObject(PageValidation pageValidation, String objectName) {
        append("<o " + objectName + ">");
    }

    private void append(String text) {
        invokations.append(text);
        invokations.append('\n');
    }
    
    public String getInvokations() {
        return invokations.toString();
    }

}

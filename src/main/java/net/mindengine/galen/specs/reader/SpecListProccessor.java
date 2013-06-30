package net.mindengine.galen.specs.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.specs.Spec;

public class SpecListProccessor implements SpecProcessor {

    private SpecListInit specInit;

    public SpecListProccessor(SpecListInit specListInit) {
        this.specInit = specListInit;
    }

    @Override
    public Spec processSpec(String paramsText) {
        if (paramsText == null || paramsText.isEmpty()) {
            throw new IncorrectSpecException("Missing parameters for spec");
        }
        else {
            
            String []arr = paramsText.split(",");
            List<String> childObjectList = new LinkedList<String>();
            for (String item : arr) {
                item = item.trim();
                if (!item.isEmpty()) {
                    childObjectList.add(item);
                }
            }
            
            return specInit.init(childObjectList);
        }
    }

}

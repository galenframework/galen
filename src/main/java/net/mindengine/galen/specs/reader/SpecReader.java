package net.mindengine.galen.specs.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;

public class SpecReader {
    
    @SuppressWarnings("serial")
    private static Map<String, SpecProcessor> _specsMap = new HashMap<String, SpecProcessor>() {{
        put("absent", new SimpleSpecProcessor(new SpecInit() {
            public Spec init() {
                return new SpecAbsent();
            }
        }));
        
        put("contains", new SpecListProccessor(new SpecListInit() {
            public Spec init(List<String> list) {
                return new SpecContains(list);
            }
        }));
    }};


    public Spec read(String specText) {
        if (specText == null) {
            throw new NullPointerException("Spec text should not be null");
        }
        else if(specText.trim().isEmpty()) {
            throw new IncorrectSpecException("Spec text should not be empty");
        }
        
        specText = specText.trim();
        
        String args[] = specText.split(":");
        
        String paramsText = null;
        
        if (args.length > 2) {
            throw new IncorrectSpecException("Incorrect format");
        }
        else if (args.length == 2) {
            paramsText = args[1].trim();
        }
        
        return readSpecWithParams(args[0].trim(), paramsText);
    }

    private Spec readSpecWithParams(String specName, String paramsText) {
        if (_specsMap.containsKey(specName)) {
            return _specsMap.get(specName).processSpec(paramsText);
        }
        else throw new IncorrectSpecException("Such constraint does not exist: " + specName); 
    }


}

package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.specs.Alignment.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mindengine.galen.specs.Alignment;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecWidth;

public class SpecReader {
    private Map<Pattern, SpecProcessor> specsMap = new HashMap<Pattern, SpecProcessor>();
    
    public SpecReader() {
        initSpecs();
    }
    
    private void initSpecs() {
        
        putSpec("absent", new SimpleSpecProcessor(new SpecInit() {
            public Spec init() {
                return new SpecAbsent();
            }
        }));
        
        putSpec("contains", new SpecListProccessor(new SpecListInit() {
            public Spec init(String specName, List<String> list) {
                return new SpecContains(list);
            }
        }));
        
        putSpec("width", new SpecRangeProcessor(new SpecRangeInit() {
            public Spec init(Range range) {
                return new SpecWidth(range);
            }
        }));
        
        putSpec("height", new SpecRangeProcessor(new SpecRangeInit() {
            public Spec init(Range range) {
                return new SpecHeight(range);
            }
        }));
        
        putSpec("horizontally.*", new SpecListProccessor(new SpecListInit(){
            @Override
            public Spec init(String specName, List<String> list) {
                String arguments = specName.substring("horizontally".length()).trim();
                Alignment alignment = Alignment.parse(arguments);
                if (alignment.isOneOf(CENTERED, TOP, BOTTOM)) {
                    return new SpecHorizontally(alignment, list);
                }
                else {
                    throw new IncorrectSpecException("Horizontal spec doesn't allow this alignment: " + alignment.toString());
                }
            }
        }));
        
        putSpec("vertically.*", new SpecListProccessor(new SpecListInit(){
            @Override
            public Spec init(String specName, List<String> list) {
                String arguments = specName.substring("Vertically".length()).trim();
                Alignment alignment = Alignment.parse(arguments);
                if (alignment.isOneOf(CENTERED, LEFT, RIGHT)) {
                    return new SpecVertically(alignment, list);
                }
                else {
                    throw new IncorrectSpecException("Vertical spec doesn't allow this alignment: " + alignment.toString());
                }
            }
        }));
        
    }


    
    
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
        return findMatchingSpec(specName).processSpec(specName, paramsText); 
    }

    private SpecProcessor findMatchingSpec(String specName) {
        
        for (Map.Entry<Pattern, SpecProcessor> entry : specsMap.entrySet()) {
            Matcher matcher = entry.getKey().matcher(specName);
            if (matcher.matches()) {
                return entry.getValue();
            }
        }
        throw new IncorrectSpecException("Such constraint does not exist: " + specName);
    }

    private void putSpec(String patternText, SpecProcessor specProcessor) {
        specsMap.put(Pattern.compile(patternText), specProcessor);
    }
}

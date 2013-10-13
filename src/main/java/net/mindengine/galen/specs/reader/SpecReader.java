/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.specs.reader;

import static net.mindengine.galen.parser.Expectations.expectThese;
import static net.mindengine.galen.parser.Expectations.locations;
import static net.mindengine.galen.parser.Expectations.objectName;
import static net.mindengine.galen.parser.Expectations.range;
import static net.mindengine.galen.specs.Alignment.ALL;
import static net.mindengine.galen.specs.Alignment.BOTTOM;
import static net.mindengine.galen.specs.Alignment.CENTERED;
import static net.mindengine.galen.specs.Alignment.LEFT;
import static net.mindengine.galen.specs.Alignment.RIGHT;
import static net.mindengine.galen.specs.Alignment.TOP;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Alignment;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbove;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecBelow;
import net.mindengine.galen.specs.SpecCentered;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecOn;
import net.mindengine.galen.specs.SpecText;
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
        
        putSpec("contains(\\s+partly)?", new SpecListProccessor(new SpecListInit() {
            public Spec init(String specName, List<String> list) {
                String arguments = specName.substring("contains".length()).trim();
                
                boolean isPartly = (!arguments.isEmpty() && arguments.equals("partly"));
                return new SpecContains(list, isPartly);
            }
        }));
        
        putSpec("width", new SpecComplexProcessor(expectThese(range()), new SpecComplexInit() {
            public Spec init(String specName, Object[] args) {
                return new SpecWidth((Range) args[0]);
            }
        }));
        
        putSpec("height", new SpecComplexProcessor(expectThese(range()), new SpecComplexInit() {
            public Spec init(String specName, Object[] args) {
                return new SpecHeight((Range) args[0]);
            }
        }));
        
        putSpec("horizontally.*", new SpecListProccessor(new SpecListInit(){
            @Override
            public Spec init(String specName, List<String> list) {
                String arguments = specName.substring("horizontally".length()).trim();
                
                Alignment alignment = null;
                if (arguments.isEmpty()){
                    alignment = Alignment.ALL;
                }
                else {
                    alignment = Alignment.parse(arguments);
                }
                
                if (alignment.isOneOf(CENTERED, TOP, BOTTOM, ALL)) {
                    return new SpecHorizontally(alignment, list);
                }
                else {
                    throw new SyntaxException(UNKNOWN_LINE, "Horizontal spec doesn't allow this alignment: " + alignment.toString());
                }
            }
        }));
        
        putSpec("vertically.*", new SpecListProccessor(new SpecListInit(){
            @Override
            public Spec init(String specName, List<String> list) {
                String arguments = specName.substring("Vertically".length()).trim();
                
                Alignment alignment = null;
                if (arguments.isEmpty()){
                    alignment = Alignment.ALL;
                }
                else {
                    alignment = Alignment.parse(arguments);
                }
                
                if (alignment.isOneOf(CENTERED, LEFT, RIGHT, ALL)) {
                    return new SpecVertically(alignment, list);
                }
                else {
                    throw new SyntaxException(UNKNOWN_LINE, "Vertical spec doesn't allow this alignment: " + alignment.toString());
                }
            }
        }));
        
        putSpec("text\\s+.*", new SpecProcessor() {
            @Override
            public Spec processSpec(String specName, String paramsText) {
                String arguments = specName.substring("text".length()).trim();
                
                if (arguments.isEmpty()) {
                    throw new SyntaxException(UNKNOWN_LINE, "Text validation is not fully specified");
                }
                
                SpecText.Type type = null;
                if (arguments.equals("is")) {
                    type = SpecText.Type.IS;
                }
                else if (arguments.equals("contains")) {
                    type = SpecText.Type.CONTAINS;
                }
                else if (arguments.equals("starts")) {
                    type = SpecText.Type.STARTS;
                }
                else if (arguments.equals("ends")) {
                    type = SpecText.Type.ENDS;
                }
                else if (arguments.equals("matches")) {
                    type = SpecText.Type.MATCHES;
                }
                else throw new SyntaxException(UNKNOWN_LINE, "Unknown text validation: " + arguments); 
                
                return new SpecText(type, paramsText.trim());
            }
        });
        
        putSpec("inside.*", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(String specName, Object[] args) {
                String leftoverName = specName.substring(6).trim();
                
                String objectName = (String) args[0];
                List<Location> locations = (List<Location>) args[1];
                
                SpecInside spec =  new SpecInside(objectName, locations);
                
                if (leftoverName.equals("partly")) {
                    spec.setPartly(true);
                }
                return spec;
            }
        }));
        
        putSpec("near", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(String specName, Object[] args) {
                String objectName = (String) args[0];
                List<Location> locations = (List<Location>) args[1];
                
                return new SpecNear(objectName, locations);
            }
        }));
        
        putSpec("(above|below)", new SpecComplexProcessor(expectThese(objectName(), range()), new SpecComplexInit() {
			@Override
			public Spec init(String specName, Object[] args) {
				String objectName = (String) args[0];
				Range range = (Range)args[1];
				
				if (specName.equals("above")) {
					return new SpecAbove(objectName, range);
				}
				else return new SpecBelow(objectName, range);
			}
        }));
        
        putSpec("centered(\\s+(horizontally|vertically))?\\s+(on|inside)", new SpecProcessor() {
			@Override
			public Spec processSpec(String specName, String paramsText) {
				specName = specName.replace("centered", "").trim();
				String args[] = specName.split(" ");
				
				SpecCentered.Alignment alignment = SpecCentered.Alignment.ALL;
				SpecCentered.Location location = null;
				if (args.length == 1) {
					location = SpecCentered.Location.fromString(args[0]);
				}
				else {
					alignment = SpecCentered.Alignment.fromString(args[0]);
					location = SpecCentered.Location.fromString(args[1]);
				}
				
				if (paramsText.trim().isEmpty()) {
					throw new SyntaxException("There is no object defined in spec");
				}
				
				return new SpecCentered(paramsText.trim(), alignment, location);
			}
		});
        
        putSpec("on", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(String specName, Object[] args) {
                String objectName = (String) args[0];
                List<Location> locations = (List<Location>) args[1];
                
                return new SpecOn(objectName, locations);
            }
        }));
    }

    public Spec read(String specText) {
        if (specText == null) {
            throw new NullPointerException("Spec text should not be null");
        }
        else if(specText.trim().isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, "Spec text should not be empty");
        }
        
        specText = specText.trim();
        
        int splitterIndex = specText.indexOf(":");
        
        String statement = specText;
        String paramsText = "";
        if (splitterIndex > 0) {
            statement = specText.substring(0, splitterIndex);
            if (splitterIndex < specText.length()) {
                paramsText = specText.substring(splitterIndex + 1);
            }
        }
        
        Spec spec = readSpecWithParams(statement, paramsText);
        if (spec != null) {
            spec.setOriginalText(specText);
        }
        return spec;
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
        throw new SyntaxException(UNKNOWN_LINE, "Such constraint does not exist: " + specName);
    }

    private void putSpec(String patternText, SpecProcessor specProcessor) {
        specsMap.put(Pattern.compile(patternText), specProcessor);
    }
}

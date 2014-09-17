/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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

import static net.mindengine.galen.parser.Expectations.*;
import static net.mindengine.galen.specs.Alignment.ALL;
import static net.mindengine.galen.specs.Alignment.BOTTOM;
import static net.mindengine.galen.specs.Alignment.CENTERED;
import static net.mindengine.galen.specs.Alignment.LEFT;
import static net.mindengine.galen.specs.Alignment.RIGHT;
import static net.mindengine.galen.specs.Alignment.TOP;
import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.parser.*;
import net.mindengine.galen.specs.*;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.rainbow4j.filters.BlurFilter;
import net.mindengine.rainbow4j.filters.ContrastFilter;
import net.mindengine.rainbow4j.filters.DenoiseFilter;
import net.mindengine.rainbow4j.filters.ImageFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class SpecReader {
    
    private static final Place NULL_PLACE = null;
    private Properties properties;
    private Map<Pattern, SpecProcessor> specsMap = new HashMap<Pattern, SpecProcessor>();
    private Browser browser;

    public SpecReader(Properties properties, Browser browser) {
        initSpecs();
        this.properties = properties;
        this.setBrowser(browser);
    }
    
    private void initSpecs() {
        
        putSpec("absent", new SimpleSpecProcessor(new SpecInit() {
            public Spec init() {
                return new SpecAbsent();
            }
        }));
        
        putSpec("visible", new SimpleSpecProcessor(new SpecInit() {
            public Spec init() {
                return new SpecVisible();
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
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                return new SpecWidth((Range) args[0]);
            }
        }));
        
        putSpec("height", new SpecComplexProcessor(expectThese(range()), new SpecComplexInit() {
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                return new SpecHeight((Range) args[0]);
            }
        }));
        
        putSpec("text\\s+.*", new SpecProcessor() {
            @Override
            public Spec processSpec(String specName, String paramsText, String contextPath) {
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
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
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
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                String objectName = (String) args[0];
                List<Location> locations = (List<Location>) args[1];
                
                return new SpecNear(objectName, locations);
            }
        }));
        
        putSpec("(above|below)", new SpecProcessor() {
            @Override
            public Spec processSpec(String specName, String paramsText, String contextPath) throws IOException {
				
                StringCharReader reader = new StringCharReader(paramsText.trim());
                String objectName = new ExpectWord().read(reader);
                
                Range range;
                if (reader.hasMore()) {
                    range = Expectations.range().read(reader);
                }
                else {
                    range = Range.greaterThan(-1.0);
                }
                
				if (specName.equals("above")) {
					return new SpecAbove(objectName, range);
				}
				else return new SpecBelow(objectName, range);
			}
        });
        
        putSpec("aligned\\s+.*", new SpecObjectAndErrorRateProcessor(new SpecObjectAndErrorRateInit() {
            
            @Override
            public Spec init(String specName, String objectName, Integer errorRate) {
                String arguments = specName.substring("aligned".length()).trim();
                
                StringCharReader reader = new StringCharReader(arguments);
                
                String[] words = ExpectWord.readAllWords(reader);
                
                if (words.length == 0) {
                    throw new SyntaxException("Alignment is not defined. Should be either 'vertically' either 'horizonally'");
                }
                String type = words[0];
                
                Alignment alignment = Alignment.ALL;
                if (words.length > 1) {
                    alignment = Alignment.parse(words[1]);
                }
                
                
                if (errorRate == null) {
                    errorRate = 0;
                }
                
                if (type.equals("horizontally")) {
                    if (alignment.isOneOf(CENTERED, TOP, BOTTOM, ALL)) {
                        return new SpecHorizontally(alignment, objectName).withErrorRate(errorRate);
                    }
                    else {
                        throw new SyntaxException(UNKNOWN_LINE, "Horizontal alignment doesn't allow this side: " + alignment.toString());
                    }
                }
                else if (type.equals("vertically")) {
                    if (alignment.isOneOf(CENTERED, LEFT, RIGHT, ALL)) {
                        return new SpecVertically(alignment, objectName).withErrorRate(errorRate);
                    }
                    else {
                        throw new SyntaxException(UNKNOWN_LINE, "Verticall alignment doesn't allow this side: " + alignment.toString());
                    }
                }
                else {
                    throw new SyntaxException("Unknown alignment: " + type);
                }
            }
        }));
        
        putSpec("centered\\s.*", new SpecObjectAndErrorRateProcessor(new SpecObjectAndErrorRateInit() {
            
            @Override
            public Spec init(String specName, String objectName, Integer errorRate) {
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
                
                // Setting default 2 px error rate in case it was not provided in page spec
                if (errorRate == null) {
                    errorRate = 2;
                }
                
                return new SpecCentered(objectName, alignment, location).withErrorRate(errorRate);
            }
		}));
        
        putSpec("(on\\s.*|on)", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                String objectName = (String) args[0];
                
                String[] words = ExpectWord.readAllWords(new StringCharReader(specName));
                
                if (words.length > 3) {
                    throw new SyntaxException("Too many sides. Should use only 2");
                }
                
                
                Side sideHorizontal = Side.TOP;
                Side sideVertical = Side.LEFT;
                
                boolean isFirstHorizontal = false;
                if (words.length > 1) {
                    Side side = Side.fromString(words[1]);
                    if (side == Side.TOP || side == Side.BOTTOM) {
                        isFirstHorizontal = true;
                        sideHorizontal = side;
                    }
                    else sideVertical = side;
                }
                
                if (words.length > 2) {
                    Side side = Side.fromString(words[2]);
                    if (side == Side.TOP || side == Side.BOTTOM) {
                        if (isFirstHorizontal) {
                            throw new SyntaxException("Cannot use theses sides: " + words[1] + " " + words[2]);
                        }
                        sideHorizontal = side;
                    }
                    else {
                        if (!isFirstHorizontal) {
                            throw new SyntaxException("Cannot use theses sides: " + words[1] + " " + words[2]);
                        }
                        sideVertical = side;
                    }
                }
                
                
                List<Location> locations = (List<Location>) args[1];
                
                return new SpecOn(objectName, sideHorizontal, sideVertical, locations);
            }
        }));
        
        putSpec("component", new SpecProcessor() {
            
            @Override
            public Spec processSpec(String specName, String paramsText, String contextPath) throws IOException {
                String childFilePath = paramsText.trim();
                if (childFilePath.isEmpty()) {
                    throw new SyntaxException("File path to component spec is not specified");
                }
                
                String fullFilePath = childFilePath;
                if (contextPath != null) {
                    fullFilePath = contextPath + File.separator + childFilePath;
                }
                
                SpecComponent spec = new SpecComponent();
                spec.setSpecPath(fullFilePath);
                return spec;
            }
        });
        
        putSpec("color\\s+scheme", new SpecComplexProcessor(expectThese(colorRanges()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                
                List<ColorRange> colorRanges = (List<ColorRange>)args[0];
                if (colorRanges == null || colorRanges.size() == 0) {
                    throw new SyntaxException("There are no colors defined");
                }
                
                SpecColorScheme spec = new SpecColorScheme();
                spec.setColorRanges(colorRanges);
                return spec;
            }
        }));

        putSpec("image", new SpecComplexProcessor(expectThese(commaSeparatedRepeatedKeyValues()), new SpecComplexInit() {
            @Override
            public Spec init(String specName, String paramsText, String contextPath, Object[] args) {
                Map<String, List<String>> parameters = (Map<String, List<String>>)args[0];

                SpecImage spec = new SpecImage();

                if (parameters.containsKey("file")) {

                    List<String> filePaths = parameters.get("file");

                    if (filePaths == null || filePaths.size() == 0) {
                        throw new SyntaxException("You should provide 'file' parameter");
                    }

                    if (contextPath != null) {

                        List<String> fullFilePaths = new LinkedList<String>();
                        for (String path : filePaths) {
                            fullFilePaths.add(contextPath + File.separator + path);
                        }
                        spec.setImagePaths(fullFilePaths);
                    }
                    else {
                        spec.setImagePaths(filePaths);
                    }
                }
                else throw new SyntaxException("You should specify a file");


                if (parameters.containsKey("error")) {
                    Pair<Double, String> error = parseError(parameters.get("error").get(0));
                    if (error.getRight().equals("%")) {
                        spec.setMaxPercentage(error.getLeft());
                    }
                    else if (error.getRight().equals("px")) {
                        spec.setMaxPixels(error.getLeft().intValue());
                    }
                    else throw new SyntaxException("Unknown error unit: " + error.getRight());
                }

                if (parameters.containsKey("tolerance")) {
                    spec.setTolerance(parseIntegerParameter("tolerance", parameters.get("tolerance").get(0)));
                }

                if (parameters.containsKey("stretch")) {
                    spec.setStretch(true);
                }
                else {
                    spec.setStretch(false);
                }

                if (parameters.containsKey("area")) {
                    spec.setSelectedArea(parseRect(parameters.get("area").get(0)));
                }

                if (parameters.containsKey("filter")) {
                    List<ImageFilter> filters = new LinkedList<ImageFilter>();

                    for (String filterText : parameters.get("filter")) {
                        filters.add(parseImageFilter(filterText));
                    }
                    spec.setFilters(filters);
                }

                if (parameters.containsKey("map-filter")) {
                    List<ImageFilter> filters = new LinkedList<ImageFilter>();

                    for (String filterText : parameters.get("map-filter")) {
                        filters.add(parseImageFilter(filterText));
                    }
                    spec.setMapFilters(filters);
                }

                return spec;
            }
        }));
        

    }

    private ImageFilter parseImageFilter(String filterText) {
        StringCharReader reader = new StringCharReader(filterText);

        String filterName = new ExpectWord().read(reader);
        Double value = new ExpectNumber().read(reader);

        if ("contrast".equals(filterName)) {
            return new ContrastFilter(value.intValue());
        }
        else if ("blur".equals(filterName)) {
            return new BlurFilter(value.intValue());
        }
        else if ("denoise".equals(filterName)) {
            return new DenoiseFilter(value.intValue());
        }
        else throw new SyntaxException("Unknown image filter: " + filterName);
    }

    private Rect parseRect(String text) {
        Integer[] numbers = new Integer[4];

        StringCharReader reader = new StringCharReader(text);
        for (int i=0;i<numbers.length; i++) {
            numbers[i] = new ExpectNumber().read(reader).intValue();
        }

        return new Rect(numbers);
    }

    private Pair<Double, String> parseError(String text) {
        StringCharReader reader = new StringCharReader(text);

        Double number = Expectations.number().read(reader);
        String unit = Expectations.word().read(reader);

        return new ImmutablePair<Double, String>(number, unit);
    }

    private Integer parseIntegerParameter(String name, String value) {
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        }
        else throw new SyntaxException(name + " parameter should be integer: " + value);
    }

    public Spec read(String specText) throws IOException {
        return read(specText, ".", NULL_PLACE);
    }

    public Spec read(String specText, String contextPath) throws IOException {
        return read(specText, contextPath, NULL_PLACE);
    }
    public Spec read(String specText, String contextPath, Place place) throws IOException {
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
        
        Spec spec = readSpecWithParams(statement, paramsText, contextPath);
        if (spec != null) {
            spec.setOriginalText(specText);
            spec.setProperties(properties);
        }
        spec.setPlace(place);
        return spec;
    }

    private Spec readSpecWithParams(String specName, String paramsText, String contextPath) throws IOException {
        return findMatchingSpec(specName).processSpec(specName, paramsText, contextPath); 
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

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}

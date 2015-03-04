/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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

import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.parser.*;
import net.mindengine.galen.specs.*;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.rainbow4j.filters.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class SpecReader {

    private static final Place NULL_PLACE = null;
    private Properties properties;
    private final Map<Pattern, SpecProcessor> specsMap = new HashMap<Pattern, SpecProcessor>();

    public SpecReader(final Properties properties) {
        initSpecs();
        this.properties = properties;
    }

    private void initSpecs() {

        putSpec("absent", new SimpleSpecProcessor(new SpecInit() {
            @Override
            public Spec init() {
                return new SpecAbsent();
            }
        }));

        putSpec("visible", new SimpleSpecProcessor(new SpecInit() {
            @Override
            public Spec init() {
                return new SpecVisible();
            }
        }));

        putSpec("contains(\\s+partly)?", new SpecListProccessor(new SpecListInit() {
            @Override
            public Spec init(final String specName, final List<String> list) {
                final String arguments = specName.substring("contains".length()).trim();

                final boolean isPartly = (!arguments.isEmpty() && arguments.equals("partly"));
                return new SpecContains(list, isPartly);
            }
        }));

        putSpec("width", new SpecComplexProcessor(expectThese(range()), new SpecComplexInit() {
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                return new SpecWidth((Range) args[0]);
            }
        }));

        putSpec("height", new SpecComplexProcessor(expectThese(range()), new SpecComplexInit() {
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                return new SpecHeight((Range) args[0]);
            }
        }));

        putSpec("text\\s+.*", new SpecProcessor() {
            @Override
            public Spec processSpec(final String specName, final String paramsText, final String contextPath) {
                final String arguments = specName.substring("text".length()).trim();

                final List<String> allWords = Expectations.readAllWords(arguments);

                if (CollectionUtils.isNotEmpty(allWords)) {
                    final String type = allWords.get(allWords.size() - 1);

                    allWords.remove(allWords.size() - 1);
                    return new SpecText(SpecText.Type.fromString(type), paramsText.trim()).withOperations(allWords);
                } else {
                    throw new SyntaxException("Missing validation type (is, starts, ends, contains, matches)");
                }
            }
        });

        putSpec("css\\s+.*", new SpecProcessor() {
            @Override
            public Spec processSpec(final String specName, final String paramsText, final String contextPath) {
                final String arguments = specName.substring("css".length()).trim();

                final StringCharReader reader = new StringCharReader(arguments);

                final String cssPropertyName = Expectations.word().read(reader);
                final String typeString = Expectations.word().read(reader);

                if (cssPropertyName.isEmpty()) {
                    throw new SyntaxException("Missing css property name");
                }
                if (typeString.isEmpty()) {
                    throw new SyntaxException("Missing validation type (is, contains, starts, ends, matches)");
                }
                return new SpecCss(cssPropertyName, SpecText.Type.fromString(typeString), paramsText.trim());
            }
        });

        putSpec("inside.*", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                final String leftoverName = specName.substring(6).trim();

                final String objectName = (String) args[0];
                final List<Location> locations = (List<Location>) args[1];

                final SpecInside spec = new SpecInside(objectName, locations);

                if (leftoverName.equals("partly")) {
                    spec.setPartly(true);
                }
                return spec;
            }
        }));

        putSpec("near", new SpecComplexProcessor(expectThese(objectName(), locations()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                final String objectName = (String) args[0];
                final List<Location> locations = (List<Location>) args[1];

                if (CollectionUtils.isEmpty(locations)) {
                    throw new SyntaxException(UNKNOWN_LINE, "There is no location defined");
                }
                return new SpecNear(objectName, locations);
            }
        }));

        putSpec("(above|below)", new SpecProcessor() {
            @Override
            public Spec processSpec(final String specName, final String paramsText, final String contextPath) throws IOException {

                final StringCharReader reader = new StringCharReader(paramsText.trim());
                final String objectName = new ExpectWord().read(reader);

                Range range;
                if (reader.hasMore()) {
                    range = Expectations.range().read(reader);
                } else {
                    range = Range.greaterThan(-1.0);
                }

                if (specName.equals("above")) {
                    return new SpecAbove(objectName, range);
                } else {
                    return new SpecBelow(objectName, range);
                }
            }
        });

        putSpec("aligned\\s+.*", new SpecObjectAndErrorRateProcessor(new SpecObjectAndErrorRateInit() {

            @Override
            public Spec init(final String specName, final String objectName, Integer errorRate) {
                final String arguments = specName.substring("aligned".length()).trim();

                final StringCharReader reader = new StringCharReader(arguments);

                final String[] words = ExpectWord.readAllWords(reader);

                if (words.length == 0) {
                    throw new SyntaxException("Alignment is not defined. Should be either 'vertically' either 'horizonally'");
                }
                final String type = words[0];

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
                    } else {
                        throw new SyntaxException(UNKNOWN_LINE, "Horizontal alignment doesn't allow this side: " + alignment.toString());
                    }
                } else if (type.equals("vertically")) {
                    if (alignment.isOneOf(CENTERED, LEFT, RIGHT, ALL)) {
                        return new SpecVertically(alignment, objectName).withErrorRate(errorRate);
                    } else {
                        throw new SyntaxException(UNKNOWN_LINE, "Verticall alignment doesn't allow this side: " + alignment.toString());
                    }
                } else {
                    throw new SyntaxException("Unknown alignment: " + type);
                }
            }
        }));

        putSpec("centered\\s.*", new SpecObjectAndErrorRateProcessor(new SpecObjectAndErrorRateInit() {

            @Override
            public Spec init(String specName, final String objectName, Integer errorRate) {
                specName = specName.replace("centered", "").trim();
                final String args[] = specName.split(" ");

                SpecCentered.Alignment alignment = SpecCentered.Alignment.ALL;
                SpecCentered.Location location = null;
                if (args.length == 1) {
                    location = SpecCentered.Location.fromString(args[0]);
                } else {
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
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                final String objectName = (String) args[0];

                final String[] words = ExpectWord.readAllWords(new StringCharReader(specName));

                if (words.length > 3) {
                    throw new SyntaxException("Too many sides. Should use only 2");
                }

                Side sideHorizontal = Side.TOP;
                Side sideVertical = Side.LEFT;

                boolean isFirstHorizontal = false;
                if (words.length > 1) {
                    final Side side = Side.fromString(words[1]);
                    if (side == Side.TOP || side == Side.BOTTOM) {
                        isFirstHorizontal = true;
                        sideHorizontal = side;
                    } else {
                        sideVertical = side;
                    }
                }

                if (words.length > 2) {
                    final Side side = Side.fromString(words[2]);
                    if (side == Side.TOP || side == Side.BOTTOM) {
                        if (isFirstHorizontal) {
                            throw new SyntaxException("Cannot use theses sides: " + words[1] + " " + words[2]);
                        }
                        sideHorizontal = side;
                    } else {
                        if (!isFirstHorizontal) {
                            throw new SyntaxException("Cannot use theses sides: " + words[1] + " " + words[2]);
                        }
                        sideVertical = side;
                    }
                }

                final List<Location> locations = (List<Location>) args[1];
                if (locations == null || locations.size() == 0) {
                    throw new SyntaxException(UNKNOWN_LINE, "There is no location defined");
                }

                return new SpecOn(objectName, sideHorizontal, sideVertical, locations);
            }
        }));

        putSpec("component.*", new SpecProcessor() {

            @Override
            public Spec processSpec(final String specName, final String paramsText, final String contextPath) throws IOException {

                final String childFilePath = paramsText.trim();
                if (childFilePath.isEmpty()) {
                    throw new SyntaxException("File path to component spec is not specified");
                }

                String fullFilePath = childFilePath;
                if (contextPath != null) {
                    fullFilePath = contextPath + File.separator + childFilePath;
                }

                final SpecComponent spec = new SpecComponent();
                spec.setSpecPath(fullFilePath);

                if (getSecondWord(specName).equals("frame")) {
                    spec.setFrame(true);
                }

                return spec;
            }
        });

        putSpec("color\\s+scheme", new SpecComplexProcessor(expectThese(colorRanges()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {

                final List<ColorRange> colorRanges = (List<ColorRange>) args[0];
                if (CollectionUtils.isEmpty(colorRanges)) {
                    throw new SyntaxException("There are no colors defined");
                }

                final SpecColorScheme spec = new SpecColorScheme();
                spec.setColorRanges(colorRanges);
                return spec;
            }
        }));

        putSpec("image", new SpecComplexProcessor(expectThese(commaSeparatedRepeatedKeyValues()), new SpecComplexInit() {
            @SuppressWarnings("unchecked")
            @Override
            public Spec init(final String specName, final String paramsText, final String contextPath, final Object[] args) {
                final List<Pair<String, String>> parameters = (List<Pair<String, String>>) args[0];

                final SpecImage spec = new SpecImage();
                spec.setImagePaths(new LinkedList<String>());
                spec.setStretch(false);
                spec.setErrorRate(GalenConfig.getConfig().getImageSpecDefaultErrorRate());
                spec.setTolerance(GalenConfig.getConfig().getImageSpecDefaultTolerance());

                for (final Pair<String, String> parameter : parameters) {
                    if ("file".equals(parameter.getKey())) {
                        if (contextPath != null) {
                            spec.getImagePaths().add(contextPath + File.separator + parameter.getValue());
                        } else {
                            spec.getImagePaths().add(parameter.getValue());
                        }
                    } else if ("error".equals(parameter.getKey())) {
                        spec.setErrorRate(SpecImage.ErrorRate.fromString(parameter.getValue()));
                    } else if ("tolerance".equals(parameter.getKey())) {
                        spec.setTolerance(parseIntegerParameter("tolerance", parameter.getValue()));
                    } else if ("stretch".equals(parameter.getKey())) {
                        spec.setStretch(true);
                    } else if ("area".equals(parameter.getKey())) {
                        spec.setSelectedArea(parseRect(parameter.getValue()));
                    } else if ("filter".equals(parameter.getKey())) {
                        final ImageFilter filter = parseImageFilter(parameter.getValue());
                        spec.getOriginalFilters().add(filter);
                        spec.getSampleFilters().add(filter);
                    } else if ("filter-a".equals(parameter.getKey())) {
                        final ImageFilter filter = parseImageFilter(parameter.getValue());
                        spec.getOriginalFilters().add(filter);
                    } else if ("filter-b".equals(parameter.getKey())) {
                        final ImageFilter filter = parseImageFilter(parameter.getValue());
                        spec.getSampleFilters().add(filter);
                    } else if ("map-filter".equals(parameter.getKey())) {
                        final ImageFilter filter = parseImageFilter(parameter.getValue());
                        spec.getMapFilters().add(filter);
                    } else if ("crop-if-outside".equals(parameter.getKey())) {
                        spec.setCropIfOutside(true);
                    } else {
                        throw new SyntaxException("Unknown parameter: " + parameter.getKey());
                    }
                }

                if (spec.getImagePaths() == null || spec.getImagePaths().size() == 0) {
                    throw new SyntaxException("There are no images defined");
                }
                return spec;
            }
        }));

    }

    private String getSecondWord(final String text) {
        final StringCharReader reader = new StringCharReader(text);
        Expectations.word().read(reader);
        return Expectations.word().read(reader);
    }

    private ImageFilter parseImageFilter(final String filterText) {
        final StringCharReader reader = new StringCharReader(filterText);

        final String filterName = new ExpectWord().read(reader);
        final Double value = new ExpectNumber().read(reader);

        if ("contrast".equals(filterName)) {
            return new ContrastFilter(value.intValue());
        } else if ("blur".equals(filterName)) {
            return new BlurFilter(value.intValue());
        } else if ("denoise".equals(filterName)) {
            return new DenoiseFilter(value.intValue());
        } else if ("saturation".equals(filterName)) {
            return new SaturationFilter(value.intValue());
        } else if ("quantinize".equals(filterName)) {
            return new QuantinizeFilter(value.intValue());
        } else {
            throw new SyntaxException("Unknown image filter: " + filterName);
        }
    }

    private Rect parseRect(final String text) {
        final Integer[] numbers = new Integer[4];

        final StringCharReader reader = new StringCharReader(text);
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new ExpectNumber().read(reader).intValue();
        }

        return new Rect(numbers);
    }

    private Integer parseIntegerParameter(final String name, final String value) {
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        } else {
            throw new SyntaxException(name + " parameter should be integer: " + value);
        }
    }

    public Spec read(final String specText) throws IOException {
        return read(specText, ".", NULL_PLACE);
    }

    public Spec read(final String specText, final String contextPath) throws IOException {
        return read(specText, contextPath, NULL_PLACE);
    }

    public Spec read(String specText, final String contextPath, final Place place) throws IOException {
        if (specText == null) {
            throw new NullPointerException("Spec text should not be null");
        } else if (specText.trim().isEmpty()) {
            throw new SyntaxException(UNKNOWN_LINE, "Spec text should not be empty");
        }

        specText = specText.trim();

        final int splitterIndex = specText.indexOf(":");

        String statement = specText;
        String paramsText = "";
        if (splitterIndex > 0) {
            statement = specText.substring(0, splitterIndex);
            if (splitterIndex < specText.length()) {
                paramsText = specText.substring(splitterIndex + 1);
            }
        }

        final Spec spec = readSpecWithParams(statement, paramsText, contextPath);
        if (spec != null) {
            spec.setOriginalText(specText);
            spec.setProperties(properties);
        }
        spec.setPlace(place);
        return spec;
    }

    private Spec readSpecWithParams(final String specName, final String paramsText, final String contextPath) throws IOException {
        return findMatchingSpec(specName).processSpec(specName, paramsText, contextPath);
    }

    private SpecProcessor findMatchingSpec(final String specName) {

        for (final Map.Entry<Pattern, SpecProcessor> entry : specsMap.entrySet()) {
            final Matcher matcher = entry.getKey().matcher(specName);
            if (matcher.matches()) {
                return entry.getValue();
            }
        }
        throw new SyntaxException(UNKNOWN_LINE, "Such constraint does not exist: " + specName);
    }

    private void putSpec(final String patternText, final SpecProcessor specProcessor) {
        specsMap.put(Pattern.compile(patternText), specProcessor);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
}

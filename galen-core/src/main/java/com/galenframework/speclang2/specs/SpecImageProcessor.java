/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.speclang2.specs;

import com.galenframework.page.Rect;
import com.galenframework.parser.ExpectNumber;
import com.galenframework.parser.ExpectWord;
import com.galenframework.parser.SyntaxException;
import com.galenframework.rainbow4j.ImageHandler;
import com.galenframework.rainbow4j.Rainbow4J;
import com.galenframework.rainbow4j.colorscheme.ColorClassifier;
import com.galenframework.rainbow4j.filters.*;
import com.galenframework.specs.SpecImage;
import com.galenframework.parser.StringCharReader;
import com.galenframework.config.GalenConfig;
import com.galenframework.parser.Expectations;
import com.galenframework.specs.Spec;
import com.galenframework.utils.GalenUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static com.galenframework.parser.ExpectColorRanges.parseColor;
import static com.galenframework.parser.ExpectColorRanges.parseColorClassifier;
import static java.util.Collections.singletonList;

public class SpecImageProcessor implements SpecProcessor {

    @Override
    public Spec process(StringCharReader reader, String contextPath) {
        List<Pair<String, String>> parameters = Expectations.commaSeparatedRepeatedKeyValues().read(reader);
        SpecImage spec = new SpecImage();
        spec.setImagePaths(new LinkedList<String>());
        spec.setStretch(false);
        spec.setErrorRate(GalenConfig.getConfig().getImageSpecDefaultErrorRate());
        spec.setTolerance(GalenConfig.getConfig().getImageSpecDefaultTolerance());

        for (Pair<String, String> parameter : parameters) {
            if ("file".equals(parameter.getKey())) {
                if (contextPath != null) {
                    spec.getImagePaths().add(contextPath + File.separator + parameter.getValue());
                }
                else {
                    spec.getImagePaths().add(parameter.getValue());
                }
            }
            else if ("error".equals(parameter.getKey())) {
                spec.setErrorRate(SpecImage.ErrorRate.fromString(parameter.getValue()));
            }
            else if ("tolerance".equals(parameter.getKey())) {
                spec.setTolerance(parseIntegerParameter("tolerance", parameter.getValue()));
            }
            else if ("analyze-offset".equals(parameter.getKey())) {
                spec.setAnalyzeOffset(parseIntegerParameter("analyze-offset", parameter.getValue()));
            }
            else if ("stretch".equals(parameter.getKey())) {
                spec.setStretch(true);
            }
            else if ("area".equals(parameter.getKey())) {
                spec.setSelectedArea(parseRect(parameter.getValue()));
            }
            else if ("filter".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue(), contextPath);
                spec.getOriginalFilters().add(filter);
                spec.getSampleFilters().add(filter);
            }
            else if ("filter-a".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue(), contextPath);
                spec.getOriginalFilters().add(filter);
            }
            else if ("filter-b".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue(), contextPath);
                spec.getSampleFilters().add(filter);
            }
            else if ("map-filter".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue(), contextPath);
                spec.getMapFilters().add(filter);
            }
            else if ("crop-if-outside".equals(parameter.getKey())) {
                spec.setCropIfOutside(true);
            }
            else if ("exclude-objects".equals(parameter.getKey())) {
                spec.setExcludedObjects(parseExcludeObjects(parameter.getValue()));
            }
            else {
                throw new SyntaxException("Unknown parameter: " + parameter.getKey());
            }
        }

        if (spec.getImagePaths() == null || spec.getImagePaths().size() == 0) {
            throw new SyntaxException("There are no images defined");
        }
        return spec;
    }

    private String parseExcludeObjects(String value) {
        if (value.startsWith("[") && value.endsWith("]")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private Integer parseIntegerParameter(String name, String value) {
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        }
        else throw new SyntaxException(name + " parameter should be integer: " + value);
    }

    private ImageFilter parseImageFilter(String filterText, String contextPath) {
        StringCharReader reader = new StringCharReader(filterText);

        String filterName = new ExpectWord().read(reader);


        if ("mask".equals(filterName)) {
            return parseMaskFilter(contextPath, reader);
        } else if ("replace-colors".equals(filterName)) {
            return parseReplaceColorsFilter(reader);
        } else {
            return parseSimpleFilter(reader, filterName);
        }
    }


    private ImageFilter parseReplaceColorsFilter(StringCharReader reader) {
        List<ColorClassifier> classifiers = new LinkedList<>();
        Color replaceColor = null;

        int tolerance = ReplaceColorsDefinition.DEFAULT_COLOR_TOLERANCE_FOR_SPECTRUM;
        int radius = ReplaceColorsDefinition.DEFAULT_RADIUS;

        while (reader.hasMore()) {
            String word = reader.readWord();
            if ("with".equals(word)) {
                replaceColor = parseColor(reader.readWord());
            } else if ("tolerance".equals(word)) {
                tolerance = parseInt(reader);
            } else if ("radius".equals(word)) {
                radius = parseInt(reader);
            } else {
                classifiers.add(parseColorClassifier(word));
            }
        }

        if (replaceColor == null) {
            throw new SyntaxException("Replace color was not specified");
        }
        ReplaceColorsDefinition colorDefinition = new ReplaceColorsDefinition(replaceColor, classifiers);
        colorDefinition.setTolerance(tolerance);
        colorDefinition.setRadius(radius);
        return new ReplaceColorsFilter(singletonList(colorDefinition));
    }

    private int parseInt(StringCharReader reader) {
        Double value = Expectations.number().read(reader);
        if (value != null) {
            return value.intValue();
        }
        return 0;
    }

    private ImageFilter parseSimpleFilter(StringCharReader reader, String filterName) {
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
        else if ("saturation".equals(filterName)) {
            return new SaturationFilter(value.intValue());
        }
        else if ("quantinize".equals(filterName)) {
            return new QuantinizeFilter(value.intValue());
        } else {
            throw new SyntaxException("Unknown image filter: " + filterName);
        }
    }

    private ImageFilter parseMaskFilter(String contextPath, StringCharReader reader) {
        String imagePath = reader.getTheRest().trim();

        if (imagePath.isEmpty()) {
            throw new SyntaxException("Mask filter image path is not defined");
        }

        String fullImagePath = imagePath;

        if (contextPath != null && !contextPath.isEmpty()) {
            fullImagePath = contextPath + File.separator + imagePath;
        }
        try {

            InputStream stream = GalenUtils.findMandatoryFileOrResourceAsStream(fullImagePath);

            return new MaskFilter(new ImageHandler(Rainbow4J.loadImage(stream)));
        } catch (IOException exception) {
            throw new SyntaxException("Couldn't load " + fullImagePath, exception);
        }
    }

    private Rect parseRect(String text) {
        Integer[] numbers = new Integer[4];

        StringCharReader reader = new StringCharReader(text);
        for (int i=0;i<numbers.length; i++) {
            numbers[i] = new ExpectNumber().read(reader).intValue();
        }

        return new Rect(numbers);
    }
}

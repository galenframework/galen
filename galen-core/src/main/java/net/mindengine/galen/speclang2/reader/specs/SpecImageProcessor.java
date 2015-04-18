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
package net.mindengine.galen.speclang2.reader.specs;

import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.parser.ExpectNumber;
import net.mindengine.galen.parser.ExpectWord;
import net.mindengine.galen.parser.Expectations;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecImage;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.rainbow4j.filters.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
            else if ("stretch".equals(parameter.getKey())) {
                spec.setStretch(true);
            }
            else if ("area".equals(parameter.getKey())) {
                spec.setSelectedArea(parseRect(parameter.getValue()));
            }
            else if ("filter".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue());
                spec.getOriginalFilters().add(filter);
                spec.getSampleFilters().add(filter);
            }
            else if ("filter-a".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue());
                spec.getOriginalFilters().add(filter);
            }
            else if ("filter-b".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue());
                spec.getSampleFilters().add(filter);
            }
            else if ("map-filter".equals(parameter.getKey())) {
                ImageFilter filter = parseImageFilter(parameter.getValue());
                spec.getMapFilters().add(filter);
            }
            else if ("crop-if-outside".equals(parameter.getKey())) {
                spec.setCropIfOutside(true);
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

    private Integer parseIntegerParameter(String name, String value) {
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value);
        }
        else throw new SyntaxException(name + " parameter should be integer: " + value);
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
        else if ("saturation".equals(filterName)) {
            return new SaturationFilter(value.intValue());
        }
        else if ("quantinize".equals(filterName)) {
            return new QuantinizeFilter(value.intValue());
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
}

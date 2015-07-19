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
package com.galenframework.speclang2.reader.specs;

import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.*;
import com.galenframework.specs.reader.StringCharReader;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.reader.StringCharReader;

import java.util.HashMap;
import java.util.Map;


public class SpecReader {
    public static Map<String, SpecProcessor> specProcessors = initSpecProcessors();

    public SpecReader() {
        initSpecProcessors();
    }

    private static Map<String, SpecProcessor> initSpecProcessors() {
        return new HashMap<String, SpecProcessor>() {{
            put("inside", new SpecInsideProcessor());
            put("contains", new SpecContainsProcessor());
            put("near", new SpecNearProcessor());
            put("aligned", new SpecAlignedProcessor());
            put("absent", new SingleWordSpecProcessor() {
                @Override
                public Spec createSpec() {
                    return new SpecAbsent();
                }
            });
            put("visible", new SingleWordSpecProcessor() {
                @Override
                public Spec createSpec() {
                    return new SpecVisible();
                }
            });
            put("width", new SpecWithRangeProcessor() {
                @Override
                public Spec createSpec(Range range) {
                    return new SpecWidth(range);
                }
            });
            put("height", new SpecWithRangeProcessor() {
                @Override
                public Spec createSpec(Range range) {
                    return new SpecHeight(range);
                }
            });
            put("text", new SpecTextProcessor());
            put("css", new SpecCssProcessor());
            put("above", new SpecWithObjectAndRangeProcessor() {
                @Override
                public Spec createSpec(String objectName, Range range) {
                    return new SpecAbove(objectName, range);
                }
            });
            put("below", new SpecWithObjectAndRangeProcessor() {
                @Override
                public Spec createSpec(String objectName, Range range) {
                    return new SpecBelow(objectName, range);
                }
            });
            put("left-of", new SpecWithObjectAndRangeProcessor() {
                @Override
                public Spec createSpec(String objectName, Range range) {
                    return new SpecLeftOf(objectName, range);
                }
            });
            put("right-of", new SpecWithObjectAndRangeProcessor() {
                @Override
                public Spec createSpec(String objectName, Range range) {
                    return new SpecRightOf(objectName, range);
                }
            });
            put("centered", new SpecCenteredProcessor());
            put("on", new SpecOnProcessor());
            put("color-scheme", new SpecColorSchemeProcessor());
            put("image", new SpecImageProcessor());
            put("component", new SpecComponentProcessor());
        }};
    }

    public Spec read(String specText, String contextPath) {
        if (specText == null) {
            throw new IllegalArgumentException("specText argument should not be null");
        }

        specText = specText.trim();

        if (specText.isEmpty()) {
            throw new IllegalArgumentException("specText should not be empty");
        }

        StringCharReader reader = new StringCharReader(specText);
        String firstWord = reader.readWord();

        SpecProcessor specProcessor = specProcessors.get(firstWord);

        if (specProcessor != null) {
            Spec spec = specProcessor.process(reader, contextPath);
            spec.setOriginalText(specText);
            return spec;
        } else {
            throw new SyntaxException("Unknown spec: " + firstWord);
        }
    }

    public Spec read(String specText) {
        return read(specText, ".");
    }
}

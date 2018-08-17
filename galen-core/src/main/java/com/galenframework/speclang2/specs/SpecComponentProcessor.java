/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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

import com.galenframework.parser.Expectations;
import com.galenframework.specs.SpecComponent;
import com.galenframework.parser.StringCharReader;
import com.galenframework.specs.Spec;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecComponentProcessor implements SpecProcessor {
    @Override
    public Spec process(StringCharReader reader, String contextPath) {
        SpecComponent spec = new SpecComponent();

        int initialPosition = reader.currentCursorPosition();
        if (reader.readWord().equals("frame")) {
            spec.setFrame(true);
        } else {
            reader.moveCursorTo(initialPosition);
        }

        String filePath = reader.readSafeUntilSymbol(',').trim();

        List<Pair<String, String>> unprocessedArguments = Expectations.commaSeparatedRepeatedKeyValues().read(reader);
        spec.setArguments(processArguments(unprocessedArguments));

        if (contextPath != null && !contextPath.equals(".")) {
            filePath = contextPath + File.separator + filePath;
        }

        spec.setSpecPath(filePath);
        return spec;
    }

    private Map<String, Object> processArguments(List<Pair<String, String>> unprocessedArguments) {
        Map<String, Object> arguments = new HashMap<>();

        for (Pair<String, String> textArgument : unprocessedArguments) {
            arguments.put(textArgument.getKey(), processArgumentValue(textArgument.getValue()));
        }
        return arguments;
    }

    private Object processArgumentValue(String value) {
        try {
            if (value == null) {
                return "";
            }

            if (NumberUtils.isDigits(value)) {
                return Long.parseLong(value);
            } else if (NumberUtils.isNumber(value)) {
                return Double.parseDouble(value);
            } else if (value.equals("true")){
                return true;
            } else if (value.equals("false")){
                return false;
            } else {
                return value;
            }
        } catch (Exception ex) {
            return value;
        }
    }
}

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
package net.mindengine.galen.parser;

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.galen.specs.page.CorrectionsRect;

public class Expectations {

    
    public static List<Expectation<?>> expectThese(Expectation<?>...expectations) {
        return Arrays.asList(expectations);
    }

    public static Expectation<List<Side>> sides() {
        return new ExpectSides();
    }

    public static Expectation<Range> range() {
        return new ExpectRange();
    }

    public static Expectation<String> objectName() {
        return new ExpectWord();
    }
    
    public static Expectation<String[]> commandLineArguments() {
        return new ExpectCommandLineArguments();
    }
    
    public static boolean isDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t';
    }
    
    public static boolean isWordDelimeter(char symbol) {
        return symbol == ' ' || symbol == '\t' || symbol == ',';
    }
    
    public static boolean isNumeric(char symbol) {
        return symbol == '-' || (symbol >= '0' && symbol <= '9');
    }

    public static Expectation<List<Location>> locations() {
        return new ExpectLocations();
    }

    public static Expectation<CorrectionsRect> corrections() {
        return new ExpectCorrection();
    }

    public static Expectation<List<ColorRange>> colorRanges() {
        return new ExpectColorRanges();
    }

    
}

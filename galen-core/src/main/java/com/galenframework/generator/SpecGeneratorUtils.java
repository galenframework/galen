/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SpecGeneratorUtils {
    public static String findNamingPattern(List<String> allObjectNames, PageItemNode[] pins) {
        if (pins.length > 1) {
            Set<String> suffix = new HashSet<>();
            for (PageItemNode pin : pins) {
                suffix.add(stripOffDigitAtTheEnd(pin.getPageItem().getName()));
                if (suffix.size() > 1) {
                    return null;
                }
            }

            String firstPart = suffix.iterator().next();
            if (verifyMatchesAllNamesExactly(allObjectNames, firstPart, pins.length)) {
                return firstPart + "*";
            } else {
                return null;
            }
        }
        return null;
    }

    private static boolean verifyMatchesAllNamesExactly(List<String> allObjectNames, String firstPart, int amount) {
        Pattern pattern = Pattern.compile("\\Q" + firstPart + "\\E.*");
        int count = 0;
        for (String name : allObjectNames) {
            if (pattern.matcher(name).matches()) {
                count++;
                if (count > amount) {
                    return false;
                }
            }
        }
        return count == amount;
    }

    private static String stripOffDigitAtTheEnd(String name) {
        for (int i = name.length() - 1; i > 0; i--) {
            char symbol = name.charAt(i);
            if (symbol < 48 || symbol > 57) {
                return name.substring(0, i + 1);
            }
        }
        return name;
    }
}

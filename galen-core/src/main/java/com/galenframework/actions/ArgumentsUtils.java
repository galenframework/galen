/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishubin on 2015/07/20.
 */
public class ArgumentsUtils {

    public static String[] processSystemProperties(String[] args) {
        ArrayList<String> list = new ArrayList<String>();

        for (String arg : args) {
            if (arg.startsWith("-D")) {
                setSystemProperty(arg);
            }
            else {
                list.add(arg);
            }
        }
        return list.toArray(new String[]{});
    }

    private static void setSystemProperty(String systemPropertyDefinition) {
        String pairKeyAndValue = systemPropertyDefinition.substring(2);
        int equalSignPosition = pairKeyAndValue.indexOf('=');
        if (equalSignPosition > 0) {
            System.setProperty(pairKeyAndValue.substring(0, equalSignPosition), pairKeyAndValue.substring(equalSignPosition+1));
        }
        else {
            throw new IllegalArgumentException("Cannot parse: " + systemPropertyDefinition);
        }
    }

    public static List<String> convertTags(String optionValue) {
        if (optionValue != null) {
            List<String> tags = new LinkedList<String>();
            String[] array = optionValue.split(",");

            for (String item : array) {
                String tag = item.trim();

                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            }
            return tags;
        }
        return null;
    }
}

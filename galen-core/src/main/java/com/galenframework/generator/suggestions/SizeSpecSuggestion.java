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
package com.galenframework.generator.suggestions;

import com.galenframework.generator.*;
import com.galenframework.generator.math.Rect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class SizeSpecSuggestion extends SingleArgSpecSuggestion {
    public static final String S_SIZE = "s_size";

    @Override
    public String getName() {
        return S_SIZE;
    }

    @Override
    protected SuggestionTestResult testIt(SuggestionOptions options, PageItemNode pin) {
        String itemName = pin.getPageItem().getName();

        Rect area = pin.getPageItem().getArea();
        if (area.getWidth() == area.getHeight() && area.getWidth() <= 200) {
            return new SuggestionTestResult().addGeneratedRule(
                itemName,
                new SpecStatement(
                    format("| %s should be squared with %dpx size", pin.getPageItem().getName(), area.getWidth()),
                    asList(
                        new SpecAssertion(AssertionEdge.left(itemName), AssertionEdge.right(itemName)),
                        new SpecAssertion(AssertionEdge.top(itemName), AssertionEdge.bottom(itemName))
                    )
                )
            );
        } else {
            List<SpecStatement> specs = new LinkedList<>();
            if (area.getWidth() <= 90) {
                specs.add(new SpecStatement(
                    format("width %dpx", area.getWidth()),
                    singletonList(new SpecAssertion(AssertionEdge.left(itemName), AssertionEdge.right(itemName)))
                ));
            }
            if (area.getHeight() <= 90) {
                specs.add(new SpecStatement(
                    format("height %dpx", area.getHeight()),
                    singletonList(new SpecAssertion(AssertionEdge.top(itemName), AssertionEdge.bottom(itemName)))
                ));
            }
            if (specs.size() > 0) {
                return new SuggestionTestResult().addObjectSpecs(pin.getPageItem().getName(), specs);
            }
        }
        return null;
    }
}

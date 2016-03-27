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
package com.galenframework.speclang2.pagespec.rules;

import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.StringCharReader;

/**
 * Created by ishubin on 2015/02/22.
 */
public class RuleParseStateParameter extends RuleParseState {

    private int amountOfOpenCurlyBraces = 1;

    @Override
    public void process(RuleBuilder ruleBuilder, StringCharReader reader) {

        RuleBuilder.ParameterChunk chunk = ruleBuilder.newParameterChunk();

        boolean notFinished = true;

        while(reader.hasMore() && notFinished) {
            char symbol = reader.next();

            if (symbol == '{') {
                amountOfOpenCurlyBraces += 1;
                chunk.appendSymbol(symbol);
            } else if (symbol == '}') {
                amountOfOpenCurlyBraces -= 1;

                if (amountOfOpenCurlyBraces == 0) {
                    notFinished = false;
                } else {
                    chunk.appendSymbol(symbol);
                }
            } else {
                chunk.appendSymbol(symbol);
            }
        }

        if (amountOfOpenCurlyBraces > 0) {
            throw new SyntaxException("Missing '}' to close parameter definition");
        }
    }
}
